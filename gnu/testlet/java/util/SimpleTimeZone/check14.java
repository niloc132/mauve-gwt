// Test SimpleTimeZone.check14().

// Written by Jerry Quinn <jlquinn@optonline.net>

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

// Tags: JDK1.4

// Verify the constructors added in JDK 1.4

package gnu.testlet.java.util.SimpleTimeZone;

import gnu.testlet.Testlet;
import gnu.testlet.TestHarness;
import java.util.*;

public class check14 implements Testlet
{
  public void test (TestHarness harness)
  {
    int rawOff = -18000000;	// 5 hours
    int dstOff = 3600000;	// 1 hour

    // Create a timezone for UTC-5 with daylight savings starting on
    // April 10 at 12 noon, ending September 10, 12 noon in daylight
    // savings, 1 hour shift.

    // All three should represent the same period
    SimpleTimeZone tzwall =
      new SimpleTimeZone(rawOff, "Z1",
			 4, 10, 0, 43200000, SimpleTimeZone.WALL_TIME,
			 9, 10, 0, 43200000, SimpleTimeZone.WALL_TIME,
			 dstOff);

    // Start time is same between WALL_TIME and STANDARD_TIME.  End
    // time is in STANDARD_TIME, not DST.  So ending at the same time
    // really means ending earlier in standard time.
    SimpleTimeZone tzstd =
      new SimpleTimeZone(rawOff, "Z2",
			 4, 10, 0, 43200000, SimpleTimeZone.STANDARD_TIME,
			 9, 10, 0, 39600000, SimpleTimeZone.STANDARD_TIME,
			 dstOff);

    // Times are UTC, so later than 
    SimpleTimeZone tzutc =
      new SimpleTimeZone(rawOff, "Z3",
			 4, 10, 0, 61200000, SimpleTimeZone.UTC_TIME,
			 9, 10, 0, 57600000, SimpleTimeZone.UTC_TIME,
			 dstOff);

    int wall;
    int std;
    int utc;

    // test 1/2 hour before dst
    wall = tzwall.getOffset(GregorianCalendar.AD, 2000, 4, 10, 0, 41400000);
    std = tzstd.getOffset(GregorianCalendar.AD, 2000, 4, 10, 0, 41400000);
    utc = tzutc.getOffset(GregorianCalendar.AD, 2000, 4, 10, 0, 41400000);

    harness.check(wall, rawOff);
    harness.check(std, rawOff);
    harness.check(utc, rawOff);
    
    // test 1/2 hour into dst
    wall = tzwall.getOffset(GregorianCalendar.AD, 2000, 4, 10, 0, 45000000);
    std = tzstd.getOffset(GregorianCalendar.AD, 2000, 4, 10, 0, 45000000);
    utc = tzutc.getOffset(GregorianCalendar.AD, 2000, 4, 10, 0, 45000000);

    harness.check(wall, rawOff + dstOff);
    harness.check(std, rawOff + dstOff);
    harness.check(utc, rawOff + dstOff);
    
    // test 1/2 hour before fall back to standard time
    wall = tzwall.getOffset(GregorianCalendar.AD, 2000, 9, 10, 0, 41400000);
    std = tzstd.getOffset(GregorianCalendar.AD, 2000, 9, 10, 0, 41400000);
    utc = tzutc.getOffset(GregorianCalendar.AD, 2000, 9, 10, 0, 41400000);

    harness.check(wall, rawOff + dstOff);
    harness.check(std, rawOff + dstOff);
    harness.check(utc, rawOff + dstOff);
    
    // test 1/2 hour after fall back to standard time
    wall = tzwall.getOffset(GregorianCalendar.AD, 2000, 9, 10, 0, 45000000);
    std = tzstd.getOffset(GregorianCalendar.AD, 2000, 9, 10, 0, 45000000);
    utc = tzutc.getOffset(GregorianCalendar.AD, 2000, 9, 10, 0, 45000000);

    harness.check(wall, rawOff);
    harness.check(std, rawOff);
    harness.check(utc, rawOff);

  }
}