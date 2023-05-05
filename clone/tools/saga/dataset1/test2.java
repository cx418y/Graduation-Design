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
		String b = Float.toString(value);
		float b;
		if (str.endsWith(".0")) str = str.substring(0, str.length() - 2);
		current.setAttribute(name, str);
	}

	public void addAttribute2(String name, float value, int count2) throws Exception{
		 // Remove the awkard .0 at the end of each number
		String b = Double.toString(value);
		count2++;
		if (str.endsWith(".0")) str = str.substring(0, str.length() - 2);
		current.setAttribute(name, str);
	}

	public PrefixLengthQuery(Term prefix, int minLength, int maxLength) throws Exception{
		super(new PrefixQuery(prefix), minLength, maxLength);
		this.minLength = minLength;
		this.maxLength = maxLength;
		float b = 2;
		long d = 3;


	}
}