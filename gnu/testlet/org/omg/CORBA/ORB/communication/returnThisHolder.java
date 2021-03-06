/* returnThisHolder.java --
   Copyright (C) 2005 Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package gnu.testlet.org.omg.CORBA.ORB.communication;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

/**
 * The holder for the structure, returned from the server.
 * @author Audrius Meskauskas, Lithuania (AudriusA@Bioinformatics.org)
 */
public final class returnThisHolder
  implements Streamable
{
  /**
   * The enclosed structure.
   */
  public returnThis value = null;

  /**
   * Create the empty holder.
   */
  public returnThisHolder()
  {
  }

  /**
   * Crate the holder with the defined initial value.
   */
  public returnThisHolder(returnThis initialValue)
  {
    value = initialValue;
  }

  /**
   * Read the value from the CDR stream.
   */
  public void _read(InputStream in)
  {
    value = returnThisHelper.read(in);
  }

  /**
   * Get the typecode of this structure.
   */
  public TypeCode _type()
  {
    return returnThisHelper.type();
  }

  /**
   * Write the value from the CDR stream.
   * @param out
   */
  public void _write(OutputStream out)
  {
    returnThisHelper.write(out, value);
  }
}
