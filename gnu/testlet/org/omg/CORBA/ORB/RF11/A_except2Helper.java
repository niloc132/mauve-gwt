// Tags: not-a-test
// Copyright (c) 2000, 2001 NEC Corporation.

// Adapted for Mauve by Audrius Meskauskas <audriusa@bluewin.ch>

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

/*
This code originally came from the OMG's CORBA Open Source Testing project,
which lived at cost.omg.org. That site no longer exists.

All the contributing companies agreed to release their tests under the
terms of the GNU Lesser General Public License, available in the file
COPYING.LIB.

The code has been modified integrating into Mauve test environment and
removing tests that are not yet supported by Suns jre 1.4. Hence the license
is now GPL.

We downloaded the code from http://sourceforge.net/projects/corba-cost/,
administrated by Duncan Grisby.
*/


package gnu.testlet.org.omg.CORBA.ORB.RF11;

public abstract class A_except2Helper
{
  private static String _id = "IDL:gnu/testlet/org/omg/CORBA/ORB/RF11/A_except2:1.0";

  public static void insert(org.omg.CORBA.Any a, A_except2 that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
    a.type(type());
    write(out, that);
    a.read_value(out.create_input_stream(), type());
  }

  public static A_except2 extract(org.omg.CORBA.Any a)
  {
    return read(a.create_input_stream());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;

  public static synchronized org.omg.CORBA.TypeCode type()
  {
    if (__typeCode == null)
      {
        synchronized (org.omg.CORBA.TypeCode.class)
          {
            if (__typeCode == null)
              {
                if (__active)
                  {
                    return org.omg.CORBA.ORB.init().create_recursive_tc(_id);
                  }
                __active = true;

                org.omg.CORBA.StructMember[] _members0 =
                  new org.omg.CORBA.StructMember[ 3 ];
                org.omg.CORBA.TypeCode _tcOf_members0 = null;
                _tcOf_members0 =
                  org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_any);
                _members0 [ 0 ] =
                  new org.omg.CORBA.StructMember("v10", _tcOf_members0, null);
                _tcOf_members0 = org.omg.CORBA.ORB.init().create_string_tc(0);
                _members0 [ 1 ] =
                  new org.omg.CORBA.StructMember("v11", _tcOf_members0, null);
                _tcOf_members0 = org.omg.CORBA.ObjectHelper.type();
                _members0 [ 2 ] =
                  new org.omg.CORBA.StructMember("v12", _tcOf_members0, null);
                __typeCode =
                  org.omg.CORBA.ORB.init().create_struct_tc(A_except2Helper.id(),
                                                            "A_except2",
                                                            _members0
                                                           );
                __active = false;
              }
          }
      }
    return __typeCode;
  }

  public static String id()
  {
    return _id;
  }

  public static A_except2 read(org.omg.CORBA.portable.InputStream istream)
  {
    A_except2 value = new A_except2();

    // read and discard the repository ID
    istream.read_string();
    value.v10 = istream.read_any();
    value.v11 = istream.read_string();
    value.v12 = org.omg.CORBA.ObjectHelper.read(istream);
    return value;
  }

  public static void write(org.omg.CORBA.portable.OutputStream ostream,
                           A_except2 value
                          )
  {
    // write the repository ID
    ostream.write_string(id());
    ostream.write_any(value.v10);
    ostream.write_string(value.v11);
    org.omg.CORBA.ObjectHelper.write(ostream, value.v12);
  }
}
