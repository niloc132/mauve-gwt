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
// the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
// Boston, MA 02110-1301 USA.

package gnu.testlet.javax.swing.plaf.metal.MetalFileChooserUI;

import java.awt.Dimension;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalFileChooserUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Some checks for the getMinimumSize() method in the 
 * {@link MetalFileChooserUI} class.  
 */
public class getMinimumSize implements Testlet 
{

  /**
   * Runs the test using the specified harness.
   * 
   * @param harness  the test harness (<code>null</code> not permitted).
   */
  public void test(TestHarness harness)  
  {
    // test with DefaultMetalTheme
    try
      {
        MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
        UIManager.setLookAndFeel(new MetalLookAndFeel());
      }
    catch (UnsupportedLookAndFeelException e)
      {
        e.printStackTrace();  
      }
    JFileChooser fc = new JFileChooser();
    MetalFileChooserUI ui = new MetalFileChooserUI(fc);
    
    // check that the method returns a new instance every time?
    Dimension d1 = ui.getPreferredSize(fc);
    Dimension d2 = ui.getPreferredSize(fc);
    harness.check(d1 != d2);
    
    // does the method look at the filechooser passed in?
    boolean pass = false;
    try
    {
      /*Dimension d3 =*/ ui.getPreferredSize(null);    
    }
    catch (NullPointerException e)
    {
      pass = true;
    }
    harness.check(pass);
  }

}
