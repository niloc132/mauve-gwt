/* TestletCollectionGenerator.java -- FIXME: describe
   Copyright (C) 2013 FIXME: your info here
This file is part of Mauve.

Mauve is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

Mauve is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with Mauve; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

*/

// Tags: FIXME

package com.colinalworth.gwt.mauve;

import gnu.testlet.Testlet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.colinalworth.gwt.mauve.client.impl.AbstractTestletCollectionImpl;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class TestletCollectionGenerator extends Generator {
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		TypeOracle oracle = context.getTypeOracle();
		
		JClassType toGenerate = oracle.findType(typeName);//TestletCollection

		if (toGenerate == null) {
			logger.log(TreeLogger.ERROR, typeName + " could not be found");
			throw new UnableToCompleteException();
		}

		String packageName = toGenerate.getPackage().getName();
		String simpleSourceName = toGenerate.getName().replace('.', '_') + "_Impl";
		PrintWriter pw = context.tryCreate(logger, packageName, simpleSourceName);
		if (pw == null) {
			return packageName + "." + simpleSourceName;
		}

		ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(packageName, simpleSourceName);
		factory.setSuperclass(AbstractTestletCollectionImpl.class.getName());
		factory.addImplementedInterface(typeName);
		SourceWriter sw = factory.createSourceWriter(context, pw);
		
		JClassType testletType = oracle.findType(Testlet.class.getName());
		sw.println("public %1$s<%2$s> getAll() {", List.class.getName(), testletType.getQualifiedSourceName());
		sw.indent();
		sw.println("%1$s<%2$s> list = new %1$s<%2$s>();", ArrayList.class.getName(), testletType.getQualifiedSourceName());
		for (JClassType type : oracle.getTypes()) {
			if (type.isAssignableTo(testletType)) {
				if (type.findConstructor(new JType[0]) != null) {
					sw.println("list.add(new %1$s());", type.getQualifiedSourceName());
				} else {
					logger.log(Type.WARN, "Class " + type + " has no default ctor, skipping");
				}
			}
		}
		sw.println("return list;");
		sw.outdent();
		sw.println("}");
		
		
		sw.commit(logger);
		return factory.getCreatedClassName();
	}

}
