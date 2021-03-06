//Tags: JDK1.2

//Copyright (C) 2004 David Gilbert (david.gilbert@object-refinery.com)

//This file is part of Mauve.

//Mauve is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2, or (at your option)
//any later version.

//Mauve is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with Mauve; see the file COPYING.  If not, write to
//the Free Software Foundation, 59 Temple Place - Suite 330,
//Boston, MA 02111-1307, USA.  */

package gnu.testlet.java.awt.geom.Rectangle2D.Float;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import java.awt.geom.Rectangle2D;

/**
 * Tests the setRect() methods for the {@link Rectangle2D.Float} class.
 */
public class setRect implements Testlet
{

  /**
   * Some checks for the setRect() methods in {@link Rectangle2D.Float}.
   */
  public void test(TestHarness harness) 
  {
    // setRect(float, float, float, float)
    Rectangle2D r1 = new Rectangle2D.Float();
    r1.setRect(1.0f, 2.0f, 3.0f, 4.0f);
    harness.check(r1.getX(), 1.0);
    harness.check(r1.getY(), 2.0);
    harness.check(r1.getWidth(), 3.0);
    harness.check(r1.getHeight(), 4.0);
  
    // setRect(Rectangle2D)
    r1.setRect(new Rectangle2D.Float(5.0f, 6.0f, 7.0f, 8.0f));
    harness.check(r1.getX(), 5.0);
    harness.check(r1.getY(), 6.0);
    harness.check(r1.getWidth(), 7.0);
    harness.check(r1.getHeight(), 8.0);
  
    boolean pass = false;
    try 
    {
      r1.setRect(null);
    }
    catch (NullPointerException e) 
    {
      pass = true;    
    }
    harness.check(pass);
  }

}
