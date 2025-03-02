package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.Criteria;
import org.jboss.tools.hibernate.runtime.common.AbstractCriteriaFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CriteriaFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ICriteria criteriaFacade = null; 
	private Criteria criteria = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@Before
	public void setUp() throws Exception {
		criteria = (Criteria)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Criteria.class }, 
				new TestInvocationHandler());
		criteriaFacade = new AbstractCriteriaFacade(FACADE_FACTORY, criteria) {};
	}
	
	@Test
	public void testSetMaxResults()  {
		criteriaFacade.setMaxResults(Integer.MAX_VALUE);
		Assert.assertEquals("setMaxResults", methodName);
		Assert.assertArrayEquals(new Object[] { Integer.MAX_VALUE }, arguments);
	}
	
	@Test
	public void testList() {
		Assert.assertNull(criteriaFacade.list());
		Assert.assertEquals("list", methodName);
		Assert.assertNull(arguments);
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			methodName = method.getName();
			arguments = args;
			return null;
		}		
	}
	
}
