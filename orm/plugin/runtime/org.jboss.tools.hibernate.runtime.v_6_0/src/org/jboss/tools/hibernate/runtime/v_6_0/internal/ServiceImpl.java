package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Hibernate;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractService;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.ConfigurationMetadataDescriptor;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.DummyMetadataBuildingContext;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.DummyMetadataDescriptor;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.JdbcMetadataConfiguration;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.JpaConfiguration;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.JpaMappingFileHelper;
import org.xml.sax.EntityResolver;

public class ServiceImpl extends AbstractService {

	private static final String HIBERNATE_VERSION = "6.0";
	
	private IFacadeFactory facadeFactory = new FacadeFactoryImpl();

	@Override
	public IConfiguration newAnnotationConfiguration() {
		return newDefaultConfiguration();
	}

	@Override
	public IConfiguration newJpaConfiguration(
			String entityResolver, 
			String persistenceUnit,
			Map<Object, Object> overrides) {
		return facadeFactory.createConfiguration(
				new JpaConfiguration(persistenceUnit, overrides));
	}

	@Override
	public IConfiguration newDefaultConfiguration() {
		getUsageTracker().trackNewConfigurationEvent(HIBERNATE_VERSION);
		return facadeFactory.createConfiguration(new Configuration());
	}

	@Override
	public IHibernateMappingExporter newHibernateMappingExporter(
			IConfiguration hcfg, File file) {
		return facadeFactory.createHibernateMappingExporter(
				new HibernateMappingExporterExtension(
						facadeFactory,
						hcfg,
						file));
	}

	@Override
	public ISchemaExport newSchemaExport(IConfiguration hcfg) {
		SchemaExportFacadeImpl result = 
			(SchemaExportFacadeImpl)facadeFactory.createSchemaExport(new SchemaExport());
		result.setConfiguration(hcfg);
		return result;
	}

	@Override
	public IHQLCodeAssist newHQLCodeAssist(IConfiguration hcfg) {
		IHQLCodeAssist result = null;
		if (hcfg instanceof ConfigurationFacadeImpl) {
			result = facadeFactory.createHQLCodeAssist(
					new HQLCodeAssist(((ConfigurationFacadeImpl)hcfg).getMetadata()));
		}
		return result;
	}

	@Override
	public IConfiguration newJDBCMetaDataConfiguration() {
		return facadeFactory.createConfiguration(new JdbcMetadataConfiguration());
	}

	@Override
	public IExporter createExporter(String exporterClassName) {
		Exporter exporter = (Exporter)Util.getInstance(
				exporterClassName, 
				facadeFactory.getClassLoader());
		if (CfgExporter.class.getName().equals(exporterClassName)) {
			exporter.getProperties().put(
					ExporterConstants.METADATA_DESCRIPTOR, 
					new DummyMetadataDescriptor());
		} else {
			exporter.getProperties().put(
					ExporterConstants.METADATA_DESCRIPTOR,
					new ConfigurationMetadataDescriptor((ConfigurationFacadeImpl)newDefaultConfiguration()));
		}
		return facadeFactory.createExporter(exporter);
	}

	@Override
	public IArtifactCollector newArtifactCollector() {
		return facadeFactory.createArtifactCollector(new DefaultArtifactCollector());
	}

	@Override
	public IHQLQueryPlan newHQLQueryPlan(String query, boolean shallow, ISessionFactory sessionFactory) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeFactory newTypeFactory() {
		// target for ITypeFactory is a dummy Object
		return facadeFactory.createTypeFactory();
	}

	@Override
	public INamingStrategy newNamingStrategy(String strategyClassName) {
		try {
			return facadeFactory.createNamingStrategy(
					Class.forName(strategyClassName).newInstance());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	public IOverrideRepository newOverrideRepository() {
		return facadeFactory.createOverrideRepository(new OverrideRepository());
	}

	@Override
	public ITableFilter newTableFilter() {
		return facadeFactory.createTableFilter(new TableFilter());
	}


	@Override
	public IReverseEngineeringSettings newReverseEngineeringSettings(
			IReverseEngineeringStrategy res) {
		return facadeFactory.createReverseEngineeringSettings(
				new RevengSettings(
						(RevengStrategy)((IFacade)res).getTarget()));
	}

	@Override
	public IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy() {
		return facadeFactory.createReverseEngineeringStrategy(
				new DefaultStrategy());
	}

	@Override
	public Map<String, List<ITable>> collectDatabaseTables(Properties properties, IReverseEngineeringStrategy strategy,
			IProgressListener progressListener) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IReverseEngineeringStrategy newReverseEngineeringStrategy(
			String strategyName,
			IReverseEngineeringStrategy delegate) {
		RevengStrategy delegateTarget = 
				(RevengStrategy)((IFacade)delegate).getTarget();
		Object target = 
				newReverseEngineeringStrategy(strategyName, delegateTarget);
		return facadeFactory.createReverseEngineeringStrategy(target);
	}

	@Override
	public String getReverseEngineeringStrategyClassName() {
		return RevengStrategy.class.getName();
	}

	@Override
	public ICfg2HbmTool newCfg2HbmTool() {
		return facadeFactory.createCfg2HbmTool(new Cfg2HbmTool());
	}

	@Override
	public IProperty newProperty() {
		return facadeFactory.createProperty(new Property());
	}

	@Override
	public ITable newTable(String name) {
		Table target = new Table(name);
		target.setPrimaryKey(new PrimaryKey(target));
		return facadeFactory.createTable(target);
	}

	@Override
	public IColumn newColumn(String string) {
		return facadeFactory.createColumn(new Column(string));
	}

	@Override
	public String newDialect(Properties properties, final Connection connection) {
		ServiceRegistry serviceRegistry = buildServiceRegistry(properties);
		DialectFactory dialectFactory = serviceRegistry.getService(DialectFactory.class);
		Dialect dialect = dialectFactory.buildDialect(
				properties, 
				new DialectResolutionInfoSource() {
					@Override
					public DialectResolutionInfo getDialectResolutionInfo() {
						try {
							return new DatabaseMetaDataDialectResolutionInfoAdapter( connection.getMetaData() );
						}
						catch ( SQLException sqlException ) {
							throw new HibernateException(
									"Unable to access java.sql.DatabaseMetaData to determine appropriate Dialect to use",
									sqlException
							);
						}
					}
				}
		);
		return dialect != null ? dialect.toString() : null;
	}

	@Override
	public Class<?> getDriverManagerConnectionProviderClass() {
		return DriverManagerConnectionProviderImpl.class;
	}

	@Override
	public IEnvironment getEnvironment() {
		return facadeFactory.createEnvironment();
	}

	@Override
	public IValue newSimpleValue() {
		return facadeFactory.createValue(new BasicValue(DummyMetadataBuildingContext.INSTANCE));
	}

	@Override
	public IValue newPrimitiveArray(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new PrimitiveArray(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newArray(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new Array(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newBag(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new Bag(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}
	
	@Override
	public IValue newList(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new org.hibernate.mapping.List(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newMap(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new org.hibernate.mapping.Map(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newSet(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new Set(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newManyToOne(ITable table) {
		return facadeFactory.createValue(
				new ManyToOne(
						DummyMetadataBuildingContext.INSTANCE, 
						(Table)((IFacade)table).getTarget()));
	}


	@Override
	public IValue newOneToMany(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new OneToMany(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IValue newOneToOne(IPersistentClass persistentClass) {
		return facadeFactory.createValue(
				new OneToOne(
						DummyMetadataBuildingContext.INSTANCE, 
						((PersistentClass)((IFacade)persistentClass).getTarget()).getTable(), 
						(PersistentClass)((IFacade)persistentClass).getTarget()));
	}

	@Override
	public IPersistentClass newSingleTableSubclass(IPersistentClass persistentClass) {
		IPersistentClass result = facadeFactory.createPersistentClass(
				new SingleTableSubclass(
						(PersistentClass)((IFacade)persistentClass).getTarget(),
						null));
		((AbstractPersistentClassFacade)result).setSuperClass(persistentClass);
		return result;
	}

	@Override
	public IPersistentClass newJoinedSubclass(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		IPersistentClass result = facadeFactory.createPersistentClass(
				new JoinedSubclass(
						(PersistentClass)((IFacade)persistentClass).getTarget(),
						null));
		((AbstractPersistentClassFacade)result).setSuperClass(persistentClass);
		return result;
	}

	@Override
	public IPersistentClass newSpecialRootClass(IProperty ormElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistentClass newRootClass() {
		return facadeFactory.createPersistentClass(new RootClass(null));
	}

	@Override
	public boolean isInitialized(Object object) {
		return Hibernate.isInitialized(object);
	}

	@Override
	public List<String> getJPAMappingFilePaths(String persistenceUnitName, EntityResolver entityResolver) {
		return JpaMappingFileHelper.findMappingFiles(persistenceUnitName);
	}
	
	@Override
	public Class<?> getClassWithoutInitializingProxy(Object reflectedObject) {
		return HibernateProxyHelper.getClassWithoutInitializingProxy(reflectedObject);
	}

	@Override
	public ClassLoader getClassLoader() {
		return ServiceImpl.class.getClassLoader();
	}

	private ServiceRegistry buildServiceRegistry(Properties properties) {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(properties);
		return builder.build();
	}

	private Object newReverseEngineeringStrategy(final String className, Object delegate) {
        try {
            Class<?> clazz = classForName(className);
			Constructor<?> constructor = 
					clazz.getConstructor(
							new Class[] { RevengStrategy.class });
            return constructor.newInstance(new Object[] { delegate });
        }
        catch (NoSuchMethodException e) {
			try {
				ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
				Class<?> clazz = null;
				if ( contextClassLoader != null ) {
					clazz = contextClassLoader.loadClass(className);
				} else {
					clazz = Class.forName( className );
				}
				if (clazz != null) {
					return clazz.newInstance();
				} else {
					throw new HibernateException("Class " + className + " could not be found.");
				}
			}
			catch (Exception eq) {
				throw new HibernateException(eq);
			}
		}
        catch (Exception e) {
			throw new HibernateException(e);
		}
    }

	private Class<?> classForName(String name) throws ClassNotFoundException {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if ( classLoader != null ) {
				return classLoader.loadClass(name);
			}
		}
		catch ( Throwable ignore ) {
		}
		return Class.forName( name );
	}

}
