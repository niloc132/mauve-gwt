// Tags: not-a-test

// Copyright (C) 2002 Free Software Foundation, Inc.
// Written by Mark Wielaard (mark@klomp.org)

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
// Boston, MA 02111-1307, USA.  */

package gnu.testlet.java.net.URLClassLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import gnu.testlet.Testlet;
import gnu.testlet.TestHarness;

/**
 * Helper class for getResource classes that checks resources when
 * URLClassLoader is set.
 */
public abstract class getResourceBase implements Testlet
{
  protected TestHarness harness;
  protected URLClassLoader ucl;

  protected void check(String resource)
  {
    int index = resource.lastIndexOf('/');
    String name, path, dir;
    if (index != -1)
      {
	name = resource.substring(index+1);
	path = resource.substring(0, index);
	index = path.lastIndexOf('/');
	path = path + '/';
	if (index != -1)
	  dir = path.substring(index);
	else
	  dir = '/' + path;
      }
    else
      {
	path = "";
	name = resource;
	dir = "";
      }

    harness.debug(" == resource='" + resource
		    + "'; name='" + name
		    + "'; dir='" + dir
		    + "'; path='" + path + "'");

    URL u = ucl.getResource(resource);
    harness.debug(u != null ? u.toString() : null);
    harness.check(u != null, resource);

    u = ucl.getResource("no-" + resource);
    harness.debug(u != null ? u.toString() : null);
    harness.check(u == null, "no-" + resource);

    u = ucl.getResource(path + '/' + "no-" + name);
    harness.debug(u != null ? u.toString() : null);
    harness.check(u == null, path + '/' + "no-" + name);

    u = ucl.getResource("./" + resource);
    harness.debug(u != null ? u.toString() : null);
    harness.check(u != null, "./" + resource);

    u = ucl.getResource(path + "./" + name);
    harness.debug(u != null ? u.toString() : null);
    harness.check(u != null, path + "./" + name);

    u = ucl.getResource(".\\" + resource);
    harness.debug(u != null ? u.toString() : null);
    harness.check(u == null, "no .\\" + resource);

    if (!dir.equals(""))
      {
	u = ucl.getResource(path + ".." + dir + name);
	harness.debug(u != null ? u.toString() : null);
	harness.check(u != null, path + ".." + dir + name);
      }

    if (!path.equals(""))
      {
	u = ucl.getResource(path + "//" + name);
	harness.debug(u != null ? u.toString() : null);
	harness.check(u != null, path + "//" + name);
      }

    u = ucl.getResource("\\" + resource);
    harness.debug(u != null ? u.toString() : null);
    harness.check(u == null, "no \\" + resource);

    u = ucl.getResource(":" + resource);
    harness.debug(u != null ? u.toString() : null);
    harness.check(u == null, "no :" + resource);
  }
}