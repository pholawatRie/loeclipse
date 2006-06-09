/*************************************************************************
 *
 * $RCSfile: AbstractOOo.java,v $
 *
 * $Revision: 1.1 $
 *
 * last change: $Author: cedricbosdo $ $Date: 2006/06/09 06:13:59 $
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
package org.openoffice.ide.eclipse.core.internal.model;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Image;
import org.openoffice.ide.eclipse.core.OOEclipsePlugin;
import org.openoffice.ide.eclipse.core.gui.ITableElement;
import org.openoffice.ide.eclipse.core.i18n.I18nConstants;
import org.openoffice.ide.eclipse.core.model.OOoContainer;
import org.openoffice.ide.eclipse.core.preferences.IOOo;
import org.openoffice.ide.eclipse.core.preferences.InvalidConfigException;

/**
 * Helper class to add the table element features to the OOo classes. All the
 * {@link IOOo} interface still has to be implemented by the subclasses
 * 
 * @author cbosdonnat
 *
 */
public abstract class AbstractOOo implements IOOo, ITableElement {

	public static final String NAME = "__ooo_name";
	
	public static final String PATH = "__ooo_path";

	private String home;
	private String name;
	
	/**
	 * Creating a new OOo or URE instance specifying its home directory
	 * 
	 * @param oooHome the OpenOffice.org or URE home directory
	 * @throws InvalidConfigException is thrown if the home directory doesn't
	 * 		contains the required files and directories
	 */
	public AbstractOOo(String oooHome) throws InvalidConfigException {
		setHome(oooHome);
	}
	
	public AbstractOOo(String oooHome, String aName) throws InvalidConfigException {
		setHome(oooHome);
		setName(aName);
	}
	
	/* (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.core.preferences.IOOo#setHome(java.lang.String)
	 */
	public void setHome(String aHome) throws InvalidConfigException {

		Path homePath = new Path(aHome);
		File homeFile = homePath.toFile();
		
		/* Checks if the directory exists */
		if (!homeFile.isDirectory() || !homeFile.canRead()) {
			home = null;
			throw new InvalidConfigException(
					OOEclipsePlugin.getTranslationString(
							I18nConstants.NOT_EXISTING_DIR) + 
							homeFile.getAbsolutePath(), 
					InvalidConfigException.INVALID_OOO_HOME);
		}
		
		home = aHome;
			
		/* Checks if URE_HOME/share/java is a directory */
		Path javaPath = new Path(getClassesPath());
		File javaDir = javaPath.toFile();
		
		if (!javaDir.isDirectory() || !javaDir.canRead()) {
			home = null;
			throw new InvalidConfigException(
					OOEclipsePlugin.getTranslationString(
							I18nConstants.NOT_EXISTING_DIR) + 
							javaDir.getAbsolutePath(), 
					InvalidConfigException.INVALID_OOO_HOME);
		}
		
		/* Checks if URE_HOME/share/misc/types.rdb is a readable file */
		Path typesPath = new Path(getTypesPath());
		File typesFile = typesPath.toFile();
		
		if (!typesFile.isFile() || !typesFile.canRead()) {
			home = null;
			throw new InvalidConfigException(
					OOEclipsePlugin.getTranslationString(
							I18nConstants.NOT_EXISTING_FILE) + 
							typesFile.getAbsolutePath(), 
					InvalidConfigException.INVALID_OOO_HOME);
		}
		
		/* Checks if URE_HOME/share/misc/services.rdb is a readable file */
		Path servicesPath = new Path(getServicesPath());
		File servicesFile = servicesPath.toFile();
		
		if (!servicesFile.isFile() || !servicesFile.canRead()) {
			home = null;
			throw new InvalidConfigException(
					OOEclipsePlugin.getTranslationString(
							I18nConstants.NOT_EXISTING_FILE) + 
							typesFile.getAbsolutePath(), 
					InvalidConfigException.INVALID_OOO_HOME);
		}
		
		/* Checks if URE_HOME/lib/unorc is a readable file */
		Path unorcPath = new Path(getUnorcPath());
		File unorcFile = unorcPath.toFile();
		
		if (!unorcFile.isFile() || !unorcFile.canRead()) {
			home = null;
			throw new InvalidConfigException(
					OOEclipsePlugin.getTranslationString(
							I18nConstants.NOT_EXISTING_FILE) + 
							unorcFile.getAbsolutePath(), 
					InvalidConfigException.INVALID_OOO_HOME);
		}
	}

	/**
	 * Set the new name only if it's neither null nor the empty string. The name
	 * will be rendered unique and therefore may be changed.
	 * 
	 * @param aName the name to set
	 */
	protected void setName(String aName) {
		if (aName != null && !aName.equals("")) {
			name = OOoContainer.getOOoContainer().getUniqueName(aName);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.core.preferences.IOOo#getHome()
	 */
	public String getHome() {
		return home;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.core.preferences.IOOo#getName()
	 */
	public String getName() {
		return name;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.core.preferences.IOOo#createUnoCommand(java.lang.String, java.lang.String, java.lang.String[])
	 */
	public String createUnoCommand(String implementationName, 
			String libLocation, String[] registriesPath, String[] args) {
		
		String command = "";
		
		// Put the args into one string
		String sArgs = "";
		for (int i=0; i<args.length; i++) {
			sArgs += args[i];
			
			if (i < args.length -1) {
				sArgs += " ";
			}
		}
		
		// Transform the registries into a string to give to UNO
		String additionnalRegistries = "";
		for (int i=0; i<registriesPath.length; i++) {
			additionnalRegistries += "-ro " + registriesPath[i];
			
			if (i < registriesPath.length -1) {
				additionnalRegistries += " ";
			}
		}
		
		// Get the paths to OOo instance types and services registries
		Path typesPath = new Path(getTypesPath());
		Path servicesPath = new Path(getServicesPath());
		
		command = getUnoPath() +
			" -c " + implementationName + 
			" -l " + libLocation + 
			" -ro file:///" + typesPath.toOSString() +
			" -ro file:///" + servicesPath.toOSString() + 
			" " + additionnalRegistries + 
			" -- " + sArgs; 
		
		return command;
	}
	
	//-------------------------------------------- ITableElement Implementation
	
	/*
	 *  (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.gui.ITableElement#getImage(java.lang.String)
	 */
	public Image getImage(String property) {
		return null;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.gui.ITableElement#getLabel(java.lang.String)
	 */
	public String getLabel(String property) {
		String result = "";
		if (property.equals(NAME)) {
			result = getName();
		} else if (property.equals(PATH)) {
			result = getHome();
		}
		return result;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.gui.ITableElement#getProperties()
	 */
	public String[] getProperties() {
		return new String[] {NAME, PATH};
	}

	/*
	 *  (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.gui.ITableElement#canModify(java.lang.String)
	 */
	public boolean canModify(String property) {
		return false;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.gui.ITableElement#getValue(java.lang.String)
	 */
	public Object getValue(String property) {
		return null;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.gui.ITableElement#setValue(java.lang.String, java.lang.Object)
	 */
	public void setValue(String property, Object value) {
		// Nothing to do
	}
}