/*************************************************************************
 *
 * $RCSfile: NewUnoProjectPage.java,v $
 *
 * $Revision: 1.6 $
 *
 * last change: $Author: cedricbosdo $ $Date: 2006/02/19 11:32:41 $
 *
 * The Contents of this file are made available subject to the terms of
 * either of the GNU Lesser General Public License Version 2.1
 *
 * Sun Microsystems Inc., October, 2000
 *
 *
 * GNU Lesser General Public License Version 2.1
 * =============================================
 * Copyright 2000 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, CA 94303, USA
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 * 
 * The Initial Developer of the Original Code is: Sun Microsystems, Inc..
 *
 * Copyright: 2002 by Sun Microsystems, Inc.
 *
 * All Rights Reserved.
 *
 * Contributor(s): Cedric Bosdonnat
 *
 *
 ************************************************************************/
package org.openoffice.ide.eclipse.wizards;

import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.openoffice.ide.eclipse.OOEclipsePlugin;
import org.openoffice.ide.eclipse.gui.OOoTable;
import org.openoffice.ide.eclipse.gui.SDKTable;
import org.openoffice.ide.eclipse.gui.rows.ChoiceRow;
import org.openoffice.ide.eclipse.gui.rows.FieldEvent;
import org.openoffice.ide.eclipse.gui.rows.IFieldChangedListener;
import org.openoffice.ide.eclipse.gui.rows.TextRow;
import org.openoffice.ide.eclipse.i18n.I18nConstants;
import org.openoffice.ide.eclipse.i18n.ImagesConstants;
import org.openoffice.ide.eclipse.model.UnoidlProject;
import org.openoffice.ide.eclipse.preferences.ConfigListener;
import org.openoffice.ide.eclipse.preferences.ooo.OOo;
import org.openoffice.ide.eclipse.preferences.ooo.OOoContainer;
import org.openoffice.ide.eclipse.preferences.sdk.SDK;
import org.openoffice.ide.eclipse.preferences.sdk.SDKContainer;

/**
 * Uses the default Project wizard page and add some UNO-IDL special
 * fields: SDK choice company prefix and Output path
 * 
 * @author cbosdonnat
 *
 */
public class NewUnoProjectPage extends WizardNewProjectCreationPage 
							   implements IFieldChangedListener, 
							      		  ConfigListener{
	
	/* Constants defining the field properties used to react to field change events */
	private static final String PREFIX = "__prefix";
	private static final String OUTPUT_EXT = "__output_ext";
	private static final String SDK = "__sdk";
	private static final String OOO = "__ooo";
	private static final String LANGUAGE = "__language";
	
	/**
	 * Prefix field object
	 */
	private TextRow prefixRow;
	
	/**
	 * Implementation extension field object
	 */
	private TextRow outputExt;
	
	/**
	 * SDK used for the project selection row
	 */
	private ChoiceRow sdkRow;
	
	/**
	 * OOo used for the project selection row
	 */
	private ChoiceRow oooRow;
	
	/**
	 * Programming language to use for code generation 
	 */
	private ChoiceRow languageRow;
	
	/**
	 * Specific error message label. <code>setErrorMessage()</code> will
	 * use this row instead of the standard one.
	 */
	private Label messageLabel;
	private Label messageIcon;
	
	public NewUnoProjectPage() {
		super(OOEclipsePlugin.getTranslationString(
				I18nConstants.NEW_PROJECT_TITLE));
		
		setTitle(OOEclipsePlugin.getTranslationString(
				I18nConstants.NEW_PROJECT_TITLE));
		
		setDescription(OOEclipsePlugin.getTranslationString(
				I18nConstants.NEW_PROJECT_MESSAGE));
		
		setImageDescriptor(OOEclipsePlugin.getImageDescriptor(
				ImagesConstants.NEWPROJECT_WIZ));
		
		OOoContainer.getOOoContainer().addListener(this);
		SDKContainer.getSDKContainer().addListener(this);
	}
	
	public void dispose() {
		super.dispose();
		
		OOoContainer.getOOoContainer().removeListener(this);
		SDKContainer.getSDKContainer().removeListener(this);
	}
	
	/**
	 * Returns the entered company prefix
	 * 
	 * @return company prefix entered
	 */
	public String getPrefix(){
		String prefix = "";
		if (null != prefixRow) {
			prefix = prefixRow.getValue();
		}
		return prefix;
	}
	
	/**
	 * Returns the entered ouput extension
	 * 
	 * @return ouput extension entered
	 */
	public String getOutputExt(){
		String output = "";
		if (null != outputExt) {
			output = outputExt.getValue();
		}
		return output;
	}
	
	/**
	 * Returns the selected SDK Name
	 * 
	 * @return SDK name selected
	 */
	public String getSDKName(){
		String sdkName = "";
		if (null != sdkRow) {
			sdkName = sdkRow.getValue();
		}
		return sdkName;
	}
	
	/**
	 * Returns the selected OOo Name
	 * 
	 * @return OOo name selected
	 */
	public String getOOoName(){
		String oooName = "";
		if (null != oooRow) {
			oooName = oooRow.getValue();
		}
		return oooName;
	}
	
	public int getChosenLanguage(){
		 String value = languageRow.getValue();
		 int result = UnoidlProject.JAVA_LANGUAGE;
		 
		 if (value.equals(OOEclipsePlugin.getTranslationString(
				 I18nConstants.JAVA))){
			 result = UnoidlProject.JAVA_LANGUAGE;
			 
		 } else if (value.equals(OOEclipsePlugin.getTranslationString(
				 I18nConstants.CPP))){
			 result = UnoidlProject.CPP_LANGUAGE;
			 
		 } else if (value.equals(OOEclipsePlugin.getTranslationString(
				 I18nConstants.PYTHON))){
			 result = UnoidlProject.PYTHON_LANGUAGE;
		 }
		 
		 return result;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		// Inherits the parents control
		
		super.createControl(parent);
		Composite control = (Composite)getControl();
		
		
		
		Composite body = new Composite(control, SWT.NONE);
		body.setLayout(new GridLayout(3, false));
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Add the company prefix field
		prefixRow = new TextRow(body, PREFIX, 
						OOEclipsePlugin.getTranslationString(I18nConstants.COMPANY_PREFIX));
		prefixRow.setFieldChangedListener(this);
		
		// Add the output directory field
		outputExt = new TextRow(body, OUTPUT_EXT,
						OOEclipsePlugin.getTranslationString(I18nConstants.OUTPUT_EXT));
		outputExt.setValue("comp"); // Setting default value
		outputExt.setFieldChangedListener(this);
		
		// Add the SDK choice field
		sdkRow = new ChoiceRow(body, SDK,
						OOEclipsePlugin.getTranslationString(I18nConstants.USED_SDK),
						OOEclipsePlugin.getTranslationString(I18nConstants.SDKS));
		sdkRow.setFieldChangedListener(this);
		sdkRow.setBrowseSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				
				// Open the SDK Configuration page
				TableDialog dialog = new TableDialog(getShell(), true);
				dialog.create();
				dialog.open();
				
			}
		});
		
		fillSDKRow();
		
		
		// Add the OOo choice field
		oooRow = new ChoiceRow(body, OOO,
						OOEclipsePlugin.getTranslationString(I18nConstants.USED_OOO),
						OOEclipsePlugin.getTranslationString(I18nConstants.OOOS));
		oooRow.setFieldChangedListener(this);
		oooRow.setBrowseSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				
				// Open the OOo Configuration page
				TableDialog dialog = new TableDialog(getShell(), false);
				dialog.create();
				dialog.open();
			}
		});
		
		fillOOoRow();
		
		
		
		// Adding the programming language row 
		languageRow = new ChoiceRow(body, LANGUAGE,
						OOEclipsePlugin.getTranslationString(I18nConstants.PROG_LANGUAGE));
		
		// Sets the available programming languages
		// TODO Add CPP and PYTHON when they will be ready
		languageRow.add(OOEclipsePlugin.getTranslationString(I18nConstants.JAVA));
		languageRow.select(0);
		languageRow.setFieldChangedListener(this);
		
		// Add an error message label
		Composite messageComposite = new Composite(control, SWT.NONE);
		messageComposite.setLayout(new GridLayout(2, false));
		messageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		messageIcon = new Label(messageComposite, SWT.LEFT);
		messageIcon.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING |
											   GridData.VERTICAL_ALIGN_END));
		messageIcon.setImage(OOEclipsePlugin.getImage(ImagesConstants.ERROR));
		messageIcon.setVisible(false);
		
		messageLabel = new Label(messageComposite, SWT.LEFT);
		messageLabel.setLayoutData(new GridData(GridData.FILL_BOTH |
				                                GridData.VERTICAL_ALIGN_END));
	}
	
	private void fillSDKRow (){
		
		if (null != sdkRow){
			// Adding the SDK names to the combo box 
			SDKContainer sdkContainer = SDKContainer.getSDKContainer();
			String[] sdks = new String[sdkContainer.getSDKCount()];
			Vector sdkKeys = sdkContainer.getSDKKeys();
			for (int i=0, length=sdkContainer.getSDKCount(); i<length; i++){
				sdks[i] = (String)sdkKeys.get(i);
			}
			
			sdkRow.removeAll();
			sdkRow.addAll(sdks);
			sdkRow.select(0);   // The default SDK is randomly the first one
		}
	}

	private void fillOOoRow(){
		
		if (null != oooRow){
			
			// Adding the OOo names to the combo box 
			OOoContainer oooContainer = OOoContainer.getOOoContainer();
			String[] ooos = new String[oooContainer.getOOoCount()];
			Vector oooKeys = oooContainer.getOOoKeys();
			for (int i=0, length=oooContainer.getOOoCount(); i<length; i++){
				ooos[i] = (String)oooKeys.get(i);
			}
			
			oooRow.removeAll();
			oooRow.addAll(ooos);
			oooRow.select(0);   // The default OOo is randomly the first one
		}
	}
	
	private boolean isChanging = false;
	
	/*
	 *  (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.gui.rows.IFieldChangedListener#fieldChanged(org.openoffice.ide.eclipse.gui.rows.FieldEvent)
	 */
	public void fieldChanged(FieldEvent e) {
		
		if (!isChanging){
			
			setPageComplete(validatePage());
			
			// Check the prefix correctness
			if (e.getProperty().equals(PREFIX)){
				
				String newCompanyPrefix = e.getValue();
				/**
				 * <p>The company prefix is a package like name used by the project
				 * to build the idl file path and the implementation path.</p>
				 */
				
				if (!newCompanyPrefix.matches(
						"([a-zA-Z][a-zA-Z0-9]*)(.[a-zA-Z][a-zA-Z0-9]*)*")){
					/**
					 * <p>If the new company prefix is invalid, an error message
					 * is set.</p>
					 */
					
					setErrorMessage(OOEclipsePlugin.getTranslationString(
							I18nConstants.COMPANY_PREFIX_ERROR));
					
					setPageComplete(false);
				} else {
					setErrorMessage(null);
					
					IWizardPage nextPage = getWizard().getNextPage(this);
					if (nextPage instanceof NewScopedElementWizardPage) {
						NewScopedElementWizardPage aNextScoped = 
							(NewScopedElementWizardPage) nextPage;
						aNextScoped.setPackageRoot(getPrefix());
						aNextScoped.setPackage("", true);
					}
				}
			}
			
			// Check the implementation extension correctness
			if (e.getProperty().equals(OUTPUT_EXT)){
				String newOuputExt = e.getValue();
				/**
				 * <p>The implementation extension is a single word which could 
				 * contain numbers. It have to begin with a letter.</p> 
				 */
				
				if (!newOuputExt.matches("[a-zA-Z][a-zA-Z0-9]*")){
					/**
					 * <p>If the new implementation extension is invalid, it is set to
					 * the empty string with an error message.</p>
					 */
					
					setErrorMessage(OOEclipsePlugin.getTranslationString(
										I18nConstants.OUTPUT_EXT_ERROR));
					setPageComplete(false);
				} else {
					setErrorMessage(null);
				}
			}
			
			if (e.getProperty().equals(OOO)) {
				IWizardPage next = getWizard().getNextPage(this);
				if (next instanceof NewScopedElementWizardPage) {
					NewScopedElementWizardPage aNext = 
						(NewScopedElementWizardPage) next;
					
					aNext.setOOoInstance(
							OOoContainer.getOOoContainer().getOOo(getOOoName()));
				}
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#setErrorMessage(java.lang.String)
	 */
	public void setErrorMessage(String newMessage) {
		
		if (null != messageLabel){
			if (null == newMessage){
				messageLabel.setText("");
				messageIcon.setVisible(false);
				messageLabel.setVisible(false);
			} else {
				messageLabel.setText(newMessage);
				messageIcon.setVisible(true);
				messageLabel.setVisible(true);
			}
		}
	}
	
	public void ConfigAdded(Object element) {
		if (element instanceof OOo){
			fillOOoRow();
		} else {
			fillSDKRow();
		}
	}

	public void ConfigRemoved(Object element) {
		
		if (null == element || element instanceof OOo){
			fillOOoRow();
		} 
		
		if (null == element || element instanceof SDK) {
			fillSDKRow();
		}
	}

	public void ConfigUpdated(Object element) {
		if (element instanceof OOo){
			fillOOoRow();
		} else {
			fillSDKRow();
		}
	};
	
	protected boolean validatePage() {
		boolean result = super.validatePage();
		
		boolean constraint = !(null == getSDKName() || getSDKName().equals("") ||
				null == getPrefix() || getPrefix().equals("") ||
				 null == getOutputExt() || getOutputExt().equals(""));
		
		result = result && constraint;
		
		if (result) {
			IWizardPage next = getWizard().getNextPage(this);
			if (next instanceof NewScopedElementWizardPage) {
				NewScopedElementWizardPage aScopedNext = (NewScopedElementWizardPage) next;
				
				// Sets the project name as the service name default value
				String serviceName = getProjectName().trim().toLowerCase();
				String firstLetter = serviceName.substring(0, 1).toUpperCase();
				serviceName = firstLetter + serviceName.substring(1);
				
				aScopedNext.setName(serviceName, false);
			}
		}
		
		return result;
	}
	
	private UnoidlProject unoProject = null;
	
	public UnoidlProject getUnoidlProject() {
		if (null == unoProject) {
			createUnoidlProject();
		}
		
		return unoProject;
	}
	
	protected void createUnoidlProject() {
		
		final IProject project = getProjectHandle();
		final String prefix = getPrefix();
		final String outputExt = getOutputExt();
		final int language = getChosenLanguage();
		final String sdkname = getSDKName();
		final String oooname = getOOoName();
					
		// Creates the new project whithout it's builders
		createProject(project, null);
		
		// Create the ouput and idl packages
		try {
			unoProject = (UnoidlProject)project.getNature(
										OOEclipsePlugin.UNO_NATURE_ID);
			
			unoProject.setCompanyPrefix(prefix);
			unoProject.setOutputExtension(outputExt);
			unoProject.setOuputLanguage(language);
			unoProject.setSdk(
					SDKContainer.getSDKContainer().getSDK(sdkname));
			unoProject.setOOo(
					OOoContainer.getOOoContainer().getOOo(oooname));
			
			// Creation of the unoidl package
			unoProject.createUnoidlPackage(null);
			
			// Creation of the Code Packages
			unoProject.createCodePackage(null);
			
			// Creation of the urd output directory
			unoProject.createUrdDir(null);
			
		} catch (CoreException e) {
			MessageDialog dialog = new MessageDialog(
					getShell(),
					OOEclipsePlugin.getTranslationString(I18nConstants.UNO_PLUGIN_ERROR),
					null,
					OOEclipsePlugin.getTranslationString(I18nConstants.PROJECT_CREATION_FAILED),
					MessageDialog.ERROR,
					new String[]{OOEclipsePlugin.getTranslationString(I18nConstants.OK)},
					0);
			dialog.setBlockOnOpen(true);
			dialog.create();
			dialog.open();
		
			try {
				unoProject.getProject().delete(true, true, null);
				unoProject = null;
			} catch (CoreException ex){
				// Impossible to delete the project
			}
		}
	}
	
	/**
	 * This method creates and opens the project with the Java and Uno natures
	 * 
	 * @param project project to create
	 * @param monitor monitor used to report the creation state
	 */
	protected void createProject(IProject project, IProgressMonitor monitor) {
		try {
			if (!project.exists()){
				project.create(monitor);
			}
			
			if (!project.isOpen()){
				project.open(monitor);
			}
			
			IProjectDescription description = project.getDescription();
			String[] natureIds = description.getNatureIds();
			String[] newNatureIds = new String[natureIds.length+1];
			System.arraycopy(natureIds, 0, newNatureIds, 0, natureIds.length);
			
			// Adding the Uno Nature
			newNatureIds[natureIds.length] = OOEclipsePlugin.UNO_NATURE_ID;
			
			description.setNatureIds(newNatureIds);
			project.setDescription(description, monitor);
			
		} catch (CoreException e) {
			OOEclipsePlugin.logError(OOEclipsePlugin.getTranslationString(
					I18nConstants.NATURE_SET_FAILED), e);
		}
	}
	
	private class TableDialog extends Dialog {
		
		private boolean editSDK = true;
		
		private Object table;
		
		TableDialog (Shell parentShell, boolean editSDK){
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE);
			this.editSDK = editSDK;
			
			setBlockOnOpen(true); // This dialog is a modal one
			if (editSDK) {
				setTitle(OOEclipsePlugin.getTranslationString(I18nConstants.SDKS));
			} else {
				setTitle(OOEclipsePlugin.getTranslationString(I18nConstants.OOOS));
			}
		}
		
		protected Control createDialogArea(Composite parent) {
			
			if (editSDK){
				table = new SDKTable(parent);
				((SDKTable)table).getPreferences();
			} else {
				table = new OOoTable(parent);
				((OOoTable)table).getPreferences();
			}
				
			return parent;
		}
		
		protected void okPressed() {
			super.okPressed();
			
			if (editSDK){
				((SDKTable)table).savePreferences();
			} else {
				((OOoTable)table).savePreferences();
			}
		}
	}
}