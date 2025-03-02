package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.jboss.tools.hibernate.runtime.common.AbstractTableFilterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.junit.Test;

public class TableFilterFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testSetExclude() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = new AbstractTableFilterFacade(FACADE_FACTORY, tableFilter) {};
		assertNull(tableFilter.getExclude());
		tableFilterFacade.setExclude(true);
		assertTrue(tableFilter.getExclude());
	}
	
	@Test
	public void testSetMatchCatalog() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = new AbstractTableFilterFacade(FACADE_FACTORY, tableFilter) {};
		assertNotEquals("foo", tableFilter.getMatchCatalog());
		tableFilterFacade.setMatchCatalog("foo");
		assertEquals("foo", tableFilter.getMatchCatalog());
	}
		
	@Test
	public void testSetMatchSchema() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = new AbstractTableFilterFacade(FACADE_FACTORY, tableFilter) {};
		assertNotEquals("foo", tableFilter.getMatchSchema());
		tableFilterFacade.setMatchSchema("foo");
		assertEquals("foo", tableFilter.getMatchSchema());
	}
		
	@Test
	public void testSetMatchName() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = new AbstractTableFilterFacade(FACADE_FACTORY, tableFilter) {};
		assertNotEquals("foo", tableFilter.getMatchName());
		tableFilterFacade.setMatchName("foo");
		assertEquals("foo", tableFilter.getMatchName());
	}
		
	@Test
	public void testGetExclude() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = new AbstractTableFilterFacade(FACADE_FACTORY, tableFilter) {};
		assertNull(tableFilterFacade.getExclude());
		tableFilter.setExclude(true);
		assertTrue(tableFilterFacade.getExclude());
	}
		
	@Test
	public void testGetMatchCatalog() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = new AbstractTableFilterFacade(FACADE_FACTORY, tableFilter) {};
		assertNotEquals("foo", tableFilterFacade.getMatchCatalog());
		tableFilter.setMatchCatalog("foo");
		assertEquals("foo", tableFilterFacade.getMatchCatalog());
	}
		
	@Test
	public void testGetMatchSchema() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = new AbstractTableFilterFacade(FACADE_FACTORY, tableFilter) {};
		assertNotEquals("foo", tableFilterFacade.getMatchSchema());
		tableFilter.setMatchSchema("foo");
		assertEquals("foo", tableFilterFacade.getMatchSchema());
	}
		
	@Test
	public void testGetMatchName() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = new AbstractTableFilterFacade(FACADE_FACTORY, tableFilter) {};
		assertNotEquals("foo", tableFilterFacade.getMatchName());
		tableFilter.setMatchName("foo");
		assertEquals("foo", tableFilterFacade.getMatchName());
	}
		
}
