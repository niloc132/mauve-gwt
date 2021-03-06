//Tags: JDK1.2

//Copyright (C) 2004 David Gilbert <david.gilbert@object-refinery.com>

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

package gnu.testlet.java.awt.geom.AffineTransform;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

/**
 * Some tests for the {@link AffineTransform} class.
 */
public class createInverse implements Testlet 
{

  /**
   * Runs the test using the specified harness.
   * 
   * @param harness  the test harness (<code>null</code> not permitted).
   */
  public void test(TestHarness harness)      
  {
    // inverse of identity is identity
    AffineTransform t1 = new AffineTransform();
    try 
    {
      AffineTransform t2 = t1.createInverse();
      harness.check(t2.getType() == AffineTransform.TYPE_IDENTITY);
    }
    catch (NoninvertibleTransformException e)
    {
      harness.check(false);
    }
    
    
    t1 = new AffineTransform(7.0, 8.0, 9.0, 10.0, 11.0, 12.0);
    try 
    {
      AffineTransform t3 = t1.createInverse();
      harness.check(t3.getScaleX(), -5.0);
      harness.check(t3.getShearX(), 4.5);
      harness.check(t3.getTranslateX(), 1.0);
      harness.check(t3.getShearY(), 4.0);
      harness.check(t3.getScaleY(), -3.5);
      harness.check(t3.getTranslateY(), -2.0);
    }
    catch (NoninvertibleTransformException e)
    {
      harness.check(false);
    }
    
    AffineTransform t4 = new AffineTransform(3.0, 3.0, 3.0, 3.0, 3.0, 3.0);
    try 
    {
      @SuppressWarnings("unused")
      AffineTransform t5 = t4.createInverse();
      harness.check(false);
    }
    catch (NoninvertibleTransformException e)
    {
      harness.check(true);
    }
  }

}
