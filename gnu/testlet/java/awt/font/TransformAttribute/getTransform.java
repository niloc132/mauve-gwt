// Tags: JDK1.2

// Copyright (C) 2005 David Gilbert <david.gilbert@object-refinery.com>

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

package gnu.testlet.java.awt.font.TransformAttribute;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;

/**
 * Some checks for the getTransform() method in the {@link TransformAttribute} 
 * class.
 */
public class getTransform implements Testlet 
{

  /**
   * Runs the test using the specified harness. 
   * 
   * @param harness  the test harness (<code>null</code> not permitted).
   */
  public void test(TestHarness harness)      
  {
    AffineTransform t1 = AffineTransform.getTranslateInstance(1.2, 3.4);
    TransformAttribute ta = new TransformAttribute(t1);
    AffineTransform t2 = ta.getTransform();
    harness.check(t1, t2);
    
    // the returned transform should be a clone of the wrapped transform,
    // so changing it should not impact the TransformAttribute...
    t2.setToIdentity();
    AffineTransform t3 = ta.getTransform();
    harness.check(!t3.equals(t2));
  }

}
