package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.ide.completion.IHQLCompletionRequestor;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLCodeAssistFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionHandler;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.junit.Before;
import org.junit.Test;

public class HQLCodeAssistFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	private static final HQLCompletionProposal HQL_COMPLETION_PROPOSAL = new HQLCompletionProposal(0, 0);

	private IHQLCodeAssist hqlCodeAssistFacade = null;
	private org.hibernate.tool.ide.completion.IHQLCodeAssist hqlCodeAssistTarget = null;
	
	private IHQLCompletionProposal acceptedProposal = null;
	
	@Before
	public void before() {
		hqlCodeAssistTarget = new org.hibernate.tool.ide.completion.IHQLCodeAssist() {			
			@Override
			public void codeComplete(String query, int currentOffset, IHQLCompletionRequestor handler) {
				handler.accept(HQL_COMPLETION_PROPOSAL);
			}
		};
		hqlCodeAssistFacade = new AbstractHQLCodeAssistFacade(FACADE_FACTORY, hqlCodeAssistTarget) {};
	}
	
	@Test
	public void testCodeComplete() {
		assertNull(acceptedProposal);
		hqlCodeAssistFacade.codeComplete("foo", Integer.MAX_VALUE, new TestHQLCompletionHandler());
		assertNotNull(acceptedProposal);
		assertSame(HQL_COMPLETION_PROPOSAL, ((IFacade)acceptedProposal).getTarget());
	}
	
	private class TestHQLCompletionHandler implements IHQLCompletionHandler {
		@Override
		public boolean accept(IHQLCompletionProposal proposal) {
			acceptedProposal = proposal;
			return false;
		}
		@Override
		public void completionFailure(String errorMessage) {
			// TODO Auto-generated method stub
			
		}		
	}
	
}
