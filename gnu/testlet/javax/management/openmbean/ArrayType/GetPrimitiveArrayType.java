// Tags: JDK1.5

// Copyright (C) 2007 Andrew John Hughes <gnu_andrew@member.fsf.org>

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
// the Free Software Foundation, 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

package gnu.testlet.javax.management.openmbean.ArrayType;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import javax.management.openmbean.ArrayType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;

/**
 * Tests {@link ArrayType#getPrimitiveArrayType(OpenType)}
 * for 1-dimensional simple arrays.
 *
 * @author <a href="mailto:gnu_andrew@member.fsf.org">Andrew John Hughes</a>
 */
public class GetPrimitiveArrayType
  implements Testlet
{
  
  public void test(TestHarness h)
  {
    ArrayType type = null;
    h.checkPoint("1-dimensional integer array");
    type = ArrayType.getPrimitiveArrayType(int[].class);
    h.check(type.getClassName(), "[I");
    h.check(type.getTypeName(), "[I");
    h.check(type.getElementOpenType().getClassName(), "java.lang.Integer");
    h.check(type.getDescription(), "1-dimension array of int");
    h.checkPoint("2-dimensional integer array");
    type = ArrayType.getPrimitiveArrayType(int[][].class);
    h.check(type.getClassName(), "[[I");
    h.check(type.getTypeName(), "[[I");
    h.check(type.getElementOpenType().getClassName(), "java.lang.Integer");
    h.check(type.getDescription(), "2-dimension array of int");
    h.checkPoint("3-dimensional integer array");
    type = ArrayType.getPrimitiveArrayType(int[][][].class);
    h.check(type.getClassName(), "[[[I");
    h.check(type.getTypeName(), "[[[I");
    h.check(type.getElementOpenType().getClassName(), "java.lang.Integer");
    h.check(type.getDescription(), "3-dimension array of int");
    try
      {
	ArrayType.getPrimitiveArrayType(int.class);
	h.fail("int.class allowed.");
      }
    catch (IllegalArgumentException e)
      {
	h.check(true, "Exception thrown for int.class");
      }
    try
      {
	ArrayType.getPrimitiveArrayType(String.class);
	h.fail("String.class allowed.");
      }
    catch (IllegalArgumentException e)
      {
	h.check(true, "Exception thrown for String.class");
      }
    
  }
    
  
}
