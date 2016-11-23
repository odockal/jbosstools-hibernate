package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.Assert;
import org.junit.Test;

public class ValueFacadeTest {
	
	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Value valueTarget = null;
	private IValue valueFacade = null;
	
	@Test
	public void testIsSimpleValue() {
		valueTarget = new SimpleValue();
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertTrue(valueFacade.isSimpleValue());
		valueTarget = new Set(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isSimpleValue());
	}
	
	@Test
	public void testIsCollection() {
		valueTarget = new SimpleValue();
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isCollection());
		valueTarget = new Set(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertTrue(valueFacade.isCollection());
	}

}
