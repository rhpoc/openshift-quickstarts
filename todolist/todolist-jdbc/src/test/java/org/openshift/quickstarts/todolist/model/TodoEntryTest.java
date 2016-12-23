package org.openshift.quickstarts.todolist.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TodoEntryTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		//fail("Not yet implemented");
		TodoEntry te = new TodoEntry();
		te.setSummary("summary");
		assert "summary".equals(te.getSummary());
	}

}
