/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

public class ReadManager implements Runnable {

	public void addAttribute(String name, float value) throws Exception{
		 // Remove the awkard .0 at the end of each number
		String c = Float.toString(value);
		float b;
		if (str.endsWith(".0")) str = str.substring(0, str.length() - 2);
		((Element) current).setAttribute(name, str);
	}

	public void addAttribute3(String name, float value, int count3) throws Exception{
		 // Remove the awkard .0 at the end of each number
		String c = Double.toString(value);
		count3++;
		if (str.endsWith(".0")) str = str.substring(0, str.length() - 2);
		((Element) current).setAttribute(name, str);
	}
}