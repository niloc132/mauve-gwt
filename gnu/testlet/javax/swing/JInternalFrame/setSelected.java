/* setSelected.java -- Tests the setSelected() method.
   Copyright (C) 2006 Roman Kennke (kennke@aicas.com)
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

// Tags: JDK1.2

package gnu.testlet.javax.swing.JInternalFrame;

import java.beans.PropertyVetoException;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

/**
 * Tests the functionality of the setSelected() method in JInternalFrame.
 *
 * @author Roman Kennke (kennke@aicas.com)
 */
public class setSelected implements Testlet
{

  boolean repainted;

  /**
   * A subclass of JInternalFrame for testing.
   */
  class TestInternalFrame extends JInternalFrame
  {
    public void repaint(long t, int x, int y, int w, int h)
    {
      super.repaint(t, x, y, w, h);
      repainted = true;
    }
  }

  /**
   * The entry point into this test.
   *
   * @param harness the test harness to use
   */
  public void test(TestHarness harness)
  {
    testRepaint(harness);
  }

  /**
   * Tests if setSelected should trigger a repaint.
   *
   * @param harness the test harness to use
   */
  public void testRepaint(TestHarness h)
  {
    h.checkPoint("testRepaint");
    JInternalFrame f = new TestInternalFrame();
    f.setVisible(true);

    JFrame fr = null;
    try
      {
        // First we try with visible but not showing.
        repainted = false;
        f.setSelected(true);
        h.check(repainted, false);
        repainted = false;
        f.setSelected(false);
        h.check(repainted, false);

        // Now we do the same with the internal frame showing.
        fr = new JFrame();
        fr.getContentPane().add(f);
        fr.setSize(100, 100);
        fr.setVisible(true);

        // Check precondition (not selected).
        h.check(f.isSelected(), false);
        // Change state to selected.
        repainted = false;
        f.setSelected(true);
        h.check(repainted, true);
        // No state change.
        repainted = false;
        f.setSelected(true);
        h.check(repainted, false);
        // State change to false.
        repainted = false;
        f.setSelected(false);
        h.check(repainted, true);
        // No state change.
        repainted = false;
        f.setSelected(false);
        h.check(repainted, false);
      }
    catch (PropertyVetoException ex)
      {
        h.fail("PropertyVetoException");
      }
    finally
      {
        if (fr != null)
          fr.dispose();
      }
  }

}
