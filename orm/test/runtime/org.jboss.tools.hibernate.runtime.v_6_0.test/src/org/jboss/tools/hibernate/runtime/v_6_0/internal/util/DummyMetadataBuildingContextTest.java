package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.junit.Test;

public class DummyMetadataBuildingContextTest {
	
	@Test
	public void testInstance() {
		assertNotNull(DummyMetadataBuildingContext.INSTANCE);
		StandardServiceRegistry serviceRegistry = DummyMetadataBuildingContext.INSTANCE
				.getBootstrapContext().getServiceRegistry();
		JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
		Dialect dialect = jdbcServices.getDialect();
		assertTrue(dialect instanceof DummyMetadataBuildingContext.DummyDialect);
	}

}
