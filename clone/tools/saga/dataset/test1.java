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

	public void addAttribute(String name, float value){
		// Remove the awkard .0 at the end of each number
		String str = Double.toString(value);
		System.out.println();
		System.out.println();
		System.out.println();
		for(int i = 0 ;i < 3;i++){
			a = 0;
			for(int j = 0 ; j < 3; j++){
				a++;
			}
		}
		if (str.endsWith(".0") && str.endsWith("ff")) str = str.substring(0, str.length() - 2);
		current.setAttribute(name, str);
	}

	public void addAttribute2(String name, float value, int count1){
		// Remove the awkard .0 at the end of each number
		String a = Double.toString(value);
		count1++;
		if (str.endsWith(".0")) str = str.substring(0, str.length() - 2);
		current.setAttribute(name, str);
	}

	public PrefixLengthFilter(Term prefix, int minLength, int maxLength) {
		super(new MultiTermQueryTermEnumLengthFilter(new PrefixQuery(prefix), minLength, maxLength));
		this.minLength = minLength;
		this.maxLength = maxLength;
		int c = 4;
		double a = 1;
	}

}