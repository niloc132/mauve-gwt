// Tags: GUI JDK1.4

// Copyright (C) 2004 Red Hat

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

package gnu.testlet.java.awt.Robot;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import java.awt.*;
import java.awt.event.*;
import java.awt.AWTException;

/**
 * Checks that mouseWheel in the {@link Robot} class generates a
 * {@link MouseWheelEvent} on a {@link Button}.
 */
public class mouseWheel implements Testlet
{
  int mouseWheelUpCount = 0;
  int mouseWheelDownCount = 0;

  /**
   * Runs the test using the specified harness.
   *
   * @param harness  the test harness (<code>null</code> not permitted).
   */
  public void test (TestHarness harness)
  {
    Robot r = null;

    // construct robot
    try
      {
	r = new Robot ();
      }
    catch (AWTException e)
      {
	harness.fail ("caught AWT exception: " + e.getMessage ());
      }

    Frame f = new Frame ();
    Button b = new Button ("test");

    b.addMouseWheelListener (new MouseWheelListener ()
      {
	public void mouseWheelMoved (MouseWheelEvent event)
	{
	  int wheelRotation = event.getWheelRotation ();
	  if (wheelRotation < 0)
	    mouseWheelUpCount += wheelRotation;
	  else
	    mouseWheelDownCount += wheelRotation;
	}
      });

    f.add (b);

    f.setSize (100, 100);
    f.setLocation (250, 250);
    f.show ();

    r.setAutoDelay (100);

    // scroll the mouse wheel up and down
    r.mouseMove (300, 300);
    r.mouseWheel (-4);
    r.mouseWheel (5);
    r.mouseWheel (-1);
    r.mouseWheel (3);
    r.mouseWheel (2);
    r.mouseWheel (-3);

    r.waitForIdle ();

    harness.check (mouseWheelUpCount == -8);
    harness.check (mouseWheelDownCount == 10);
  }
}
