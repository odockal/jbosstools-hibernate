package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseReader;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public interface IFacadeFactory {
	
	ClassLoader getClassLoader();
	IArtifactCollector createArtifactCollector(Object target);
	ICfg2HbmTool createCfg2HbmTool(Object target);
	INamingStrategy createNamingStrategy(Object target);
	IReverseEngineeringSettings createReverseEngineeringSettings(Object target);
	IReverseEngineeringStrategy createReverseEngineeringStrategy(Object target);
	IOverrideRepository createOverrideRepository(Object target);
	ISchemaExport createSchemaExport(Object target);
	IGenericExporter createGenericExporter(Object target);
	IHbm2DDLExporter createHbm2DDLExporter(Object target);
	IQueryExporter createQueryExporter(Object target);
	ITableFilter createTableFilter(Object target);
	IExporter createExporter(Object target);
	IClassMetadata createClassMetadata(Object target);
	ICollectionMetadata createCollectionMetadata(Object target);
	IColumn createColumn(Object target);
	IConfiguration createConfiguration(Object target);
	ICriteria createCriteria(Object target);
	IEntityMetamodel createEntityMetamodel(Object target);
	IEnvironment createEnvironment();
	IForeignKey createForeignKey(Object target);
	IHibernateMappingExporter createHibernateMappingExporter(Object target);
	IHQLCodeAssist createHQLCodeAssist(Object target);
	IHQLCompletionProposal createHQLCompletionProposal(Object target);
	IHQLQueryPlan createHQLQueryPlan(Object target);
	IDatabaseReader createDatabaseReader(Object target);
	IJoin createJoin(Object target);
	IPersistentClass createPersistentClass(Object target);
	IPOJOClass createPOJOClass(Object target);
	IPrimaryKey createPrimaryKey(Object target);
	IProperty createProperty(Object target);
	IQuery createQuery(Object target);
	IQueryTranslator createQueryTranslator(Object target);
	ISessionFactory createSessionFactory(Object target);
	ISession createSession(Object target);
	IPersistentClass createSpecialRootClass(IProperty property);
	ITable createTable(Object target);
	ITypeFactory createTypeFactory();
	IType createType(Object target);
	IValue createValue(Object target);
	
}
