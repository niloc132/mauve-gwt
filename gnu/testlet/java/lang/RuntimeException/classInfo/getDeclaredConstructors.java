// Test for method java.lang.RuntimeException.getClass().getDeclaredConstructors()

// Copyright (C) 2012 Pavel Tisnovsky <ptisnovs@redhat.com>

// This file is part of Mauve.

// Mauve is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.

// Mauve is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with Mauve; see the file COPYING.  If not, write to
// the Free Software Foundation, Inc., 51 Franklin Street,
// Fifth Floor, Boston, MA 02110-1301 USA.

// Tags: JDK1.5

package gnu.testlet.java.lang.RuntimeException.classInfo;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import java.lang.RuntimeException;
import java.util.Map;
import java.util.HashMap;



/**
 * Test for method java.lang.RuntimeException.getClass().getDeclaredConstructors()
 */
public class getDeclaredConstructors implements Testlet
{

    /**
     * Runs the test using the specified harness.
     *
     * @param harness  the test harness (<code>null</code> not permitted).
     */
    public void test(TestHarness harness)
    {
        // map of declared constructors which should exists
        Map<String, String> testedDeclaredConstructors = null;

        // map of declared constructors for (Open)JDK6
        Map<String, String> testedDeclaredConstructors_jdk6 = new HashMap<String, String>();

        // map of declared constructors for (Open)JDK7
        Map<String, String> testedDeclaredConstructors_jdk7 = new HashMap<String, String>();

        // map for constructors declared in (Open)JDK6
        testedDeclaredConstructors_jdk6.put("public java.lang.RuntimeException()", "java.lang.RuntimeException");
        testedDeclaredConstructors_jdk6.put("public java.lang.RuntimeException(java.lang.String)", "java.lang.RuntimeException");
        testedDeclaredConstructors_jdk6.put("public java.lang.RuntimeException(java.lang.String,java.lang.Throwable)", "java.lang.RuntimeException");
        testedDeclaredConstructors_jdk6.put("public java.lang.RuntimeException(java.lang.Throwable)", "java.lang.RuntimeException");

        // map for constructors declared in (Open)JDK7
        testedDeclaredConstructors_jdk7.put("protected java.lang.RuntimeException(java.lang.String,java.lang.Throwable,boolean,boolean)", "java.lang.RuntimeException");
        testedDeclaredConstructors_jdk7.put("public java.lang.RuntimeException(java.lang.Throwable)", "java.lang.RuntimeException");
        testedDeclaredConstructors_jdk7.put("public java.lang.RuntimeException(java.lang.String,java.lang.Throwable)", "java.lang.RuntimeException");
        testedDeclaredConstructors_jdk7.put("public java.lang.RuntimeException(java.lang.String)", "java.lang.RuntimeException");
        testedDeclaredConstructors_jdk7.put("public java.lang.RuntimeException()", "java.lang.RuntimeException");

        // create instance of a class RuntimeException
        final Object o = new RuntimeException("java.lang.RuntimeException");

        // get a runtime class of an object "o"
        final Class c = o.getClass();

        // get the right map containing constructor signatures
        testedDeclaredConstructors = getJavaVersion() < 7 ? testedDeclaredConstructors_jdk6 : testedDeclaredConstructors_jdk7;

        // get all declared constructors for this class
        java.lang.reflect.Constructor[] declaredConstructors = c.getDeclaredConstructors();

        // expected number of constructors
        final int expectedNumberOfConstructors = testedDeclaredConstructors.size();

        // basic check for a number of constructors
        harness.check(declaredConstructors.length, expectedNumberOfConstructors);

        // check if all declared constructors exist
        for (java.lang.reflect.Constructor declaredConstructor : declaredConstructors) {
            // constructor name should consists of package name + class name
            String constructorName = declaredConstructor.getName();
            // modifiers + package + class name + parameter types
            String constructorString = declaredConstructor.toString().replaceAll(" native ", " ");
            harness.check(testedDeclaredConstructors.containsKey(constructorString));
            harness.check(testedDeclaredConstructors.get(constructorString), constructorName);
        }
    }

    /**
     * Returns version of Java. The input could have the following form: "1.7.0_06"
     * and we are interested only in "7" in this case.
     * 
     * @return Java version
     */
    protected int getJavaVersion() {
        String javaVersionStr = System.getProperty("java.version");
        String[] parts = javaVersionStr.split("\\.");
        return Integer.parseInt(parts[1]);
    }
}

