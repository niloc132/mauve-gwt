/* AbstractTestletCollectionImpl.java -- FIXME: describe
   Copyright (C) 2013 FIXME: your info here
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

// Tags: FIXME

package com.colinalworth.gwt.mauve.client.impl;

import gnu.testlet.Testlet;

import java.util.List;

import com.colinalworth.gwt.mauve.client.TestletCollection;

public abstract class AbstractTestletCollectionImpl implements TestletCollection {

	public abstract List<Testlet> getAll();

	@Override
	public Testlet getByName(String testName) {
		// TODO
		return null;
	}

	@Override
	public String getName(Testlet test) {
		// TODO
		return null;
	}

}
