// Test for instanceof operator applied to a class java.lang.NoSuchMethodError

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

package gnu.testlet.java.lang.NoSuchMethodError.classInfo;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import java.lang.NoSuchMethodError;
import java.lang.IncompatibleClassChangeError;
import java.lang.LinkageError;
import java.lang.Error;
import java.lang.Throwable;
import java.lang.Object;



/**
 * Test for instanceof operator applied to a class java.lang.NoSuchMethodError
 */
public class InstanceOf implements Testlet
{

    /**
     * Runs the test using the specified harness.
     *
     * @param harness  the test harness (<code>null</code> not permitted).
     */
    public void test(TestHarness harness)
    {
        // create instance of a class NoSuchMethodError
        NoSuchMethodError o = new NoSuchMethodError("NoSuchMethodError");

        // basic check of instanceof operator
        harness.check(o instanceof NoSuchMethodError);

        // check operator instanceof against all superclasses
        harness.check(o instanceof IncompatibleClassChangeError);
        harness.check(o instanceof LinkageError);
        harness.check(o instanceof Error);
        harness.check(o instanceof Throwable);
        harness.check(o instanceof Object);
    }
}

