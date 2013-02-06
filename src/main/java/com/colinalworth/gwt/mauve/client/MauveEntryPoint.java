/* MauveEntryPoint.java -- FIXME: describe
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

package com.colinalworth.gwt.mauve.client;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.shared.GWT;

public class MauveEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
		runAllTests();
	}
	
	public void runAllTests() {
		final TestHarness harness = GWT.create(TestHarness.class);
		TestletCollection allTests = GWT.create(TestletCollection.class);
		
		final Iterator<Testlet> iter = allTests.getAll().iterator();
		Scheduler.get().scheduleIncremental(new RepeatingCommand() {
			@Override
			public boolean execute() {
				iter.next().test(harness);
				return iter.hasNext();
			}
		});
	}

}
