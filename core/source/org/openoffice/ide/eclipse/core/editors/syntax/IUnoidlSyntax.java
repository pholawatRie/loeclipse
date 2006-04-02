/*************************************************************************
 *
 * $RCSfile: IUnoidlSyntax.java,v $
 *
 * $Revision: 1.1 $
 *
 * last change: $Author: cedricbosdo $ $Date: 2006/04/02 20:13:04 $
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
package org.openoffice.ide.eclipse.core.editors.syntax;

/**
 * TODOC
 * 
 * @author cbosdonnat
 *
 */
public interface IUnoidlSyntax {
	
	public static final String[] RESERVED_WORDS = {
		"published",      // new with the OpenOffice.org 2.0 SDK 
		"get",            // new with the OpenOffice.org 2.0 SDK 
		"set",			  // new with the OpenOffice.org 2.0 SDK
		"service",
		"singleton",
		"type",
        "module",
        "interface",
        "struct",
        "const",
        "constants",
        "exception",
        "enum",
        "raises",
        "typedef"
	};
	
	public static final String[] MODIFIERS = {
		"bound",
		"constrained",
		"maybeambiguous",
		"maybedefault",
		"maybevoid",
        "oneway",
		"optional",
        "readonly",
        "in", "out", "inout",
        "attribute",
		"transient",
		"removable"
	};

	public static final String[] CONSTANTS = {
		"TRUE",
		"True",
		"FALSE",
		"False"
	};
	
	public static final String[] TYPES = {
        "string",
        "short",
        "long",
        "byte",
        "hyper",
        "float",
        "boolean",
        "any",
        "char",
        "double",
        "long",
        "void",
        "sequence",
        "unsigned",
        "...",
        "any..."       // in the uno idl grammar, there could be a space, or not
	};
}
