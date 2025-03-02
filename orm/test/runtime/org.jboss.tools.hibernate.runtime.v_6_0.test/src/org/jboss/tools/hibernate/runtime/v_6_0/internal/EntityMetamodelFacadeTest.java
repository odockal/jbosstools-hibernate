package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.internal.BootstrapContextImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.junit.Before;
import org.junit.Test;

public class EntityMetamodelFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	final static Object PROPERTY_VALUE = new Object();
	final static Integer PROPERTY_INDEX = Integer.MAX_VALUE;
	
	private EntityMetamodelFacadeImpl entityMetamodelFacade = null;
	
	@Before
	public void before() {
		entityMetamodelFacade = new EntityMetamodelFacadeImpl(
				FACADE_FACTORY, 
				createFooBarPersister());
	}
	
	@Test
	public void testGetTuplizerPropertyValue() {
		assertSame(PROPERTY_VALUE, entityMetamodelFacade.getTuplizerPropertyValue(null, 0));
	}
	
	@Test
	public void testGetPropertyIndexOrNull() {
		assertSame(PROPERTY_INDEX, entityMetamodelFacade.getPropertyIndexOrNull("foo"));
	}
	
	
	private EntityPersister createFooBarPersister() {
		return (EntityPersister)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { EntityPersister.class }, 
				new TestInvocationHandler());
	}
	
	private static class FooBarMetamodel extends EntityMetamodel {

		private static final long serialVersionUID = 1L;

		private static FooBarMetamodel create() {
			StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
			builder.applySetting("hibernate.dialect", TestDialect.class.getName());
			StandardServiceRegistry serviceRegistry = builder.build();
			MetadataBuildingOptionsImpl metadataBuildingOptions = 
					new MetadataBuildingOptionsImpl(serviceRegistry);	
			BootstrapContextImpl bootstrapContext = new BootstrapContextImpl(
					serviceRegistry, 
					metadataBuildingOptions);
			metadataBuildingOptions.setBootstrapContext(bootstrapContext);
			InFlightMetadataCollector inFlightMetadataCollector = 
					new InFlightMetadataCollectorImpl(
							bootstrapContext,
							metadataBuildingOptions);
			MetadataBuildingContext metadataBuildingContext = 
					new MetadataBuildingContextRootImpl(
							bootstrapContext, 
							metadataBuildingOptions, 
							inFlightMetadataCollector);
			MetadataSources metadataSources = new MetadataSources(serviceRegistry);
			SessionFactoryImplementor sessionFactoryImplementor = 
					(SessionFactoryImplementor)metadataSources.buildMetadata().buildSessionFactory();
			RootClass rootClass = new RootClass(null);
			SimpleValue basicValue = new BasicValue(metadataBuildingContext);
			basicValue.setTypeName(Integer.class.getName());
			rootClass.setIdentifier(basicValue);
			return new FooBarMetamodel(rootClass, sessionFactoryImplementor);
		}
		
		private static FooBarMetamodel INSTANCE = create();
		
		private FooBarMetamodel(
				PersistentClass persistentClass, 
				SessionFactoryImplementor sessionFactory) {
			super(persistentClass, null, sessionFactory);
		}
		
		@Override
		public Integer getPropertyIndexOrNull(String id) {
			return PROPERTY_INDEX;
		}
		
	}
		
	private static class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			if (methodName.equals("getEntityMetamodel")) {
				return FooBarMetamodel.INSTANCE;
			} else if (methodName.equals("getPropertyValue")) {
				return PROPERTY_VALUE;
			}
			return null;
		}
		
	}

	public static class TestDialect extends Dialect {
		@Override public int getVersion() { return 0; }
	}
	
}
