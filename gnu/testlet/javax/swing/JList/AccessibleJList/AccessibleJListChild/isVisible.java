// Tags: JDK1.2

// Uses: ../TestList

// Copyright (C) 2005 Roman Kennke <kennke@aicas.com>

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

package gnu.testlet.javax.swing.JList.AccessibleJList.AccessibleJListChild;

import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;
import gnu.testlet.javax.swing.JList.AccessibleJList.TestList;

/**
 * Checks if the isVisible method works correctly. The value of
 * isVisible depends on the values of JList.getFirstVisibleIndex and
 * JList.getLastVisibleIndex.
 *
 * Additionally we check the accessible state set, if it reflects the visible
 * state.
 *
 * @author Roman Kennke (kennke@aicas.com)
 */
public class isVisible implements Testlet
{

  /**
   * Overridden to provide customized isVisible method. This is used to 
   * check if the isVisible method of AccessibleJListChild forwards to
   * the JList or not.
   *
   * @author Roman Kennke (kennke@aicas.com)
   */
  class MyTestList extends TestList
  {
    boolean visible;
    int first;
    int last;

    /**
     * Creates a new instance of MyTestList.
     *
     * @param items some test items
     */
    MyTestList(Object[] items)
    {
      super(items);
    }
    public boolean isVisible()
    {
      return visible;
    }
    public int getLastVisibleIndex()
    {
      return last;
    }
    public int getFirstVisibleIndex()
    {
      return first;
    }
  }

  public void test(TestHarness harness)
  {
    MyTestList l = new MyTestList(new String[]{"item"});
    TestList.AccessibleTestList al =
      (TestList.AccessibleTestList) l.getAccessibleContext();
    AccessibleComponent child = (AccessibleComponent) al.getAccessibleChild(0);
    l.visible = true;
    harness.check(child.isVisible(), true);
    l.visible = false;
    harness.check(child.isVisible(), true);
    harness.check(l.isVisible(), false);

    // The above test show that isVisible is independent from the parent JList.
    // Check if this reacts to child.setVisible() (It does not).
    child.setVisible(true);
    harness.check(child.isVisible(), true);
    child.setVisible(false);
    harness.check(child.isVisible(), true);

    // The only thing left is that isVisible depends on the values
    // of JList.getFirstVisibleIndex and JList.getLastVisibleIndex
    AccessibleContext ctx = (AccessibleContext) child;
    AccessibleStateSet states;
    l.first = 0;
    l.last = 0;
    harness.check(child.isVisible(), true);
    states = ctx.getAccessibleStateSet();
    harness.check(states.contains(AccessibleState.VISIBLE));
    l.first = 1;
    l.last = 1;
    harness.check(child.isVisible(), false);
    states = ctx.getAccessibleStateSet();
    harness.check(!states.contains(AccessibleState.VISIBLE));
  }

}
