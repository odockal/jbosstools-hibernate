package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractTableFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.DummyMetadataBuildingContext;
import org.junit.Test;

public class TableFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testGetName() {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		assertNull(tableFacade.getName());
		table.setName("foo");
		assertEquals("foo", tableFacade.getName());
	}
	
	@Test
	public void testAddColumn() {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		Column column = new Column("foo");
		IColumn columnFacade = FACADE_FACTORY.createColumn(column);
		assertNull(table.getColumn(column));
		tableFacade.addColumn(columnFacade);
		assertSame(column, table.getColumn(column));
	}
	
	@Test
	public void testGetCatalog() {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		assertNull(tableFacade.getCatalog());
		table.setCatalog("foo");
		assertEquals("foo", tableFacade.getCatalog());
	}
	
	@Test
	public void testGetSchema() {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		assertNull(tableFacade.getSchema());
		table.setSchema("foo");
		assertEquals("foo", tableFacade.getSchema());
	}
	
	@Test
	public void testGetPrimaryKey() {
		Table table = new Table();
		PrimaryKey primaryKey = new PrimaryKey(table);
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		assertNull(tableFacade.getPrimaryKey());
		table.setPrimaryKey(primaryKey);
		IPrimaryKey primaryKeyFacade = tableFacade.getPrimaryKey();
		assertSame(primaryKey, ((IFacade)primaryKeyFacade).getTarget());
	}
	
	@Test
	public void testGetColumnIterator() {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		Iterator<IColumn> columnIterator = tableFacade.getColumnIterator();
		assertFalse(columnIterator.hasNext());
		Column column = new Column("foo");
		table.addColumn(column);
		tableFacade = FACADE_FACTORY.createTable(table);
		columnIterator = tableFacade.getColumnIterator();
		IColumn columnFacade = columnIterator.next();
		assertSame(column, ((IFacade)columnFacade).getTarget());
	}
	
	@Test
	public void testGetComment() {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		assertNull(tableFacade.getComment());
		table.setComment("foo");
		assertEquals("foo", tableFacade.getComment());
	}
	
	@Test
	public void testGetRowId() {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		assertNull(tableFacade.getRowId());
		table.setRowId("foo");
		assertEquals("foo", tableFacade.getRowId());
	}
	
	@Test
	public void testGetSubselect() {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		assertNull(tableFacade.getSubselect());		
		table.setSubselect("foo");
		assertEquals("foo", tableFacade.getSubselect());
	}
	
	@Test
	public void testHasDenormalizedTables() throws Exception {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		assertFalse(tableFacade.hasDenormalizedTables());
		Method method = Table.class.getDeclaredMethod(
				"setHasDenormalizedTables", 
				new Class[] { });
		method.setAccessible(true);
		method.invoke(table, new Object[] { });
		assertTrue(tableFacade.hasDenormalizedTables());
	}
	
	@Test
	public void testIsAbstract() {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		table.setAbstract(true);
		assertTrue(tableFacade.isAbstract());		
		table.setAbstract(false);
		assertFalse(tableFacade.isAbstract());		
	}
	
	@Test
	public void testIsAbstractUnionTable() throws Exception {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		table.setAbstract(false);
		assertFalse(tableFacade.isAbstractUnionTable());	
		table.setAbstract(true);
		assertFalse(tableFacade.isAbstractUnionTable());	
		Method method = Table.class.getDeclaredMethod(
				"setHasDenormalizedTables", 
				new Class[] { });
		method.setAccessible(true);
		method.invoke(table, new Object[] { });
		assertTrue(tableFacade.isAbstractUnionTable());
	}
	
	@Test
	public void testIsPhysicalTable() {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		table.setSubselect("foo");
		assertFalse(tableFacade.isPhysicalTable());	
		table.setSubselect(null);
		assertTrue(tableFacade.isPhysicalTable());
	}
	
	@Test
	public void testGetIdentifierValue() {
		Table table = new Table();
		ITable tableFacade = new AbstractTableFacade(FACADE_FACTORY, table) {};
		IValue valueFacade = tableFacade.getIdentifierValue();
		assertNull(valueFacade);
		KeyValue value = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		table.setIdentifierValue(value);
		valueFacade = tableFacade.getIdentifierValue();
		assertSame(value, ((IFacade)valueFacade).getTarget());
	}
	
}
