// Tags: JDK1.5

// Copyright (C) 2004 Roman Kennke <kennke@aicas.com>

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

package gnu.testlet.javax.swing.JDialog;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRootPane;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

public class isRootPaneCheckingEnabled implements Testlet
{

  /**
   * Overrides some protected methods to make them public for testing.
   *
   * @author Roman Kennke (kennke@aicas.com)
   */
  class TestDialog extends JDialog
  {
    public boolean isRootPaneCheckingEnabled()
    {
      return super.isRootPaneCheckingEnabled();
    }
    public void setRootPaneCheckingEnabled(boolean b)
    {
      super.setRootPaneCheckingEnabled(b);
    }
  }

  /**
   * Starts the test run.
   *
   * @param harness the test harness to use
   */
  public void test(TestHarness harness)
  {
    testRootPaneCheckingEnabled(harness);
    testRootPaneCheckingDisabled(harness);
  }

  /**
   * Checks the behaviour with rootPaneCheckingEnabled==true. Adds to the frame
   * should go to the contentPane.
   *
   * @param harness the test harness to use
   */
  private void testRootPaneCheckingEnabled(TestHarness harness)
  {
    harness.checkPoint("rootPaneCheckingEnabled");
    TestDialog f = new TestDialog();
    f.setRootPaneCheckingEnabled(true);
    JLabel c = new JLabel("Hello");
    f.add(c);
    Component[] children = f.getComponents();
    // The frame now still has 1 child, the rootPane.
    harness.check(children.length, 1);
    harness.check(children[0] instanceof JRootPane);
    // Instead, the add has gone to the contentPane which now also has 1 child,
    // the label.
    Component[] content = f.getContentPane().getComponents();
    harness.check(content.length, 1);
    harness.check(content[0], c);
  }

  /**
   * Checks the behaviour with rootPaneCheckingEnabled==false. Adds to the frame
   * should go directly to the frame.
   *
   * @param harness the test harness to use
   */
  private void testRootPaneCheckingDisabled(TestHarness harness)
  {
    harness.checkPoint("rootPaneCheckingDisabled");
    TestDialog f = new TestDialog();
    f.setRootPaneCheckingEnabled(false);
    JLabel c = new JLabel("Hello");
    f.add(c);
    Component[] children = f.getComponents();
    // The frame now has 2 children, the rootPane and the label.
    harness.check(children.length, 2);
    harness.check(children[0] instanceof JRootPane);
    harness.check(children[1], c);
  }
}
