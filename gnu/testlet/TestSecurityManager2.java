// Copyright (c) 2004  Stephen Crawley <crawley@dstc.edu.au>

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

package gnu.testlet;

import java.security.Permission;
import java.security.SecurityPermission;
import java.util.PropertyPermission;

public class TestSecurityManager2 extends SecurityManager
{
  private SecurityManager oldManager;

  private Permission[] mustCheck = new Permission[0];
  // On some JDKs, setting the security manager triggers some network
  // initialization that reads system properties ...
  private Permission[] mayCheck = 
    new Permission[]{new SecurityPermission("getProperty.*"),
		     new PropertyPermission("*", "read")};
  private boolean[] checked = new boolean[0];

  public TestSecurityManager2() {
    super();
  }

  public void install()
  {
    SecurityManager oldsm = System.getSecurityManager();
    
    if (oldsm == this)
      throw new IllegalStateException("already installed");

    oldManager = oldsm;
    System.setSecurityManager(this);
  }


  public void uninstall()
  {
    mayCheck = new Permission[]{new RuntimePermission("setSecurityManager")};
    System.setSecurityManager(oldManager);
  }

  public void checkPermission(Permission perm) 
    throws SecurityException
  {
    // System.err.println("checkPermission(" + perm + ")");
    checkCheck(perm);
  }

  public void checkPermission(Permission perm, Object context)
    throws SecurityException
  {
    checkPermission(perm, null);
  }

  public void prepareChecks(Permission[] mustCheck, Permission[] mayCheck) {
    this.mayCheck = mayCheck;
    this.mustCheck = mustCheck;
    this.checked = new boolean[mustCheck.length];
  }

  public void checkAllChecked(TestHarness harness) {
    boolean allChecked = true;
    for (int i = 0; i < checked.length; i++) {
      if (!checked[i]) {
	harness.debug("Unchecked permission: " + mustCheck[i]);
	allChecked = false;
      }
    }
    
    harness.check(allChecked);
  }

  /**
   * Check that this permission is one that we should be checking
   * @param perm the permission to be checked
   * @return true if at least one of the 'overrideAllow' permissions
   * implies 'perm'.
   */
  private void checkCheck(Permission perm) 
    throws SecurityException
  {
    boolean matched = false;
    for (int i = 0; i < mustCheck.length; i++) {
      if (mustCheck[i].implies(perm)) {
	matched = true;
	checked[i] = true;
      }
    }
    if (!matched) {
      for (int i = 0; i < mayCheck.length; i++) {
	if (mayCheck[i].implies(perm)) {
	  matched = true;
	}
      }
    }
    if (!matched) {
      throw new SecurityException("Unexpected check: " + perm);
    }
  }
}