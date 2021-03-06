// Tags: GUI JDK1.3

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
 * Checks that mouseMove in the {@link Robot} class moves the mouse
 * pointer.
 */
public class mouseMove implements Testlet
{
  int mouseX = 0;
  int mouseY = 0;

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

    b.addMouseListener (new MouseAdapter ()
      {
	public void mouseClicked (MouseEvent event)
	{
	  mouseX = event.getX ();
	  mouseY = event.getY ();
	}
      });

    f.add (b);

    f.setSize (100, 100);
    f.setLocation (250, 250);
    f.show ();

    r.setAutoDelay (100);

    // click the button
    r.mouseMove (300, 300);
    r.mousePress (InputEvent.BUTTON1_MASK);
    r.mouseRelease (InputEvent.BUTTON1_MASK);

    r.waitForIdle ();

    Insets i = f.getInsets ();

    // the insets values will only be correct if the window manager
    // supports the _NET_FRAME_EXTENTS and _NET_REQUEST_FRAME_EXTENTS
    // hints.
    harness.check (mouseX + i.left == 50 && mouseY + i.top == 50);
  }
}
