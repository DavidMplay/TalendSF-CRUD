
package salesforcetalend.salesforce_0_1;

import routines.Numeric;
import routines.DataOperation;
import routines.TalendDataGenerator;
import routines.TalendStringUtil;
import routines.TalendString;
import routines.MDM;
import routines.StringHandling;
import routines.Relational;
import routines.TalendDate;
import routines.Mathematical;
import routines.SQLike;
import routines.system.*;
import routines.system.api.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Comparator;

@SuppressWarnings("unused")

/**
 * Job: Salesforce Purpose: <br>
 * Description: <br>
 * 
 * @author vudavidgo@gmail.com
 * @version 8.0.1.20250126_0750-patch
 * @status
 */
public class Salesforce implements TalendJob {
	static {
		System.setProperty("TalendJob.log", "Salesforce.log");
	}

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(Salesforce.class);

	protected static void logIgnoredError(String message, Throwable cause) {
		log.error(message, cause);

	}

	public final Object obj = new Object();

	// for transmiting parameters purpose
	private Object valueObject = null;

	public Object getValueObject() {
		return this.valueObject;
	}

	public void setValueObject(Object valueObject) {
		this.valueObject = valueObject;
	}

	private final static String defaultCharset = java.nio.charset.Charset.defaultCharset().name();

	private final static String utf8Charset = "UTF-8";

	public static String taskExecutionId = null;

	public static String jobExecutionId = java.util.UUID.randomUUID().toString();;

	// contains type for every context property
	public class PropertiesWithType extends java.util.Properties {
		private static final long serialVersionUID = 1L;
		private java.util.Map<String, String> propertyTypes = new java.util.HashMap<>();

		public PropertiesWithType(java.util.Properties properties) {
			super(properties);
		}

		public PropertiesWithType() {
			super();
		}

		public void setContextType(String key, String type) {
			propertyTypes.put(key, type);
		}

		public String getContextType(String key) {
			return propertyTypes.get(key);
		}
	}

	// create and load default properties
	private java.util.Properties defaultProps = new java.util.Properties();

	// create application properties with default
	public class ContextProperties extends PropertiesWithType {

		private static final long serialVersionUID = 1L;

		public ContextProperties(java.util.Properties properties) {
			super(properties);
		}

		public ContextProperties() {
			super();
		}

		public void synchronizeContext() {

		}

		// if the stored or passed value is "<TALEND_NULL>" string, it mean null
		public String getStringValue(String key) {
			String origin_value = this.getProperty(key);
			if (NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY.equals(origin_value)) {
				return null;
			}
			return origin_value;
		}

	}

	protected ContextProperties context = new ContextProperties(); // will be instanciated by MS.

	public ContextProperties getContext() {
		return this.context;
	}

	protected java.util.Map<String, String> defaultProperties = new java.util.HashMap<String, String>();
	protected java.util.Map<String, String> additionalProperties = new java.util.HashMap<String, String>();

	public java.util.Map<String, String> getDefaultProperties() {
		return this.defaultProperties;
	}

	public java.util.Map<String, String> getAdditionalProperties() {
		return this.additionalProperties;
	}

	private final String jobVersion = "0.1";
	private final String jobName = "Salesforce";
	private final String projectName = "SALESFORCETALEND";
	public Integer errorCode = null;
	private String currentComponent = "";
	public static boolean isStandaloneMS = Boolean.valueOf("false");

	private void s(final String component) {
		try {
			org.talend.metrics.DataReadTracker.setCurrentComponent(jobName, component);
		} catch (Exception | NoClassDefFoundError e) {
			// ignore
		}
	}

	private void mdc(final String subJobName, final String subJobPidPrefix) {
		mdcInfo.forEach(org.slf4j.MDC::put);
		org.slf4j.MDC.put("_subJobName", subJobName);
		org.slf4j.MDC.put("_subJobPid", subJobPidPrefix + subJobPidCounter.getAndIncrement());
	}

	private void sh(final String componentId) {
		ok_Hash.put(componentId, false);
		start_Hash.put(componentId, System.currentTimeMillis());
	}

	{
		s("none");
	}

	private String cLabel = null;

	private final java.util.Map<String, Object> globalMap = new java.util.HashMap<String, Object>();
	private final static java.util.Map<String, Object> junitGlobalMap = new java.util.HashMap<String, Object>();

	private final java.util.Map<String, Long> start_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Long> end_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Boolean> ok_Hash = new java.util.HashMap<String, Boolean>();
	public final java.util.List<String[]> globalBuffer = new java.util.ArrayList<String[]>();

	private final JobStructureCatcherUtils talendJobLog = new JobStructureCatcherUtils(jobName,
			"_2kxvcPA1Ee-2rNEZYaVnnw", "0.1");
	private org.talend.job.audit.JobAuditLogger auditLogger_talendJobLog = null;

	private RunStat runStat = new RunStat(talendJobLog, System.getProperty("audit.interval"));

	// OSGi DataSource
	private final static String KEY_DB_DATASOURCES = "KEY_DB_DATASOURCES";

	private final static String KEY_DB_DATASOURCES_RAW = "KEY_DB_DATASOURCES_RAW";

	public void setDataSources(java.util.Map<String, javax.sql.DataSource> dataSources) {
		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		for (java.util.Map.Entry<String, javax.sql.DataSource> dataSourceEntry : dataSources.entrySet()) {
			talendDataSources.put(dataSourceEntry.getKey(),
					new routines.system.TalendDataSource(dataSourceEntry.getValue()));
		}
		globalMap.put(KEY_DB_DATASOURCES, talendDataSources);
		globalMap.put(KEY_DB_DATASOURCES_RAW, new java.util.HashMap<String, javax.sql.DataSource>(dataSources));
	}

	public void setDataSourceReferences(List serviceReferences) throws Exception {

		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		java.util.Map<String, javax.sql.DataSource> dataSources = new java.util.HashMap<String, javax.sql.DataSource>();

		for (java.util.Map.Entry<String, javax.sql.DataSource> entry : BundleUtils
				.getServices(serviceReferences, javax.sql.DataSource.class).entrySet()) {
			dataSources.put(entry.getKey(), entry.getValue());
			talendDataSources.put(entry.getKey(), new routines.system.TalendDataSource(entry.getValue()));
		}

		globalMap.put(KEY_DB_DATASOURCES, talendDataSources);
		globalMap.put(KEY_DB_DATASOURCES_RAW, new java.util.HashMap<String, javax.sql.DataSource>(dataSources));
	}

	private final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
	private final java.io.PrintStream errorMessagePS = new java.io.PrintStream(new java.io.BufferedOutputStream(baos));

	public String getExceptionStackTrace() {
		if ("failure".equals(this.getStatus())) {
			errorMessagePS.flush();
			return baos.toString();
		}
		return null;
	}

	private Exception exception;

	public Exception getException() {
		if ("failure".equals(this.getStatus())) {
			return this.exception;
		}
		return null;
	}

	private class TalendException extends Exception {

		private static final long serialVersionUID = 1L;

		private java.util.Map<String, Object> globalMap = null;
		private Exception e = null;

		private String currentComponent = null;
		private String cLabel = null;

		private String virtualComponentName = null;

		public void setVirtualComponentName(String virtualComponentName) {
			this.virtualComponentName = virtualComponentName;
		}

		private TalendException(Exception e, String errorComponent, final java.util.Map<String, Object> globalMap) {
			this.currentComponent = errorComponent;
			this.globalMap = globalMap;
			this.e = e;
		}

		private TalendException(Exception e, String errorComponent, String errorComponentLabel,
				final java.util.Map<String, Object> globalMap) {
			this(e, errorComponent, globalMap);
			this.cLabel = errorComponentLabel;
		}

		public Exception getException() {
			return this.e;
		}

		public String getCurrentComponent() {
			return this.currentComponent;
		}

		public String getExceptionCauseMessage(Exception e) {
			Throwable cause = e;
			String message = null;
			int i = 10;
			while (null != cause && 0 < i--) {
				message = cause.getMessage();
				if (null == message) {
					cause = cause.getCause();
				} else {
					break;
				}
			}
			if (null == message) {
				message = e.getClass().getName();
			}
			return message;
		}

		@Override
		public void printStackTrace() {
			if (!(e instanceof TalendException || e instanceof TDieException)) {
				if (virtualComponentName != null && currentComponent.indexOf(virtualComponentName + "_") == 0) {
					globalMap.put(virtualComponentName + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
				}
				globalMap.put(currentComponent + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
				System.err.println("Exception in component " + currentComponent + " (" + jobName + ")");
			}
			if (!(e instanceof TDieException)) {
				if (e instanceof TalendException) {
					e.printStackTrace();
				} else {
					e.printStackTrace();
					e.printStackTrace(errorMessagePS);
					Salesforce.this.exception = e;
				}
			}
			if (!(e instanceof TalendException)) {
				try {
					for (java.lang.reflect.Method m : this.getClass().getEnclosingClass().getMethods()) {
						if (m.getName().compareTo(currentComponent + "_error") == 0) {
							m.invoke(Salesforce.this, new Object[] { e, currentComponent, globalMap });
							break;
						}
					}

					if (!(e instanceof TDieException)) {
						if (enableLogStash) {
							talendJobLog.addJobExceptionMessage(currentComponent, cLabel, null, e);
							talendJobLogProcess(globalMap);
						}
					}
				} catch (Exception e) {
					this.e.printStackTrace();
				}
			}
		}
	}

	public void tSalesforceConnection_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tSalesforceConnection_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tSalesforceInput_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tSalesforceInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tMap_1_error(Exception exception, String errorComponent, final java.util.Map<String, Object> globalMap)
			throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tSalesforceInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tLogRow_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tSalesforceInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void talendJobLog_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		talendJobLog_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tSalesforceConnection_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void tSalesforceInput_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void talendJobLog_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void tSalesforceConnection_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tSalesforceConnection_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("tSalesforceConnection_1", "niN7z7_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [tSalesforceConnection_1 begin ] start
				 */

				sh("tSalesforceConnection_1");

				s(currentComponent = "tSalesforceConnection_1");

				cLabel = "DavidSalesforce";

				int tos_count_tSalesforceConnection_1 = 0;

				if (enableLogStash) {
					talendJobLog.addCM("tSalesforceConnection_1", "DavidSalesforce", "tSalesforceConnection");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				boolean doesNodeBelongToRequest_tSalesforceConnection_1 = 0 == 0;
				@SuppressWarnings("unchecked")
				java.util.Map<String, Object> restRequest_tSalesforceConnection_1 = (java.util.Map<String, Object>) globalMap
						.get("restRequest");
				String currentTRestRequestOperation_tSalesforceConnection_1 = (String) (restRequest_tSalesforceConnection_1 != null
						? restRequest_tSalesforceConnection_1.get("OPERATION")
						: null);

				org.talend.components.api.component.ComponentDefinition def_tSalesforceConnection_1 = new org.talend.components.salesforce.tsalesforceconnection.TSalesforceConnectionDefinition();

				org.talend.components.api.component.runtime.Writer writer_tSalesforceConnection_1 = null;
				org.talend.components.api.component.runtime.Reader reader_tSalesforceConnection_1 = null;

				org.talend.components.salesforce.SalesforceConnectionProperties props_tSalesforceConnection_1 = (org.talend.components.salesforce.SalesforceConnectionProperties) def_tSalesforceConnection_1
						.createRuntimeProperties();
				props_tSalesforceConnection_1.setValue("endpoint", "https://login.salesforce.com/services/Soap/u/57.0");

				props_tSalesforceConnection_1.setValue("loginType",
						org.talend.components.salesforce.SalesforceConnectionProperties.LoginType.Basic);

				props_tSalesforceConnection_1.setValue("bulkConnection", false);

				props_tSalesforceConnection_1.setValue("reuseSession", false);

				props_tSalesforceConnection_1.setValue("needCompression", false);

				props_tSalesforceConnection_1.setValue("timeout", 60000);

				props_tSalesforceConnection_1.setValue("httpChunked", true);

				props_tSalesforceConnection_1.setValue("clientId", "");

				props_tSalesforceConnection_1.userPassword.setValue("securityKey",
						routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:CRIVbSUlJFitu7KHvhBqiM3TcImENjpRerJ3xmHskymTXUZ3EbrxD1xu1hLuFINzomFj2W8="));

				props_tSalesforceConnection_1.userPassword.setValue("useAuth", false);

				props_tSalesforceConnection_1.userPassword.setValue("userId", "vudavidgo1@gmail.com");

				props_tSalesforceConnection_1.userPassword.setValue("password",
						routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:l+9Tzo/vMxLtElb346T4N/5dBQk+IM8u+AEXn+THquTxuXkB"));

				props_tSalesforceConnection_1.sslProperties.setValue("mutualAuth", false);

				props_tSalesforceConnection_1.proxy.setValue("useProxy", false);

				props_tSalesforceConnection_1.proxy.userPassword.setValue("useAuth", false);

				props_tSalesforceConnection_1.referencedComponent.setValue("referenceDefinitionName",
						"tSalesforceConnection");

				if (org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE == props_tSalesforceConnection_1.referencedComponent.referenceType
						.getValue()) {
					final String referencedComponentInstanceId_tSalesforceConnection_1 = props_tSalesforceConnection_1.referencedComponent.componentInstanceId
							.getStringValue();
					if (referencedComponentInstanceId_tSalesforceConnection_1 != null) {
						org.talend.daikon.properties.Properties referencedComponentProperties_tSalesforceConnection_1 = (org.talend.daikon.properties.Properties) globalMap
								.get(referencedComponentInstanceId_tSalesforceConnection_1
										+ "_COMPONENT_RUNTIME_PROPERTIES");
						props_tSalesforceConnection_1.referencedComponent
								.setReference(referencedComponentProperties_tSalesforceConnection_1);
					}
				}
				globalMap.put("tSalesforceConnection_1_COMPONENT_RUNTIME_PROPERTIES", props_tSalesforceConnection_1);
				globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
				globalMap.put("TALEND_COMPONENTS_VERSION", "0.37.41");
				java.net.URL mappings_url_tSalesforceConnection_1 = this.getClass().getResource("/xmlMappings");
				globalMap.put("tSalesforceConnection_1_MAPPINGS_URL", mappings_url_tSalesforceConnection_1);

				org.talend.components.api.container.RuntimeContainer container_tSalesforceConnection_1 = new org.talend.components.api.container.RuntimeContainer() {
					public Object getComponentData(String componentId, String key) {
						return globalMap.get(componentId + "_" + key);
					}

					public void setComponentData(String componentId, String key, Object data) {
						globalMap.put(componentId + "_" + key, data);
					}

					public String getCurrentComponentId() {
						return "tSalesforceConnection_1";
					}

					public Object getGlobalData(String key) {
						return globalMap.get(key);
					}
				};

				int nb_line_tSalesforceConnection_1 = 0;

				org.talend.components.api.component.ConnectorTopology topology_tSalesforceConnection_1 = null;
				topology_tSalesforceConnection_1 = org.talend.components.api.component.ConnectorTopology.NONE;

				org.talend.daikon.runtime.RuntimeInfo runtime_info_tSalesforceConnection_1 = def_tSalesforceConnection_1
						.getRuntimeInfo(org.talend.components.api.component.runtime.ExecutionEngine.DI,
								props_tSalesforceConnection_1, topology_tSalesforceConnection_1);
				java.util.Set<org.talend.components.api.component.ConnectorTopology> supported_connector_topologies_tSalesforceConnection_1 = def_tSalesforceConnection_1
						.getSupportedConnectorTopologies();

				org.talend.components.api.component.runtime.RuntimableRuntime componentRuntime_tSalesforceConnection_1 = (org.talend.components.api.component.runtime.RuntimableRuntime) (Class
						.forName(runtime_info_tSalesforceConnection_1.getRuntimeClassName()).newInstance());
				org.talend.daikon.properties.ValidationResult initVr_tSalesforceConnection_1 = componentRuntime_tSalesforceConnection_1
						.initialize(container_tSalesforceConnection_1, props_tSalesforceConnection_1);

				if (initVr_tSalesforceConnection_1
						.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
					throw new RuntimeException(initVr_tSalesforceConnection_1.getMessage());
				}

				if (componentRuntime_tSalesforceConnection_1 instanceof org.talend.components.api.component.runtime.ComponentDriverInitialization) {
					org.talend.components.api.component.runtime.ComponentDriverInitialization compDriverInitialization_tSalesforceConnection_1 = (org.talend.components.api.component.runtime.ComponentDriverInitialization) componentRuntime_tSalesforceConnection_1;
					compDriverInitialization_tSalesforceConnection_1.runAtDriver(container_tSalesforceConnection_1);
				}

				org.talend.components.api.component.runtime.SourceOrSink sourceOrSink_tSalesforceConnection_1 = null;
				if (componentRuntime_tSalesforceConnection_1 instanceof org.talend.components.api.component.runtime.SourceOrSink) {
					sourceOrSink_tSalesforceConnection_1 = (org.talend.components.api.component.runtime.SourceOrSink) componentRuntime_tSalesforceConnection_1;
					if (doesNodeBelongToRequest_tSalesforceConnection_1) {
						org.talend.daikon.properties.ValidationResult vr_tSalesforceConnection_1 = sourceOrSink_tSalesforceConnection_1
								.validate(container_tSalesforceConnection_1);
						if (vr_tSalesforceConnection_1
								.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
							throw new RuntimeException(vr_tSalesforceConnection_1.getMessage());
						}
					}
				}

				/**
				 * [tSalesforceConnection_1 begin ] stop
				 */

				/**
				 * [tSalesforceConnection_1 main ] start
				 */

				s(currentComponent = "tSalesforceConnection_1");

				cLabel = "DavidSalesforce";

				tos_count_tSalesforceConnection_1++;

				/**
				 * [tSalesforceConnection_1 main ] stop
				 */

				/**
				 * [tSalesforceConnection_1 process_data_begin ] start
				 */

				s(currentComponent = "tSalesforceConnection_1");

				cLabel = "DavidSalesforce";

				/**
				 * [tSalesforceConnection_1 process_data_begin ] stop
				 */

				/**
				 * [tSalesforceConnection_1 process_data_end ] start
				 */

				s(currentComponent = "tSalesforceConnection_1");

				cLabel = "DavidSalesforce";

				/**
				 * [tSalesforceConnection_1 process_data_end ] stop
				 */

				/**
				 * [tSalesforceConnection_1 end ] start
				 */

				s(currentComponent = "tSalesforceConnection_1");

				cLabel = "DavidSalesforce";

// end of generic

				ok_Hash.put("tSalesforceConnection_1", true);
				end_Hash.put("tSalesforceConnection_1", System.currentTimeMillis());

				/**
				 * [tSalesforceConnection_1 end ] stop
				 */

			} // end the resume

			if (resumeEntryMethodName == null || globalResumeTicket) {
				resumeUtil.addLog("CHECKPOINT", "CONNECTION:SUBJOB_OK:tSalesforceConnection_1:OnSubjobOk", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("OnSubjobOk1", 0, "ok");
			}

			tSalesforceInput_1Process(globalMap);

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tSalesforceConnection_1 finally ] start
				 */

				s(currentComponent = "tSalesforceConnection_1");

				cLabel = "DavidSalesforce";

// finally of generic

				/**
				 * [tSalesforceConnection_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tSalesforceConnection_1_SUBPROCESS_STATE", 1);
	}

	public static class mapOut1Struct implements routines.system.IPersistableRow<mapOut1Struct> {
		final static byte[] commonByteArrayLock_SALESFORCETALEND_Salesforce = new byte[0];
		static byte[] commonByteArray_SALESFORCETALEND_Salesforce = new byte[0];

		public boolean IsDeleted;

		public boolean getIsDeleted() {
			return this.IsDeleted;
		}

		public Boolean IsDeletedIsNullable() {
			return false;
		}

		public Boolean IsDeletedIsKey() {
			return false;
		}

		public Integer IsDeletedLength() {
			return null;
		}

		public Integer IsDeletedPrecision() {
			return null;
		}

		public String IsDeletedDefault() {

			return null;

		}

		public String IsDeletedComment() {

			return "";

		}

		public String IsDeletedPattern() {

			return "";

		}

		public String IsDeletedOriginalDbColumnName() {

			return "IsDeleted";

		}

		public String MasterRecordId;

		public String getMasterRecordId() {
			return this.MasterRecordId;
		}

		public Boolean MasterRecordIdIsNullable() {
			return false;
		}

		public Boolean MasterRecordIdIsKey() {
			return false;
		}

		public Integer MasterRecordIdLength() {
			return 18;
		}

		public Integer MasterRecordIdPrecision() {
			return null;
		}

		public String MasterRecordIdDefault() {

			return null;

		}

		public String MasterRecordIdComment() {

			return "";

		}

		public String MasterRecordIdPattern() {

			return "";

		}

		public String MasterRecordIdOriginalDbColumnName() {

			return "MasterRecordId";

		}

		public String Name;

		public String getName() {
			return this.Name;
		}

		public Boolean NameIsNullable() {
			return true;
		}

		public Boolean NameIsKey() {
			return false;
		}

		public Integer NameLength() {
			return 255;
		}

		public Integer NamePrecision() {
			return null;
		}

		public String NameDefault() {

			return null;

		}

		public String NameComment() {

			return "";

		}

		public String NamePattern() {

			return "";

		}

		public String NameOriginalDbColumnName() {

			return "Name";

		}

		public String Type;

		public String getType() {
			return this.Type;
		}

		public Boolean TypeIsNullable() {
			return false;
		}

		public Boolean TypeIsKey() {
			return false;
		}

		public Integer TypeLength() {
			return 255;
		}

		public Integer TypePrecision() {
			return null;
		}

		public String TypeDefault() {

			return null;

		}

		public String TypeComment() {

			return "";

		}

		public String TypePattern() {

			return "";

		}

		public String TypeOriginalDbColumnName() {

			return "Type";

		}

		public String ParentId;

		public String getParentId() {
			return this.ParentId;
		}

		public Boolean ParentIdIsNullable() {
			return false;
		}

		public Boolean ParentIdIsKey() {
			return false;
		}

		public Integer ParentIdLength() {
			return 18;
		}

		public Integer ParentIdPrecision() {
			return null;
		}

		public String ParentIdDefault() {

			return null;

		}

		public String ParentIdComment() {

			return "";

		}

		public String ParentIdPattern() {

			return "";

		}

		public String ParentIdOriginalDbColumnName() {

			return "ParentId";

		}

		public String BillingStreet;

		public String getBillingStreet() {
			return this.BillingStreet;
		}

		public Boolean BillingStreetIsNullable() {
			return false;
		}

		public Boolean BillingStreetIsKey() {
			return false;
		}

		public Integer BillingStreetLength() {
			return 255;
		}

		public Integer BillingStreetPrecision() {
			return null;
		}

		public String BillingStreetDefault() {

			return null;

		}

		public String BillingStreetComment() {

			return "";

		}

		public String BillingStreetPattern() {

			return "";

		}

		public String BillingStreetOriginalDbColumnName() {

			return "BillingStreet";

		}

		public String BillingCity;

		public String getBillingCity() {
			return this.BillingCity;
		}

		public Boolean BillingCityIsNullable() {
			return false;
		}

		public Boolean BillingCityIsKey() {
			return false;
		}

		public Integer BillingCityLength() {
			return 40;
		}

		public Integer BillingCityPrecision() {
			return null;
		}

		public String BillingCityDefault() {

			return null;

		}

		public String BillingCityComment() {

			return "";

		}

		public String BillingCityPattern() {

			return "";

		}

		public String BillingCityOriginalDbColumnName() {

			return "BillingCity";

		}

		public String BillingState;

		public String getBillingState() {
			return this.BillingState;
		}

		public Boolean BillingStateIsNullable() {
			return false;
		}

		public Boolean BillingStateIsKey() {
			return false;
		}

		public Integer BillingStateLength() {
			return 80;
		}

		public Integer BillingStatePrecision() {
			return null;
		}

		public String BillingStateDefault() {

			return null;

		}

		public String BillingStateComment() {

			return "";

		}

		public String BillingStatePattern() {

			return "";

		}

		public String BillingStateOriginalDbColumnName() {

			return "BillingState";

		}

		public String BillingPostalCode;

		public String getBillingPostalCode() {
			return this.BillingPostalCode;
		}

		public Boolean BillingPostalCodeIsNullable() {
			return false;
		}

		public Boolean BillingPostalCodeIsKey() {
			return false;
		}

		public Integer BillingPostalCodeLength() {
			return 20;
		}

		public Integer BillingPostalCodePrecision() {
			return null;
		}

		public String BillingPostalCodeDefault() {

			return null;

		}

		public String BillingPostalCodeComment() {

			return "";

		}

		public String BillingPostalCodePattern() {

			return "";

		}

		public String BillingPostalCodeOriginalDbColumnName() {

			return "BillingPostalCode";

		}

		public String BillingCountry;

		public String getBillingCountry() {
			return this.BillingCountry;
		}

		public Boolean BillingCountryIsNullable() {
			return false;
		}

		public Boolean BillingCountryIsKey() {
			return false;
		}

		public Integer BillingCountryLength() {
			return 80;
		}

		public Integer BillingCountryPrecision() {
			return null;
		}

		public String BillingCountryDefault() {

			return null;

		}

		public String BillingCountryComment() {

			return "";

		}

		public String BillingCountryPattern() {

			return "";

		}

		public String BillingCountryOriginalDbColumnName() {

			return "BillingCountry";

		}

		public double BillingLatitude;

		public double getBillingLatitude() {
			return this.BillingLatitude;
		}

		public Boolean BillingLatitudeIsNullable() {
			return false;
		}

		public Boolean BillingLatitudeIsKey() {
			return false;
		}

		public Integer BillingLatitudeLength() {
			return 18;
		}

		public Integer BillingLatitudePrecision() {
			return 15;
		}

		public String BillingLatitudeDefault() {

			return null;

		}

		public String BillingLatitudeComment() {

			return "";

		}

		public String BillingLatitudePattern() {

			return "";

		}

		public String BillingLatitudeOriginalDbColumnName() {

			return "BillingLatitude";

		}

		public double BillingLongitude;

		public double getBillingLongitude() {
			return this.BillingLongitude;
		}

		public Boolean BillingLongitudeIsNullable() {
			return false;
		}

		public Boolean BillingLongitudeIsKey() {
			return false;
		}

		public Integer BillingLongitudeLength() {
			return 18;
		}

		public Integer BillingLongitudePrecision() {
			return 15;
		}

		public String BillingLongitudeDefault() {

			return null;

		}

		public String BillingLongitudeComment() {

			return "";

		}

		public String BillingLongitudePattern() {

			return "";

		}

		public String BillingLongitudeOriginalDbColumnName() {

			return "BillingLongitude";

		}

		public String BillingGeocodeAccuracy;

		public String getBillingGeocodeAccuracy() {
			return this.BillingGeocodeAccuracy;
		}

		public Boolean BillingGeocodeAccuracyIsNullable() {
			return false;
		}

		public Boolean BillingGeocodeAccuracyIsKey() {
			return false;
		}

		public Integer BillingGeocodeAccuracyLength() {
			return 40;
		}

		public Integer BillingGeocodeAccuracyPrecision() {
			return null;
		}

		public String BillingGeocodeAccuracyDefault() {

			return null;

		}

		public String BillingGeocodeAccuracyComment() {

			return "";

		}

		public String BillingGeocodeAccuracyPattern() {

			return "";

		}

		public String BillingGeocodeAccuracyOriginalDbColumnName() {

			return "BillingGeocodeAccuracy";

		}

		public String BillingAddress;

		public String getBillingAddress() {
			return this.BillingAddress;
		}

		public Boolean BillingAddressIsNullable() {
			return false;
		}

		public Boolean BillingAddressIsKey() {
			return false;
		}

		public Integer BillingAddressLength() {
			return null;
		}

		public Integer BillingAddressPrecision() {
			return null;
		}

		public String BillingAddressDefault() {

			return null;

		}

		public String BillingAddressComment() {

			return "";

		}

		public String BillingAddressPattern() {

			return "";

		}

		public String BillingAddressOriginalDbColumnName() {

			return "BillingAddress";

		}

		public String ShippingStreet;

		public String getShippingStreet() {
			return this.ShippingStreet;
		}

		public Boolean ShippingStreetIsNullable() {
			return false;
		}

		public Boolean ShippingStreetIsKey() {
			return false;
		}

		public Integer ShippingStreetLength() {
			return 255;
		}

		public Integer ShippingStreetPrecision() {
			return null;
		}

		public String ShippingStreetDefault() {

			return null;

		}

		public String ShippingStreetComment() {

			return "";

		}

		public String ShippingStreetPattern() {

			return "";

		}

		public String ShippingStreetOriginalDbColumnName() {

			return "ShippingStreet";

		}

		public String ShippingCity;

		public String getShippingCity() {
			return this.ShippingCity;
		}

		public Boolean ShippingCityIsNullable() {
			return false;
		}

		public Boolean ShippingCityIsKey() {
			return false;
		}

		public Integer ShippingCityLength() {
			return 40;
		}

		public Integer ShippingCityPrecision() {
			return null;
		}

		public String ShippingCityDefault() {

			return null;

		}

		public String ShippingCityComment() {

			return "";

		}

		public String ShippingCityPattern() {

			return "";

		}

		public String ShippingCityOriginalDbColumnName() {

			return "ShippingCity";

		}

		public String ShippingState;

		public String getShippingState() {
			return this.ShippingState;
		}

		public Boolean ShippingStateIsNullable() {
			return false;
		}

		public Boolean ShippingStateIsKey() {
			return false;
		}

		public Integer ShippingStateLength() {
			return 80;
		}

		public Integer ShippingStatePrecision() {
			return null;
		}

		public String ShippingStateDefault() {

			return null;

		}

		public String ShippingStateComment() {

			return "";

		}

		public String ShippingStatePattern() {

			return "";

		}

		public String ShippingStateOriginalDbColumnName() {

			return "ShippingState";

		}

		public String ShippingPostalCode;

		public String getShippingPostalCode() {
			return this.ShippingPostalCode;
		}

		public Boolean ShippingPostalCodeIsNullable() {
			return false;
		}

		public Boolean ShippingPostalCodeIsKey() {
			return false;
		}

		public Integer ShippingPostalCodeLength() {
			return 20;
		}

		public Integer ShippingPostalCodePrecision() {
			return null;
		}

		public String ShippingPostalCodeDefault() {

			return null;

		}

		public String ShippingPostalCodeComment() {

			return "";

		}

		public String ShippingPostalCodePattern() {

			return "";

		}

		public String ShippingPostalCodeOriginalDbColumnName() {

			return "ShippingPostalCode";

		}

		public String ShippingCountry;

		public String getShippingCountry() {
			return this.ShippingCountry;
		}

		public Boolean ShippingCountryIsNullable() {
			return false;
		}

		public Boolean ShippingCountryIsKey() {
			return false;
		}

		public Integer ShippingCountryLength() {
			return 80;
		}

		public Integer ShippingCountryPrecision() {
			return null;
		}

		public String ShippingCountryDefault() {

			return null;

		}

		public String ShippingCountryComment() {

			return "";

		}

		public String ShippingCountryPattern() {

			return "";

		}

		public String ShippingCountryOriginalDbColumnName() {

			return "ShippingCountry";

		}

		public double ShippingLatitude;

		public double getShippingLatitude() {
			return this.ShippingLatitude;
		}

		public Boolean ShippingLatitudeIsNullable() {
			return false;
		}

		public Boolean ShippingLatitudeIsKey() {
			return false;
		}

		public Integer ShippingLatitudeLength() {
			return 18;
		}

		public Integer ShippingLatitudePrecision() {
			return 15;
		}

		public String ShippingLatitudeDefault() {

			return null;

		}

		public String ShippingLatitudeComment() {

			return "";

		}

		public String ShippingLatitudePattern() {

			return "";

		}

		public String ShippingLatitudeOriginalDbColumnName() {

			return "ShippingLatitude";

		}

		public double ShippingLongitude;

		public double getShippingLongitude() {
			return this.ShippingLongitude;
		}

		public Boolean ShippingLongitudeIsNullable() {
			return false;
		}

		public Boolean ShippingLongitudeIsKey() {
			return false;
		}

		public Integer ShippingLongitudeLength() {
			return 18;
		}

		public Integer ShippingLongitudePrecision() {
			return 15;
		}

		public String ShippingLongitudeDefault() {

			return null;

		}

		public String ShippingLongitudeComment() {

			return "";

		}

		public String ShippingLongitudePattern() {

			return "";

		}

		public String ShippingLongitudeOriginalDbColumnName() {

			return "ShippingLongitude";

		}

		public String ShippingGeocodeAccuracy;

		public String getShippingGeocodeAccuracy() {
			return this.ShippingGeocodeAccuracy;
		}

		public Boolean ShippingGeocodeAccuracyIsNullable() {
			return false;
		}

		public Boolean ShippingGeocodeAccuracyIsKey() {
			return false;
		}

		public Integer ShippingGeocodeAccuracyLength() {
			return 40;
		}

		public Integer ShippingGeocodeAccuracyPrecision() {
			return null;
		}

		public String ShippingGeocodeAccuracyDefault() {

			return null;

		}

		public String ShippingGeocodeAccuracyComment() {

			return "";

		}

		public String ShippingGeocodeAccuracyPattern() {

			return "";

		}

		public String ShippingGeocodeAccuracyOriginalDbColumnName() {

			return "ShippingGeocodeAccuracy";

		}

		public String ShippingAddress;

		public String getShippingAddress() {
			return this.ShippingAddress;
		}

		public Boolean ShippingAddressIsNullable() {
			return false;
		}

		public Boolean ShippingAddressIsKey() {
			return false;
		}

		public Integer ShippingAddressLength() {
			return null;
		}

		public Integer ShippingAddressPrecision() {
			return null;
		}

		public String ShippingAddressDefault() {

			return null;

		}

		public String ShippingAddressComment() {

			return "";

		}

		public String ShippingAddressPattern() {

			return "";

		}

		public String ShippingAddressOriginalDbColumnName() {

			return "ShippingAddress";

		}

		public String Phone;

		public String getPhone() {
			return this.Phone;
		}

		public Boolean PhoneIsNullable() {
			return false;
		}

		public Boolean PhoneIsKey() {
			return false;
		}

		public Integer PhoneLength() {
			return 40;
		}

		public Integer PhonePrecision() {
			return null;
		}

		public String PhoneDefault() {

			return null;

		}

		public String PhoneComment() {

			return "";

		}

		public String PhonePattern() {

			return "";

		}

		public String PhoneOriginalDbColumnName() {

			return "Phone";

		}

		public String Fax;

		public String getFax() {
			return this.Fax;
		}

		public Boolean FaxIsNullable() {
			return false;
		}

		public Boolean FaxIsKey() {
			return false;
		}

		public Integer FaxLength() {
			return 40;
		}

		public Integer FaxPrecision() {
			return null;
		}

		public String FaxDefault() {

			return null;

		}

		public String FaxComment() {

			return "";

		}

		public String FaxPattern() {

			return "";

		}

		public String FaxOriginalDbColumnName() {

			return "Fax";

		}

		public String AccountNumber;

		public String getAccountNumber() {
			return this.AccountNumber;
		}

		public Boolean AccountNumberIsNullable() {
			return false;
		}

		public Boolean AccountNumberIsKey() {
			return false;
		}

		public Integer AccountNumberLength() {
			return 40;
		}

		public Integer AccountNumberPrecision() {
			return null;
		}

		public String AccountNumberDefault() {

			return null;

		}

		public String AccountNumberComment() {

			return "";

		}

		public String AccountNumberPattern() {

			return "";

		}

		public String AccountNumberOriginalDbColumnName() {

			return "AccountNumber";

		}

		public String Website;

		public String getWebsite() {
			return this.Website;
		}

		public Boolean WebsiteIsNullable() {
			return false;
		}

		public Boolean WebsiteIsKey() {
			return false;
		}

		public Integer WebsiteLength() {
			return 255;
		}

		public Integer WebsitePrecision() {
			return null;
		}

		public String WebsiteDefault() {

			return null;

		}

		public String WebsiteComment() {

			return "";

		}

		public String WebsitePattern() {

			return "";

		}

		public String WebsiteOriginalDbColumnName() {

			return "Website";

		}

		public String PhotoUrl;

		public String getPhotoUrl() {
			return this.PhotoUrl;
		}

		public Boolean PhotoUrlIsNullable() {
			return false;
		}

		public Boolean PhotoUrlIsKey() {
			return false;
		}

		public Integer PhotoUrlLength() {
			return 255;
		}

		public Integer PhotoUrlPrecision() {
			return null;
		}

		public String PhotoUrlDefault() {

			return null;

		}

		public String PhotoUrlComment() {

			return "";

		}

		public String PhotoUrlPattern() {

			return "";

		}

		public String PhotoUrlOriginalDbColumnName() {

			return "PhotoUrl";

		}

		public String Sic;

		public String getSic() {
			return this.Sic;
		}

		public Boolean SicIsNullable() {
			return false;
		}

		public Boolean SicIsKey() {
			return false;
		}

		public Integer SicLength() {
			return 20;
		}

		public Integer SicPrecision() {
			return null;
		}

		public String SicDefault() {

			return null;

		}

		public String SicComment() {

			return "";

		}

		public String SicPattern() {

			return "";

		}

		public String SicOriginalDbColumnName() {

			return "Sic";

		}

		public String Industry;

		public String getIndustry() {
			return this.Industry;
		}

		public Boolean IndustryIsNullable() {
			return false;
		}

		public Boolean IndustryIsKey() {
			return false;
		}

		public Integer IndustryLength() {
			return 255;
		}

		public Integer IndustryPrecision() {
			return null;
		}

		public String IndustryDefault() {

			return null;

		}

		public String IndustryComment() {

			return "";

		}

		public String IndustryPattern() {

			return "";

		}

		public String IndustryOriginalDbColumnName() {

			return "Industry";

		}

		public BigDecimal AnnualRevenue;

		public BigDecimal getAnnualRevenue() {
			return this.AnnualRevenue;
		}

		public Boolean AnnualRevenueIsNullable() {
			return false;
		}

		public Boolean AnnualRevenueIsKey() {
			return false;
		}

		public Integer AnnualRevenueLength() {
			return 18;
		}

		public Integer AnnualRevenuePrecision() {
			return null;
		}

		public String AnnualRevenueDefault() {

			return null;

		}

		public String AnnualRevenueComment() {

			return "";

		}

		public String AnnualRevenuePattern() {

			return "";

		}

		public String AnnualRevenueOriginalDbColumnName() {

			return "AnnualRevenue";

		}

		public int NumberOfEmployees;

		public int getNumberOfEmployees() {
			return this.NumberOfEmployees;
		}

		public Boolean NumberOfEmployeesIsNullable() {
			return false;
		}

		public Boolean NumberOfEmployeesIsKey() {
			return false;
		}

		public Integer NumberOfEmployeesLength() {
			return null;
		}

		public Integer NumberOfEmployeesPrecision() {
			return null;
		}

		public String NumberOfEmployeesDefault() {

			return null;

		}

		public String NumberOfEmployeesComment() {

			return "";

		}

		public String NumberOfEmployeesPattern() {

			return "";

		}

		public String NumberOfEmployeesOriginalDbColumnName() {

			return "NumberOfEmployees";

		}

		public String Ownership;

		public String getOwnership() {
			return this.Ownership;
		}

		public Boolean OwnershipIsNullable() {
			return false;
		}

		public Boolean OwnershipIsKey() {
			return false;
		}

		public Integer OwnershipLength() {
			return 255;
		}

		public Integer OwnershipPrecision() {
			return null;
		}

		public String OwnershipDefault() {

			return null;

		}

		public String OwnershipComment() {

			return "";

		}

		public String OwnershipPattern() {

			return "";

		}

		public String OwnershipOriginalDbColumnName() {

			return "Ownership";

		}

		public String TickerSymbol;

		public String getTickerSymbol() {
			return this.TickerSymbol;
		}

		public Boolean TickerSymbolIsNullable() {
			return false;
		}

		public Boolean TickerSymbolIsKey() {
			return false;
		}

		public Integer TickerSymbolLength() {
			return 20;
		}

		public Integer TickerSymbolPrecision() {
			return null;
		}

		public String TickerSymbolDefault() {

			return null;

		}

		public String TickerSymbolComment() {

			return "";

		}

		public String TickerSymbolPattern() {

			return "";

		}

		public String TickerSymbolOriginalDbColumnName() {

			return "TickerSymbol";

		}

		public String Description;

		public String getDescription() {
			return this.Description;
		}

		public Boolean DescriptionIsNullable() {
			return false;
		}

		public Boolean DescriptionIsKey() {
			return false;
		}

		public Integer DescriptionLength() {
			return 32000;
		}

		public Integer DescriptionPrecision() {
			return null;
		}

		public String DescriptionDefault() {

			return null;

		}

		public String DescriptionComment() {

			return "";

		}

		public String DescriptionPattern() {

			return "";

		}

		public String DescriptionOriginalDbColumnName() {

			return "Description";

		}

		public String Rating;

		public String getRating() {
			return this.Rating;
		}

		public Boolean RatingIsNullable() {
			return false;
		}

		public Boolean RatingIsKey() {
			return false;
		}

		public Integer RatingLength() {
			return 255;
		}

		public Integer RatingPrecision() {
			return null;
		}

		public String RatingDefault() {

			return null;

		}

		public String RatingComment() {

			return "";

		}

		public String RatingPattern() {

			return "";

		}

		public String RatingOriginalDbColumnName() {

			return "Rating";

		}

		public String Site;

		public String getSite() {
			return this.Site;
		}

		public Boolean SiteIsNullable() {
			return false;
		}

		public Boolean SiteIsKey() {
			return false;
		}

		public Integer SiteLength() {
			return 80;
		}

		public Integer SitePrecision() {
			return null;
		}

		public String SiteDefault() {

			return null;

		}

		public String SiteComment() {

			return "";

		}

		public String SitePattern() {

			return "";

		}

		public String SiteOriginalDbColumnName() {

			return "Site";

		}

		public String OwnerId;

		public String getOwnerId() {
			return this.OwnerId;
		}

		public Boolean OwnerIdIsNullable() {
			return false;
		}

		public Boolean OwnerIdIsKey() {
			return false;
		}

		public Integer OwnerIdLength() {
			return 18;
		}

		public Integer OwnerIdPrecision() {
			return null;
		}

		public String OwnerIdDefault() {

			return null;

		}

		public String OwnerIdComment() {

			return "";

		}

		public String OwnerIdPattern() {

			return "";

		}

		public String OwnerIdOriginalDbColumnName() {

			return "OwnerId";

		}

		public java.util.Date CreatedDate;

		public java.util.Date getCreatedDate() {
			return this.CreatedDate;
		}

		public Boolean CreatedDateIsNullable() {
			return false;
		}

		public Boolean CreatedDateIsKey() {
			return false;
		}

		public Integer CreatedDateLength() {
			return null;
		}

		public Integer CreatedDatePrecision() {
			return null;
		}

		public String CreatedDateDefault() {

			return null;

		}

		public String CreatedDateComment() {

			return "";

		}

		public String CreatedDatePattern() {

			return "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

		}

		public String CreatedDateOriginalDbColumnName() {

			return "CreatedDate";

		}

		public String CreatedById;

		public String getCreatedById() {
			return this.CreatedById;
		}

		public Boolean CreatedByIdIsNullable() {
			return false;
		}

		public Boolean CreatedByIdIsKey() {
			return false;
		}

		public Integer CreatedByIdLength() {
			return 18;
		}

		public Integer CreatedByIdPrecision() {
			return null;
		}

		public String CreatedByIdDefault() {

			return null;

		}

		public String CreatedByIdComment() {

			return "";

		}

		public String CreatedByIdPattern() {

			return "";

		}

		public String CreatedByIdOriginalDbColumnName() {

			return "CreatedById";

		}

		public java.util.Date LastModifiedDate;

		public java.util.Date getLastModifiedDate() {
			return this.LastModifiedDate;
		}

		public Boolean LastModifiedDateIsNullable() {
			return false;
		}

		public Boolean LastModifiedDateIsKey() {
			return false;
		}

		public Integer LastModifiedDateLength() {
			return null;
		}

		public Integer LastModifiedDatePrecision() {
			return null;
		}

		public String LastModifiedDateDefault() {

			return null;

		}

		public String LastModifiedDateComment() {

			return "";

		}

		public String LastModifiedDatePattern() {

			return "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

		}

		public String LastModifiedDateOriginalDbColumnName() {

			return "LastModifiedDate";

		}

		public String LastModifiedById;

		public String getLastModifiedById() {
			return this.LastModifiedById;
		}

		public Boolean LastModifiedByIdIsNullable() {
			return false;
		}

		public Boolean LastModifiedByIdIsKey() {
			return false;
		}

		public Integer LastModifiedByIdLength() {
			return 18;
		}

		public Integer LastModifiedByIdPrecision() {
			return null;
		}

		public String LastModifiedByIdDefault() {

			return null;

		}

		public String LastModifiedByIdComment() {

			return "";

		}

		public String LastModifiedByIdPattern() {

			return "";

		}

		public String LastModifiedByIdOriginalDbColumnName() {

			return "LastModifiedById";

		}

		public java.util.Date SystemModstamp;

		public java.util.Date getSystemModstamp() {
			return this.SystemModstamp;
		}

		public Boolean SystemModstampIsNullable() {
			return false;
		}

		public Boolean SystemModstampIsKey() {
			return false;
		}

		public Integer SystemModstampLength() {
			return null;
		}

		public Integer SystemModstampPrecision() {
			return null;
		}

		public String SystemModstampDefault() {

			return null;

		}

		public String SystemModstampComment() {

			return "";

		}

		public String SystemModstampPattern() {

			return "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

		}

		public String SystemModstampOriginalDbColumnName() {

			return "SystemModstamp";

		}

		public java.util.Date LastActivityDate;

		public java.util.Date getLastActivityDate() {
			return this.LastActivityDate;
		}

		public Boolean LastActivityDateIsNullable() {
			return false;
		}

		public Boolean LastActivityDateIsKey() {
			return false;
		}

		public Integer LastActivityDateLength() {
			return null;
		}

		public Integer LastActivityDatePrecision() {
			return null;
		}

		public String LastActivityDateDefault() {

			return null;

		}

		public String LastActivityDateComment() {

			return "";

		}

		public String LastActivityDatePattern() {

			return "yyyy-MM-dd";

		}

		public String LastActivityDateOriginalDbColumnName() {

			return "LastActivityDate";

		}

		public java.util.Date LastViewedDate;

		public java.util.Date getLastViewedDate() {
			return this.LastViewedDate;
		}

		public Boolean LastViewedDateIsNullable() {
			return false;
		}

		public Boolean LastViewedDateIsKey() {
			return false;
		}

		public Integer LastViewedDateLength() {
			return null;
		}

		public Integer LastViewedDatePrecision() {
			return null;
		}

		public String LastViewedDateDefault() {

			return null;

		}

		public String LastViewedDateComment() {

			return "";

		}

		public String LastViewedDatePattern() {

			return "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

		}

		public String LastViewedDateOriginalDbColumnName() {

			return "LastViewedDate";

		}

		public java.util.Date LastReferencedDate;

		public java.util.Date getLastReferencedDate() {
			return this.LastReferencedDate;
		}

		public Boolean LastReferencedDateIsNullable() {
			return false;
		}

		public Boolean LastReferencedDateIsKey() {
			return false;
		}

		public Integer LastReferencedDateLength() {
			return null;
		}

		public Integer LastReferencedDatePrecision() {
			return null;
		}

		public String LastReferencedDateDefault() {

			return null;

		}

		public String LastReferencedDateComment() {

			return "";

		}

		public String LastReferencedDatePattern() {

			return "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

		}

		public String LastReferencedDateOriginalDbColumnName() {

			return "LastReferencedDate";

		}

		public String Jigsaw;

		public String getJigsaw() {
			return this.Jigsaw;
		}

		public Boolean JigsawIsNullable() {
			return false;
		}

		public Boolean JigsawIsKey() {
			return false;
		}

		public Integer JigsawLength() {
			return 20;
		}

		public Integer JigsawPrecision() {
			return null;
		}

		public String JigsawDefault() {

			return null;

		}

		public String JigsawComment() {

			return "";

		}

		public String JigsawPattern() {

			return "";

		}

		public String JigsawOriginalDbColumnName() {

			return "Jigsaw";

		}

		public String JigsawCompanyId;

		public String getJigsawCompanyId() {
			return this.JigsawCompanyId;
		}

		public Boolean JigsawCompanyIdIsNullable() {
			return false;
		}

		public Boolean JigsawCompanyIdIsKey() {
			return false;
		}

		public Integer JigsawCompanyIdLength() {
			return 20;
		}

		public Integer JigsawCompanyIdPrecision() {
			return null;
		}

		public String JigsawCompanyIdDefault() {

			return null;

		}

		public String JigsawCompanyIdComment() {

			return "";

		}

		public String JigsawCompanyIdPattern() {

			return "";

		}

		public String JigsawCompanyIdOriginalDbColumnName() {

			return "JigsawCompanyId";

		}

		public String CleanStatus;

		public String getCleanStatus() {
			return this.CleanStatus;
		}

		public Boolean CleanStatusIsNullable() {
			return false;
		}

		public Boolean CleanStatusIsKey() {
			return false;
		}

		public Integer CleanStatusLength() {
			return 40;
		}

		public Integer CleanStatusPrecision() {
			return null;
		}

		public String CleanStatusDefault() {

			return null;

		}

		public String CleanStatusComment() {

			return "";

		}

		public String CleanStatusPattern() {

			return "";

		}

		public String CleanStatusOriginalDbColumnName() {

			return "CleanStatus";

		}

		public String AccountSource;

		public String getAccountSource() {
			return this.AccountSource;
		}

		public Boolean AccountSourceIsNullable() {
			return false;
		}

		public Boolean AccountSourceIsKey() {
			return false;
		}

		public Integer AccountSourceLength() {
			return 255;
		}

		public Integer AccountSourcePrecision() {
			return null;
		}

		public String AccountSourceDefault() {

			return null;

		}

		public String AccountSourceComment() {

			return "";

		}

		public String AccountSourcePattern() {

			return "";

		}

		public String AccountSourceOriginalDbColumnName() {

			return "AccountSource";

		}

		public String DunsNumber;

		public String getDunsNumber() {
			return this.DunsNumber;
		}

		public Boolean DunsNumberIsNullable() {
			return false;
		}

		public Boolean DunsNumberIsKey() {
			return false;
		}

		public Integer DunsNumberLength() {
			return 9;
		}

		public Integer DunsNumberPrecision() {
			return null;
		}

		public String DunsNumberDefault() {

			return null;

		}

		public String DunsNumberComment() {

			return "";

		}

		public String DunsNumberPattern() {

			return "";

		}

		public String DunsNumberOriginalDbColumnName() {

			return "DunsNumber";

		}

		public String Tradestyle;

		public String getTradestyle() {
			return this.Tradestyle;
		}

		public Boolean TradestyleIsNullable() {
			return false;
		}

		public Boolean TradestyleIsKey() {
			return false;
		}

		public Integer TradestyleLength() {
			return 255;
		}

		public Integer TradestylePrecision() {
			return null;
		}

		public String TradestyleDefault() {

			return null;

		}

		public String TradestyleComment() {

			return "";

		}

		public String TradestylePattern() {

			return "";

		}

		public String TradestyleOriginalDbColumnName() {

			return "Tradestyle";

		}

		public String NaicsCode;

		public String getNaicsCode() {
			return this.NaicsCode;
		}

		public Boolean NaicsCodeIsNullable() {
			return false;
		}

		public Boolean NaicsCodeIsKey() {
			return false;
		}

		public Integer NaicsCodeLength() {
			return 8;
		}

		public Integer NaicsCodePrecision() {
			return null;
		}

		public String NaicsCodeDefault() {

			return null;

		}

		public String NaicsCodeComment() {

			return "";

		}

		public String NaicsCodePattern() {

			return "";

		}

		public String NaicsCodeOriginalDbColumnName() {

			return "NaicsCode";

		}

		public String NaicsDesc;

		public String getNaicsDesc() {
			return this.NaicsDesc;
		}

		public Boolean NaicsDescIsNullable() {
			return false;
		}

		public Boolean NaicsDescIsKey() {
			return false;
		}

		public Integer NaicsDescLength() {
			return 120;
		}

		public Integer NaicsDescPrecision() {
			return null;
		}

		public String NaicsDescDefault() {

			return null;

		}

		public String NaicsDescComment() {

			return "";

		}

		public String NaicsDescPattern() {

			return "";

		}

		public String NaicsDescOriginalDbColumnName() {

			return "NaicsDesc";

		}

		public String YearStarted;

		public String getYearStarted() {
			return this.YearStarted;
		}

		public Boolean YearStartedIsNullable() {
			return false;
		}

		public Boolean YearStartedIsKey() {
			return false;
		}

		public Integer YearStartedLength() {
			return 4;
		}

		public Integer YearStartedPrecision() {
			return null;
		}

		public String YearStartedDefault() {

			return null;

		}

		public String YearStartedComment() {

			return "";

		}

		public String YearStartedPattern() {

			return "";

		}

		public String YearStartedOriginalDbColumnName() {

			return "YearStarted";

		}

		public String SicDesc;

		public String getSicDesc() {
			return this.SicDesc;
		}

		public Boolean SicDescIsNullable() {
			return false;
		}

		public Boolean SicDescIsKey() {
			return false;
		}

		public Integer SicDescLength() {
			return 80;
		}

		public Integer SicDescPrecision() {
			return null;
		}

		public String SicDescDefault() {

			return null;

		}

		public String SicDescComment() {

			return "";

		}

		public String SicDescPattern() {

			return "";

		}

		public String SicDescOriginalDbColumnName() {

			return "SicDesc";

		}

		public String DandbCompanyId;

		public String getDandbCompanyId() {
			return this.DandbCompanyId;
		}

		public Boolean DandbCompanyIdIsNullable() {
			return false;
		}

		public Boolean DandbCompanyIdIsKey() {
			return false;
		}

		public Integer DandbCompanyIdLength() {
			return 18;
		}

		public Integer DandbCompanyIdPrecision() {
			return null;
		}

		public String DandbCompanyIdDefault() {

			return null;

		}

		public String DandbCompanyIdComment() {

			return "";

		}

		public String DandbCompanyIdPattern() {

			return "";

		}

		public String DandbCompanyIdOriginalDbColumnName() {

			return "DandbCompanyId";

		}

		public String OperatingHoursId;

		public String getOperatingHoursId() {
			return this.OperatingHoursId;
		}

		public Boolean OperatingHoursIdIsNullable() {
			return false;
		}

		public Boolean OperatingHoursIdIsKey() {
			return false;
		}

		public Integer OperatingHoursIdLength() {
			return 18;
		}

		public Integer OperatingHoursIdPrecision() {
			return null;
		}

		public String OperatingHoursIdDefault() {

			return null;

		}

		public String OperatingHoursIdComment() {

			return "";

		}

		public String OperatingHoursIdPattern() {

			return "";

		}

		public String OperatingHoursIdOriginalDbColumnName() {

			return "OperatingHoursId";

		}

		public String CustomerPriority__c;

		public String getCustomerPriority__c() {
			return this.CustomerPriority__c;
		}

		public Boolean CustomerPriority__cIsNullable() {
			return false;
		}

		public Boolean CustomerPriority__cIsKey() {
			return false;
		}

		public Integer CustomerPriority__cLength() {
			return 255;
		}

		public Integer CustomerPriority__cPrecision() {
			return null;
		}

		public String CustomerPriority__cDefault() {

			return null;

		}

		public String CustomerPriority__cComment() {

			return "";

		}

		public String CustomerPriority__cPattern() {

			return "";

		}

		public String CustomerPriority__cOriginalDbColumnName() {

			return "CustomerPriority__c";

		}

		public String SLA__c;

		public String getSLA__c() {
			return this.SLA__c;
		}

		public Boolean SLA__cIsNullable() {
			return false;
		}

		public Boolean SLA__cIsKey() {
			return false;
		}

		public Integer SLA__cLength() {
			return 255;
		}

		public Integer SLA__cPrecision() {
			return null;
		}

		public String SLA__cDefault() {

			return null;

		}

		public String SLA__cComment() {

			return "";

		}

		public String SLA__cPattern() {

			return "";

		}

		public String SLA__cOriginalDbColumnName() {

			return "SLA__c";

		}

		public String Active__c;

		public String getActive__c() {
			return this.Active__c;
		}

		public Boolean Active__cIsNullable() {
			return false;
		}

		public Boolean Active__cIsKey() {
			return false;
		}

		public Integer Active__cLength() {
			return 255;
		}

		public Integer Active__cPrecision() {
			return null;
		}

		public String Active__cDefault() {

			return null;

		}

		public String Active__cComment() {

			return "";

		}

		public String Active__cPattern() {

			return "";

		}

		public String Active__cOriginalDbColumnName() {

			return "Active__c";

		}

		public double NumberofLocations__c;

		public double getNumberofLocations__c() {
			return this.NumberofLocations__c;
		}

		public Boolean NumberofLocations__cIsNullable() {
			return false;
		}

		public Boolean NumberofLocations__cIsKey() {
			return false;
		}

		public Integer NumberofLocations__cLength() {
			return 3;
		}

		public Integer NumberofLocations__cPrecision() {
			return null;
		}

		public String NumberofLocations__cDefault() {

			return null;

		}

		public String NumberofLocations__cComment() {

			return "";

		}

		public String NumberofLocations__cPattern() {

			return "";

		}

		public String NumberofLocations__cOriginalDbColumnName() {

			return "NumberofLocations__c";

		}

		public String UpsellOpportunity__c;

		public String getUpsellOpportunity__c() {
			return this.UpsellOpportunity__c;
		}

		public Boolean UpsellOpportunity__cIsNullable() {
			return false;
		}

		public Boolean UpsellOpportunity__cIsKey() {
			return false;
		}

		public Integer UpsellOpportunity__cLength() {
			return 255;
		}

		public Integer UpsellOpportunity__cPrecision() {
			return null;
		}

		public String UpsellOpportunity__cDefault() {

			return null;

		}

		public String UpsellOpportunity__cComment() {

			return "";

		}

		public String UpsellOpportunity__cPattern() {

			return "";

		}

		public String UpsellOpportunity__cOriginalDbColumnName() {

			return "UpsellOpportunity__c";

		}

		public String SLASerialNumber__c;

		public String getSLASerialNumber__c() {
			return this.SLASerialNumber__c;
		}

		public Boolean SLASerialNumber__cIsNullable() {
			return false;
		}

		public Boolean SLASerialNumber__cIsKey() {
			return false;
		}

		public Integer SLASerialNumber__cLength() {
			return 10;
		}

		public Integer SLASerialNumber__cPrecision() {
			return null;
		}

		public String SLASerialNumber__cDefault() {

			return null;

		}

		public String SLASerialNumber__cComment() {

			return "";

		}

		public String SLASerialNumber__cPattern() {

			return "";

		}

		public String SLASerialNumber__cOriginalDbColumnName() {

			return "SLASerialNumber__c";

		}

		public java.util.Date SLAExpirationDate__c;

		public java.util.Date getSLAExpirationDate__c() {
			return this.SLAExpirationDate__c;
		}

		public Boolean SLAExpirationDate__cIsNullable() {
			return false;
		}

		public Boolean SLAExpirationDate__cIsKey() {
			return false;
		}

		public Integer SLAExpirationDate__cLength() {
			return null;
		}

		public Integer SLAExpirationDate__cPrecision() {
			return null;
		}

		public String SLAExpirationDate__cDefault() {

			return null;

		}

		public String SLAExpirationDate__cComment() {

			return "";

		}

		public String SLAExpirationDate__cPattern() {

			return "yyyy-MM-dd";

		}

		public String SLAExpirationDate__cOriginalDbColumnName() {

			return "SLAExpirationDate__c";

		}

		public String Email__c;

		public String getEmail__c() {
			return this.Email__c;
		}

		public Boolean Email__cIsNullable() {
			return false;
		}

		public Boolean Email__cIsKey() {
			return false;
		}

		public Integer Email__cLength() {
			return 80;
		}

		public Integer Email__cPrecision() {
			return null;
		}

		public String Email__cDefault() {

			return null;

		}

		public String Email__cComment() {

			return "";

		}

		public String Email__cPattern() {

			return "";

		}

		public String Email__cOriginalDbColumnName() {

			return "Email__c";

		}

		public String ExternalID__c;

		public String getExternalID__c() {
			return this.ExternalID__c;
		}

		public Boolean ExternalID__cIsNullable() {
			return true;
		}

		public Boolean ExternalID__cIsKey() {
			return false;
		}

		public Integer ExternalID__cLength() {
			return 100;
		}

		public Integer ExternalID__cPrecision() {
			return null;
		}

		public String ExternalID__cDefault() {

			return null;

		}

		public String ExternalID__cComment() {

			return "";

		}

		public String ExternalID__cPattern() {

			return "";

		}

		public String ExternalID__cOriginalDbColumnName() {

			return "ExternalID__c";

		}

		public String Status__c;

		public String getStatus__c() {
			return this.Status__c;
		}

		public Boolean Status__cIsNullable() {
			return false;
		}

		public Boolean Status__cIsKey() {
			return false;
		}

		public Integer Status__cLength() {
			return 30;
		}

		public Integer Status__cPrecision() {
			return null;
		}

		public String Status__cDefault() {

			return null;

		}

		public String Status__cComment() {

			return "";

		}

		public String Status__cPattern() {

			return "";

		}

		public String Status__cOriginalDbColumnName() {

			return "Status__c";

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_SALESFORCETALEND_Salesforce.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Salesforce.length == 0) {
						commonByteArray_SALESFORCETALEND_Salesforce = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Salesforce = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_SALESFORCETALEND_Salesforce, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Salesforce, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_SALESFORCETALEND_Salesforce.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Salesforce.length == 0) {
						commonByteArray_SALESFORCETALEND_Salesforce = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Salesforce = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_SALESFORCETALEND_Salesforce, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Salesforce, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		private java.util.Date readDate(ObjectInputStream dis) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(dis.readLong());
			}
			return dateReturn;
		}

		private java.util.Date readDate(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = unmarshaller.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(unmarshaller.readLong());
			}
			return dateReturn;
		}

		private void writeDate(java.util.Date date1, ObjectOutputStream dos) throws IOException {
			if (date1 == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeLong(date1.getTime());
			}
		}

		private void writeDate(java.util.Date date1, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (date1 == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeLong(date1.getTime());
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Salesforce) {

				try {

					int length = 0;

					this.IsDeleted = dis.readBoolean();

					this.MasterRecordId = readString(dis);

					this.Name = readString(dis);

					this.Type = readString(dis);

					this.ParentId = readString(dis);

					this.BillingStreet = readString(dis);

					this.BillingCity = readString(dis);

					this.BillingState = readString(dis);

					this.BillingPostalCode = readString(dis);

					this.BillingCountry = readString(dis);

					this.BillingLatitude = dis.readDouble();

					this.BillingLongitude = dis.readDouble();

					this.BillingGeocodeAccuracy = readString(dis);

					this.BillingAddress = readString(dis);

					this.ShippingStreet = readString(dis);

					this.ShippingCity = readString(dis);

					this.ShippingState = readString(dis);

					this.ShippingPostalCode = readString(dis);

					this.ShippingCountry = readString(dis);

					this.ShippingLatitude = dis.readDouble();

					this.ShippingLongitude = dis.readDouble();

					this.ShippingGeocodeAccuracy = readString(dis);

					this.ShippingAddress = readString(dis);

					this.Phone = readString(dis);

					this.Fax = readString(dis);

					this.AccountNumber = readString(dis);

					this.Website = readString(dis);

					this.PhotoUrl = readString(dis);

					this.Sic = readString(dis);

					this.Industry = readString(dis);

					this.AnnualRevenue = (BigDecimal) dis.readObject();

					this.NumberOfEmployees = dis.readInt();

					this.Ownership = readString(dis);

					this.TickerSymbol = readString(dis);

					this.Description = readString(dis);

					this.Rating = readString(dis);

					this.Site = readString(dis);

					this.OwnerId = readString(dis);

					this.CreatedDate = readDate(dis);

					this.CreatedById = readString(dis);

					this.LastModifiedDate = readDate(dis);

					this.LastModifiedById = readString(dis);

					this.SystemModstamp = readDate(dis);

					this.LastActivityDate = readDate(dis);

					this.LastViewedDate = readDate(dis);

					this.LastReferencedDate = readDate(dis);

					this.Jigsaw = readString(dis);

					this.JigsawCompanyId = readString(dis);

					this.CleanStatus = readString(dis);

					this.AccountSource = readString(dis);

					this.DunsNumber = readString(dis);

					this.Tradestyle = readString(dis);

					this.NaicsCode = readString(dis);

					this.NaicsDesc = readString(dis);

					this.YearStarted = readString(dis);

					this.SicDesc = readString(dis);

					this.DandbCompanyId = readString(dis);

					this.OperatingHoursId = readString(dis);

					this.CustomerPriority__c = readString(dis);

					this.SLA__c = readString(dis);

					this.Active__c = readString(dis);

					this.NumberofLocations__c = dis.readDouble();

					this.UpsellOpportunity__c = readString(dis);

					this.SLASerialNumber__c = readString(dis);

					this.SLAExpirationDate__c = readDate(dis);

					this.Email__c = readString(dis);

					this.ExternalID__c = readString(dis);

					this.Status__c = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Salesforce) {

				try {

					int length = 0;

					this.IsDeleted = dis.readBoolean();

					this.MasterRecordId = readString(dis);

					this.Name = readString(dis);

					this.Type = readString(dis);

					this.ParentId = readString(dis);

					this.BillingStreet = readString(dis);

					this.BillingCity = readString(dis);

					this.BillingState = readString(dis);

					this.BillingPostalCode = readString(dis);

					this.BillingCountry = readString(dis);

					this.BillingLatitude = dis.readDouble();

					this.BillingLongitude = dis.readDouble();

					this.BillingGeocodeAccuracy = readString(dis);

					this.BillingAddress = readString(dis);

					this.ShippingStreet = readString(dis);

					this.ShippingCity = readString(dis);

					this.ShippingState = readString(dis);

					this.ShippingPostalCode = readString(dis);

					this.ShippingCountry = readString(dis);

					this.ShippingLatitude = dis.readDouble();

					this.ShippingLongitude = dis.readDouble();

					this.ShippingGeocodeAccuracy = readString(dis);

					this.ShippingAddress = readString(dis);

					this.Phone = readString(dis);

					this.Fax = readString(dis);

					this.AccountNumber = readString(dis);

					this.Website = readString(dis);

					this.PhotoUrl = readString(dis);

					this.Sic = readString(dis);

					this.Industry = readString(dis);

					this.AnnualRevenue = (BigDecimal) dis.readObject();

					this.NumberOfEmployees = dis.readInt();

					this.Ownership = readString(dis);

					this.TickerSymbol = readString(dis);

					this.Description = readString(dis);

					this.Rating = readString(dis);

					this.Site = readString(dis);

					this.OwnerId = readString(dis);

					this.CreatedDate = readDate(dis);

					this.CreatedById = readString(dis);

					this.LastModifiedDate = readDate(dis);

					this.LastModifiedById = readString(dis);

					this.SystemModstamp = readDate(dis);

					this.LastActivityDate = readDate(dis);

					this.LastViewedDate = readDate(dis);

					this.LastReferencedDate = readDate(dis);

					this.Jigsaw = readString(dis);

					this.JigsawCompanyId = readString(dis);

					this.CleanStatus = readString(dis);

					this.AccountSource = readString(dis);

					this.DunsNumber = readString(dis);

					this.Tradestyle = readString(dis);

					this.NaicsCode = readString(dis);

					this.NaicsDesc = readString(dis);

					this.YearStarted = readString(dis);

					this.SicDesc = readString(dis);

					this.DandbCompanyId = readString(dis);

					this.OperatingHoursId = readString(dis);

					this.CustomerPriority__c = readString(dis);

					this.SLA__c = readString(dis);

					this.Active__c = readString(dis);

					this.NumberofLocations__c = dis.readDouble();

					this.UpsellOpportunity__c = readString(dis);

					this.SLASerialNumber__c = readString(dis);

					this.SLAExpirationDate__c = readDate(dis);

					this.Email__c = readString(dis);

					this.ExternalID__c = readString(dis);

					this.Status__c = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// boolean

				dos.writeBoolean(this.IsDeleted);

				// String

				writeString(this.MasterRecordId, dos);

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.Type, dos);

				// String

				writeString(this.ParentId, dos);

				// String

				writeString(this.BillingStreet, dos);

				// String

				writeString(this.BillingCity, dos);

				// String

				writeString(this.BillingState, dos);

				// String

				writeString(this.BillingPostalCode, dos);

				// String

				writeString(this.BillingCountry, dos);

				// double

				dos.writeDouble(this.BillingLatitude);

				// double

				dos.writeDouble(this.BillingLongitude);

				// String

				writeString(this.BillingGeocodeAccuracy, dos);

				// String

				writeString(this.BillingAddress, dos);

				// String

				writeString(this.ShippingStreet, dos);

				// String

				writeString(this.ShippingCity, dos);

				// String

				writeString(this.ShippingState, dos);

				// String

				writeString(this.ShippingPostalCode, dos);

				// String

				writeString(this.ShippingCountry, dos);

				// double

				dos.writeDouble(this.ShippingLatitude);

				// double

				dos.writeDouble(this.ShippingLongitude);

				// String

				writeString(this.ShippingGeocodeAccuracy, dos);

				// String

				writeString(this.ShippingAddress, dos);

				// String

				writeString(this.Phone, dos);

				// String

				writeString(this.Fax, dos);

				// String

				writeString(this.AccountNumber, dos);

				// String

				writeString(this.Website, dos);

				// String

				writeString(this.PhotoUrl, dos);

				// String

				writeString(this.Sic, dos);

				// String

				writeString(this.Industry, dos);

				// BigDecimal

				dos.writeObject(this.AnnualRevenue);

				// int

				dos.writeInt(this.NumberOfEmployees);

				// String

				writeString(this.Ownership, dos);

				// String

				writeString(this.TickerSymbol, dos);

				// String

				writeString(this.Description, dos);

				// String

				writeString(this.Rating, dos);

				// String

				writeString(this.Site, dos);

				// String

				writeString(this.OwnerId, dos);

				// java.util.Date

				writeDate(this.CreatedDate, dos);

				// String

				writeString(this.CreatedById, dos);

				// java.util.Date

				writeDate(this.LastModifiedDate, dos);

				// String

				writeString(this.LastModifiedById, dos);

				// java.util.Date

				writeDate(this.SystemModstamp, dos);

				// java.util.Date

				writeDate(this.LastActivityDate, dos);

				// java.util.Date

				writeDate(this.LastViewedDate, dos);

				// java.util.Date

				writeDate(this.LastReferencedDate, dos);

				// String

				writeString(this.Jigsaw, dos);

				// String

				writeString(this.JigsawCompanyId, dos);

				// String

				writeString(this.CleanStatus, dos);

				// String

				writeString(this.AccountSource, dos);

				// String

				writeString(this.DunsNumber, dos);

				// String

				writeString(this.Tradestyle, dos);

				// String

				writeString(this.NaicsCode, dos);

				// String

				writeString(this.NaicsDesc, dos);

				// String

				writeString(this.YearStarted, dos);

				// String

				writeString(this.SicDesc, dos);

				// String

				writeString(this.DandbCompanyId, dos);

				// String

				writeString(this.OperatingHoursId, dos);

				// String

				writeString(this.CustomerPriority__c, dos);

				// String

				writeString(this.SLA__c, dos);

				// String

				writeString(this.Active__c, dos);

				// double

				dos.writeDouble(this.NumberofLocations__c);

				// String

				writeString(this.UpsellOpportunity__c, dos);

				// String

				writeString(this.SLASerialNumber__c, dos);

				// java.util.Date

				writeDate(this.SLAExpirationDate__c, dos);

				// String

				writeString(this.Email__c, dos);

				// String

				writeString(this.ExternalID__c, dos);

				// String

				writeString(this.Status__c, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// boolean

				dos.writeBoolean(this.IsDeleted);

				// String

				writeString(this.MasterRecordId, dos);

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.Type, dos);

				// String

				writeString(this.ParentId, dos);

				// String

				writeString(this.BillingStreet, dos);

				// String

				writeString(this.BillingCity, dos);

				// String

				writeString(this.BillingState, dos);

				// String

				writeString(this.BillingPostalCode, dos);

				// String

				writeString(this.BillingCountry, dos);

				// double

				dos.writeDouble(this.BillingLatitude);

				// double

				dos.writeDouble(this.BillingLongitude);

				// String

				writeString(this.BillingGeocodeAccuracy, dos);

				// String

				writeString(this.BillingAddress, dos);

				// String

				writeString(this.ShippingStreet, dos);

				// String

				writeString(this.ShippingCity, dos);

				// String

				writeString(this.ShippingState, dos);

				// String

				writeString(this.ShippingPostalCode, dos);

				// String

				writeString(this.ShippingCountry, dos);

				// double

				dos.writeDouble(this.ShippingLatitude);

				// double

				dos.writeDouble(this.ShippingLongitude);

				// String

				writeString(this.ShippingGeocodeAccuracy, dos);

				// String

				writeString(this.ShippingAddress, dos);

				// String

				writeString(this.Phone, dos);

				// String

				writeString(this.Fax, dos);

				// String

				writeString(this.AccountNumber, dos);

				// String

				writeString(this.Website, dos);

				// String

				writeString(this.PhotoUrl, dos);

				// String

				writeString(this.Sic, dos);

				// String

				writeString(this.Industry, dos);

				// BigDecimal

				dos.clearInstanceCache();
				dos.writeObject(this.AnnualRevenue);

				// int

				dos.writeInt(this.NumberOfEmployees);

				// String

				writeString(this.Ownership, dos);

				// String

				writeString(this.TickerSymbol, dos);

				// String

				writeString(this.Description, dos);

				// String

				writeString(this.Rating, dos);

				// String

				writeString(this.Site, dos);

				// String

				writeString(this.OwnerId, dos);

				// java.util.Date

				writeDate(this.CreatedDate, dos);

				// String

				writeString(this.CreatedById, dos);

				// java.util.Date

				writeDate(this.LastModifiedDate, dos);

				// String

				writeString(this.LastModifiedById, dos);

				// java.util.Date

				writeDate(this.SystemModstamp, dos);

				// java.util.Date

				writeDate(this.LastActivityDate, dos);

				// java.util.Date

				writeDate(this.LastViewedDate, dos);

				// java.util.Date

				writeDate(this.LastReferencedDate, dos);

				// String

				writeString(this.Jigsaw, dos);

				// String

				writeString(this.JigsawCompanyId, dos);

				// String

				writeString(this.CleanStatus, dos);

				// String

				writeString(this.AccountSource, dos);

				// String

				writeString(this.DunsNumber, dos);

				// String

				writeString(this.Tradestyle, dos);

				// String

				writeString(this.NaicsCode, dos);

				// String

				writeString(this.NaicsDesc, dos);

				// String

				writeString(this.YearStarted, dos);

				// String

				writeString(this.SicDesc, dos);

				// String

				writeString(this.DandbCompanyId, dos);

				// String

				writeString(this.OperatingHoursId, dos);

				// String

				writeString(this.CustomerPriority__c, dos);

				// String

				writeString(this.SLA__c, dos);

				// String

				writeString(this.Active__c, dos);

				// double

				dos.writeDouble(this.NumberofLocations__c);

				// String

				writeString(this.UpsellOpportunity__c, dos);

				// String

				writeString(this.SLASerialNumber__c, dos);

				// java.util.Date

				writeDate(this.SLAExpirationDate__c, dos);

				// String

				writeString(this.Email__c, dos);

				// String

				writeString(this.ExternalID__c, dos);

				// String

				writeString(this.Status__c, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("IsDeleted=" + String.valueOf(IsDeleted));
			sb.append(",MasterRecordId=" + MasterRecordId);
			sb.append(",Name=" + Name);
			sb.append(",Type=" + Type);
			sb.append(",ParentId=" + ParentId);
			sb.append(",BillingStreet=" + BillingStreet);
			sb.append(",BillingCity=" + BillingCity);
			sb.append(",BillingState=" + BillingState);
			sb.append(",BillingPostalCode=" + BillingPostalCode);
			sb.append(",BillingCountry=" + BillingCountry);
			sb.append(",BillingLatitude=" + String.valueOf(BillingLatitude));
			sb.append(",BillingLongitude=" + String.valueOf(BillingLongitude));
			sb.append(",BillingGeocodeAccuracy=" + BillingGeocodeAccuracy);
			sb.append(",BillingAddress=" + BillingAddress);
			sb.append(",ShippingStreet=" + ShippingStreet);
			sb.append(",ShippingCity=" + ShippingCity);
			sb.append(",ShippingState=" + ShippingState);
			sb.append(",ShippingPostalCode=" + ShippingPostalCode);
			sb.append(",ShippingCountry=" + ShippingCountry);
			sb.append(",ShippingLatitude=" + String.valueOf(ShippingLatitude));
			sb.append(",ShippingLongitude=" + String.valueOf(ShippingLongitude));
			sb.append(",ShippingGeocodeAccuracy=" + ShippingGeocodeAccuracy);
			sb.append(",ShippingAddress=" + ShippingAddress);
			sb.append(",Phone=" + Phone);
			sb.append(",Fax=" + Fax);
			sb.append(",AccountNumber=" + AccountNumber);
			sb.append(",Website=" + Website);
			sb.append(",PhotoUrl=" + PhotoUrl);
			sb.append(",Sic=" + Sic);
			sb.append(",Industry=" + Industry);
			sb.append(",AnnualRevenue=" + String.valueOf(AnnualRevenue));
			sb.append(",NumberOfEmployees=" + String.valueOf(NumberOfEmployees));
			sb.append(",Ownership=" + Ownership);
			sb.append(",TickerSymbol=" + TickerSymbol);
			sb.append(",Description=" + Description);
			sb.append(",Rating=" + Rating);
			sb.append(",Site=" + Site);
			sb.append(",OwnerId=" + OwnerId);
			sb.append(",CreatedDate=" + String.valueOf(CreatedDate));
			sb.append(",CreatedById=" + CreatedById);
			sb.append(",LastModifiedDate=" + String.valueOf(LastModifiedDate));
			sb.append(",LastModifiedById=" + LastModifiedById);
			sb.append(",SystemModstamp=" + String.valueOf(SystemModstamp));
			sb.append(",LastActivityDate=" + String.valueOf(LastActivityDate));
			sb.append(",LastViewedDate=" + String.valueOf(LastViewedDate));
			sb.append(",LastReferencedDate=" + String.valueOf(LastReferencedDate));
			sb.append(",Jigsaw=" + Jigsaw);
			sb.append(",JigsawCompanyId=" + JigsawCompanyId);
			sb.append(",CleanStatus=" + CleanStatus);
			sb.append(",AccountSource=" + AccountSource);
			sb.append(",DunsNumber=" + DunsNumber);
			sb.append(",Tradestyle=" + Tradestyle);
			sb.append(",NaicsCode=" + NaicsCode);
			sb.append(",NaicsDesc=" + NaicsDesc);
			sb.append(",YearStarted=" + YearStarted);
			sb.append(",SicDesc=" + SicDesc);
			sb.append(",DandbCompanyId=" + DandbCompanyId);
			sb.append(",OperatingHoursId=" + OperatingHoursId);
			sb.append(",CustomerPriority__c=" + CustomerPriority__c);
			sb.append(",SLA__c=" + SLA__c);
			sb.append(",Active__c=" + Active__c);
			sb.append(",NumberofLocations__c=" + String.valueOf(NumberofLocations__c));
			sb.append(",UpsellOpportunity__c=" + UpsellOpportunity__c);
			sb.append(",SLASerialNumber__c=" + SLASerialNumber__c);
			sb.append(",SLAExpirationDate__c=" + String.valueOf(SLAExpirationDate__c));
			sb.append(",Email__c=" + Email__c);
			sb.append(",ExternalID__c=" + ExternalID__c);
			sb.append(",Status__c=" + Status__c);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			sb.append(IsDeleted);

			sb.append("|");

			if (MasterRecordId == null) {
				sb.append("<null>");
			} else {
				sb.append(MasterRecordId);
			}

			sb.append("|");

			if (Name == null) {
				sb.append("<null>");
			} else {
				sb.append(Name);
			}

			sb.append("|");

			if (Type == null) {
				sb.append("<null>");
			} else {
				sb.append(Type);
			}

			sb.append("|");

			if (ParentId == null) {
				sb.append("<null>");
			} else {
				sb.append(ParentId);
			}

			sb.append("|");

			if (BillingStreet == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingStreet);
			}

			sb.append("|");

			if (BillingCity == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingCity);
			}

			sb.append("|");

			if (BillingState == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingState);
			}

			sb.append("|");

			if (BillingPostalCode == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingPostalCode);
			}

			sb.append("|");

			if (BillingCountry == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingCountry);
			}

			sb.append("|");

			sb.append(BillingLatitude);

			sb.append("|");

			sb.append(BillingLongitude);

			sb.append("|");

			if (BillingGeocodeAccuracy == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingGeocodeAccuracy);
			}

			sb.append("|");

			if (BillingAddress == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingAddress);
			}

			sb.append("|");

			if (ShippingStreet == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingStreet);
			}

			sb.append("|");

			if (ShippingCity == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingCity);
			}

			sb.append("|");

			if (ShippingState == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingState);
			}

			sb.append("|");

			if (ShippingPostalCode == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingPostalCode);
			}

			sb.append("|");

			if (ShippingCountry == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingCountry);
			}

			sb.append("|");

			sb.append(ShippingLatitude);

			sb.append("|");

			sb.append(ShippingLongitude);

			sb.append("|");

			if (ShippingGeocodeAccuracy == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingGeocodeAccuracy);
			}

			sb.append("|");

			if (ShippingAddress == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingAddress);
			}

			sb.append("|");

			if (Phone == null) {
				sb.append("<null>");
			} else {
				sb.append(Phone);
			}

			sb.append("|");

			if (Fax == null) {
				sb.append("<null>");
			} else {
				sb.append(Fax);
			}

			sb.append("|");

			if (AccountNumber == null) {
				sb.append("<null>");
			} else {
				sb.append(AccountNumber);
			}

			sb.append("|");

			if (Website == null) {
				sb.append("<null>");
			} else {
				sb.append(Website);
			}

			sb.append("|");

			if (PhotoUrl == null) {
				sb.append("<null>");
			} else {
				sb.append(PhotoUrl);
			}

			sb.append("|");

			if (Sic == null) {
				sb.append("<null>");
			} else {
				sb.append(Sic);
			}

			sb.append("|");

			if (Industry == null) {
				sb.append("<null>");
			} else {
				sb.append(Industry);
			}

			sb.append("|");

			if (AnnualRevenue == null) {
				sb.append("<null>");
			} else {
				sb.append(AnnualRevenue);
			}

			sb.append("|");

			sb.append(NumberOfEmployees);

			sb.append("|");

			if (Ownership == null) {
				sb.append("<null>");
			} else {
				sb.append(Ownership);
			}

			sb.append("|");

			if (TickerSymbol == null) {
				sb.append("<null>");
			} else {
				sb.append(TickerSymbol);
			}

			sb.append("|");

			if (Description == null) {
				sb.append("<null>");
			} else {
				sb.append(Description);
			}

			sb.append("|");

			if (Rating == null) {
				sb.append("<null>");
			} else {
				sb.append(Rating);
			}

			sb.append("|");

			if (Site == null) {
				sb.append("<null>");
			} else {
				sb.append(Site);
			}

			sb.append("|");

			if (OwnerId == null) {
				sb.append("<null>");
			} else {
				sb.append(OwnerId);
			}

			sb.append("|");

			if (CreatedDate == null) {
				sb.append("<null>");
			} else {
				sb.append(CreatedDate);
			}

			sb.append("|");

			if (CreatedById == null) {
				sb.append("<null>");
			} else {
				sb.append(CreatedById);
			}

			sb.append("|");

			if (LastModifiedDate == null) {
				sb.append("<null>");
			} else {
				sb.append(LastModifiedDate);
			}

			sb.append("|");

			if (LastModifiedById == null) {
				sb.append("<null>");
			} else {
				sb.append(LastModifiedById);
			}

			sb.append("|");

			if (SystemModstamp == null) {
				sb.append("<null>");
			} else {
				sb.append(SystemModstamp);
			}

			sb.append("|");

			if (LastActivityDate == null) {
				sb.append("<null>");
			} else {
				sb.append(LastActivityDate);
			}

			sb.append("|");

			if (LastViewedDate == null) {
				sb.append("<null>");
			} else {
				sb.append(LastViewedDate);
			}

			sb.append("|");

			if (LastReferencedDate == null) {
				sb.append("<null>");
			} else {
				sb.append(LastReferencedDate);
			}

			sb.append("|");

			if (Jigsaw == null) {
				sb.append("<null>");
			} else {
				sb.append(Jigsaw);
			}

			sb.append("|");

			if (JigsawCompanyId == null) {
				sb.append("<null>");
			} else {
				sb.append(JigsawCompanyId);
			}

			sb.append("|");

			if (CleanStatus == null) {
				sb.append("<null>");
			} else {
				sb.append(CleanStatus);
			}

			sb.append("|");

			if (AccountSource == null) {
				sb.append("<null>");
			} else {
				sb.append(AccountSource);
			}

			sb.append("|");

			if (DunsNumber == null) {
				sb.append("<null>");
			} else {
				sb.append(DunsNumber);
			}

			sb.append("|");

			if (Tradestyle == null) {
				sb.append("<null>");
			} else {
				sb.append(Tradestyle);
			}

			sb.append("|");

			if (NaicsCode == null) {
				sb.append("<null>");
			} else {
				sb.append(NaicsCode);
			}

			sb.append("|");

			if (NaicsDesc == null) {
				sb.append("<null>");
			} else {
				sb.append(NaicsDesc);
			}

			sb.append("|");

			if (YearStarted == null) {
				sb.append("<null>");
			} else {
				sb.append(YearStarted);
			}

			sb.append("|");

			if (SicDesc == null) {
				sb.append("<null>");
			} else {
				sb.append(SicDesc);
			}

			sb.append("|");

			if (DandbCompanyId == null) {
				sb.append("<null>");
			} else {
				sb.append(DandbCompanyId);
			}

			sb.append("|");

			if (OperatingHoursId == null) {
				sb.append("<null>");
			} else {
				sb.append(OperatingHoursId);
			}

			sb.append("|");

			if (CustomerPriority__c == null) {
				sb.append("<null>");
			} else {
				sb.append(CustomerPriority__c);
			}

			sb.append("|");

			if (SLA__c == null) {
				sb.append("<null>");
			} else {
				sb.append(SLA__c);
			}

			sb.append("|");

			if (Active__c == null) {
				sb.append("<null>");
			} else {
				sb.append(Active__c);
			}

			sb.append("|");

			sb.append(NumberofLocations__c);

			sb.append("|");

			if (UpsellOpportunity__c == null) {
				sb.append("<null>");
			} else {
				sb.append(UpsellOpportunity__c);
			}

			sb.append("|");

			if (SLASerialNumber__c == null) {
				sb.append("<null>");
			} else {
				sb.append(SLASerialNumber__c);
			}

			sb.append("|");

			if (SLAExpirationDate__c == null) {
				sb.append("<null>");
			} else {
				sb.append(SLAExpirationDate__c);
			}

			sb.append("|");

			if (Email__c == null) {
				sb.append("<null>");
			} else {
				sb.append(Email__c);
			}

			sb.append("|");

			if (ExternalID__c == null) {
				sb.append("<null>");
			} else {
				sb.append(ExternalID__c);
			}

			sb.append("|");

			if (Status__c == null) {
				sb.append("<null>");
			} else {
				sb.append(Status__c);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(mapOut1Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public static class rmappingStruct implements routines.system.IPersistableRow<rmappingStruct> {
		final static byte[] commonByteArrayLock_SALESFORCETALEND_Salesforce = new byte[0];
		static byte[] commonByteArray_SALESFORCETALEND_Salesforce = new byte[0];

		public String Id;

		public String getId() {
			return this.Id;
		}

		public Boolean IdIsNullable() {
			return false;
		}

		public Boolean IdIsKey() {
			return false;
		}

		public Integer IdLength() {
			return 18;
		}

		public Integer IdPrecision() {
			return null;
		}

		public String IdDefault() {

			return null;

		}

		public String IdComment() {

			return "";

		}

		public String IdPattern() {

			return "";

		}

		public String IdOriginalDbColumnName() {

			return "Id";

		}

		public boolean IsDeleted;

		public boolean getIsDeleted() {
			return this.IsDeleted;
		}

		public Boolean IsDeletedIsNullable() {
			return false;
		}

		public Boolean IsDeletedIsKey() {
			return false;
		}

		public Integer IsDeletedLength() {
			return null;
		}

		public Integer IsDeletedPrecision() {
			return null;
		}

		public String IsDeletedDefault() {

			return null;

		}

		public String IsDeletedComment() {

			return "";

		}

		public String IsDeletedPattern() {

			return "";

		}

		public String IsDeletedOriginalDbColumnName() {

			return "IsDeleted";

		}

		public String MasterRecordId;

		public String getMasterRecordId() {
			return this.MasterRecordId;
		}

		public Boolean MasterRecordIdIsNullable() {
			return false;
		}

		public Boolean MasterRecordIdIsKey() {
			return false;
		}

		public Integer MasterRecordIdLength() {
			return 18;
		}

		public Integer MasterRecordIdPrecision() {
			return null;
		}

		public String MasterRecordIdDefault() {

			return null;

		}

		public String MasterRecordIdComment() {

			return "";

		}

		public String MasterRecordIdPattern() {

			return "";

		}

		public String MasterRecordIdOriginalDbColumnName() {

			return "MasterRecordId";

		}

		public String Name;

		public String getName() {
			return this.Name;
		}

		public Boolean NameIsNullable() {
			return true;
		}

		public Boolean NameIsKey() {
			return false;
		}

		public Integer NameLength() {
			return 255;
		}

		public Integer NamePrecision() {
			return null;
		}

		public String NameDefault() {

			return null;

		}

		public String NameComment() {

			return "";

		}

		public String NamePattern() {

			return "";

		}

		public String NameOriginalDbColumnName() {

			return "Name";

		}

		public String Type;

		public String getType() {
			return this.Type;
		}

		public Boolean TypeIsNullable() {
			return false;
		}

		public Boolean TypeIsKey() {
			return false;
		}

		public Integer TypeLength() {
			return 255;
		}

		public Integer TypePrecision() {
			return null;
		}

		public String TypeDefault() {

			return null;

		}

		public String TypeComment() {

			return "";

		}

		public String TypePattern() {

			return "";

		}

		public String TypeOriginalDbColumnName() {

			return "Type";

		}

		public String ParentId;

		public String getParentId() {
			return this.ParentId;
		}

		public Boolean ParentIdIsNullable() {
			return false;
		}

		public Boolean ParentIdIsKey() {
			return false;
		}

		public Integer ParentIdLength() {
			return 18;
		}

		public Integer ParentIdPrecision() {
			return null;
		}

		public String ParentIdDefault() {

			return null;

		}

		public String ParentIdComment() {

			return "";

		}

		public String ParentIdPattern() {

			return "";

		}

		public String ParentIdOriginalDbColumnName() {

			return "ParentId";

		}

		public String BillingStreet;

		public String getBillingStreet() {
			return this.BillingStreet;
		}

		public Boolean BillingStreetIsNullable() {
			return false;
		}

		public Boolean BillingStreetIsKey() {
			return false;
		}

		public Integer BillingStreetLength() {
			return 255;
		}

		public Integer BillingStreetPrecision() {
			return null;
		}

		public String BillingStreetDefault() {

			return null;

		}

		public String BillingStreetComment() {

			return "";

		}

		public String BillingStreetPattern() {

			return "";

		}

		public String BillingStreetOriginalDbColumnName() {

			return "BillingStreet";

		}

		public String BillingCity;

		public String getBillingCity() {
			return this.BillingCity;
		}

		public Boolean BillingCityIsNullable() {
			return false;
		}

		public Boolean BillingCityIsKey() {
			return false;
		}

		public Integer BillingCityLength() {
			return 40;
		}

		public Integer BillingCityPrecision() {
			return null;
		}

		public String BillingCityDefault() {

			return null;

		}

		public String BillingCityComment() {

			return "";

		}

		public String BillingCityPattern() {

			return "";

		}

		public String BillingCityOriginalDbColumnName() {

			return "BillingCity";

		}

		public String BillingState;

		public String getBillingState() {
			return this.BillingState;
		}

		public Boolean BillingStateIsNullable() {
			return false;
		}

		public Boolean BillingStateIsKey() {
			return false;
		}

		public Integer BillingStateLength() {
			return 80;
		}

		public Integer BillingStatePrecision() {
			return null;
		}

		public String BillingStateDefault() {

			return null;

		}

		public String BillingStateComment() {

			return "";

		}

		public String BillingStatePattern() {

			return "";

		}

		public String BillingStateOriginalDbColumnName() {

			return "BillingState";

		}

		public String BillingPostalCode;

		public String getBillingPostalCode() {
			return this.BillingPostalCode;
		}

		public Boolean BillingPostalCodeIsNullable() {
			return false;
		}

		public Boolean BillingPostalCodeIsKey() {
			return false;
		}

		public Integer BillingPostalCodeLength() {
			return 20;
		}

		public Integer BillingPostalCodePrecision() {
			return null;
		}

		public String BillingPostalCodeDefault() {

			return null;

		}

		public String BillingPostalCodeComment() {

			return "";

		}

		public String BillingPostalCodePattern() {

			return "";

		}

		public String BillingPostalCodeOriginalDbColumnName() {

			return "BillingPostalCode";

		}

		public String BillingCountry;

		public String getBillingCountry() {
			return this.BillingCountry;
		}

		public Boolean BillingCountryIsNullable() {
			return false;
		}

		public Boolean BillingCountryIsKey() {
			return false;
		}

		public Integer BillingCountryLength() {
			return 80;
		}

		public Integer BillingCountryPrecision() {
			return null;
		}

		public String BillingCountryDefault() {

			return null;

		}

		public String BillingCountryComment() {

			return "";

		}

		public String BillingCountryPattern() {

			return "";

		}

		public String BillingCountryOriginalDbColumnName() {

			return "BillingCountry";

		}

		public double BillingLatitude;

		public double getBillingLatitude() {
			return this.BillingLatitude;
		}

		public Boolean BillingLatitudeIsNullable() {
			return false;
		}

		public Boolean BillingLatitudeIsKey() {
			return false;
		}

		public Integer BillingLatitudeLength() {
			return 18;
		}

		public Integer BillingLatitudePrecision() {
			return 15;
		}

		public String BillingLatitudeDefault() {

			return null;

		}

		public String BillingLatitudeComment() {

			return "";

		}

		public String BillingLatitudePattern() {

			return "";

		}

		public String BillingLatitudeOriginalDbColumnName() {

			return "BillingLatitude";

		}

		public double BillingLongitude;

		public double getBillingLongitude() {
			return this.BillingLongitude;
		}

		public Boolean BillingLongitudeIsNullable() {
			return false;
		}

		public Boolean BillingLongitudeIsKey() {
			return false;
		}

		public Integer BillingLongitudeLength() {
			return 18;
		}

		public Integer BillingLongitudePrecision() {
			return 15;
		}

		public String BillingLongitudeDefault() {

			return null;

		}

		public String BillingLongitudeComment() {

			return "";

		}

		public String BillingLongitudePattern() {

			return "";

		}

		public String BillingLongitudeOriginalDbColumnName() {

			return "BillingLongitude";

		}

		public String BillingGeocodeAccuracy;

		public String getBillingGeocodeAccuracy() {
			return this.BillingGeocodeAccuracy;
		}

		public Boolean BillingGeocodeAccuracyIsNullable() {
			return false;
		}

		public Boolean BillingGeocodeAccuracyIsKey() {
			return false;
		}

		public Integer BillingGeocodeAccuracyLength() {
			return 40;
		}

		public Integer BillingGeocodeAccuracyPrecision() {
			return null;
		}

		public String BillingGeocodeAccuracyDefault() {

			return null;

		}

		public String BillingGeocodeAccuracyComment() {

			return "";

		}

		public String BillingGeocodeAccuracyPattern() {

			return "";

		}

		public String BillingGeocodeAccuracyOriginalDbColumnName() {

			return "BillingGeocodeAccuracy";

		}

		public String BillingAddress;

		public String getBillingAddress() {
			return this.BillingAddress;
		}

		public Boolean BillingAddressIsNullable() {
			return false;
		}

		public Boolean BillingAddressIsKey() {
			return false;
		}

		public Integer BillingAddressLength() {
			return null;
		}

		public Integer BillingAddressPrecision() {
			return null;
		}

		public String BillingAddressDefault() {

			return null;

		}

		public String BillingAddressComment() {

			return "";

		}

		public String BillingAddressPattern() {

			return "";

		}

		public String BillingAddressOriginalDbColumnName() {

			return "BillingAddress";

		}

		public String ShippingStreet;

		public String getShippingStreet() {
			return this.ShippingStreet;
		}

		public Boolean ShippingStreetIsNullable() {
			return false;
		}

		public Boolean ShippingStreetIsKey() {
			return false;
		}

		public Integer ShippingStreetLength() {
			return 255;
		}

		public Integer ShippingStreetPrecision() {
			return null;
		}

		public String ShippingStreetDefault() {

			return null;

		}

		public String ShippingStreetComment() {

			return "";

		}

		public String ShippingStreetPattern() {

			return "";

		}

		public String ShippingStreetOriginalDbColumnName() {

			return "ShippingStreet";

		}

		public String ShippingCity;

		public String getShippingCity() {
			return this.ShippingCity;
		}

		public Boolean ShippingCityIsNullable() {
			return false;
		}

		public Boolean ShippingCityIsKey() {
			return false;
		}

		public Integer ShippingCityLength() {
			return 40;
		}

		public Integer ShippingCityPrecision() {
			return null;
		}

		public String ShippingCityDefault() {

			return null;

		}

		public String ShippingCityComment() {

			return "";

		}

		public String ShippingCityPattern() {

			return "";

		}

		public String ShippingCityOriginalDbColumnName() {

			return "ShippingCity";

		}

		public String ShippingState;

		public String getShippingState() {
			return this.ShippingState;
		}

		public Boolean ShippingStateIsNullable() {
			return false;
		}

		public Boolean ShippingStateIsKey() {
			return false;
		}

		public Integer ShippingStateLength() {
			return 80;
		}

		public Integer ShippingStatePrecision() {
			return null;
		}

		public String ShippingStateDefault() {

			return null;

		}

		public String ShippingStateComment() {

			return "";

		}

		public String ShippingStatePattern() {

			return "";

		}

		public String ShippingStateOriginalDbColumnName() {

			return "ShippingState";

		}

		public String ShippingPostalCode;

		public String getShippingPostalCode() {
			return this.ShippingPostalCode;
		}

		public Boolean ShippingPostalCodeIsNullable() {
			return false;
		}

		public Boolean ShippingPostalCodeIsKey() {
			return false;
		}

		public Integer ShippingPostalCodeLength() {
			return 20;
		}

		public Integer ShippingPostalCodePrecision() {
			return null;
		}

		public String ShippingPostalCodeDefault() {

			return null;

		}

		public String ShippingPostalCodeComment() {

			return "";

		}

		public String ShippingPostalCodePattern() {

			return "";

		}

		public String ShippingPostalCodeOriginalDbColumnName() {

			return "ShippingPostalCode";

		}

		public String ShippingCountry;

		public String getShippingCountry() {
			return this.ShippingCountry;
		}

		public Boolean ShippingCountryIsNullable() {
			return false;
		}

		public Boolean ShippingCountryIsKey() {
			return false;
		}

		public Integer ShippingCountryLength() {
			return 80;
		}

		public Integer ShippingCountryPrecision() {
			return null;
		}

		public String ShippingCountryDefault() {

			return null;

		}

		public String ShippingCountryComment() {

			return "";

		}

		public String ShippingCountryPattern() {

			return "";

		}

		public String ShippingCountryOriginalDbColumnName() {

			return "ShippingCountry";

		}

		public double ShippingLatitude;

		public double getShippingLatitude() {
			return this.ShippingLatitude;
		}

		public Boolean ShippingLatitudeIsNullable() {
			return false;
		}

		public Boolean ShippingLatitudeIsKey() {
			return false;
		}

		public Integer ShippingLatitudeLength() {
			return 18;
		}

		public Integer ShippingLatitudePrecision() {
			return 15;
		}

		public String ShippingLatitudeDefault() {

			return null;

		}

		public String ShippingLatitudeComment() {

			return "";

		}

		public String ShippingLatitudePattern() {

			return "";

		}

		public String ShippingLatitudeOriginalDbColumnName() {

			return "ShippingLatitude";

		}

		public double ShippingLongitude;

		public double getShippingLongitude() {
			return this.ShippingLongitude;
		}

		public Boolean ShippingLongitudeIsNullable() {
			return false;
		}

		public Boolean ShippingLongitudeIsKey() {
			return false;
		}

		public Integer ShippingLongitudeLength() {
			return 18;
		}

		public Integer ShippingLongitudePrecision() {
			return 15;
		}

		public String ShippingLongitudeDefault() {

			return null;

		}

		public String ShippingLongitudeComment() {

			return "";

		}

		public String ShippingLongitudePattern() {

			return "";

		}

		public String ShippingLongitudeOriginalDbColumnName() {

			return "ShippingLongitude";

		}

		public String ShippingGeocodeAccuracy;

		public String getShippingGeocodeAccuracy() {
			return this.ShippingGeocodeAccuracy;
		}

		public Boolean ShippingGeocodeAccuracyIsNullable() {
			return false;
		}

		public Boolean ShippingGeocodeAccuracyIsKey() {
			return false;
		}

		public Integer ShippingGeocodeAccuracyLength() {
			return 40;
		}

		public Integer ShippingGeocodeAccuracyPrecision() {
			return null;
		}

		public String ShippingGeocodeAccuracyDefault() {

			return null;

		}

		public String ShippingGeocodeAccuracyComment() {

			return "";

		}

		public String ShippingGeocodeAccuracyPattern() {

			return "";

		}

		public String ShippingGeocodeAccuracyOriginalDbColumnName() {

			return "ShippingGeocodeAccuracy";

		}

		public String ShippingAddress;

		public String getShippingAddress() {
			return this.ShippingAddress;
		}

		public Boolean ShippingAddressIsNullable() {
			return false;
		}

		public Boolean ShippingAddressIsKey() {
			return false;
		}

		public Integer ShippingAddressLength() {
			return null;
		}

		public Integer ShippingAddressPrecision() {
			return null;
		}

		public String ShippingAddressDefault() {

			return null;

		}

		public String ShippingAddressComment() {

			return "";

		}

		public String ShippingAddressPattern() {

			return "";

		}

		public String ShippingAddressOriginalDbColumnName() {

			return "ShippingAddress";

		}

		public String Phone;

		public String getPhone() {
			return this.Phone;
		}

		public Boolean PhoneIsNullable() {
			return false;
		}

		public Boolean PhoneIsKey() {
			return false;
		}

		public Integer PhoneLength() {
			return 40;
		}

		public Integer PhonePrecision() {
			return null;
		}

		public String PhoneDefault() {

			return null;

		}

		public String PhoneComment() {

			return "";

		}

		public String PhonePattern() {

			return "";

		}

		public String PhoneOriginalDbColumnName() {

			return "Phone";

		}

		public String Fax;

		public String getFax() {
			return this.Fax;
		}

		public Boolean FaxIsNullable() {
			return false;
		}

		public Boolean FaxIsKey() {
			return false;
		}

		public Integer FaxLength() {
			return 40;
		}

		public Integer FaxPrecision() {
			return null;
		}

		public String FaxDefault() {

			return null;

		}

		public String FaxComment() {

			return "";

		}

		public String FaxPattern() {

			return "";

		}

		public String FaxOriginalDbColumnName() {

			return "Fax";

		}

		public String AccountNumber;

		public String getAccountNumber() {
			return this.AccountNumber;
		}

		public Boolean AccountNumberIsNullable() {
			return false;
		}

		public Boolean AccountNumberIsKey() {
			return false;
		}

		public Integer AccountNumberLength() {
			return 40;
		}

		public Integer AccountNumberPrecision() {
			return null;
		}

		public String AccountNumberDefault() {

			return null;

		}

		public String AccountNumberComment() {

			return "";

		}

		public String AccountNumberPattern() {

			return "";

		}

		public String AccountNumberOriginalDbColumnName() {

			return "AccountNumber";

		}

		public String Website;

		public String getWebsite() {
			return this.Website;
		}

		public Boolean WebsiteIsNullable() {
			return false;
		}

		public Boolean WebsiteIsKey() {
			return false;
		}

		public Integer WebsiteLength() {
			return 255;
		}

		public Integer WebsitePrecision() {
			return null;
		}

		public String WebsiteDefault() {

			return null;

		}

		public String WebsiteComment() {

			return "";

		}

		public String WebsitePattern() {

			return "";

		}

		public String WebsiteOriginalDbColumnName() {

			return "Website";

		}

		public String PhotoUrl;

		public String getPhotoUrl() {
			return this.PhotoUrl;
		}

		public Boolean PhotoUrlIsNullable() {
			return false;
		}

		public Boolean PhotoUrlIsKey() {
			return false;
		}

		public Integer PhotoUrlLength() {
			return 255;
		}

		public Integer PhotoUrlPrecision() {
			return null;
		}

		public String PhotoUrlDefault() {

			return null;

		}

		public String PhotoUrlComment() {

			return "";

		}

		public String PhotoUrlPattern() {

			return "";

		}

		public String PhotoUrlOriginalDbColumnName() {

			return "PhotoUrl";

		}

		public String Sic;

		public String getSic() {
			return this.Sic;
		}

		public Boolean SicIsNullable() {
			return false;
		}

		public Boolean SicIsKey() {
			return false;
		}

		public Integer SicLength() {
			return 20;
		}

		public Integer SicPrecision() {
			return null;
		}

		public String SicDefault() {

			return null;

		}

		public String SicComment() {

			return "";

		}

		public String SicPattern() {

			return "";

		}

		public String SicOriginalDbColumnName() {

			return "Sic";

		}

		public String Industry;

		public String getIndustry() {
			return this.Industry;
		}

		public Boolean IndustryIsNullable() {
			return false;
		}

		public Boolean IndustryIsKey() {
			return false;
		}

		public Integer IndustryLength() {
			return 255;
		}

		public Integer IndustryPrecision() {
			return null;
		}

		public String IndustryDefault() {

			return null;

		}

		public String IndustryComment() {

			return "";

		}

		public String IndustryPattern() {

			return "";

		}

		public String IndustryOriginalDbColumnName() {

			return "Industry";

		}

		public BigDecimal AnnualRevenue;

		public BigDecimal getAnnualRevenue() {
			return this.AnnualRevenue;
		}

		public Boolean AnnualRevenueIsNullable() {
			return false;
		}

		public Boolean AnnualRevenueIsKey() {
			return false;
		}

		public Integer AnnualRevenueLength() {
			return 18;
		}

		public Integer AnnualRevenuePrecision() {
			return null;
		}

		public String AnnualRevenueDefault() {

			return null;

		}

		public String AnnualRevenueComment() {

			return "";

		}

		public String AnnualRevenuePattern() {

			return "";

		}

		public String AnnualRevenueOriginalDbColumnName() {

			return "AnnualRevenue";

		}

		public int NumberOfEmployees;

		public int getNumberOfEmployees() {
			return this.NumberOfEmployees;
		}

		public Boolean NumberOfEmployeesIsNullable() {
			return false;
		}

		public Boolean NumberOfEmployeesIsKey() {
			return false;
		}

		public Integer NumberOfEmployeesLength() {
			return null;
		}

		public Integer NumberOfEmployeesPrecision() {
			return null;
		}

		public String NumberOfEmployeesDefault() {

			return null;

		}

		public String NumberOfEmployeesComment() {

			return "";

		}

		public String NumberOfEmployeesPattern() {

			return "";

		}

		public String NumberOfEmployeesOriginalDbColumnName() {

			return "NumberOfEmployees";

		}

		public String Ownership;

		public String getOwnership() {
			return this.Ownership;
		}

		public Boolean OwnershipIsNullable() {
			return false;
		}

		public Boolean OwnershipIsKey() {
			return false;
		}

		public Integer OwnershipLength() {
			return 255;
		}

		public Integer OwnershipPrecision() {
			return null;
		}

		public String OwnershipDefault() {

			return null;

		}

		public String OwnershipComment() {

			return "";

		}

		public String OwnershipPattern() {

			return "";

		}

		public String OwnershipOriginalDbColumnName() {

			return "Ownership";

		}

		public String TickerSymbol;

		public String getTickerSymbol() {
			return this.TickerSymbol;
		}

		public Boolean TickerSymbolIsNullable() {
			return false;
		}

		public Boolean TickerSymbolIsKey() {
			return false;
		}

		public Integer TickerSymbolLength() {
			return 20;
		}

		public Integer TickerSymbolPrecision() {
			return null;
		}

		public String TickerSymbolDefault() {

			return null;

		}

		public String TickerSymbolComment() {

			return "";

		}

		public String TickerSymbolPattern() {

			return "";

		}

		public String TickerSymbolOriginalDbColumnName() {

			return "TickerSymbol";

		}

		public String Description;

		public String getDescription() {
			return this.Description;
		}

		public Boolean DescriptionIsNullable() {
			return false;
		}

		public Boolean DescriptionIsKey() {
			return false;
		}

		public Integer DescriptionLength() {
			return 32000;
		}

		public Integer DescriptionPrecision() {
			return null;
		}

		public String DescriptionDefault() {

			return null;

		}

		public String DescriptionComment() {

			return "";

		}

		public String DescriptionPattern() {

			return "";

		}

		public String DescriptionOriginalDbColumnName() {

			return "Description";

		}

		public String Rating;

		public String getRating() {
			return this.Rating;
		}

		public Boolean RatingIsNullable() {
			return false;
		}

		public Boolean RatingIsKey() {
			return false;
		}

		public Integer RatingLength() {
			return 255;
		}

		public Integer RatingPrecision() {
			return null;
		}

		public String RatingDefault() {

			return null;

		}

		public String RatingComment() {

			return "";

		}

		public String RatingPattern() {

			return "";

		}

		public String RatingOriginalDbColumnName() {

			return "Rating";

		}

		public String Site;

		public String getSite() {
			return this.Site;
		}

		public Boolean SiteIsNullable() {
			return false;
		}

		public Boolean SiteIsKey() {
			return false;
		}

		public Integer SiteLength() {
			return 80;
		}

		public Integer SitePrecision() {
			return null;
		}

		public String SiteDefault() {

			return null;

		}

		public String SiteComment() {

			return "";

		}

		public String SitePattern() {

			return "";

		}

		public String SiteOriginalDbColumnName() {

			return "Site";

		}

		public String OwnerId;

		public String getOwnerId() {
			return this.OwnerId;
		}

		public Boolean OwnerIdIsNullable() {
			return false;
		}

		public Boolean OwnerIdIsKey() {
			return false;
		}

		public Integer OwnerIdLength() {
			return 18;
		}

		public Integer OwnerIdPrecision() {
			return null;
		}

		public String OwnerIdDefault() {

			return null;

		}

		public String OwnerIdComment() {

			return "";

		}

		public String OwnerIdPattern() {

			return "";

		}

		public String OwnerIdOriginalDbColumnName() {

			return "OwnerId";

		}

		public java.util.Date CreatedDate;

		public java.util.Date getCreatedDate() {
			return this.CreatedDate;
		}

		public Boolean CreatedDateIsNullable() {
			return false;
		}

		public Boolean CreatedDateIsKey() {
			return false;
		}

		public Integer CreatedDateLength() {
			return null;
		}

		public Integer CreatedDatePrecision() {
			return null;
		}

		public String CreatedDateDefault() {

			return null;

		}

		public String CreatedDateComment() {

			return "";

		}

		public String CreatedDatePattern() {

			return "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

		}

		public String CreatedDateOriginalDbColumnName() {

			return "CreatedDate";

		}

		public String CreatedById;

		public String getCreatedById() {
			return this.CreatedById;
		}

		public Boolean CreatedByIdIsNullable() {
			return false;
		}

		public Boolean CreatedByIdIsKey() {
			return false;
		}

		public Integer CreatedByIdLength() {
			return 18;
		}

		public Integer CreatedByIdPrecision() {
			return null;
		}

		public String CreatedByIdDefault() {

			return null;

		}

		public String CreatedByIdComment() {

			return "";

		}

		public String CreatedByIdPattern() {

			return "";

		}

		public String CreatedByIdOriginalDbColumnName() {

			return "CreatedById";

		}

		public java.util.Date LastModifiedDate;

		public java.util.Date getLastModifiedDate() {
			return this.LastModifiedDate;
		}

		public Boolean LastModifiedDateIsNullable() {
			return false;
		}

		public Boolean LastModifiedDateIsKey() {
			return false;
		}

		public Integer LastModifiedDateLength() {
			return null;
		}

		public Integer LastModifiedDatePrecision() {
			return null;
		}

		public String LastModifiedDateDefault() {

			return null;

		}

		public String LastModifiedDateComment() {

			return "";

		}

		public String LastModifiedDatePattern() {

			return "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

		}

		public String LastModifiedDateOriginalDbColumnName() {

			return "LastModifiedDate";

		}

		public String LastModifiedById;

		public String getLastModifiedById() {
			return this.LastModifiedById;
		}

		public Boolean LastModifiedByIdIsNullable() {
			return false;
		}

		public Boolean LastModifiedByIdIsKey() {
			return false;
		}

		public Integer LastModifiedByIdLength() {
			return 18;
		}

		public Integer LastModifiedByIdPrecision() {
			return null;
		}

		public String LastModifiedByIdDefault() {

			return null;

		}

		public String LastModifiedByIdComment() {

			return "";

		}

		public String LastModifiedByIdPattern() {

			return "";

		}

		public String LastModifiedByIdOriginalDbColumnName() {

			return "LastModifiedById";

		}

		public java.util.Date SystemModstamp;

		public java.util.Date getSystemModstamp() {
			return this.SystemModstamp;
		}

		public Boolean SystemModstampIsNullable() {
			return false;
		}

		public Boolean SystemModstampIsKey() {
			return false;
		}

		public Integer SystemModstampLength() {
			return null;
		}

		public Integer SystemModstampPrecision() {
			return null;
		}

		public String SystemModstampDefault() {

			return null;

		}

		public String SystemModstampComment() {

			return "";

		}

		public String SystemModstampPattern() {

			return "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

		}

		public String SystemModstampOriginalDbColumnName() {

			return "SystemModstamp";

		}

		public java.util.Date LastActivityDate;

		public java.util.Date getLastActivityDate() {
			return this.LastActivityDate;
		}

		public Boolean LastActivityDateIsNullable() {
			return false;
		}

		public Boolean LastActivityDateIsKey() {
			return false;
		}

		public Integer LastActivityDateLength() {
			return null;
		}

		public Integer LastActivityDatePrecision() {
			return null;
		}

		public String LastActivityDateDefault() {

			return null;

		}

		public String LastActivityDateComment() {

			return "";

		}

		public String LastActivityDatePattern() {

			return "yyyy-MM-dd";

		}

		public String LastActivityDateOriginalDbColumnName() {

			return "LastActivityDate";

		}

		public java.util.Date LastViewedDate;

		public java.util.Date getLastViewedDate() {
			return this.LastViewedDate;
		}

		public Boolean LastViewedDateIsNullable() {
			return false;
		}

		public Boolean LastViewedDateIsKey() {
			return false;
		}

		public Integer LastViewedDateLength() {
			return null;
		}

		public Integer LastViewedDatePrecision() {
			return null;
		}

		public String LastViewedDateDefault() {

			return null;

		}

		public String LastViewedDateComment() {

			return "";

		}

		public String LastViewedDatePattern() {

			return "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

		}

		public String LastViewedDateOriginalDbColumnName() {

			return "LastViewedDate";

		}

		public java.util.Date LastReferencedDate;

		public java.util.Date getLastReferencedDate() {
			return this.LastReferencedDate;
		}

		public Boolean LastReferencedDateIsNullable() {
			return false;
		}

		public Boolean LastReferencedDateIsKey() {
			return false;
		}

		public Integer LastReferencedDateLength() {
			return null;
		}

		public Integer LastReferencedDatePrecision() {
			return null;
		}

		public String LastReferencedDateDefault() {

			return null;

		}

		public String LastReferencedDateComment() {

			return "";

		}

		public String LastReferencedDatePattern() {

			return "yyyy-MM-dd'T'HH:mm:ss'.000Z'";

		}

		public String LastReferencedDateOriginalDbColumnName() {

			return "LastReferencedDate";

		}

		public String Jigsaw;

		public String getJigsaw() {
			return this.Jigsaw;
		}

		public Boolean JigsawIsNullable() {
			return false;
		}

		public Boolean JigsawIsKey() {
			return false;
		}

		public Integer JigsawLength() {
			return 20;
		}

		public Integer JigsawPrecision() {
			return null;
		}

		public String JigsawDefault() {

			return null;

		}

		public String JigsawComment() {

			return "";

		}

		public String JigsawPattern() {

			return "";

		}

		public String JigsawOriginalDbColumnName() {

			return "Jigsaw";

		}

		public String JigsawCompanyId;

		public String getJigsawCompanyId() {
			return this.JigsawCompanyId;
		}

		public Boolean JigsawCompanyIdIsNullable() {
			return false;
		}

		public Boolean JigsawCompanyIdIsKey() {
			return false;
		}

		public Integer JigsawCompanyIdLength() {
			return 20;
		}

		public Integer JigsawCompanyIdPrecision() {
			return null;
		}

		public String JigsawCompanyIdDefault() {

			return null;

		}

		public String JigsawCompanyIdComment() {

			return "";

		}

		public String JigsawCompanyIdPattern() {

			return "";

		}

		public String JigsawCompanyIdOriginalDbColumnName() {

			return "JigsawCompanyId";

		}

		public String CleanStatus;

		public String getCleanStatus() {
			return this.CleanStatus;
		}

		public Boolean CleanStatusIsNullable() {
			return false;
		}

		public Boolean CleanStatusIsKey() {
			return false;
		}

		public Integer CleanStatusLength() {
			return 40;
		}

		public Integer CleanStatusPrecision() {
			return null;
		}

		public String CleanStatusDefault() {

			return null;

		}

		public String CleanStatusComment() {

			return "";

		}

		public String CleanStatusPattern() {

			return "";

		}

		public String CleanStatusOriginalDbColumnName() {

			return "CleanStatus";

		}

		public String AccountSource;

		public String getAccountSource() {
			return this.AccountSource;
		}

		public Boolean AccountSourceIsNullable() {
			return false;
		}

		public Boolean AccountSourceIsKey() {
			return false;
		}

		public Integer AccountSourceLength() {
			return 255;
		}

		public Integer AccountSourcePrecision() {
			return null;
		}

		public String AccountSourceDefault() {

			return null;

		}

		public String AccountSourceComment() {

			return "";

		}

		public String AccountSourcePattern() {

			return "";

		}

		public String AccountSourceOriginalDbColumnName() {

			return "AccountSource";

		}

		public String DunsNumber;

		public String getDunsNumber() {
			return this.DunsNumber;
		}

		public Boolean DunsNumberIsNullable() {
			return false;
		}

		public Boolean DunsNumberIsKey() {
			return false;
		}

		public Integer DunsNumberLength() {
			return 9;
		}

		public Integer DunsNumberPrecision() {
			return null;
		}

		public String DunsNumberDefault() {

			return null;

		}

		public String DunsNumberComment() {

			return "";

		}

		public String DunsNumberPattern() {

			return "";

		}

		public String DunsNumberOriginalDbColumnName() {

			return "DunsNumber";

		}

		public String Tradestyle;

		public String getTradestyle() {
			return this.Tradestyle;
		}

		public Boolean TradestyleIsNullable() {
			return false;
		}

		public Boolean TradestyleIsKey() {
			return false;
		}

		public Integer TradestyleLength() {
			return 255;
		}

		public Integer TradestylePrecision() {
			return null;
		}

		public String TradestyleDefault() {

			return null;

		}

		public String TradestyleComment() {

			return "";

		}

		public String TradestylePattern() {

			return "";

		}

		public String TradestyleOriginalDbColumnName() {

			return "Tradestyle";

		}

		public String NaicsCode;

		public String getNaicsCode() {
			return this.NaicsCode;
		}

		public Boolean NaicsCodeIsNullable() {
			return false;
		}

		public Boolean NaicsCodeIsKey() {
			return false;
		}

		public Integer NaicsCodeLength() {
			return 8;
		}

		public Integer NaicsCodePrecision() {
			return null;
		}

		public String NaicsCodeDefault() {

			return null;

		}

		public String NaicsCodeComment() {

			return "";

		}

		public String NaicsCodePattern() {

			return "";

		}

		public String NaicsCodeOriginalDbColumnName() {

			return "NaicsCode";

		}

		public String NaicsDesc;

		public String getNaicsDesc() {
			return this.NaicsDesc;
		}

		public Boolean NaicsDescIsNullable() {
			return false;
		}

		public Boolean NaicsDescIsKey() {
			return false;
		}

		public Integer NaicsDescLength() {
			return 120;
		}

		public Integer NaicsDescPrecision() {
			return null;
		}

		public String NaicsDescDefault() {

			return null;

		}

		public String NaicsDescComment() {

			return "";

		}

		public String NaicsDescPattern() {

			return "";

		}

		public String NaicsDescOriginalDbColumnName() {

			return "NaicsDesc";

		}

		public String YearStarted;

		public String getYearStarted() {
			return this.YearStarted;
		}

		public Boolean YearStartedIsNullable() {
			return false;
		}

		public Boolean YearStartedIsKey() {
			return false;
		}

		public Integer YearStartedLength() {
			return 4;
		}

		public Integer YearStartedPrecision() {
			return null;
		}

		public String YearStartedDefault() {

			return null;

		}

		public String YearStartedComment() {

			return "";

		}

		public String YearStartedPattern() {

			return "";

		}

		public String YearStartedOriginalDbColumnName() {

			return "YearStarted";

		}

		public String SicDesc;

		public String getSicDesc() {
			return this.SicDesc;
		}

		public Boolean SicDescIsNullable() {
			return false;
		}

		public Boolean SicDescIsKey() {
			return false;
		}

		public Integer SicDescLength() {
			return 80;
		}

		public Integer SicDescPrecision() {
			return null;
		}

		public String SicDescDefault() {

			return null;

		}

		public String SicDescComment() {

			return "";

		}

		public String SicDescPattern() {

			return "";

		}

		public String SicDescOriginalDbColumnName() {

			return "SicDesc";

		}

		public String DandbCompanyId;

		public String getDandbCompanyId() {
			return this.DandbCompanyId;
		}

		public Boolean DandbCompanyIdIsNullable() {
			return false;
		}

		public Boolean DandbCompanyIdIsKey() {
			return false;
		}

		public Integer DandbCompanyIdLength() {
			return 18;
		}

		public Integer DandbCompanyIdPrecision() {
			return null;
		}

		public String DandbCompanyIdDefault() {

			return null;

		}

		public String DandbCompanyIdComment() {

			return "";

		}

		public String DandbCompanyIdPattern() {

			return "";

		}

		public String DandbCompanyIdOriginalDbColumnName() {

			return "DandbCompanyId";

		}

		public String OperatingHoursId;

		public String getOperatingHoursId() {
			return this.OperatingHoursId;
		}

		public Boolean OperatingHoursIdIsNullable() {
			return false;
		}

		public Boolean OperatingHoursIdIsKey() {
			return false;
		}

		public Integer OperatingHoursIdLength() {
			return 18;
		}

		public Integer OperatingHoursIdPrecision() {
			return null;
		}

		public String OperatingHoursIdDefault() {

			return null;

		}

		public String OperatingHoursIdComment() {

			return "";

		}

		public String OperatingHoursIdPattern() {

			return "";

		}

		public String OperatingHoursIdOriginalDbColumnName() {

			return "OperatingHoursId";

		}

		public String CustomerPriority__c;

		public String getCustomerPriority__c() {
			return this.CustomerPriority__c;
		}

		public Boolean CustomerPriority__cIsNullable() {
			return false;
		}

		public Boolean CustomerPriority__cIsKey() {
			return false;
		}

		public Integer CustomerPriority__cLength() {
			return 255;
		}

		public Integer CustomerPriority__cPrecision() {
			return null;
		}

		public String CustomerPriority__cDefault() {

			return null;

		}

		public String CustomerPriority__cComment() {

			return "";

		}

		public String CustomerPriority__cPattern() {

			return "";

		}

		public String CustomerPriority__cOriginalDbColumnName() {

			return "CustomerPriority__c";

		}

		public String SLA__c;

		public String getSLA__c() {
			return this.SLA__c;
		}

		public Boolean SLA__cIsNullable() {
			return false;
		}

		public Boolean SLA__cIsKey() {
			return false;
		}

		public Integer SLA__cLength() {
			return 255;
		}

		public Integer SLA__cPrecision() {
			return null;
		}

		public String SLA__cDefault() {

			return null;

		}

		public String SLA__cComment() {

			return "";

		}

		public String SLA__cPattern() {

			return "";

		}

		public String SLA__cOriginalDbColumnName() {

			return "SLA__c";

		}

		public String Active__c;

		public String getActive__c() {
			return this.Active__c;
		}

		public Boolean Active__cIsNullable() {
			return false;
		}

		public Boolean Active__cIsKey() {
			return false;
		}

		public Integer Active__cLength() {
			return 255;
		}

		public Integer Active__cPrecision() {
			return null;
		}

		public String Active__cDefault() {

			return null;

		}

		public String Active__cComment() {

			return "";

		}

		public String Active__cPattern() {

			return "";

		}

		public String Active__cOriginalDbColumnName() {

			return "Active__c";

		}

		public double NumberofLocations__c;

		public double getNumberofLocations__c() {
			return this.NumberofLocations__c;
		}

		public Boolean NumberofLocations__cIsNullable() {
			return false;
		}

		public Boolean NumberofLocations__cIsKey() {
			return false;
		}

		public Integer NumberofLocations__cLength() {
			return 3;
		}

		public Integer NumberofLocations__cPrecision() {
			return null;
		}

		public String NumberofLocations__cDefault() {

			return null;

		}

		public String NumberofLocations__cComment() {

			return "";

		}

		public String NumberofLocations__cPattern() {

			return "";

		}

		public String NumberofLocations__cOriginalDbColumnName() {

			return "NumberofLocations__c";

		}

		public String UpsellOpportunity__c;

		public String getUpsellOpportunity__c() {
			return this.UpsellOpportunity__c;
		}

		public Boolean UpsellOpportunity__cIsNullable() {
			return false;
		}

		public Boolean UpsellOpportunity__cIsKey() {
			return false;
		}

		public Integer UpsellOpportunity__cLength() {
			return 255;
		}

		public Integer UpsellOpportunity__cPrecision() {
			return null;
		}

		public String UpsellOpportunity__cDefault() {

			return null;

		}

		public String UpsellOpportunity__cComment() {

			return "";

		}

		public String UpsellOpportunity__cPattern() {

			return "";

		}

		public String UpsellOpportunity__cOriginalDbColumnName() {

			return "UpsellOpportunity__c";

		}

		public String SLASerialNumber__c;

		public String getSLASerialNumber__c() {
			return this.SLASerialNumber__c;
		}

		public Boolean SLASerialNumber__cIsNullable() {
			return false;
		}

		public Boolean SLASerialNumber__cIsKey() {
			return false;
		}

		public Integer SLASerialNumber__cLength() {
			return 10;
		}

		public Integer SLASerialNumber__cPrecision() {
			return null;
		}

		public String SLASerialNumber__cDefault() {

			return null;

		}

		public String SLASerialNumber__cComment() {

			return "";

		}

		public String SLASerialNumber__cPattern() {

			return "";

		}

		public String SLASerialNumber__cOriginalDbColumnName() {

			return "SLASerialNumber__c";

		}

		public java.util.Date SLAExpirationDate__c;

		public java.util.Date getSLAExpirationDate__c() {
			return this.SLAExpirationDate__c;
		}

		public Boolean SLAExpirationDate__cIsNullable() {
			return false;
		}

		public Boolean SLAExpirationDate__cIsKey() {
			return false;
		}

		public Integer SLAExpirationDate__cLength() {
			return null;
		}

		public Integer SLAExpirationDate__cPrecision() {
			return null;
		}

		public String SLAExpirationDate__cDefault() {

			return null;

		}

		public String SLAExpirationDate__cComment() {

			return "";

		}

		public String SLAExpirationDate__cPattern() {

			return "yyyy-MM-dd";

		}

		public String SLAExpirationDate__cOriginalDbColumnName() {

			return "SLAExpirationDate__c";

		}

		public String Email__c;

		public String getEmail__c() {
			return this.Email__c;
		}

		public Boolean Email__cIsNullable() {
			return false;
		}

		public Boolean Email__cIsKey() {
			return false;
		}

		public Integer Email__cLength() {
			return 80;
		}

		public Integer Email__cPrecision() {
			return null;
		}

		public String Email__cDefault() {

			return null;

		}

		public String Email__cComment() {

			return "";

		}

		public String Email__cPattern() {

			return "";

		}

		public String Email__cOriginalDbColumnName() {

			return "Email__c";

		}

		public String ExternalID__c;

		public String getExternalID__c() {
			return this.ExternalID__c;
		}

		public Boolean ExternalID__cIsNullable() {
			return true;
		}

		public Boolean ExternalID__cIsKey() {
			return false;
		}

		public Integer ExternalID__cLength() {
			return 100;
		}

		public Integer ExternalID__cPrecision() {
			return null;
		}

		public String ExternalID__cDefault() {

			return null;

		}

		public String ExternalID__cComment() {

			return "";

		}

		public String ExternalID__cPattern() {

			return "";

		}

		public String ExternalID__cOriginalDbColumnName() {

			return "ExternalID__c";

		}

		public String Status__c;

		public String getStatus__c() {
			return this.Status__c;
		}

		public Boolean Status__cIsNullable() {
			return false;
		}

		public Boolean Status__cIsKey() {
			return false;
		}

		public Integer Status__cLength() {
			return 30;
		}

		public Integer Status__cPrecision() {
			return null;
		}

		public String Status__cDefault() {

			return null;

		}

		public String Status__cComment() {

			return "";

		}

		public String Status__cPattern() {

			return "";

		}

		public String Status__cOriginalDbColumnName() {

			return "Status__c";

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_SALESFORCETALEND_Salesforce.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Salesforce.length == 0) {
						commonByteArray_SALESFORCETALEND_Salesforce = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Salesforce = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_SALESFORCETALEND_Salesforce, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Salesforce, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_SALESFORCETALEND_Salesforce.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Salesforce.length == 0) {
						commonByteArray_SALESFORCETALEND_Salesforce = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Salesforce = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_SALESFORCETALEND_Salesforce, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Salesforce, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		private java.util.Date readDate(ObjectInputStream dis) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(dis.readLong());
			}
			return dateReturn;
		}

		private java.util.Date readDate(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = unmarshaller.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(unmarshaller.readLong());
			}
			return dateReturn;
		}

		private void writeDate(java.util.Date date1, ObjectOutputStream dos) throws IOException {
			if (date1 == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeLong(date1.getTime());
			}
		}

		private void writeDate(java.util.Date date1, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (date1 == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeLong(date1.getTime());
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Salesforce) {

				try {

					int length = 0;

					this.Id = readString(dis);

					this.IsDeleted = dis.readBoolean();

					this.MasterRecordId = readString(dis);

					this.Name = readString(dis);

					this.Type = readString(dis);

					this.ParentId = readString(dis);

					this.BillingStreet = readString(dis);

					this.BillingCity = readString(dis);

					this.BillingState = readString(dis);

					this.BillingPostalCode = readString(dis);

					this.BillingCountry = readString(dis);

					this.BillingLatitude = dis.readDouble();

					this.BillingLongitude = dis.readDouble();

					this.BillingGeocodeAccuracy = readString(dis);

					this.BillingAddress = readString(dis);

					this.ShippingStreet = readString(dis);

					this.ShippingCity = readString(dis);

					this.ShippingState = readString(dis);

					this.ShippingPostalCode = readString(dis);

					this.ShippingCountry = readString(dis);

					this.ShippingLatitude = dis.readDouble();

					this.ShippingLongitude = dis.readDouble();

					this.ShippingGeocodeAccuracy = readString(dis);

					this.ShippingAddress = readString(dis);

					this.Phone = readString(dis);

					this.Fax = readString(dis);

					this.AccountNumber = readString(dis);

					this.Website = readString(dis);

					this.PhotoUrl = readString(dis);

					this.Sic = readString(dis);

					this.Industry = readString(dis);

					this.AnnualRevenue = (BigDecimal) dis.readObject();

					this.NumberOfEmployees = dis.readInt();

					this.Ownership = readString(dis);

					this.TickerSymbol = readString(dis);

					this.Description = readString(dis);

					this.Rating = readString(dis);

					this.Site = readString(dis);

					this.OwnerId = readString(dis);

					this.CreatedDate = readDate(dis);

					this.CreatedById = readString(dis);

					this.LastModifiedDate = readDate(dis);

					this.LastModifiedById = readString(dis);

					this.SystemModstamp = readDate(dis);

					this.LastActivityDate = readDate(dis);

					this.LastViewedDate = readDate(dis);

					this.LastReferencedDate = readDate(dis);

					this.Jigsaw = readString(dis);

					this.JigsawCompanyId = readString(dis);

					this.CleanStatus = readString(dis);

					this.AccountSource = readString(dis);

					this.DunsNumber = readString(dis);

					this.Tradestyle = readString(dis);

					this.NaicsCode = readString(dis);

					this.NaicsDesc = readString(dis);

					this.YearStarted = readString(dis);

					this.SicDesc = readString(dis);

					this.DandbCompanyId = readString(dis);

					this.OperatingHoursId = readString(dis);

					this.CustomerPriority__c = readString(dis);

					this.SLA__c = readString(dis);

					this.Active__c = readString(dis);

					this.NumberofLocations__c = dis.readDouble();

					this.UpsellOpportunity__c = readString(dis);

					this.SLASerialNumber__c = readString(dis);

					this.SLAExpirationDate__c = readDate(dis);

					this.Email__c = readString(dis);

					this.ExternalID__c = readString(dis);

					this.Status__c = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Salesforce) {

				try {

					int length = 0;

					this.Id = readString(dis);

					this.IsDeleted = dis.readBoolean();

					this.MasterRecordId = readString(dis);

					this.Name = readString(dis);

					this.Type = readString(dis);

					this.ParentId = readString(dis);

					this.BillingStreet = readString(dis);

					this.BillingCity = readString(dis);

					this.BillingState = readString(dis);

					this.BillingPostalCode = readString(dis);

					this.BillingCountry = readString(dis);

					this.BillingLatitude = dis.readDouble();

					this.BillingLongitude = dis.readDouble();

					this.BillingGeocodeAccuracy = readString(dis);

					this.BillingAddress = readString(dis);

					this.ShippingStreet = readString(dis);

					this.ShippingCity = readString(dis);

					this.ShippingState = readString(dis);

					this.ShippingPostalCode = readString(dis);

					this.ShippingCountry = readString(dis);

					this.ShippingLatitude = dis.readDouble();

					this.ShippingLongitude = dis.readDouble();

					this.ShippingGeocodeAccuracy = readString(dis);

					this.ShippingAddress = readString(dis);

					this.Phone = readString(dis);

					this.Fax = readString(dis);

					this.AccountNumber = readString(dis);

					this.Website = readString(dis);

					this.PhotoUrl = readString(dis);

					this.Sic = readString(dis);

					this.Industry = readString(dis);

					this.AnnualRevenue = (BigDecimal) dis.readObject();

					this.NumberOfEmployees = dis.readInt();

					this.Ownership = readString(dis);

					this.TickerSymbol = readString(dis);

					this.Description = readString(dis);

					this.Rating = readString(dis);

					this.Site = readString(dis);

					this.OwnerId = readString(dis);

					this.CreatedDate = readDate(dis);

					this.CreatedById = readString(dis);

					this.LastModifiedDate = readDate(dis);

					this.LastModifiedById = readString(dis);

					this.SystemModstamp = readDate(dis);

					this.LastActivityDate = readDate(dis);

					this.LastViewedDate = readDate(dis);

					this.LastReferencedDate = readDate(dis);

					this.Jigsaw = readString(dis);

					this.JigsawCompanyId = readString(dis);

					this.CleanStatus = readString(dis);

					this.AccountSource = readString(dis);

					this.DunsNumber = readString(dis);

					this.Tradestyle = readString(dis);

					this.NaicsCode = readString(dis);

					this.NaicsDesc = readString(dis);

					this.YearStarted = readString(dis);

					this.SicDesc = readString(dis);

					this.DandbCompanyId = readString(dis);

					this.OperatingHoursId = readString(dis);

					this.CustomerPriority__c = readString(dis);

					this.SLA__c = readString(dis);

					this.Active__c = readString(dis);

					this.NumberofLocations__c = dis.readDouble();

					this.UpsellOpportunity__c = readString(dis);

					this.SLASerialNumber__c = readString(dis);

					this.SLAExpirationDate__c = readDate(dis);

					this.Email__c = readString(dis);

					this.ExternalID__c = readString(dis);

					this.Status__c = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				} catch (ClassNotFoundException eCNFE) {
					throw new RuntimeException(eCNFE);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.Id, dos);

				// boolean

				dos.writeBoolean(this.IsDeleted);

				// String

				writeString(this.MasterRecordId, dos);

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.Type, dos);

				// String

				writeString(this.ParentId, dos);

				// String

				writeString(this.BillingStreet, dos);

				// String

				writeString(this.BillingCity, dos);

				// String

				writeString(this.BillingState, dos);

				// String

				writeString(this.BillingPostalCode, dos);

				// String

				writeString(this.BillingCountry, dos);

				// double

				dos.writeDouble(this.BillingLatitude);

				// double

				dos.writeDouble(this.BillingLongitude);

				// String

				writeString(this.BillingGeocodeAccuracy, dos);

				// String

				writeString(this.BillingAddress, dos);

				// String

				writeString(this.ShippingStreet, dos);

				// String

				writeString(this.ShippingCity, dos);

				// String

				writeString(this.ShippingState, dos);

				// String

				writeString(this.ShippingPostalCode, dos);

				// String

				writeString(this.ShippingCountry, dos);

				// double

				dos.writeDouble(this.ShippingLatitude);

				// double

				dos.writeDouble(this.ShippingLongitude);

				// String

				writeString(this.ShippingGeocodeAccuracy, dos);

				// String

				writeString(this.ShippingAddress, dos);

				// String

				writeString(this.Phone, dos);

				// String

				writeString(this.Fax, dos);

				// String

				writeString(this.AccountNumber, dos);

				// String

				writeString(this.Website, dos);

				// String

				writeString(this.PhotoUrl, dos);

				// String

				writeString(this.Sic, dos);

				// String

				writeString(this.Industry, dos);

				// BigDecimal

				dos.writeObject(this.AnnualRevenue);

				// int

				dos.writeInt(this.NumberOfEmployees);

				// String

				writeString(this.Ownership, dos);

				// String

				writeString(this.TickerSymbol, dos);

				// String

				writeString(this.Description, dos);

				// String

				writeString(this.Rating, dos);

				// String

				writeString(this.Site, dos);

				// String

				writeString(this.OwnerId, dos);

				// java.util.Date

				writeDate(this.CreatedDate, dos);

				// String

				writeString(this.CreatedById, dos);

				// java.util.Date

				writeDate(this.LastModifiedDate, dos);

				// String

				writeString(this.LastModifiedById, dos);

				// java.util.Date

				writeDate(this.SystemModstamp, dos);

				// java.util.Date

				writeDate(this.LastActivityDate, dos);

				// java.util.Date

				writeDate(this.LastViewedDate, dos);

				// java.util.Date

				writeDate(this.LastReferencedDate, dos);

				// String

				writeString(this.Jigsaw, dos);

				// String

				writeString(this.JigsawCompanyId, dos);

				// String

				writeString(this.CleanStatus, dos);

				// String

				writeString(this.AccountSource, dos);

				// String

				writeString(this.DunsNumber, dos);

				// String

				writeString(this.Tradestyle, dos);

				// String

				writeString(this.NaicsCode, dos);

				// String

				writeString(this.NaicsDesc, dos);

				// String

				writeString(this.YearStarted, dos);

				// String

				writeString(this.SicDesc, dos);

				// String

				writeString(this.DandbCompanyId, dos);

				// String

				writeString(this.OperatingHoursId, dos);

				// String

				writeString(this.CustomerPriority__c, dos);

				// String

				writeString(this.SLA__c, dos);

				// String

				writeString(this.Active__c, dos);

				// double

				dos.writeDouble(this.NumberofLocations__c);

				// String

				writeString(this.UpsellOpportunity__c, dos);

				// String

				writeString(this.SLASerialNumber__c, dos);

				// java.util.Date

				writeDate(this.SLAExpirationDate__c, dos);

				// String

				writeString(this.Email__c, dos);

				// String

				writeString(this.ExternalID__c, dos);

				// String

				writeString(this.Status__c, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// String

				writeString(this.Id, dos);

				// boolean

				dos.writeBoolean(this.IsDeleted);

				// String

				writeString(this.MasterRecordId, dos);

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.Type, dos);

				// String

				writeString(this.ParentId, dos);

				// String

				writeString(this.BillingStreet, dos);

				// String

				writeString(this.BillingCity, dos);

				// String

				writeString(this.BillingState, dos);

				// String

				writeString(this.BillingPostalCode, dos);

				// String

				writeString(this.BillingCountry, dos);

				// double

				dos.writeDouble(this.BillingLatitude);

				// double

				dos.writeDouble(this.BillingLongitude);

				// String

				writeString(this.BillingGeocodeAccuracy, dos);

				// String

				writeString(this.BillingAddress, dos);

				// String

				writeString(this.ShippingStreet, dos);

				// String

				writeString(this.ShippingCity, dos);

				// String

				writeString(this.ShippingState, dos);

				// String

				writeString(this.ShippingPostalCode, dos);

				// String

				writeString(this.ShippingCountry, dos);

				// double

				dos.writeDouble(this.ShippingLatitude);

				// double

				dos.writeDouble(this.ShippingLongitude);

				// String

				writeString(this.ShippingGeocodeAccuracy, dos);

				// String

				writeString(this.ShippingAddress, dos);

				// String

				writeString(this.Phone, dos);

				// String

				writeString(this.Fax, dos);

				// String

				writeString(this.AccountNumber, dos);

				// String

				writeString(this.Website, dos);

				// String

				writeString(this.PhotoUrl, dos);

				// String

				writeString(this.Sic, dos);

				// String

				writeString(this.Industry, dos);

				// BigDecimal

				dos.clearInstanceCache();
				dos.writeObject(this.AnnualRevenue);

				// int

				dos.writeInt(this.NumberOfEmployees);

				// String

				writeString(this.Ownership, dos);

				// String

				writeString(this.TickerSymbol, dos);

				// String

				writeString(this.Description, dos);

				// String

				writeString(this.Rating, dos);

				// String

				writeString(this.Site, dos);

				// String

				writeString(this.OwnerId, dos);

				// java.util.Date

				writeDate(this.CreatedDate, dos);

				// String

				writeString(this.CreatedById, dos);

				// java.util.Date

				writeDate(this.LastModifiedDate, dos);

				// String

				writeString(this.LastModifiedById, dos);

				// java.util.Date

				writeDate(this.SystemModstamp, dos);

				// java.util.Date

				writeDate(this.LastActivityDate, dos);

				// java.util.Date

				writeDate(this.LastViewedDate, dos);

				// java.util.Date

				writeDate(this.LastReferencedDate, dos);

				// String

				writeString(this.Jigsaw, dos);

				// String

				writeString(this.JigsawCompanyId, dos);

				// String

				writeString(this.CleanStatus, dos);

				// String

				writeString(this.AccountSource, dos);

				// String

				writeString(this.DunsNumber, dos);

				// String

				writeString(this.Tradestyle, dos);

				// String

				writeString(this.NaicsCode, dos);

				// String

				writeString(this.NaicsDesc, dos);

				// String

				writeString(this.YearStarted, dos);

				// String

				writeString(this.SicDesc, dos);

				// String

				writeString(this.DandbCompanyId, dos);

				// String

				writeString(this.OperatingHoursId, dos);

				// String

				writeString(this.CustomerPriority__c, dos);

				// String

				writeString(this.SLA__c, dos);

				// String

				writeString(this.Active__c, dos);

				// double

				dos.writeDouble(this.NumberofLocations__c);

				// String

				writeString(this.UpsellOpportunity__c, dos);

				// String

				writeString(this.SLASerialNumber__c, dos);

				// java.util.Date

				writeDate(this.SLAExpirationDate__c, dos);

				// String

				writeString(this.Email__c, dos);

				// String

				writeString(this.ExternalID__c, dos);

				// String

				writeString(this.Status__c, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("Id=" + Id);
			sb.append(",IsDeleted=" + String.valueOf(IsDeleted));
			sb.append(",MasterRecordId=" + MasterRecordId);
			sb.append(",Name=" + Name);
			sb.append(",Type=" + Type);
			sb.append(",ParentId=" + ParentId);
			sb.append(",BillingStreet=" + BillingStreet);
			sb.append(",BillingCity=" + BillingCity);
			sb.append(",BillingState=" + BillingState);
			sb.append(",BillingPostalCode=" + BillingPostalCode);
			sb.append(",BillingCountry=" + BillingCountry);
			sb.append(",BillingLatitude=" + String.valueOf(BillingLatitude));
			sb.append(",BillingLongitude=" + String.valueOf(BillingLongitude));
			sb.append(",BillingGeocodeAccuracy=" + BillingGeocodeAccuracy);
			sb.append(",BillingAddress=" + BillingAddress);
			sb.append(",ShippingStreet=" + ShippingStreet);
			sb.append(",ShippingCity=" + ShippingCity);
			sb.append(",ShippingState=" + ShippingState);
			sb.append(",ShippingPostalCode=" + ShippingPostalCode);
			sb.append(",ShippingCountry=" + ShippingCountry);
			sb.append(",ShippingLatitude=" + String.valueOf(ShippingLatitude));
			sb.append(",ShippingLongitude=" + String.valueOf(ShippingLongitude));
			sb.append(",ShippingGeocodeAccuracy=" + ShippingGeocodeAccuracy);
			sb.append(",ShippingAddress=" + ShippingAddress);
			sb.append(",Phone=" + Phone);
			sb.append(",Fax=" + Fax);
			sb.append(",AccountNumber=" + AccountNumber);
			sb.append(",Website=" + Website);
			sb.append(",PhotoUrl=" + PhotoUrl);
			sb.append(",Sic=" + Sic);
			sb.append(",Industry=" + Industry);
			sb.append(",AnnualRevenue=" + String.valueOf(AnnualRevenue));
			sb.append(",NumberOfEmployees=" + String.valueOf(NumberOfEmployees));
			sb.append(",Ownership=" + Ownership);
			sb.append(",TickerSymbol=" + TickerSymbol);
			sb.append(",Description=" + Description);
			sb.append(",Rating=" + Rating);
			sb.append(",Site=" + Site);
			sb.append(",OwnerId=" + OwnerId);
			sb.append(",CreatedDate=" + String.valueOf(CreatedDate));
			sb.append(",CreatedById=" + CreatedById);
			sb.append(",LastModifiedDate=" + String.valueOf(LastModifiedDate));
			sb.append(",LastModifiedById=" + LastModifiedById);
			sb.append(",SystemModstamp=" + String.valueOf(SystemModstamp));
			sb.append(",LastActivityDate=" + String.valueOf(LastActivityDate));
			sb.append(",LastViewedDate=" + String.valueOf(LastViewedDate));
			sb.append(",LastReferencedDate=" + String.valueOf(LastReferencedDate));
			sb.append(",Jigsaw=" + Jigsaw);
			sb.append(",JigsawCompanyId=" + JigsawCompanyId);
			sb.append(",CleanStatus=" + CleanStatus);
			sb.append(",AccountSource=" + AccountSource);
			sb.append(",DunsNumber=" + DunsNumber);
			sb.append(",Tradestyle=" + Tradestyle);
			sb.append(",NaicsCode=" + NaicsCode);
			sb.append(",NaicsDesc=" + NaicsDesc);
			sb.append(",YearStarted=" + YearStarted);
			sb.append(",SicDesc=" + SicDesc);
			sb.append(",DandbCompanyId=" + DandbCompanyId);
			sb.append(",OperatingHoursId=" + OperatingHoursId);
			sb.append(",CustomerPriority__c=" + CustomerPriority__c);
			sb.append(",SLA__c=" + SLA__c);
			sb.append(",Active__c=" + Active__c);
			sb.append(",NumberofLocations__c=" + String.valueOf(NumberofLocations__c));
			sb.append(",UpsellOpportunity__c=" + UpsellOpportunity__c);
			sb.append(",SLASerialNumber__c=" + SLASerialNumber__c);
			sb.append(",SLAExpirationDate__c=" + String.valueOf(SLAExpirationDate__c));
			sb.append(",Email__c=" + Email__c);
			sb.append(",ExternalID__c=" + ExternalID__c);
			sb.append(",Status__c=" + Status__c);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			if (Id == null) {
				sb.append("<null>");
			} else {
				sb.append(Id);
			}

			sb.append("|");

			sb.append(IsDeleted);

			sb.append("|");

			if (MasterRecordId == null) {
				sb.append("<null>");
			} else {
				sb.append(MasterRecordId);
			}

			sb.append("|");

			if (Name == null) {
				sb.append("<null>");
			} else {
				sb.append(Name);
			}

			sb.append("|");

			if (Type == null) {
				sb.append("<null>");
			} else {
				sb.append(Type);
			}

			sb.append("|");

			if (ParentId == null) {
				sb.append("<null>");
			} else {
				sb.append(ParentId);
			}

			sb.append("|");

			if (BillingStreet == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingStreet);
			}

			sb.append("|");

			if (BillingCity == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingCity);
			}

			sb.append("|");

			if (BillingState == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingState);
			}

			sb.append("|");

			if (BillingPostalCode == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingPostalCode);
			}

			sb.append("|");

			if (BillingCountry == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingCountry);
			}

			sb.append("|");

			sb.append(BillingLatitude);

			sb.append("|");

			sb.append(BillingLongitude);

			sb.append("|");

			if (BillingGeocodeAccuracy == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingGeocodeAccuracy);
			}

			sb.append("|");

			if (BillingAddress == null) {
				sb.append("<null>");
			} else {
				sb.append(BillingAddress);
			}

			sb.append("|");

			if (ShippingStreet == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingStreet);
			}

			sb.append("|");

			if (ShippingCity == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingCity);
			}

			sb.append("|");

			if (ShippingState == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingState);
			}

			sb.append("|");

			if (ShippingPostalCode == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingPostalCode);
			}

			sb.append("|");

			if (ShippingCountry == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingCountry);
			}

			sb.append("|");

			sb.append(ShippingLatitude);

			sb.append("|");

			sb.append(ShippingLongitude);

			sb.append("|");

			if (ShippingGeocodeAccuracy == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingGeocodeAccuracy);
			}

			sb.append("|");

			if (ShippingAddress == null) {
				sb.append("<null>");
			} else {
				sb.append(ShippingAddress);
			}

			sb.append("|");

			if (Phone == null) {
				sb.append("<null>");
			} else {
				sb.append(Phone);
			}

			sb.append("|");

			if (Fax == null) {
				sb.append("<null>");
			} else {
				sb.append(Fax);
			}

			sb.append("|");

			if (AccountNumber == null) {
				sb.append("<null>");
			} else {
				sb.append(AccountNumber);
			}

			sb.append("|");

			if (Website == null) {
				sb.append("<null>");
			} else {
				sb.append(Website);
			}

			sb.append("|");

			if (PhotoUrl == null) {
				sb.append("<null>");
			} else {
				sb.append(PhotoUrl);
			}

			sb.append("|");

			if (Sic == null) {
				sb.append("<null>");
			} else {
				sb.append(Sic);
			}

			sb.append("|");

			if (Industry == null) {
				sb.append("<null>");
			} else {
				sb.append(Industry);
			}

			sb.append("|");

			if (AnnualRevenue == null) {
				sb.append("<null>");
			} else {
				sb.append(AnnualRevenue);
			}

			sb.append("|");

			sb.append(NumberOfEmployees);

			sb.append("|");

			if (Ownership == null) {
				sb.append("<null>");
			} else {
				sb.append(Ownership);
			}

			sb.append("|");

			if (TickerSymbol == null) {
				sb.append("<null>");
			} else {
				sb.append(TickerSymbol);
			}

			sb.append("|");

			if (Description == null) {
				sb.append("<null>");
			} else {
				sb.append(Description);
			}

			sb.append("|");

			if (Rating == null) {
				sb.append("<null>");
			} else {
				sb.append(Rating);
			}

			sb.append("|");

			if (Site == null) {
				sb.append("<null>");
			} else {
				sb.append(Site);
			}

			sb.append("|");

			if (OwnerId == null) {
				sb.append("<null>");
			} else {
				sb.append(OwnerId);
			}

			sb.append("|");

			if (CreatedDate == null) {
				sb.append("<null>");
			} else {
				sb.append(CreatedDate);
			}

			sb.append("|");

			if (CreatedById == null) {
				sb.append("<null>");
			} else {
				sb.append(CreatedById);
			}

			sb.append("|");

			if (LastModifiedDate == null) {
				sb.append("<null>");
			} else {
				sb.append(LastModifiedDate);
			}

			sb.append("|");

			if (LastModifiedById == null) {
				sb.append("<null>");
			} else {
				sb.append(LastModifiedById);
			}

			sb.append("|");

			if (SystemModstamp == null) {
				sb.append("<null>");
			} else {
				sb.append(SystemModstamp);
			}

			sb.append("|");

			if (LastActivityDate == null) {
				sb.append("<null>");
			} else {
				sb.append(LastActivityDate);
			}

			sb.append("|");

			if (LastViewedDate == null) {
				sb.append("<null>");
			} else {
				sb.append(LastViewedDate);
			}

			sb.append("|");

			if (LastReferencedDate == null) {
				sb.append("<null>");
			} else {
				sb.append(LastReferencedDate);
			}

			sb.append("|");

			if (Jigsaw == null) {
				sb.append("<null>");
			} else {
				sb.append(Jigsaw);
			}

			sb.append("|");

			if (JigsawCompanyId == null) {
				sb.append("<null>");
			} else {
				sb.append(JigsawCompanyId);
			}

			sb.append("|");

			if (CleanStatus == null) {
				sb.append("<null>");
			} else {
				sb.append(CleanStatus);
			}

			sb.append("|");

			if (AccountSource == null) {
				sb.append("<null>");
			} else {
				sb.append(AccountSource);
			}

			sb.append("|");

			if (DunsNumber == null) {
				sb.append("<null>");
			} else {
				sb.append(DunsNumber);
			}

			sb.append("|");

			if (Tradestyle == null) {
				sb.append("<null>");
			} else {
				sb.append(Tradestyle);
			}

			sb.append("|");

			if (NaicsCode == null) {
				sb.append("<null>");
			} else {
				sb.append(NaicsCode);
			}

			sb.append("|");

			if (NaicsDesc == null) {
				sb.append("<null>");
			} else {
				sb.append(NaicsDesc);
			}

			sb.append("|");

			if (YearStarted == null) {
				sb.append("<null>");
			} else {
				sb.append(YearStarted);
			}

			sb.append("|");

			if (SicDesc == null) {
				sb.append("<null>");
			} else {
				sb.append(SicDesc);
			}

			sb.append("|");

			if (DandbCompanyId == null) {
				sb.append("<null>");
			} else {
				sb.append(DandbCompanyId);
			}

			sb.append("|");

			if (OperatingHoursId == null) {
				sb.append("<null>");
			} else {
				sb.append(OperatingHoursId);
			}

			sb.append("|");

			if (CustomerPriority__c == null) {
				sb.append("<null>");
			} else {
				sb.append(CustomerPriority__c);
			}

			sb.append("|");

			if (SLA__c == null) {
				sb.append("<null>");
			} else {
				sb.append(SLA__c);
			}

			sb.append("|");

			if (Active__c == null) {
				sb.append("<null>");
			} else {
				sb.append(Active__c);
			}

			sb.append("|");

			sb.append(NumberofLocations__c);

			sb.append("|");

			if (UpsellOpportunity__c == null) {
				sb.append("<null>");
			} else {
				sb.append(UpsellOpportunity__c);
			}

			sb.append("|");

			if (SLASerialNumber__c == null) {
				sb.append("<null>");
			} else {
				sb.append(SLASerialNumber__c);
			}

			sb.append("|");

			if (SLAExpirationDate__c == null) {
				sb.append("<null>");
			} else {
				sb.append(SLAExpirationDate__c);
			}

			sb.append("|");

			if (Email__c == null) {
				sb.append("<null>");
			} else {
				sb.append(Email__c);
			}

			sb.append("|");

			if (ExternalID__c == null) {
				sb.append("<null>");
			} else {
				sb.append(ExternalID__c);
			}

			sb.append("|");

			if (Status__c == null) {
				sb.append("<null>");
			} else {
				sb.append(Status__c);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(rmappingStruct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void tSalesforceInput_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tSalesforceInput_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("tSalesforceInput_1", "GxaJ2W_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				rmappingStruct rmapping = new rmappingStruct();
				mapOut1Struct mapOut1 = new mapOut1Struct();

				/**
				 * [tLogRow_1 begin ] start
				 */

				sh("tLogRow_1");

				s(currentComponent = "tLogRow_1");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0, "mapOut1");

				int tos_count_tLogRow_1 = 0;

				if (log.isDebugEnabled())
					log.debug("tLogRow_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_tLogRow_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_tLogRow_1 = new StringBuilder();
							log4jParamters_tLogRow_1.append("Parameters:");
							log4jParamters_tLogRow_1.append("BASIC_MODE" + " = " + "true");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("TABLE_PRINT" + " = " + "false");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("VERTICAL" + " = " + "false");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("FIELDSEPARATOR" + " = " + "\"|\"");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("PRINT_HEADER" + " = " + "false");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("PRINT_UNIQUE_NAME" + " = " + "false");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("PRINT_COLNAMES" + " = " + "false");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("USE_FIXED_LENGTH" + " = " + "false");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("PRINT_CONTENT_WITH_LOG4J" + " = " + "true");
							log4jParamters_tLogRow_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("tLogRow_1 - " + (log4jParamters_tLogRow_1));
						}
					}
					new BytesLimit65535_tLogRow_1().limitLog4jByte();
				}
				if (enableLogStash) {
					talendJobLog.addCM("tLogRow_1", "tLogRow_1", "tLogRow");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				///////////////////////

				final String OUTPUT_FIELD_SEPARATOR_tLogRow_1 = "|";
				java.io.PrintStream consoleOut_tLogRow_1 = null;

				StringBuilder strBuffer_tLogRow_1 = null;
				int nb_line_tLogRow_1 = 0;
///////////////////////    			

				/**
				 * [tLogRow_1 begin ] stop
				 */

				/**
				 * [tMap_1 begin ] start
				 */

				sh("tMap_1");

				s(currentComponent = "tMap_1");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0, "rmapping");

				int tos_count_tMap_1 = 0;

				if (log.isDebugEnabled())
					log.debug("tMap_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_tMap_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_tMap_1 = new StringBuilder();
							log4jParamters_tMap_1.append("Parameters:");
							log4jParamters_tMap_1.append("LINK_STYLE" + " = " + "AUTO");
							log4jParamters_tMap_1.append(" | ");
							log4jParamters_tMap_1.append("TEMPORARY_DATA_DIRECTORY" + " = " + "");
							log4jParamters_tMap_1.append(" | ");
							log4jParamters_tMap_1.append("ROWS_BUFFER_SIZE" + " = " + "2000000");
							log4jParamters_tMap_1.append(" | ");
							log4jParamters_tMap_1.append("CHANGE_HASH_AND_EQUALS_FOR_BIGDECIMAL" + " = " + "true");
							log4jParamters_tMap_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("tMap_1 - " + (log4jParamters_tMap_1));
						}
					}
					new BytesLimit65535_tMap_1().limitLog4jByte();
				}
				if (enableLogStash) {
					talendJobLog.addCM("tMap_1", "tMap_1", "tMap");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

// ###############################
// # Lookup's keys initialization
				int count_rmapping_tMap_1 = 0;

// ###############################        

// ###############################
// # Vars initialization
				class Var__tMap_1__Struct {
				}
				Var__tMap_1__Struct Var__tMap_1 = new Var__tMap_1__Struct();
// ###############################

// ###############################
// # Outputs initialization
				int count_mapOut1_tMap_1 = 0;

				mapOut1Struct mapOut1_tmp = new mapOut1Struct();
// ###############################

				/**
				 * [tMap_1 begin ] stop
				 */

				/**
				 * [tSalesforceInput_1 begin ] start
				 */

				sh("tSalesforceInput_1");

				s(currentComponent = "tSalesforceInput_1");

				cLabel = "AccountQuery";

				int tos_count_tSalesforceInput_1 = 0;

				if (enableLogStash) {
					talendJobLog.addCM("tSalesforceInput_1", "AccountQuery", "tSalesforceInput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				boolean doesNodeBelongToRequest_tSalesforceInput_1 = 0 == 0;
				@SuppressWarnings("unchecked")
				java.util.Map<String, Object> restRequest_tSalesforceInput_1 = (java.util.Map<String, Object>) globalMap
						.get("restRequest");
				String currentTRestRequestOperation_tSalesforceInput_1 = (String) (restRequest_tSalesforceInput_1 != null
						? restRequest_tSalesforceInput_1.get("OPERATION")
						: null);

				org.talend.components.api.component.ComponentDefinition def_tSalesforceInput_1 = new org.talend.components.salesforce.tsalesforceinput.TSalesforceInputDefinition();

				org.talend.components.api.component.runtime.Writer writer_tSalesforceInput_1 = null;
				org.talend.components.api.component.runtime.Reader reader_tSalesforceInput_1 = null;

				org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties props_tSalesforceInput_1 = (org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties) def_tSalesforceInput_1
						.createRuntimeProperties();
				props_tSalesforceInput_1.setValue("queryMode",
						org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties.QueryMode.Query);

				props_tSalesforceInput_1.setValue("manualQuery", true);

				props_tSalesforceInput_1.setValue("query",
						"SELECT Id, IsDeleted, MasterRecordId, Name, Type, ParentId, BillingStreet, BillingCity, BillingState, BillingPostalCode"
								+ ", BillingCountry, BillingLatitude, BillingLongitude, BillingGeocodeAccuracy, BillingAddress, ShippingStreet, ShippingCit"
								+ "y, ShippingState, ShippingPostalCode, ShippingCountry, ShippingLatitude, ShippingLongitude, ShippingGeocodeAccuracy, Shi"
								+ "ppingAddress, Phone, Fax, AccountNumber, Website, PhotoUrl, Sic, Industry, AnnualRevenue, NumberOfEmployees, Ownership, "
								+ "TickerSymbol, Description, Rating, Site, OwnerId, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, SystemMo"
								+ "dstamp, LastActivityDate, LastViewedDate, LastReferencedDate, Jigsaw, JigsawCompanyId, CleanStatus, AccountSource, DunsN"
								+ "umber, Tradestyle, NaicsCode, NaicsDesc, YearStarted, SicDesc, DandbCompanyId, OperatingHoursId, CustomerPriority__c, SL"
								+ "A__c, Active__c, NumberofLocations__c, UpsellOpportunity__c, SLASerialNumber__c, SLAExpirationDate__c, Email__c, Externa"
								+ "lID__c, Status__c FROM Account");

				props_tSalesforceInput_1.setValue("includeDeleted", false);

				props_tSalesforceInput_1.setValue("batchSize", 250);

				props_tSalesforceInput_1.setValue("normalizeDelimiter", ";");

				props_tSalesforceInput_1.setValue("columnNameDelimiter", "_");

				props_tSalesforceInput_1.setValue("dataTimeUTC", true);

				props_tSalesforceInput_1.connection.userPassword.setValue("useAuth", false);

				props_tSalesforceInput_1.connection.proxy.userPassword.setValue("useAuth", false);

				props_tSalesforceInput_1.connection.referencedComponent.setValue("referenceType",
						org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE);

				props_tSalesforceInput_1.connection.referencedComponent.setValue("componentInstanceId",
						"tSalesforceConnection_1");

				props_tSalesforceInput_1.connection.referencedComponent.setValue("referenceDefinitionName",
						"tSalesforceConnection");

				props_tSalesforceInput_1.module.setValue("moduleName", "Account");

				props_tSalesforceInput_1.module.connection.userPassword.setValue("useAuth", false);

				props_tSalesforceInput_1.module.connection.proxy.userPassword.setValue("useAuth", false);

				props_tSalesforceInput_1.module.connection.referencedComponent.setValue("referenceType",
						org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE);

				props_tSalesforceInput_1.module.connection.referencedComponent.setValue("componentInstanceId",
						"tSalesforceConnection_1");

				props_tSalesforceInput_1.module.connection.referencedComponent.setValue("referenceDefinitionName",
						"tSalesforceConnection");

				class SchemaSettingTool_tSalesforceInput_1_1_fisrt {

					String getSchemaValue() {

						StringBuilder s = new StringBuilder();

						a("{\"type\":\"record\",", s);

						a("\"name\":\"MAIN\",\"fields\":[{", s);

						a("\"name\":\"Id\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Id\",\"talend.field.dbColumnName\":\"Id\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Id\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"IsDeleted\",\"type\":\"boolean\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"IsDeleted\",\"talend.field.dbColumnName\":\"IsDeleted\",\"di.column.talendType\":\"id_Boolean\",\"talend.field.pattern\":\"\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"IsDeleted\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"MasterRecordId\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"MasterRecordId\",\"talend.field.dbColumnName\":\"MasterRecordId\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"MasterRecordId\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Name\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Name\",\"talend.field.dbColumnName\":\"Name\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Name\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Type\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Type\",\"talend.field.dbColumnName\":\"Type\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Type\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ParentId\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"ParentId\",\"talend.field.dbColumnName\":\"ParentId\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ParentId\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"BillingStreet\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"BillingStreet\",\"talend.field.dbColumnName\":\"BillingStreet\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"BillingStreet\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"BillingCity\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"BillingCity\",\"talend.field.dbColumnName\":\"BillingCity\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"BillingCity\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"BillingState\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"BillingState\",\"talend.field.dbColumnName\":\"BillingState\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"80\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"BillingState\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"BillingPostalCode\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"BillingPostalCode\",\"talend.field.dbColumnName\":\"BillingPostalCode\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"BillingPostalCode\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"BillingCountry\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"BillingCountry\",\"talend.field.dbColumnName\":\"BillingCountry\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"80\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"BillingCountry\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"BillingLatitude\",\"type\":\"double\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"BillingLatitude\",\"talend.field.dbColumnName\":\"BillingLatitude\",\"di.column.talendType\":\"id_Double\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"BillingLatitude\",\"talend.field.precision\":\"15\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"BillingLongitude\",\"type\":\"double\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"BillingLongitude\",\"talend.field.dbColumnName\":\"BillingLongitude\",\"di.column.talendType\":\"id_Double\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"BillingLongitude\",\"talend.field.precision\":\"15\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"BillingGeocodeAccuracy\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"BillingGeocodeAccuracy\",\"talend.field.dbColumnName\":\"BillingGeocodeAccuracy\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"BillingGeocodeAccuracy\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"BillingAddress\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"BillingAddress\",\"talend.field.dbColumnName\":\"BillingAddress\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"BillingAddress\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ShippingStreet\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"ShippingStreet\",\"talend.field.dbColumnName\":\"ShippingStreet\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ShippingStreet\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ShippingCity\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"ShippingCity\",\"talend.field.dbColumnName\":\"ShippingCity\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ShippingCity\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ShippingState\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"ShippingState\",\"talend.field.dbColumnName\":\"ShippingState\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"80\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ShippingState\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ShippingPostalCode\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"ShippingPostalCode\",\"talend.field.dbColumnName\":\"ShippingPostalCode\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ShippingPostalCode\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ShippingCountry\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"ShippingCountry\",\"talend.field.dbColumnName\":\"ShippingCountry\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"80\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ShippingCountry\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ShippingLatitude\",\"type\":\"double\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"ShippingLatitude\",\"talend.field.dbColumnName\":\"ShippingLatitude\",\"di.column.talendType\":\"id_Double\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ShippingLatitude\",\"talend.field.precision\":\"15\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ShippingLongitude\",\"type\":\"double\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"ShippingLongitude\",\"talend.field.dbColumnName\":\"ShippingLongitude\",\"di.column.talendType\":\"id_Double\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ShippingLongitude\",\"talend.field.precision\":\"15\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ShippingGeocodeAccuracy\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"ShippingGeocodeAccuracy\",\"talend.field.dbColumnName\":\"ShippingGeocodeAccuracy\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ShippingGeocodeAccuracy\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ShippingAddress\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"ShippingAddress\",\"talend.field.dbColumnName\":\"ShippingAddress\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ShippingAddress\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Phone\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Phone\",\"talend.field.dbColumnName\":\"Phone\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Phone\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Fax\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Fax\",\"talend.field.dbColumnName\":\"Fax\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Fax\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"AccountNumber\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"AccountNumber\",\"talend.field.dbColumnName\":\"AccountNumber\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"AccountNumber\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Website\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Website\",\"talend.field.dbColumnName\":\"Website\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Website\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"PhotoUrl\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"PhotoUrl\",\"talend.field.dbColumnName\":\"PhotoUrl\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"PhotoUrl\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Sic\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Sic\",\"talend.field.dbColumnName\":\"Sic\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Sic\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Industry\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Industry\",\"talend.field.dbColumnName\":\"Industry\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Industry\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"AnnualRevenue\",\"type\":{\"type\":\"string\",\"java-class\":\"java.math.BigDecimal\"},\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"AnnualRevenue\",\"talend.field.dbColumnName\":\"AnnualRevenue\",\"di.column.talendType\":\"id_BigDecimal\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"AnnualRevenue\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"NumberOfEmployees\",\"type\":\"int\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"NumberOfEmployees\",\"talend.field.dbColumnName\":\"NumberOfEmployees\",\"di.column.talendType\":\"id_Integer\",\"talend.field.pattern\":\"\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"NumberOfEmployees\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Ownership\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Ownership\",\"talend.field.dbColumnName\":\"Ownership\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Ownership\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"TickerSymbol\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"TickerSymbol\",\"talend.field.dbColumnName\":\"TickerSymbol\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"TickerSymbol\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Description\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Description\",\"talend.field.dbColumnName\":\"Description\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"32000\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Description\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Rating\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Rating\",\"talend.field.dbColumnName\":\"Rating\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Rating\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Site\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Site\",\"talend.field.dbColumnName\":\"Site\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"80\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Site\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"OwnerId\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"OwnerId\",\"talend.field.dbColumnName\":\"OwnerId\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"OwnerId\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"CreatedDate\",\"type\":{\"type\":\"long\",\"java-class\":\"java.util.Date\"},\"di.table.comment\":\"\",\"di.prop.di.date.noLogicalType\":\"true\",\"AVRO_TECHNICAL_KEY\":\"CreatedDate\",\"talend.field.dbColumnName\":\"CreatedDate\",\"di.column.talendType\":\"id_Date\",\"talend.field.pattern\":\"yyyy-MM-dd'T'HH:mm:ss'.000Z'\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"CreatedDate\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"CreatedById\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"CreatedById\",\"talend.field.dbColumnName\":\"CreatedById\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"CreatedById\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"LastModifiedDate\",\"type\":{\"type\":\"long\",\"java-class\":\"java.util.Date\"},\"di.table.comment\":\"\",\"di.prop.di.date.noLogicalType\":\"true\",\"AVRO_TECHNICAL_KEY\":\"LastModifiedDate\",\"talend.field.dbColumnName\":\"LastModifiedDate\",\"di.column.talendType\":\"id_Date\",\"talend.field.pattern\":\"yyyy-MM-dd'T'HH:mm:ss'.000Z'\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"LastModifiedDate\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"LastModifiedById\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"LastModifiedById\",\"talend.field.dbColumnName\":\"LastModifiedById\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"LastModifiedById\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"SystemModstamp\",\"type\":{\"type\":\"long\",\"java-class\":\"java.util.Date\"},\"di.table.comment\":\"\",\"di.prop.di.date.noLogicalType\":\"true\",\"AVRO_TECHNICAL_KEY\":\"SystemModstamp\",\"talend.field.dbColumnName\":\"SystemModstamp\",\"di.column.talendType\":\"id_Date\",\"talend.field.pattern\":\"yyyy-MM-dd'T'HH:mm:ss'.000Z'\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"SystemModstamp\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"LastActivityDate\",\"type\":{\"type\":\"long\",\"java-class\":\"java.util.Date\"},\"di.table.comment\":\"\",\"di.prop.di.date.noLogicalType\":\"true\",\"AVRO_TECHNICAL_KEY\":\"LastActivityDate\",\"talend.field.dbColumnName\":\"LastActivityDate\",\"di.column.talendType\":\"id_Date\",\"talend.field.pattern\":\"yyyy-MM-dd\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"LastActivityDate\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"LastViewedDate\",\"type\":{\"type\":\"long\",\"java-class\":\"java.util.Date\"},\"di.table.comment\":\"\",\"di.prop.di.date.noLogicalType\":\"true\",\"AVRO_TECHNICAL_KEY\":\"LastViewedDate\",\"talend.field.dbColumnName\":\"LastViewedDate\",\"di.column.talendType\":\"id_Date\",\"talend.field.pattern\":\"yyyy-MM-dd'T'HH:mm:ss'.000Z'\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"LastViewedDate\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"LastReferencedDate\",\"type\":{\"type\":\"long\",\"java-class\":\"java.util.Date\"},\"di.table.comment\":\"\",\"di.prop.di.date.noLogicalType\":\"true\",\"AVRO_TECHNICAL_KEY\":\"LastReferencedDate\",\"talend.field.dbColumnName\":\"LastReferencedDate\",\"di.column.talendType\":\"id_Date\",\"talend.field.pattern\":\"yyyy-MM-dd'T'HH:mm:ss'.000Z'\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"LastReferencedDate\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Jigsaw\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Jigsaw\",\"talend.field.dbColumnName\":\"Jigsaw\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Jigsaw\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"JigsawCompanyId\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"JigsawCompanyId\",\"talend.field.dbColumnName\":\"JigsawCompanyId\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"JigsawCompanyId\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"CleanStatus\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"CleanStatus\",\"talend.field.dbColumnName\":\"CleanStatus\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"CleanStatus\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"AccountSource\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"AccountSource\",\"talend.field.dbColumnName\":\"AccountSource\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"AccountSource\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"DunsNumber\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"DunsNumber\",\"talend.field.dbColumnName\":\"DunsNumber\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"9\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"DunsNumber\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Tradestyle\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Tradestyle\",\"talend.field.dbColumnName\":\"Tradestyle\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Tradestyle\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"NaicsCode\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"NaicsCode\",\"talend.field.dbColumnName\":\"NaicsCode\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"8\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"NaicsCode\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"NaicsDesc\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"NaicsDesc\",\"talend.field.dbColumnName\":\"NaicsDesc\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"120\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"NaicsDesc\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"YearStarted\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"YearStarted\",\"talend.field.dbColumnName\":\"YearStarted\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"4\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"YearStarted\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"SicDesc\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"SicDesc\",\"talend.field.dbColumnName\":\"SicDesc\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"80\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"SicDesc\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"DandbCompanyId\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"DandbCompanyId\",\"talend.field.dbColumnName\":\"DandbCompanyId\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"DandbCompanyId\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"OperatingHoursId\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"OperatingHoursId\",\"talend.field.dbColumnName\":\"OperatingHoursId\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"18\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"OperatingHoursId\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"CustomerPriority__c\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"CustomerPriority__c\",\"talend.field.dbColumnName\":\"CustomerPriority__c\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"CustomerPriority__c\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"SLA__c\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"SLA__c\",\"talend.field.dbColumnName\":\"SLA__c\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"SLA__c\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Active__c\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Active__c\",\"talend.field.dbColumnName\":\"Active__c\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Active__c\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"NumberofLocations__c\",\"type\":\"double\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"NumberofLocations__c\",\"talend.field.dbColumnName\":\"NumberofLocations__c\",\"di.column.talendType\":\"id_Double\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"3\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"NumberofLocations__c\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"UpsellOpportunity__c\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"UpsellOpportunity__c\",\"talend.field.dbColumnName\":\"UpsellOpportunity__c\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"255\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"UpsellOpportunity__c\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"SLASerialNumber__c\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"SLASerialNumber__c\",\"talend.field.dbColumnName\":\"SLASerialNumber__c\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"10\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"SLASerialNumber__c\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"SLAExpirationDate__c\",\"type\":{\"type\":\"long\",\"java-class\":\"java.util.Date\"},\"di.table.comment\":\"\",\"di.prop.di.date.noLogicalType\":\"true\",\"AVRO_TECHNICAL_KEY\":\"SLAExpirationDate__c\",\"talend.field.dbColumnName\":\"SLAExpirationDate__c\",\"di.column.talendType\":\"id_Date\",\"talend.field.pattern\":\"yyyy-MM-dd\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"SLAExpirationDate__c\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Email__c\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Email__c\",\"talend.field.dbColumnName\":\"Email__c\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"80\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Email__c\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ExternalID__c\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"ExternalID__c\",\"talend.field.dbColumnName\":\"ExternalID__c\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"100\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ExternalID__c\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"Status__c\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"Status__c\",\"talend.field.dbColumnName\":\"Status__c\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"30\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Status__c\",\"di.column.relatedEntity\":\"\"}],\"di.table.name\":\"MAIN\",\"di.table.label\":\"MAIN\"}",
								s);

						return s.toString();

					}

					void a(String part, StringBuilder strB) {
						strB.append(part);
					}

				}

				SchemaSettingTool_tSalesforceInput_1_1_fisrt sst_tSalesforceInput_1_1_fisrt = new SchemaSettingTool_tSalesforceInput_1_1_fisrt();

				props_tSalesforceInput_1.module.main.setValue("schema", new org.apache.avro.Schema.Parser()
						.setValidateDefaults(false).parse(sst_tSalesforceInput_1_1_fisrt.getSchemaValue()));

				if (org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE == props_tSalesforceInput_1.connection.referencedComponent.referenceType
						.getValue()) {
					final String referencedComponentInstanceId_tSalesforceInput_1 = props_tSalesforceInput_1.connection.referencedComponent.componentInstanceId
							.getStringValue();
					if (referencedComponentInstanceId_tSalesforceInput_1 != null) {
						org.talend.daikon.properties.Properties referencedComponentProperties_tSalesforceInput_1 = (org.talend.daikon.properties.Properties) globalMap
								.get(referencedComponentInstanceId_tSalesforceInput_1
										+ "_COMPONENT_RUNTIME_PROPERTIES");
						props_tSalesforceInput_1.connection.referencedComponent
								.setReference(referencedComponentProperties_tSalesforceInput_1);
					}
				}
				if (org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE == props_tSalesforceInput_1.module.connection.referencedComponent.referenceType
						.getValue()) {
					final String referencedComponentInstanceId_tSalesforceInput_1 = props_tSalesforceInput_1.module.connection.referencedComponent.componentInstanceId
							.getStringValue();
					if (referencedComponentInstanceId_tSalesforceInput_1 != null) {
						org.talend.daikon.properties.Properties referencedComponentProperties_tSalesforceInput_1 = (org.talend.daikon.properties.Properties) globalMap
								.get(referencedComponentInstanceId_tSalesforceInput_1
										+ "_COMPONENT_RUNTIME_PROPERTIES");
						props_tSalesforceInput_1.module.connection.referencedComponent
								.setReference(referencedComponentProperties_tSalesforceInput_1);
					}
				}
				globalMap.put("tSalesforceInput_1_COMPONENT_RUNTIME_PROPERTIES", props_tSalesforceInput_1);
				globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
				globalMap.put("TALEND_COMPONENTS_VERSION", "0.37.41");
				java.net.URL mappings_url_tSalesforceInput_1 = this.getClass().getResource("/xmlMappings");
				globalMap.put("tSalesforceInput_1_MAPPINGS_URL", mappings_url_tSalesforceInput_1);

				org.talend.components.api.container.RuntimeContainer container_tSalesforceInput_1 = new org.talend.components.api.container.RuntimeContainer() {
					public Object getComponentData(String componentId, String key) {
						return globalMap.get(componentId + "_" + key);
					}

					public void setComponentData(String componentId, String key, Object data) {
						globalMap.put(componentId + "_" + key, data);
					}

					public String getCurrentComponentId() {
						return "tSalesforceInput_1";
					}

					public Object getGlobalData(String key) {
						return globalMap.get(key);
					}
				};

				int nb_line_tSalesforceInput_1 = 0;

				org.talend.components.api.component.ConnectorTopology topology_tSalesforceInput_1 = null;
				topology_tSalesforceInput_1 = org.talend.components.api.component.ConnectorTopology.OUTGOING;

				org.talend.daikon.runtime.RuntimeInfo runtime_info_tSalesforceInput_1 = def_tSalesforceInput_1
						.getRuntimeInfo(org.talend.components.api.component.runtime.ExecutionEngine.DI,
								props_tSalesforceInput_1, topology_tSalesforceInput_1);
				java.util.Set<org.talend.components.api.component.ConnectorTopology> supported_connector_topologies_tSalesforceInput_1 = def_tSalesforceInput_1
						.getSupportedConnectorTopologies();

				org.talend.components.api.component.runtime.RuntimableRuntime componentRuntime_tSalesforceInput_1 = (org.talend.components.api.component.runtime.RuntimableRuntime) (Class
						.forName(runtime_info_tSalesforceInput_1.getRuntimeClassName()).newInstance());
				org.talend.daikon.properties.ValidationResult initVr_tSalesforceInput_1 = componentRuntime_tSalesforceInput_1
						.initialize(container_tSalesforceInput_1, props_tSalesforceInput_1);

				if (initVr_tSalesforceInput_1
						.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
					throw new RuntimeException(initVr_tSalesforceInput_1.getMessage());
				}

				if (componentRuntime_tSalesforceInput_1 instanceof org.talend.components.api.component.runtime.ComponentDriverInitialization) {
					org.talend.components.api.component.runtime.ComponentDriverInitialization compDriverInitialization_tSalesforceInput_1 = (org.talend.components.api.component.runtime.ComponentDriverInitialization) componentRuntime_tSalesforceInput_1;
					compDriverInitialization_tSalesforceInput_1.runAtDriver(container_tSalesforceInput_1);
				}

				org.talend.components.api.component.runtime.SourceOrSink sourceOrSink_tSalesforceInput_1 = null;
				if (componentRuntime_tSalesforceInput_1 instanceof org.talend.components.api.component.runtime.SourceOrSink) {
					sourceOrSink_tSalesforceInput_1 = (org.talend.components.api.component.runtime.SourceOrSink) componentRuntime_tSalesforceInput_1;
					if (doesNodeBelongToRequest_tSalesforceInput_1) {
						org.talend.daikon.properties.ValidationResult vr_tSalesforceInput_1 = sourceOrSink_tSalesforceInput_1
								.validate(container_tSalesforceInput_1);
						if (vr_tSalesforceInput_1
								.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
							throw new RuntimeException(vr_tSalesforceInput_1.getMessage());
						}
					}
				}

				if (sourceOrSink_tSalesforceInput_1 instanceof org.talend.components.api.component.runtime.Source) {
					org.talend.components.api.component.runtime.Source source_tSalesforceInput_1 = (org.talend.components.api.component.runtime.Source) sourceOrSink_tSalesforceInput_1;
					reader_tSalesforceInput_1 = source_tSalesforceInput_1.createReader(container_tSalesforceInput_1);
					reader_tSalesforceInput_1 = new org.talend.codegen.flowvariables.runtime.FlowVariablesReader(
							reader_tSalesforceInput_1, container_tSalesforceInput_1);

					boolean multi_output_is_allowed_tSalesforceInput_1 = false;
					org.talend.components.api.component.Connector c_tSalesforceInput_1 = null;
					for (org.talend.components.api.component.Connector currentConnector : props_tSalesforceInput_1
							.getAvailableConnectors(null, true)) {
						if (currentConnector.getName().equals("MAIN")) {
							c_tSalesforceInput_1 = currentConnector;
						}

						if (currentConnector.getName().equals("REJECT")) {// it's better to move the code to javajet
							multi_output_is_allowed_tSalesforceInput_1 = true;
						}
					}
					org.apache.avro.Schema schema_tSalesforceInput_1 = props_tSalesforceInput_1
							.getSchema(c_tSalesforceInput_1, true);

					org.talend.codegen.enforcer.OutgoingSchemaEnforcer outgoingEnforcer_tSalesforceInput_1 = org.talend.codegen.enforcer.EnforcerCreator
							.createOutgoingEnforcer(schema_tSalesforceInput_1, false);

					// Create a reusable factory that converts the output of the reader to an
					// IndexedRecord.
					org.talend.daikon.avro.converter.IndexedRecordConverter<Object, ? extends org.apache.avro.generic.IndexedRecord> factory_tSalesforceInput_1 = null;

					// Iterate through the incoming data.
					boolean available_tSalesforceInput_1 = reader_tSalesforceInput_1.start();

					resourceMap.put("reader_tSalesforceInput_1", reader_tSalesforceInput_1);

					for (; available_tSalesforceInput_1; available_tSalesforceInput_1 = reader_tSalesforceInput_1
							.advance()) {
						nb_line_tSalesforceInput_1++;

						if (multi_output_is_allowed_tSalesforceInput_1) {

							rmapping = null;

						}

						try {
							Object data_tSalesforceInput_1 = reader_tSalesforceInput_1.getCurrent();

							if (multi_output_is_allowed_tSalesforceInput_1) {
								rmapping = new rmappingStruct();
							}

							// Construct the factory once when the first data arrives.
							if (factory_tSalesforceInput_1 == null) {
								factory_tSalesforceInput_1 = (org.talend.daikon.avro.converter.IndexedRecordConverter<Object, ? extends org.apache.avro.generic.IndexedRecord>) new org.talend.daikon.avro.AvroRegistry()
										.createIndexedRecordConverter(data_tSalesforceInput_1.getClass());
							}

							// Enforce the outgoing schema on the input.
							outgoingEnforcer_tSalesforceInput_1
									.setWrapped(factory_tSalesforceInput_1.convertToAvro(data_tSalesforceInput_1));
							Object columnValue_0_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(0);
							rmapping.Id = (String) (columnValue_0_tSalesforceInput_1);
							Object columnValue_1_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(1);
							if (columnValue_1_tSalesforceInput_1 == null) {
								rmapping.IsDeleted = false;
							} else {
								rmapping.IsDeleted = (boolean) (columnValue_1_tSalesforceInput_1);
							}
							Object columnValue_2_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(2);
							rmapping.MasterRecordId = (String) (columnValue_2_tSalesforceInput_1);
							Object columnValue_3_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(3);
							rmapping.Name = (String) (columnValue_3_tSalesforceInput_1);
							Object columnValue_4_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(4);
							rmapping.Type = (String) (columnValue_4_tSalesforceInput_1);
							Object columnValue_5_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(5);
							rmapping.ParentId = (String) (columnValue_5_tSalesforceInput_1);
							Object columnValue_6_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(6);
							rmapping.BillingStreet = (String) (columnValue_6_tSalesforceInput_1);
							Object columnValue_7_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(7);
							rmapping.BillingCity = (String) (columnValue_7_tSalesforceInput_1);
							Object columnValue_8_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(8);
							rmapping.BillingState = (String) (columnValue_8_tSalesforceInput_1);
							Object columnValue_9_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(9);
							rmapping.BillingPostalCode = (String) (columnValue_9_tSalesforceInput_1);
							Object columnValue_10_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(10);
							rmapping.BillingCountry = (String) (columnValue_10_tSalesforceInput_1);
							Object columnValue_11_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(11);
							if (columnValue_11_tSalesforceInput_1 == null) {
								rmapping.BillingLatitude = 0;
							} else {
								rmapping.BillingLatitude = (double) (columnValue_11_tSalesforceInput_1);
							}
							Object columnValue_12_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(12);
							if (columnValue_12_tSalesforceInput_1 == null) {
								rmapping.BillingLongitude = 0;
							} else {
								rmapping.BillingLongitude = (double) (columnValue_12_tSalesforceInput_1);
							}
							Object columnValue_13_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(13);
							rmapping.BillingGeocodeAccuracy = (String) (columnValue_13_tSalesforceInput_1);
							Object columnValue_14_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(14);
							rmapping.BillingAddress = (String) (columnValue_14_tSalesforceInput_1);
							Object columnValue_15_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(15);
							rmapping.ShippingStreet = (String) (columnValue_15_tSalesforceInput_1);
							Object columnValue_16_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(16);
							rmapping.ShippingCity = (String) (columnValue_16_tSalesforceInput_1);
							Object columnValue_17_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(17);
							rmapping.ShippingState = (String) (columnValue_17_tSalesforceInput_1);
							Object columnValue_18_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(18);
							rmapping.ShippingPostalCode = (String) (columnValue_18_tSalesforceInput_1);
							Object columnValue_19_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(19);
							rmapping.ShippingCountry = (String) (columnValue_19_tSalesforceInput_1);
							Object columnValue_20_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(20);
							if (columnValue_20_tSalesforceInput_1 == null) {
								rmapping.ShippingLatitude = 0;
							} else {
								rmapping.ShippingLatitude = (double) (columnValue_20_tSalesforceInput_1);
							}
							Object columnValue_21_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(21);
							if (columnValue_21_tSalesforceInput_1 == null) {
								rmapping.ShippingLongitude = 0;
							} else {
								rmapping.ShippingLongitude = (double) (columnValue_21_tSalesforceInput_1);
							}
							Object columnValue_22_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(22);
							rmapping.ShippingGeocodeAccuracy = (String) (columnValue_22_tSalesforceInput_1);
							Object columnValue_23_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(23);
							rmapping.ShippingAddress = (String) (columnValue_23_tSalesforceInput_1);
							Object columnValue_24_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(24);
							rmapping.Phone = (String) (columnValue_24_tSalesforceInput_1);
							Object columnValue_25_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(25);
							rmapping.Fax = (String) (columnValue_25_tSalesforceInput_1);
							Object columnValue_26_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(26);
							rmapping.AccountNumber = (String) (columnValue_26_tSalesforceInput_1);
							Object columnValue_27_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(27);
							rmapping.Website = (String) (columnValue_27_tSalesforceInput_1);
							Object columnValue_28_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(28);
							rmapping.PhotoUrl = (String) (columnValue_28_tSalesforceInput_1);
							Object columnValue_29_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(29);
							rmapping.Sic = (String) (columnValue_29_tSalesforceInput_1);
							Object columnValue_30_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(30);
							rmapping.Industry = (String) (columnValue_30_tSalesforceInput_1);
							Object columnValue_31_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(31);
							rmapping.AnnualRevenue = (BigDecimal) (columnValue_31_tSalesforceInput_1);
							Object columnValue_32_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(32);
							if (columnValue_32_tSalesforceInput_1 == null) {
								rmapping.NumberOfEmployees = 0;
							} else {
								rmapping.NumberOfEmployees = (int) (columnValue_32_tSalesforceInput_1);
							}
							Object columnValue_33_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(33);
							rmapping.Ownership = (String) (columnValue_33_tSalesforceInput_1);
							Object columnValue_34_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(34);
							rmapping.TickerSymbol = (String) (columnValue_34_tSalesforceInput_1);
							Object columnValue_35_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(35);
							rmapping.Description = (String) (columnValue_35_tSalesforceInput_1);
							Object columnValue_36_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(36);
							rmapping.Rating = (String) (columnValue_36_tSalesforceInput_1);
							Object columnValue_37_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(37);
							rmapping.Site = (String) (columnValue_37_tSalesforceInput_1);
							Object columnValue_38_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(38);
							rmapping.OwnerId = (String) (columnValue_38_tSalesforceInput_1);
							Object columnValue_39_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(39);
							rmapping.CreatedDate = (java.util.Date) (columnValue_39_tSalesforceInput_1);
							Object columnValue_40_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(40);
							rmapping.CreatedById = (String) (columnValue_40_tSalesforceInput_1);
							Object columnValue_41_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(41);
							rmapping.LastModifiedDate = (java.util.Date) (columnValue_41_tSalesforceInput_1);
							Object columnValue_42_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(42);
							rmapping.LastModifiedById = (String) (columnValue_42_tSalesforceInput_1);
							Object columnValue_43_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(43);
							rmapping.SystemModstamp = (java.util.Date) (columnValue_43_tSalesforceInput_1);
							Object columnValue_44_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(44);
							rmapping.LastActivityDate = (java.util.Date) (columnValue_44_tSalesforceInput_1);
							Object columnValue_45_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(45);
							rmapping.LastViewedDate = (java.util.Date) (columnValue_45_tSalesforceInput_1);
							Object columnValue_46_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(46);
							rmapping.LastReferencedDate = (java.util.Date) (columnValue_46_tSalesforceInput_1);
							Object columnValue_47_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(47);
							rmapping.Jigsaw = (String) (columnValue_47_tSalesforceInput_1);
							Object columnValue_48_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(48);
							rmapping.JigsawCompanyId = (String) (columnValue_48_tSalesforceInput_1);
							Object columnValue_49_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(49);
							rmapping.CleanStatus = (String) (columnValue_49_tSalesforceInput_1);
							Object columnValue_50_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(50);
							rmapping.AccountSource = (String) (columnValue_50_tSalesforceInput_1);
							Object columnValue_51_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(51);
							rmapping.DunsNumber = (String) (columnValue_51_tSalesforceInput_1);
							Object columnValue_52_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(52);
							rmapping.Tradestyle = (String) (columnValue_52_tSalesforceInput_1);
							Object columnValue_53_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(53);
							rmapping.NaicsCode = (String) (columnValue_53_tSalesforceInput_1);
							Object columnValue_54_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(54);
							rmapping.NaicsDesc = (String) (columnValue_54_tSalesforceInput_1);
							Object columnValue_55_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(55);
							rmapping.YearStarted = (String) (columnValue_55_tSalesforceInput_1);
							Object columnValue_56_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(56);
							rmapping.SicDesc = (String) (columnValue_56_tSalesforceInput_1);
							Object columnValue_57_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(57);
							rmapping.DandbCompanyId = (String) (columnValue_57_tSalesforceInput_1);
							Object columnValue_58_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(58);
							rmapping.OperatingHoursId = (String) (columnValue_58_tSalesforceInput_1);
							Object columnValue_59_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(59);
							rmapping.CustomerPriority__c = (String) (columnValue_59_tSalesforceInput_1);
							Object columnValue_60_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(60);
							rmapping.SLA__c = (String) (columnValue_60_tSalesforceInput_1);
							Object columnValue_61_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(61);
							rmapping.Active__c = (String) (columnValue_61_tSalesforceInput_1);
							Object columnValue_62_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(62);
							if (columnValue_62_tSalesforceInput_1 == null) {
								rmapping.NumberofLocations__c = 0;
							} else {
								rmapping.NumberofLocations__c = (double) (columnValue_62_tSalesforceInput_1);
							}
							Object columnValue_63_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(63);
							rmapping.UpsellOpportunity__c = (String) (columnValue_63_tSalesforceInput_1);
							Object columnValue_64_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(64);
							rmapping.SLASerialNumber__c = (String) (columnValue_64_tSalesforceInput_1);
							Object columnValue_65_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(65);
							rmapping.SLAExpirationDate__c = (java.util.Date) (columnValue_65_tSalesforceInput_1);
							Object columnValue_66_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(66);
							rmapping.Email__c = (String) (columnValue_66_tSalesforceInput_1);
							Object columnValue_67_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(67);
							rmapping.ExternalID__c = (String) (columnValue_67_tSalesforceInput_1);
							Object columnValue_68_tSalesforceInput_1 = outgoingEnforcer_tSalesforceInput_1.get(68);
							rmapping.Status__c = (String) (columnValue_68_tSalesforceInput_1);
						} catch (org.talend.components.api.exception.DataRejectException e_tSalesforceInput_1) {
							java.util.Map<String, Object> info_tSalesforceInput_1 = e_tSalesforceInput_1
									.getRejectInfo();

							// TODO use a method instead of getting method by the special key
							// "error/errorMessage"
							Object errorMessage_tSalesforceInput_1 = null;
							if (info_tSalesforceInput_1.containsKey("error")) {
								errorMessage_tSalesforceInput_1 = info_tSalesforceInput_1.get("error");
							} else if (info_tSalesforceInput_1.containsKey("errorMessage")) {
								errorMessage_tSalesforceInput_1 = info_tSalesforceInput_1.get("errorMessage");
							} else {
								errorMessage_tSalesforceInput_1 = "Rejected but error message missing";
							}
							errorMessage_tSalesforceInput_1 = "Row " + nb_line_tSalesforceInput_1 + ": "
									+ errorMessage_tSalesforceInput_1;
							System.err.println(errorMessage_tSalesforceInput_1);

							// If the record is reject, the main line record should put NULL
							rmapping = null;

						} // end of catch

						java.lang.Iterable<?> outgoingMainRecordsList_tSalesforceInput_1 = new java.util.ArrayList<Object>();
						java.util.Iterator outgoingMainRecordsIt_tSalesforceInput_1 = null;

						/**
						 * [tSalesforceInput_1 begin ] stop
						 */

						/**
						 * [tSalesforceInput_1 main ] start
						 */

						s(currentComponent = "tSalesforceInput_1");

						cLabel = "AccountQuery";

						tos_count_tSalesforceInput_1++;

						/**
						 * [tSalesforceInput_1 main ] stop
						 */

						/**
						 * [tSalesforceInput_1 process_data_begin ] start
						 */

						s(currentComponent = "tSalesforceInput_1");

						cLabel = "AccountQuery";

						/**
						 * [tSalesforceInput_1 process_data_begin ] stop
						 */

						/**
						 * [tMap_1 main ] start
						 */

						s(currentComponent = "tMap_1");

						if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

								, "rmapping", "tSalesforceInput_1", "AccountQuery", "tSalesforceInput", "tMap_1",
								"tMap_1", "tMap"

						)) {
							talendJobLogProcess(globalMap);
						}

						if (log.isTraceEnabled()) {
							log.trace("rmapping - " + (rmapping == null ? "" : rmapping.toLogString()));
						}

						boolean hasCasePrimitiveKeyWithNull_tMap_1 = false;

						// ###############################
						// # Input tables (lookups)

						boolean rejectedInnerJoin_tMap_1 = false;
						boolean mainRowRejected_tMap_1 = false;
						// ###############################
						{ // start of Var scope

							// ###############################
							// # Vars tables

							Var__tMap_1__Struct Var = Var__tMap_1;// ###############################
							// ###############################
							// # Output tables

							mapOut1 = null;

// # Output table : 'mapOut1'
							count_mapOut1_tMap_1++;

							mapOut1_tmp.IsDeleted = rmapping.IsDeleted;
							mapOut1_tmp.MasterRecordId = rmapping.MasterRecordId;
							mapOut1_tmp.Name = rmapping.Name;
							mapOut1_tmp.Type = rmapping.Type;
							mapOut1_tmp.ParentId = rmapping.ParentId;
							mapOut1_tmp.BillingStreet = rmapping.BillingStreet;
							mapOut1_tmp.BillingCity = rmapping.BillingCity;
							mapOut1_tmp.BillingState = rmapping.BillingState;
							mapOut1_tmp.BillingPostalCode = rmapping.BillingPostalCode;
							mapOut1_tmp.BillingCountry = rmapping.BillingCountry;
							mapOut1_tmp.BillingLatitude = rmapping.BillingLatitude;
							mapOut1_tmp.BillingLongitude = rmapping.BillingLongitude;
							mapOut1_tmp.BillingGeocodeAccuracy = rmapping.BillingGeocodeAccuracy;
							mapOut1_tmp.BillingAddress = rmapping.BillingAddress;
							mapOut1_tmp.ShippingStreet = rmapping.ShippingStreet;
							mapOut1_tmp.ShippingCity = rmapping.ShippingCity;
							mapOut1_tmp.ShippingState = rmapping.ShippingState;
							mapOut1_tmp.ShippingPostalCode = rmapping.ShippingPostalCode;
							mapOut1_tmp.ShippingCountry = rmapping.ShippingCountry;
							mapOut1_tmp.ShippingLatitude = rmapping.ShippingLatitude;
							mapOut1_tmp.ShippingLongitude = rmapping.ShippingLongitude;
							mapOut1_tmp.ShippingGeocodeAccuracy = rmapping.ShippingGeocodeAccuracy;
							mapOut1_tmp.ShippingAddress = rmapping.ShippingAddress;
							mapOut1_tmp.Phone = rmapping.Phone;
							mapOut1_tmp.Fax = rmapping.Fax;
							mapOut1_tmp.AccountNumber = rmapping.AccountNumber;
							mapOut1_tmp.Website = rmapping.Website;
							mapOut1_tmp.PhotoUrl = rmapping.PhotoUrl;
							mapOut1_tmp.Sic = rmapping.Sic;
							mapOut1_tmp.Industry = rmapping.Industry;
							mapOut1_tmp.AnnualRevenue = rmapping.AnnualRevenue;
							mapOut1_tmp.NumberOfEmployees = rmapping.NumberOfEmployees;
							mapOut1_tmp.Ownership = rmapping.Ownership;
							mapOut1_tmp.TickerSymbol = rmapping.TickerSymbol;
							mapOut1_tmp.Description = rmapping.Description;
							mapOut1_tmp.Rating = rmapping.Rating;
							mapOut1_tmp.Site = rmapping.Site;
							mapOut1_tmp.OwnerId = rmapping.OwnerId;
							mapOut1_tmp.CreatedDate = rmapping.CreatedDate;
							mapOut1_tmp.CreatedById = rmapping.CreatedById;
							mapOut1_tmp.LastModifiedDate = rmapping.LastModifiedDate;
							mapOut1_tmp.LastModifiedById = rmapping.LastModifiedById;
							mapOut1_tmp.SystemModstamp = rmapping.SystemModstamp;
							mapOut1_tmp.LastActivityDate = rmapping.LastActivityDate;
							mapOut1_tmp.LastViewedDate = rmapping.LastViewedDate;
							mapOut1_tmp.LastReferencedDate = rmapping.LastReferencedDate;
							mapOut1_tmp.Jigsaw = rmapping.Jigsaw;
							mapOut1_tmp.JigsawCompanyId = rmapping.JigsawCompanyId;
							mapOut1_tmp.CleanStatus = rmapping.CleanStatus;
							mapOut1_tmp.AccountSource = rmapping.AccountSource;
							mapOut1_tmp.DunsNumber = rmapping.DunsNumber;
							mapOut1_tmp.Tradestyle = rmapping.Tradestyle;
							mapOut1_tmp.NaicsCode = rmapping.NaicsCode;
							mapOut1_tmp.NaicsDesc = rmapping.NaicsDesc;
							mapOut1_tmp.YearStarted = rmapping.YearStarted;
							mapOut1_tmp.SicDesc = rmapping.SicDesc;
							mapOut1_tmp.DandbCompanyId = rmapping.DandbCompanyId;
							mapOut1_tmp.OperatingHoursId = rmapping.OperatingHoursId;
							mapOut1_tmp.CustomerPriority__c = rmapping.CustomerPriority__c;
							mapOut1_tmp.SLA__c = rmapping.SLA__c;
							mapOut1_tmp.Active__c = rmapping.Active__c;
							mapOut1_tmp.NumberofLocations__c = rmapping.NumberofLocations__c;
							mapOut1_tmp.UpsellOpportunity__c = rmapping.UpsellOpportunity__c;
							mapOut1_tmp.SLASerialNumber__c = rmapping.SLASerialNumber__c;
							mapOut1_tmp.SLAExpirationDate__c = rmapping.SLAExpirationDate__c;
							mapOut1_tmp.Email__c = rmapping.Email__c;
							mapOut1_tmp.ExternalID__c = rmapping.Id;
							mapOut1_tmp.Status__c = rmapping.Status__c;
							mapOut1 = mapOut1_tmp;
							log.debug("tMap_1 - Outputting the record " + count_mapOut1_tMap_1
									+ " of the output table 'mapOut1'.");

// ###############################

						} // end of Var scope

						rejectedInnerJoin_tMap_1 = false;

						tos_count_tMap_1++;

						/**
						 * [tMap_1 main ] stop
						 */

						/**
						 * [tMap_1 process_data_begin ] start
						 */

						s(currentComponent = "tMap_1");

						/**
						 * [tMap_1 process_data_begin ] stop
						 */

// Start of branch "mapOut1"
						if (mapOut1 != null) {

							/**
							 * [tLogRow_1 main ] start
							 */

							s(currentComponent = "tLogRow_1");

							if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

									, "mapOut1", "tMap_1", "tMap_1", "tMap", "tLogRow_1", "tLogRow_1", "tLogRow"

							)) {
								talendJobLogProcess(globalMap);
							}

							if (log.isTraceEnabled()) {
								log.trace("mapOut1 - " + (mapOut1 == null ? "" : mapOut1.toLogString()));
							}

///////////////////////		

							strBuffer_tLogRow_1 = new StringBuilder();

							strBuffer_tLogRow_1.append(String.valueOf(mapOut1.IsDeleted));

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.MasterRecordId != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.MasterRecordId));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Name != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Name));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Type != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Type));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.ParentId != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.ParentId));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.BillingStreet != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.BillingStreet));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.BillingCity != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.BillingCity));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.BillingState != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.BillingState));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.BillingPostalCode != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.BillingPostalCode));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.BillingCountry != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.BillingCountry));

							} //

							strBuffer_tLogRow_1.append("|");

							strBuffer_tLogRow_1.append(FormatterUtils.formatUnwithE(mapOut1.BillingLatitude));

							strBuffer_tLogRow_1.append("|");

							strBuffer_tLogRow_1.append(FormatterUtils.formatUnwithE(mapOut1.BillingLongitude));

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.BillingGeocodeAccuracy != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.BillingGeocodeAccuracy));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.BillingAddress != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.BillingAddress));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.ShippingStreet != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.ShippingStreet));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.ShippingCity != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.ShippingCity));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.ShippingState != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.ShippingState));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.ShippingPostalCode != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.ShippingPostalCode));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.ShippingCountry != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.ShippingCountry));

							} //

							strBuffer_tLogRow_1.append("|");

							strBuffer_tLogRow_1.append(FormatterUtils.formatUnwithE(mapOut1.ShippingLatitude));

							strBuffer_tLogRow_1.append("|");

							strBuffer_tLogRow_1.append(FormatterUtils.formatUnwithE(mapOut1.ShippingLongitude));

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.ShippingGeocodeAccuracy != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.ShippingGeocodeAccuracy));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.ShippingAddress != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.ShippingAddress));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Phone != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Phone));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Fax != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Fax));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.AccountNumber != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.AccountNumber));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Website != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Website));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.PhotoUrl != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.PhotoUrl));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Sic != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Sic));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Industry != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Industry));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.AnnualRevenue != null) { //

								strBuffer_tLogRow_1.append(mapOut1.AnnualRevenue.toPlainString());

							} //

							strBuffer_tLogRow_1.append("|");

							strBuffer_tLogRow_1.append(String.valueOf(mapOut1.NumberOfEmployees));

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Ownership != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Ownership));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.TickerSymbol != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.TickerSymbol));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Description != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Description));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Rating != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Rating));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Site != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Site));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.OwnerId != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.OwnerId));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.CreatedDate != null) { //

								strBuffer_tLogRow_1.append(FormatterUtils.format_Date(mapOut1.CreatedDate,
										"yyyy-MM-dd'T'HH:mm:ss'.000Z'"));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.CreatedById != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.CreatedById));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.LastModifiedDate != null) { //

								strBuffer_tLogRow_1.append(FormatterUtils.format_Date(mapOut1.LastModifiedDate,
										"yyyy-MM-dd'T'HH:mm:ss'.000Z'"));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.LastModifiedById != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.LastModifiedById));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.SystemModstamp != null) { //

								strBuffer_tLogRow_1.append(FormatterUtils.format_Date(mapOut1.SystemModstamp,
										"yyyy-MM-dd'T'HH:mm:ss'.000Z'"));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.LastActivityDate != null) { //

								strBuffer_tLogRow_1
										.append(FormatterUtils.format_Date(mapOut1.LastActivityDate, "yyyy-MM-dd"));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.LastViewedDate != null) { //

								strBuffer_tLogRow_1.append(FormatterUtils.format_Date(mapOut1.LastViewedDate,
										"yyyy-MM-dd'T'HH:mm:ss'.000Z'"));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.LastReferencedDate != null) { //

								strBuffer_tLogRow_1.append(FormatterUtils.format_Date(mapOut1.LastReferencedDate,
										"yyyy-MM-dd'T'HH:mm:ss'.000Z'"));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Jigsaw != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Jigsaw));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.JigsawCompanyId != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.JigsawCompanyId));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.CleanStatus != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.CleanStatus));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.AccountSource != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.AccountSource));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.DunsNumber != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.DunsNumber));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Tradestyle != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Tradestyle));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.NaicsCode != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.NaicsCode));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.NaicsDesc != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.NaicsDesc));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.YearStarted != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.YearStarted));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.SicDesc != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.SicDesc));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.DandbCompanyId != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.DandbCompanyId));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.OperatingHoursId != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.OperatingHoursId));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.CustomerPriority__c != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.CustomerPriority__c));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.SLA__c != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.SLA__c));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Active__c != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Active__c));

							} //

							strBuffer_tLogRow_1.append("|");

							strBuffer_tLogRow_1.append(FormatterUtils.formatUnwithE(mapOut1.NumberofLocations__c));

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.UpsellOpportunity__c != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.UpsellOpportunity__c));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.SLASerialNumber__c != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.SLASerialNumber__c));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.SLAExpirationDate__c != null) { //

								strBuffer_tLogRow_1
										.append(FormatterUtils.format_Date(mapOut1.SLAExpirationDate__c, "yyyy-MM-dd"));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Email__c != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Email__c));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.ExternalID__c != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.ExternalID__c));

							} //

							strBuffer_tLogRow_1.append("|");

							if (mapOut1.Status__c != null) { //

								strBuffer_tLogRow_1.append(String.valueOf(mapOut1.Status__c));

							} //

							if (globalMap.get("tLogRow_CONSOLE") != null) {
								consoleOut_tLogRow_1 = (java.io.PrintStream) globalMap.get("tLogRow_CONSOLE");
							} else {
								consoleOut_tLogRow_1 = new java.io.PrintStream(
										new java.io.BufferedOutputStream(System.out));
								globalMap.put("tLogRow_CONSOLE", consoleOut_tLogRow_1);
							}
							log.info("tLogRow_1 - Content of row " + (nb_line_tLogRow_1 + 1) + ": "
									+ strBuffer_tLogRow_1.toString());
							consoleOut_tLogRow_1.println(strBuffer_tLogRow_1.toString());
							consoleOut_tLogRow_1.flush();
							nb_line_tLogRow_1++;
//////

//////                    

///////////////////////    			

							tos_count_tLogRow_1++;

							/**
							 * [tLogRow_1 main ] stop
							 */

							/**
							 * [tLogRow_1 process_data_begin ] start
							 */

							s(currentComponent = "tLogRow_1");

							/**
							 * [tLogRow_1 process_data_begin ] stop
							 */

							/**
							 * [tLogRow_1 process_data_end ] start
							 */

							s(currentComponent = "tLogRow_1");

							/**
							 * [tLogRow_1 process_data_end ] stop
							 */

						} // End of branch "mapOut1"

						/**
						 * [tMap_1 process_data_end ] start
						 */

						s(currentComponent = "tMap_1");

						/**
						 * [tMap_1 process_data_end ] stop
						 */

						/**
						 * [tSalesforceInput_1 process_data_end ] start
						 */

						s(currentComponent = "tSalesforceInput_1");

						cLabel = "AccountQuery";

						/**
						 * [tSalesforceInput_1 process_data_end ] stop
						 */

						/**
						 * [tSalesforceInput_1 end ] start
						 */

						s(currentComponent = "tSalesforceInput_1");

						cLabel = "AccountQuery";

// end of generic

						resourceMap.put("finish_tSalesforceInput_1", Boolean.TRUE);

					} // while
				} // end of "if (sourceOrSink_tSalesforceInput_1 instanceof ...Source)"
				java.util.Map<String, Object> resultMap_tSalesforceInput_1 = null;
				if (reader_tSalesforceInput_1 != null) {
					reader_tSalesforceInput_1.close();
					resultMap_tSalesforceInput_1 = reader_tSalesforceInput_1.getReturnValues();
				}
				if (resultMap_tSalesforceInput_1 != null) {
					for (java.util.Map.Entry<String, Object> entry_tSalesforceInput_1 : resultMap_tSalesforceInput_1
							.entrySet()) {
						switch (entry_tSalesforceInput_1.getKey()) {
						case org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE:
							container_tSalesforceInput_1.setComponentData("tSalesforceInput_1", "ERROR_MESSAGE",
									entry_tSalesforceInput_1.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_TOTAL_RECORD_COUNT:
							container_tSalesforceInput_1.setComponentData("tSalesforceInput_1", "NB_LINE",
									entry_tSalesforceInput_1.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_SUCCESS_RECORD_COUNT:
							container_tSalesforceInput_1.setComponentData("tSalesforceInput_1", "NB_SUCCESS",
									entry_tSalesforceInput_1.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_REJECT_RECORD_COUNT:
							container_tSalesforceInput_1.setComponentData("tSalesforceInput_1", "NB_REJECT",
									entry_tSalesforceInput_1.getValue());
							break;
						default:
							StringBuilder studio_key_tSalesforceInput_1 = new StringBuilder();
							for (int i_tSalesforceInput_1 = 0; i_tSalesforceInput_1 < entry_tSalesforceInput_1.getKey()
									.length(); i_tSalesforceInput_1++) {
								char ch_tSalesforceInput_1 = entry_tSalesforceInput_1.getKey()
										.charAt(i_tSalesforceInput_1);
								if (Character.isUpperCase(ch_tSalesforceInput_1) && i_tSalesforceInput_1 > 0) {
									studio_key_tSalesforceInput_1.append('_');
								}
								studio_key_tSalesforceInput_1.append(ch_tSalesforceInput_1);
							}
							container_tSalesforceInput_1.setComponentData("tSalesforceInput_1",
									studio_key_tSalesforceInput_1.toString().toUpperCase(java.util.Locale.ENGLISH),
									entry_tSalesforceInput_1.getValue());
							break;
						}
					}
				}

				ok_Hash.put("tSalesforceInput_1", true);
				end_Hash.put("tSalesforceInput_1", System.currentTimeMillis());

				/**
				 * [tSalesforceInput_1 end ] stop
				 */

				/**
				 * [tMap_1 end ] start
				 */

				s(currentComponent = "tMap_1");

// ###############################
// # Lookup hashes releasing
// ###############################      
				log.debug("tMap_1 - Written records count in the table 'mapOut1': " + count_mapOut1_tMap_1 + ".");

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, "rmapping", 2, 0,
						"tSalesforceInput_1", "AccountQuery", "tSalesforceInput", "tMap_1", "tMap_1", "tMap",
						"output")) {
					talendJobLogProcess(globalMap);
				}

				if (log.isDebugEnabled())
					log.debug("tMap_1 - " + ("Done."));

				ok_Hash.put("tMap_1", true);
				end_Hash.put("tMap_1", System.currentTimeMillis());

				/**
				 * [tMap_1 end ] stop
				 */

				/**
				 * [tLogRow_1 end ] start
				 */

				s(currentComponent = "tLogRow_1");

//////
//////
				globalMap.put("tLogRow_1_NB_LINE", nb_line_tLogRow_1);
				if (log.isInfoEnabled())
					log.info("tLogRow_1 - " + ("Printed row count: ") + (nb_line_tLogRow_1) + ("."));

///////////////////////    			

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, "mapOut1", 2, 0,
						"tMap_1", "tMap_1", "tMap", "tLogRow_1", "tLogRow_1", "tLogRow", "output")) {
					talendJobLogProcess(globalMap);
				}

				if (log.isDebugEnabled())
					log.debug("tLogRow_1 - " + ("Done."));

				ok_Hash.put("tLogRow_1", true);
				end_Hash.put("tLogRow_1", System.currentTimeMillis());

				/**
				 * [tLogRow_1 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tSalesforceInput_1 finally ] start
				 */

				s(currentComponent = "tSalesforceInput_1");

				cLabel = "AccountQuery";

// finally of generic

				if (resourceMap.get("finish_tSalesforceInput_1") == null) {
					if (resourceMap.get("reader_tSalesforceInput_1") != null) {
						try {
							((org.talend.components.api.component.runtime.Reader) resourceMap
									.get("reader_tSalesforceInput_1")).close();
						} catch (java.io.IOException e_tSalesforceInput_1) {
							String errorMessage_tSalesforceInput_1 = "failed to release the resource in tSalesforceInput_1 :"
									+ e_tSalesforceInput_1.getMessage();
							System.err.println(errorMessage_tSalesforceInput_1);
						}
					}
				}

				/**
				 * [tSalesforceInput_1 finally ] stop
				 */

				/**
				 * [tMap_1 finally ] start
				 */

				s(currentComponent = "tMap_1");

				/**
				 * [tMap_1 finally ] stop
				 */

				/**
				 * [tLogRow_1 finally ] start
				 */

				s(currentComponent = "tLogRow_1");

				/**
				 * [tLogRow_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tSalesforceInput_1_SUBPROCESS_STATE", 1);
	}

	public void talendJobLogProcess(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("talendJobLog_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [talendJobLog begin ] start
				 */

				sh("talendJobLog");

				s(currentComponent = "talendJobLog");

				int tos_count_talendJobLog = 0;

				for (JobStructureCatcherUtils.JobStructureCatcherMessage jcm : talendJobLog.getMessages()) {
					org.talend.job.audit.JobContextBuilder builder_talendJobLog = org.talend.job.audit.JobContextBuilder
							.create().jobName(jcm.job_name).jobId(jcm.job_id).jobVersion(jcm.job_version)
							.custom("process_id", jcm.pid).custom("thread_id", jcm.tid).custom("pid", pid)
							.custom("father_pid", fatherPid).custom("root_pid", rootPid);
					org.talend.logging.audit.Context log_context_talendJobLog = null;

					if (jcm.log_type == JobStructureCatcherUtils.LogType.PERFORMANCE) {
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.sourceId(jcm.sourceId)
								.sourceLabel(jcm.sourceLabel).sourceConnectorType(jcm.sourceComponentName)
								.targetId(jcm.targetId).targetLabel(jcm.targetLabel)
								.targetConnectorType(jcm.targetComponentName).connectionName(jcm.current_connector)
								.rows(jcm.row_count).duration(duration).build();
						auditLogger_talendJobLog.flowExecution(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBSTART) {
						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment).build();
						auditLogger_talendJobLog.jobstart(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBEND) {
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment).duration(duration)
								.status(jcm.status).build();
						auditLogger_talendJobLog.jobstop(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.RUNCOMPONENT) {
						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment)
								.connectorType(jcm.component_name).connectorId(jcm.component_id)
								.connectorLabel(jcm.component_label).build();
						auditLogger_talendJobLog.runcomponent(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.FLOWINPUT) {// log current component
																							// input line
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.connectorType(jcm.component_name)
								.connectorId(jcm.component_id).connectorLabel(jcm.component_label)
								.connectionName(jcm.current_connector).connectionType(jcm.current_connector_type)
								.rows(jcm.total_row_number).duration(duration).build();
						auditLogger_talendJobLog.flowInput(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.FLOWOUTPUT) {// log current component
																								// output/reject line
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.connectorType(jcm.component_name)
								.connectorId(jcm.component_id).connectorLabel(jcm.component_label)
								.connectionName(jcm.current_connector).connectionType(jcm.current_connector_type)
								.rows(jcm.total_row_number).duration(duration).build();
						auditLogger_talendJobLog.flowOutput(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBERROR) {
						java.lang.Exception e_talendJobLog = jcm.exception;
						if (e_talendJobLog != null) {
							try (java.io.StringWriter sw_talendJobLog = new java.io.StringWriter();
									java.io.PrintWriter pw_talendJobLog = new java.io.PrintWriter(sw_talendJobLog)) {
								e_talendJobLog.printStackTrace(pw_talendJobLog);
								builder_talendJobLog.custom("stacktrace", sw_talendJobLog.getBuffer().substring(0,
										java.lang.Math.min(sw_talendJobLog.getBuffer().length(), 512)));
							}
						}

						if (jcm.extra_info != null) {
							builder_talendJobLog.connectorId(jcm.component_id).custom("extra_info", jcm.extra_info);
						}

						log_context_talendJobLog = builder_talendJobLog
								.connectorType(jcm.component_id.substring(0, jcm.component_id.lastIndexOf('_')))
								.connectorId(jcm.component_id)
								.connectorLabel(jcm.component_label == null ? jcm.component_id : jcm.component_label)
								.build();

						auditLogger_talendJobLog.exception(log_context_talendJobLog);
					}

				}

				/**
				 * [talendJobLog begin ] stop
				 */

				/**
				 * [talendJobLog main ] start
				 */

				s(currentComponent = "talendJobLog");

				tos_count_talendJobLog++;

				/**
				 * [talendJobLog main ] stop
				 */

				/**
				 * [talendJobLog process_data_begin ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog process_data_begin ] stop
				 */

				/**
				 * [talendJobLog process_data_end ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog process_data_end ] stop
				 */

				/**
				 * [talendJobLog end ] start
				 */

				s(currentComponent = "talendJobLog");

				ok_Hash.put("talendJobLog", true);
				end_Hash.put("talendJobLog", System.currentTimeMillis());

				/**
				 * [talendJobLog end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [talendJobLog finally ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("talendJobLog_SUBPROCESS_STATE", 1);
	}

	public String resuming_logs_dir_path = null;
	public String resuming_checkpoint_path = null;
	public String parent_part_launcher = null;
	private String resumeEntryMethodName = null;
	private boolean globalResumeTicket = false;

	public boolean watch = false;
	// portStats is null, it means don't execute the statistics
	public Integer portStats = null;
	public int portTraces = 4334;
	public String clientHost;
	public String defaultClientHost = "localhost";
	public String contextStr = "Default";
	public boolean isDefaultContext = true;
	public String pid = "0";
	public String rootPid = null;
	public String fatherPid = null;
	public String fatherNode = null;
	public long startTime = 0;
	public boolean isChildJob = false;
	public String log4jLevel = "";

	private boolean enableLogStash;

	private boolean execStat = true;

	private ThreadLocal<java.util.Map<String, String>> threadLocal = new ThreadLocal<java.util.Map<String, String>>() {
		protected java.util.Map<String, String> initialValue() {
			java.util.Map<String, String> threadRunResultMap = new java.util.HashMap<String, String>();
			threadRunResultMap.put("errorCode", null);
			threadRunResultMap.put("status", "");
			return threadRunResultMap;
		};
	};

	protected PropertiesWithType context_param = new PropertiesWithType();
	public java.util.Map<String, Object> parentContextMap = new java.util.HashMap<String, Object>();

	public String status = "";

	private final org.talend.components.common.runtime.SharedConnectionsPool connectionPool = new org.talend.components.common.runtime.SharedConnectionsPool() {
		public java.sql.Connection getDBConnection(String dbDriver, String url, String userName, String password,
				String dbConnectionName) throws ClassNotFoundException, java.sql.SQLException {
			return SharedDBConnection.getDBConnection(dbDriver, url, userName, password, dbConnectionName);
		}

		public java.sql.Connection getDBConnection(String dbDriver, String url, String dbConnectionName)
				throws ClassNotFoundException, java.sql.SQLException {
			return SharedDBConnection.getDBConnection(dbDriver, url, dbConnectionName);
		}
	};

	private static final String GLOBAL_CONNECTION_POOL_KEY = "GLOBAL_CONNECTION_POOL";

	{
		globalMap.put(GLOBAL_CONNECTION_POOL_KEY, connectionPool);
	}

	private final static java.util.Properties jobInfo = new java.util.Properties();
	private final static java.util.Map<String, String> mdcInfo = new java.util.HashMap<>();
	private final static java.util.concurrent.atomic.AtomicLong subJobPidCounter = new java.util.concurrent.atomic.AtomicLong();

	public static void main(String[] args) {
		final Salesforce SalesforceClass = new Salesforce();

		int exitCode = SalesforceClass.runJobInTOS(args);
		if (exitCode == 0) {
			log.info("TalendJob: 'Salesforce' - Done.");
		}

		System.exit(exitCode);
	}

	private void getjobInfo() {
		final String TEMPLATE_PATH = "src/main/templates/jobInfo_template.properties";
		final String BUILD_PATH = "../jobInfo.properties";
		final String path = this.getClass().getResource("").getPath();
		if (path.lastIndexOf("target") > 0) {
			final java.io.File templateFile = new java.io.File(
					path.substring(0, path.lastIndexOf("target")).concat(TEMPLATE_PATH));
			if (templateFile.exists()) {
				readJobInfo(templateFile);
				return;
			}
		}
		readJobInfo(new java.io.File(BUILD_PATH));
	}

	private void readJobInfo(java.io.File jobInfoFile) {

		if (jobInfoFile.exists()) {
			try (java.io.InputStream is = new java.io.FileInputStream(jobInfoFile)) {
				jobInfo.load(is);
			} catch (IOException e) {

				log.debug("Read jobInfo.properties file fail: " + e.getMessage());

			}
		}
		log.info(String.format("Project name: %s\tJob name: %s\tGIT Commit ID: %s\tTalend Version: %s", projectName,
				jobName, jobInfo.getProperty("gitCommitId"), "8.0.1.20250126_0750-patch"));

	}

	public String[][] runJob(String[] args) {

		int exitCode = runJobInTOS(args);
		String[][] bufferValue = new String[][] { { Integer.toString(exitCode) } };

		return bufferValue;
	}

	public boolean hastBufferOutputComponent() {
		boolean hastBufferOutput = false;

		return hastBufferOutput;
	}

	public int runJobInTOS(String[] args) {
		// reset status
		status = "";

		String lastStr = "";
		for (String arg : args) {
			if (arg.equalsIgnoreCase("--context_param")) {
				lastStr = arg;
			} else if (lastStr.equals("")) {
				evalParam(arg);
			} else {
				evalParam(lastStr + " " + arg);
				lastStr = "";
			}
		}

		final boolean enableCBP = false;
		boolean inOSGi = routines.system.BundleUtils.inOSGi();

		if (!inOSGi) {
			if (org.talend.metrics.CBPClient.getInstanceForCurrentVM() == null) {
				try {
					org.talend.metrics.CBPClient.startListenIfNotStarted(enableCBP, true);
				} catch (java.lang.Exception e) {
					errorCode = 1;
					status = "failure";
					e.printStackTrace();
					return 1;
				}
			}
		}

		enableLogStash = "true".equalsIgnoreCase(System.getProperty("audit.enabled"));

		if (!"".equals(log4jLevel)) {

			if ("trace".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.TRACE);
			} else if ("debug".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.DEBUG);
			} else if ("info".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.INFO);
			} else if ("warn".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.WARN);
			} else if ("error".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.ERROR);
			} else if ("fatal".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.FATAL);
			} else if ("off".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.OFF);
			}
			org.apache.logging.log4j.core.config.Configurator
					.setLevel(org.apache.logging.log4j.LogManager.getRootLogger().getName(), log.getLevel());

		}

		getjobInfo();
		log.info("TalendJob: 'Salesforce' - Start.");

		java.util.Set<Object> jobInfoKeys = jobInfo.keySet();
		for (Object jobInfoKey : jobInfoKeys) {
			org.slf4j.MDC.put("_" + jobInfoKey.toString(), jobInfo.get(jobInfoKey).toString());
		}
		org.slf4j.MDC.put("_pid", pid);
		org.slf4j.MDC.put("_rootPid", rootPid);
		org.slf4j.MDC.put("_fatherPid", fatherPid);
		org.slf4j.MDC.put("_projectName", projectName);
		org.slf4j.MDC.put("_startTimestamp", java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
				.format(java.time.format.DateTimeFormatter.ISO_INSTANT));
		org.slf4j.MDC.put("_jobRepositoryId", "_2kxvcPA1Ee-2rNEZYaVnnw");
		org.slf4j.MDC.put("_compiledAtTimestamp", "2025-02-21T09:57:20.194102600Z");

		java.lang.management.RuntimeMXBean mx = java.lang.management.ManagementFactory.getRuntimeMXBean();
		String[] mxNameTable = mx.getName().split("@"); //$NON-NLS-1$
		if (mxNameTable.length == 2) {
			org.slf4j.MDC.put("_systemPid", mxNameTable[0]);
		} else {
			org.slf4j.MDC.put("_systemPid", String.valueOf(java.lang.Thread.currentThread().getId()));
		}

		if (enableLogStash) {
			java.util.Properties properties_talendJobLog = new java.util.Properties();
			properties_talendJobLog.setProperty("root.logger", "audit");
			properties_talendJobLog.setProperty("encoding", "UTF-8");
			properties_talendJobLog.setProperty("application.name", "Talend Studio");
			properties_talendJobLog.setProperty("service.name", "Talend Studio Job");
			properties_talendJobLog.setProperty("instance.name", "Talend Studio Job Instance");
			properties_talendJobLog.setProperty("propagate.appender.exceptions", "none");
			properties_talendJobLog.setProperty("log.appender", "file");
			properties_talendJobLog.setProperty("appender.file.path", "audit.json");
			properties_talendJobLog.setProperty("appender.file.maxsize", "52428800");
			properties_talendJobLog.setProperty("appender.file.maxbackup", "20");
			properties_talendJobLog.setProperty("host", "false");

			System.getProperties().stringPropertyNames().stream().filter(it -> it.startsWith("audit.logger."))
					.forEach(key -> properties_talendJobLog.setProperty(key.substring("audit.logger.".length()),
							System.getProperty(key)));

			org.apache.logging.log4j.core.config.Configurator
					.setLevel(properties_talendJobLog.getProperty("root.logger"), org.apache.logging.log4j.Level.DEBUG);

			auditLogger_talendJobLog = org.talend.job.audit.JobEventAuditLoggerFactory
					.createJobAuditLogger(properties_talendJobLog);
		}

		if (clientHost == null) {
			clientHost = defaultClientHost;
		}

		if (pid == null || "0".equals(pid)) {
			pid = TalendString.getAsciiRandomString(6);
		}

		org.slf4j.MDC.put("_pid", pid);

		if (rootPid == null) {
			rootPid = pid;
		}

		org.slf4j.MDC.put("_rootPid", rootPid);

		if (fatherPid == null) {
			fatherPid = pid;
		} else {
			isChildJob = true;
		}
		org.slf4j.MDC.put("_fatherPid", fatherPid);

		if (portStats != null) {
			// portStats = -1; //for testing
			if (portStats < 0 || portStats > 65535) {
				// issue:10869, the portStats is invalid, so this client socket can't open
				System.err.println("The statistics socket port " + portStats + " is invalid.");
				execStat = false;
			}
		} else {
			execStat = false;
		}

		try {
			java.util.Dictionary<String, Object> jobProperties = null;
			if (inOSGi) {
				jobProperties = routines.system.BundleUtils.getJobProperties(jobName);

				if (jobProperties != null && jobProperties.get("context") != null) {
					contextStr = (String) jobProperties.get("context");
				}

				if (jobProperties != null && jobProperties.get("taskExecutionId") != null) {
					taskExecutionId = (String) jobProperties.get("taskExecutionId");
				}

				// extract ids from parent route
				if (null == taskExecutionId || taskExecutionId.isEmpty()) {
					for (String arg : args) {
						if (arg.startsWith("--context_param")
								&& (arg.contains("taskExecutionId") || arg.contains("jobExecutionId"))) {

							String keyValue = arg.replace("--context_param", "");
							String[] parts = keyValue.split("=");
							String[] cleanParts = java.util.Arrays.stream(parts).filter(s -> !s.isEmpty())
									.toArray(String[]::new);
							if (cleanParts.length == 2) {
								String key = cleanParts[0];
								String value = cleanParts[1];
								if ("taskExecutionId".equals(key.trim()) && null != value) {
									taskExecutionId = value.trim();
								} else if ("jobExecutionId".equals(key.trim()) && null != value) {
									jobExecutionId = value.trim();
								}
							}
						}
					}
				}
			}

			// first load default key-value pairs from application.properties
			if (isStandaloneMS) {
				context.putAll(this.getDefaultProperties());
			}
			// call job/subjob with an existing context, like: --context=production. if
			// without this parameter, there will use the default context instead.
			java.io.InputStream inContext = Salesforce.class.getClassLoader()
					.getResourceAsStream("salesforcetalend/salesforce_0_1/contexts/" + contextStr + ".properties");
			if (inContext == null) {
				inContext = Salesforce.class.getClassLoader()
						.getResourceAsStream("config/contexts/" + contextStr + ".properties");
			}
			if (inContext != null) {
				try {
					// defaultProps is in order to keep the original context value
					if (context != null && context.isEmpty()) {
						defaultProps.load(inContext);
						if (inOSGi && jobProperties != null) {
							java.util.Enumeration<String> keys = jobProperties.keys();
							while (keys.hasMoreElements()) {
								String propKey = keys.nextElement();
								if (defaultProps.containsKey(propKey)) {
									defaultProps.put(propKey, (String) jobProperties.get(propKey));
								}
							}
						}
						context = new ContextProperties(defaultProps);
					}
					if (isStandaloneMS) {
						// override context key-value pairs if provided using --context=contextName
						defaultProps.load(inContext);
						context.putAll(defaultProps);
					}
				} finally {
					inContext.close();
				}
			} else if (!isDefaultContext) {
				// print info and job continue to run, for case: context_param is not empty.
				System.err.println("Could not find the context " + contextStr);
			}
			// override key-value pairs if provided via --config.location=file1.file2 OR
			// --config.additional-location=file1,file2
			if (isStandaloneMS) {
				context.putAll(this.getAdditionalProperties());
			}

			// override key-value pairs if provide via command line like
			// --key1=value1,--key2=value2
			if (!context_param.isEmpty()) {
				context.putAll(context_param);
				// set types for params from parentJobs
				for (Object key : context_param.keySet()) {
					String context_key = key.toString();
					String context_type = context_param.getContextType(context_key);
					context.setContextType(context_key, context_type);

				}
			}
			class ContextProcessing {
				private void processContext_0() {
				}

				public void processAllContext() {
					processContext_0();
				}
			}

			new ContextProcessing().processAllContext();
		} catch (java.io.IOException ie) {
			System.err.println("Could not load context " + contextStr);
			ie.printStackTrace();
		}

		// get context value from parent directly
		if (parentContextMap != null && !parentContextMap.isEmpty()) {
		}

		// Resume: init the resumeUtil
		resumeEntryMethodName = ResumeUtil.getResumeEntryMethodName(resuming_checkpoint_path);
		resumeUtil = new ResumeUtil(resuming_logs_dir_path, isChildJob, rootPid);
		resumeUtil.initCommonInfo(pid, rootPid, fatherPid, projectName, jobName, contextStr, jobVersion);

		List<String> parametersToEncrypt = new java.util.ArrayList<String>();
		// Resume: jobStart
		resumeUtil.addLog("JOB_STARTED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "",
				"", "", "", "", resumeUtil.convertToJsonText(context, ContextProperties.class, parametersToEncrypt));

		org.slf4j.MDC.put("_context", contextStr);
		log.info("TalendJob: 'Salesforce' - Started.");
		java.util.Optional.ofNullable(org.slf4j.MDC.getCopyOfContextMap()).ifPresent(mdcInfo::putAll);

		if (execStat) {
			try {
				runStat.openSocket(!isChildJob);
				runStat.setAllPID(rootPid, fatherPid, pid, jobName);
				runStat.startThreadStat(clientHost, portStats);
				runStat.updateStatOnJob(RunStat.JOBSTART, fatherNode);
			} catch (java.io.IOException ioException) {
				ioException.printStackTrace();
			}
		}

		java.util.concurrent.ConcurrentHashMap<Object, Object> concurrentHashMap = new java.util.concurrent.ConcurrentHashMap<Object, Object>();
		globalMap.put("concurrentHashMap", concurrentHashMap);

		long startUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long endUsedMemory = 0;
		long end = 0;

		startTime = System.currentTimeMillis();

		this.globalResumeTicket = true;// to run tPreJob

		if (enableLogStash) {
			talendJobLog.addJobStartMessage();
			try {
				talendJobLogProcess(globalMap);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		this.globalResumeTicket = false;// to run others jobs

		try {
			errorCode = null;
			tSalesforceConnection_1Process(globalMap);
			if (!"failure".equals(status)) {
				status = "end";
			}
		} catch (TalendException e_tSalesforceConnection_1) {
			globalMap.put("tSalesforceConnection_1_SUBPROCESS_STATE", -1);

			e_tSalesforceConnection_1.printStackTrace();

		}

		this.globalResumeTicket = true;// to run tPostJob

		end = System.currentTimeMillis();

		if (watch) {
			System.out.println((end - startTime) + " milliseconds");
		}

		endUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		if (false) {
			System.out.println((endUsedMemory - startUsedMemory) + " bytes memory increase when running : Salesforce");
		}
		if (enableLogStash) {
			talendJobLog.addJobEndMessage(startTime, end, status);
			try {
				talendJobLogProcess(globalMap);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		if (execStat) {
			runStat.updateStatOnJob(RunStat.JOBEND, fatherNode);
			runStat.stopThreadStat();
		}
		if (!inOSGi) {
			if (org.talend.metrics.CBPClient.getInstanceForCurrentVM() != null) {
				s("none");
				org.talend.metrics.CBPClient.getInstanceForCurrentVM().sendData();
			}
		}

		int returnCode = 0;

		if (errorCode == null) {
			returnCode = status != null && status.equals("failure") ? 1 : 0;
		} else {
			returnCode = errorCode.intValue();
		}
		resumeUtil.addLog("JOB_ENDED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "", "",
				"" + returnCode, "", "", "");
		resumeUtil.flush();

		org.slf4j.MDC.remove("_subJobName");
		org.slf4j.MDC.remove("_subJobPid");
		org.slf4j.MDC.remove("_systemPid");
		log.info("TalendJob: 'Salesforce' - Finished - status: " + status + " returnCode: " + returnCode);

		return returnCode;

	}

	// only for OSGi env
	public void destroy() {
		// add CBP code for OSGI Executions
		if (null != taskExecutionId && !taskExecutionId.isEmpty()) {
			try {
				org.talend.metrics.DataReadTracker.setExecutionId(taskExecutionId, jobExecutionId, false);
				org.talend.metrics.DataReadTracker.sealCounter();
				org.talend.metrics.DataReadTracker.reset();
			} catch (Exception | NoClassDefFoundError e) {
				// ignore
			}
		}

	}

	private java.util.Map<String, Object> getSharedConnections4REST() {
		java.util.Map<String, Object> connections = new java.util.HashMap<String, Object>();

		connections.put("tSalesforceConnection_1_connection", globalMap.get("tSalesforceConnection_1_connection"));
		connections.put("tSalesforceConnection_1_COMPONENT_RUNTIME_PROPERTIES",
				globalMap.get("tSalesforceConnection_1_COMPONENT_RUNTIME_PROPERTIES"));

		return connections;
	}

	private void evalParam(String arg) {
		if (arg.startsWith("--resuming_logs_dir_path")) {
			resuming_logs_dir_path = arg.substring(25);
		} else if (arg.startsWith("--resuming_checkpoint_path")) {
			resuming_checkpoint_path = arg.substring(27);
		} else if (arg.startsWith("--parent_part_launcher")) {
			parent_part_launcher = arg.substring(23);
		} else if (arg.startsWith("--watch")) {
			watch = true;
		} else if (arg.startsWith("--stat_port=")) {
			String portStatsStr = arg.substring(12);
			if (portStatsStr != null && !portStatsStr.equals("null")) {
				portStats = Integer.parseInt(portStatsStr);
			}
		} else if (arg.startsWith("--trace_port=")) {
			portTraces = Integer.parseInt(arg.substring(13));
		} else if (arg.startsWith("--client_host=")) {
			clientHost = arg.substring(14);
		} else if (arg.startsWith("--context=")) {
			contextStr = arg.substring(10);
			isDefaultContext = false;
		} else if (arg.startsWith("--father_pid=")) {
			fatherPid = arg.substring(13);
		} else if (arg.startsWith("--root_pid=")) {
			rootPid = arg.substring(11);
		} else if (arg.startsWith("--father_node=")) {
			fatherNode = arg.substring(14);
		} else if (arg.startsWith("--pid=")) {
			pid = arg.substring(6);
		} else if (arg.startsWith("--context_type")) {
			String keyValue = arg.substring(15);
			int index = -1;
			if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
				if (fatherPid == null) {
					context_param.setContextType(keyValue.substring(0, index),
							replaceEscapeChars(keyValue.substring(index + 1)));
				} else { // the subjob won't escape the especial chars
					context_param.setContextType(keyValue.substring(0, index), keyValue.substring(index + 1));
				}

			}

		} else if (arg.startsWith("--context_param")) {
			String keyValue = arg.substring(16);
			int index = -1;
			if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
				if (fatherPid == null) {
					context_param.put(keyValue.substring(0, index), replaceEscapeChars(keyValue.substring(index + 1)));
				} else { // the subjob won't escape the especial chars
					context_param.put(keyValue.substring(0, index), keyValue.substring(index + 1));
				}
			}
		} else if (arg.startsWith("--context_file")) {
			String keyValue = arg.substring(15);
			String filePath = new String(java.util.Base64.getDecoder().decode(keyValue));
			java.nio.file.Path contextFile = java.nio.file.Paths.get(filePath);
			try (java.io.BufferedReader reader = java.nio.file.Files.newBufferedReader(contextFile)) {
				String line;
				while ((line = reader.readLine()) != null) {
					int index = -1;
					if ((index = line.indexOf('=')) > -1) {
						if (line.startsWith("--context_param")) {
							if ("id_Password".equals(context_param.getContextType(line.substring(16, index)))) {
								context_param.put(line.substring(16, index),
										routines.system.PasswordEncryptUtil.decryptPassword(line.substring(index + 1)));
							} else {
								context_param.put(line.substring(16, index), line.substring(index + 1));
							}
						} else {// --context_type
							context_param.setContextType(line.substring(15, index), line.substring(index + 1));
						}
					}
				}
			} catch (java.io.IOException e) {
				System.err.println("Could not load the context file: " + filePath);
				e.printStackTrace();
			}
		} else if (arg.startsWith("--log4jLevel=")) {
			log4jLevel = arg.substring(13);
		} else if (arg.startsWith("--audit.enabled") && arg.contains("=")) {// for trunjob call
			final int equal = arg.indexOf('=');
			final String key = arg.substring("--".length(), equal);
			System.setProperty(key, arg.substring(equal + 1));
		}
	}

	private static final String NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY = "<TALEND_NULL>";

	private final String[][] escapeChars = { { "\\\\", "\\" }, { "\\n", "\n" }, { "\\'", "\'" }, { "\\r", "\r" },
			{ "\\f", "\f" }, { "\\b", "\b" }, { "\\t", "\t" } };

	private String replaceEscapeChars(String keyValue) {

		if (keyValue == null || ("").equals(keyValue.trim())) {
			return keyValue;
		}

		StringBuilder result = new StringBuilder();
		int currIndex = 0;
		while (currIndex < keyValue.length()) {
			int index = -1;
			// judege if the left string includes escape chars
			for (String[] strArray : escapeChars) {
				index = keyValue.indexOf(strArray[0], currIndex);
				if (index >= 0) {

					result.append(keyValue.substring(currIndex, index + strArray[0].length()).replace(strArray[0],
							strArray[1]));
					currIndex = index + strArray[0].length();
					break;
				}
			}
			// if the left string doesn't include escape chars, append the left into the
			// result
			if (index < 0) {
				result.append(keyValue.substring(currIndex));
				currIndex = currIndex + keyValue.length();
			}
		}

		return result.toString();
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getStatus() {
		return status;
	}

	ResumeUtil resumeUtil = null;
}
/************************************************************************************************
 * 348572 characters generated by Talend Cloud Data Management Platform on the
 * 21. veljače 2025. u 10:57:20 CET
 ************************************************************************************************/