
package salesforcetalend.input_0_1;

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
 * Job: Input Purpose: <br>
 * Description: <br>
 * 
 * @author vudavidgo@gmail.com
 * @version 8.0.1.20250126_0750-patch
 * @status
 */
public class Input implements TalendJob {
	static {
		System.setProperty("TalendJob.log", "Input.log");
	}

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Input.class);

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
	private final String jobName = "Input";
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
			"__kNg4PA7Ee-2rNEZYaVnnw", "0.1");
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
					Input.this.exception = e;
				}
			}
			if (!(e instanceof TalendException)) {
				try {
					for (java.lang.reflect.Method m : this.getClass().getEnclosingClass().getMethods()) {
						if (m.getName().compareTo(currentComponent + "_error") == 0) {
							m.invoke(Input.this, new Object[] { e, currentComponent, globalMap });
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

	public void tFixedFlowInput_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFixedFlowInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tMap_1_error(Exception exception, String errorComponent, final java.util.Map<String, Object> globalMap)
			throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFixedFlowInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tLogRow_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFixedFlowInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tSalesforceOutput_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFixedFlowInput_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tFixedFlowInput_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFixedFlowInput_2_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tSalesforceOutput_2_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFixedFlowInput_2_onSubJobError(exception, errorComponent, globalMap);
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

	public void tFixedFlowInput_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void tFixedFlowInput_2_onSubJobError(Exception exception, String errorComponent,
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

		mdc("tSalesforceConnection_1", "LYud8w_");

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
								"enc:routine.encryption.key.v1:fQ8go1ftQRia4Qk26OARoJ7s9bsk7GZHzShGEBBlHVqErJIN7gpLIeMeFpyN9ESj+UhIzoo="));

				props_tSalesforceConnection_1.userPassword.setValue("useAuth", false);

				props_tSalesforceConnection_1.userPassword.setValue("userId", "vudavidgo1@gmail.com");

				props_tSalesforceConnection_1.userPassword.setValue("password",
						routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:dCvTEtaBPAGhIxr0DN2t8+KUPnD9BBwNDgSee9Ib1ZQeleZ4"));

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
				resumeUtil.addLog("CHECKPOINT", "CONNECTION:SUBJOB_OK:tSalesforceConnection_1:OnSubjobOk1", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("OnSubjobOk1", 0, "ok");
			}

			tFixedFlowInput_1Process(globalMap);

			if (resumeEntryMethodName == null || globalResumeTicket) {
				resumeUtil.addLog("CHECKPOINT", "CONNECTION:SUBJOB_OK:tSalesforceConnection_1:OnSubjobOk2", "",
						Thread.currentThread().getId() + "", "", "", "", "", "");
			}

			if (execStat) {
				runStat.updateStatOnConnection("OnSubjobOk2", 0, "ok");
			}

			tFixedFlowInput_2Process(globalMap);

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

	public static class row3Struct implements routines.system.IPersistableRow<row3Struct> {
		final static byte[] commonByteArrayLock_SALESFORCETALEND_Input = new byte[0];
		static byte[] commonByteArray_SALESFORCETALEND_Input = new byte[0];

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
			return null;
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

		public String ExternalId__c;

		public String getExternalId__c() {
			return this.ExternalId__c;
		}

		public Boolean ExternalId__cIsNullable() {
			return true;
		}

		public Boolean ExternalId__cIsKey() {
			return false;
		}

		public Integer ExternalId__cLength() {
			return null;
		}

		public Integer ExternalId__cPrecision() {
			return null;
		}

		public String ExternalId__cDefault() {

			return null;

		}

		public String ExternalId__cComment() {

			return "";

		}

		public String ExternalId__cPattern() {

			return "";

		}

		public String ExternalId__cOriginalDbColumnName() {

			return "ExternalId__c";

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_SALESFORCETALEND_Input.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Input.length == 0) {
						commonByteArray_SALESFORCETALEND_Input = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Input = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_SALESFORCETALEND_Input, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Input, 0, length, utf8Charset);
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
				if (length > commonByteArray_SALESFORCETALEND_Input.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Input.length == 0) {
						commonByteArray_SALESFORCETALEND_Input = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Input = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_SALESFORCETALEND_Input, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Input, 0, length, utf8Charset);
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

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Input) {

				try {

					int length = 0;

					this.Name = readString(dis);

					this.ExternalId__c = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Input) {

				try {

					int length = 0;

					this.Name = readString(dis);

					this.ExternalId__c = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.ExternalId__c, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.ExternalId__c, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("Name=" + Name);
			sb.append(",ExternalId__c=" + ExternalId__c);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			if (Name == null) {
				sb.append("<null>");
			} else {
				sb.append(Name);
			}

			sb.append("|");

			if (ExternalId__c == null) {
				sb.append("<null>");
			} else {
				sb.append(ExternalId__c);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row3Struct other) {

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

	public static class mappingStruct implements routines.system.IPersistableRow<mappingStruct> {
		final static byte[] commonByteArrayLock_SALESFORCETALEND_Input = new byte[0];
		static byte[] commonByteArray_SALESFORCETALEND_Input = new byte[0];

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
			return null;
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

		public String ExternalId__c;

		public String getExternalId__c() {
			return this.ExternalId__c;
		}

		public Boolean ExternalId__cIsNullable() {
			return true;
		}

		public Boolean ExternalId__cIsKey() {
			return false;
		}

		public Integer ExternalId__cLength() {
			return null;
		}

		public Integer ExternalId__cPrecision() {
			return null;
		}

		public String ExternalId__cDefault() {

			return null;

		}

		public String ExternalId__cComment() {

			return "";

		}

		public String ExternalId__cPattern() {

			return "";

		}

		public String ExternalId__cOriginalDbColumnName() {

			return "ExternalId__c";

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_SALESFORCETALEND_Input.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Input.length == 0) {
						commonByteArray_SALESFORCETALEND_Input = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Input = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_SALESFORCETALEND_Input, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Input, 0, length, utf8Charset);
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
				if (length > commonByteArray_SALESFORCETALEND_Input.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Input.length == 0) {
						commonByteArray_SALESFORCETALEND_Input = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Input = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_SALESFORCETALEND_Input, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Input, 0, length, utf8Charset);
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

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Input) {

				try {

					int length = 0;

					this.Name = readString(dis);

					this.ExternalId__c = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Input) {

				try {

					int length = 0;

					this.Name = readString(dis);

					this.ExternalId__c = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.ExternalId__c, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.ExternalId__c, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("Name=" + Name);
			sb.append(",ExternalId__c=" + ExternalId__c);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			if (Name == null) {
				sb.append("<null>");
			} else {
				sb.append(Name);
			}

			sb.append("|");

			if (ExternalId__c == null) {
				sb.append("<null>");
			} else {
				sb.append(ExternalId__c);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(mappingStruct other) {

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

	public static class row1Struct implements routines.system.IPersistableRow<row1Struct> {
		final static byte[] commonByteArrayLock_SALESFORCETALEND_Input = new byte[0];
		static byte[] commonByteArray_SALESFORCETALEND_Input = new byte[0];

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
			return null;
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

		public String ExternalId__c;

		public String getExternalId__c() {
			return this.ExternalId__c;
		}

		public Boolean ExternalId__cIsNullable() {
			return true;
		}

		public Boolean ExternalId__cIsKey() {
			return false;
		}

		public Integer ExternalId__cLength() {
			return null;
		}

		public Integer ExternalId__cPrecision() {
			return null;
		}

		public String ExternalId__cDefault() {

			return null;

		}

		public String ExternalId__cComment() {

			return "";

		}

		public String ExternalId__cPattern() {

			return "";

		}

		public String ExternalId__cOriginalDbColumnName() {

			return "ExternalId__c";

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_SALESFORCETALEND_Input.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Input.length == 0) {
						commonByteArray_SALESFORCETALEND_Input = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Input = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_SALESFORCETALEND_Input, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Input, 0, length, utf8Charset);
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
				if (length > commonByteArray_SALESFORCETALEND_Input.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Input.length == 0) {
						commonByteArray_SALESFORCETALEND_Input = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Input = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_SALESFORCETALEND_Input, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Input, 0, length, utf8Charset);
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

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Input) {

				try {

					int length = 0;

					this.Name = readString(dis);

					this.ExternalId__c = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Input) {

				try {

					int length = 0;

					this.Name = readString(dis);

					this.ExternalId__c = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.ExternalId__c, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// String

				writeString(this.Name, dos);

				// String

				writeString(this.ExternalId__c, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("Name=" + Name);
			sb.append(",ExternalId__c=" + ExternalId__c);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			if (Name == null) {
				sb.append("<null>");
			} else {
				sb.append(Name);
			}

			sb.append("|");

			if (ExternalId__c == null) {
				sb.append("<null>");
			} else {
				sb.append(ExternalId__c);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row1Struct other) {

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

	public void tFixedFlowInput_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tFixedFlowInput_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("tFixedFlowInput_1", "iN1CUN_");

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

				row1Struct row1 = new row1Struct();
				mappingStruct mapping = new mappingStruct();
				mappingStruct row3 = mapping;

				/**
				 * [tSalesforceOutput_1 begin ] start
				 */

				sh("tSalesforceOutput_1");

				s(currentComponent = "tSalesforceOutput_1");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0, "row3");

				int tos_count_tSalesforceOutput_1 = 0;

				if (enableLogStash) {
					talendJobLog.addCM("tSalesforceOutput_1", "tSalesforceOutput_1", "tSalesforceOutput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				boolean doesNodeBelongToRequest_tSalesforceOutput_1 = 0 == 0;
				@SuppressWarnings("unchecked")
				java.util.Map<String, Object> restRequest_tSalesforceOutput_1 = (java.util.Map<String, Object>) globalMap
						.get("restRequest");
				String currentTRestRequestOperation_tSalesforceOutput_1 = (String) (restRequest_tSalesforceOutput_1 != null
						? restRequest_tSalesforceOutput_1.get("OPERATION")
						: null);

				org.talend.components.api.component.ComponentDefinition def_tSalesforceOutput_1 = new org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputDefinition();

				org.talend.components.api.component.runtime.Writer writer_tSalesforceOutput_1 = null;
				org.talend.components.api.component.runtime.Reader reader_tSalesforceOutput_1 = null;

				org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputProperties props_tSalesforceOutput_1 = (org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputProperties) def_tSalesforceOutput_1
						.createRuntimeProperties();
				props_tSalesforceOutput_1.setValue("extendInsert", true);

				props_tSalesforceOutput_1.setValue("ceaseForError", true);

				props_tSalesforceOutput_1.setValue("commitLevel", 200);

				props_tSalesforceOutput_1.setValue("dataTimeUTC", true);

				props_tSalesforceOutput_1.setValue("logFileName", "");

				props_tSalesforceOutput_1.setValue("outputAction",
						org.talend.components.salesforce.SalesforceOutputProperties.OutputAction.INSERT);

				class SchemaSettingTool_tSalesforceOutput_1_1_fisrt {

					String getSchemaValue() {

						StringBuilder s = new StringBuilder();

						a("{\"type\":\"record\",", s);

						a("\"name\":\"mapping\",\"fields\":[{", s);

						a("\"name\":\"Name\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbType\":\"\",\"talend.field.dbColumnName\":\"Name\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Name\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ExternalId__c\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbType\":\"\",\"talend.field.dbColumnName\":\"ExternalId__c\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ExternalId__c\",\"di.column.relatedEntity\":\"\"}],\"di.table.name\":\"mapping\",\"di.table.label\":\"mapping\"}",
								s);

						return s.toString();

					}

					void a(String part, StringBuilder strB) {
						strB.append(part);
					}

				}

				SchemaSettingTool_tSalesforceOutput_1_1_fisrt sst_tSalesforceOutput_1_1_fisrt = new SchemaSettingTool_tSalesforceOutput_1_1_fisrt();

				props_tSalesforceOutput_1.schemaFlow.setValue("schema", new org.apache.avro.Schema.Parser()
						.setValidateDefaults(false).parse(sst_tSalesforceOutput_1_1_fisrt.getSchemaValue()));

				class SchemaSettingTool_tSalesforceOutput_1_2_fisrt {

					String getSchemaValue() {

						StringBuilder s = new StringBuilder();

						a("{\"type\":\"record\",", s);

						a("\"name\":\"rejectOutput\",\"fields\":[{", s);

						a("\"name\":\"Name\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbType\":\"\",\"talend.field.dbColumnName\":\"Name\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Name\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ExternalId__c\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbType\":\"\",\"talend.field.dbColumnName\":\"ExternalId__c\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ExternalId__c\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"errorCode\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"errorFields\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"errorMessage\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"}],\"di.table.name\":\"mapping\",\"di.table.label\":\"mapping\"}",
								s);

						return s.toString();

					}

					void a(String part, StringBuilder strB) {
						strB.append(part);
					}

				}

				SchemaSettingTool_tSalesforceOutput_1_2_fisrt sst_tSalesforceOutput_1_2_fisrt = new SchemaSettingTool_tSalesforceOutput_1_2_fisrt();

				props_tSalesforceOutput_1.schemaReject.setValue("schema", new org.apache.avro.Schema.Parser()
						.setValidateDefaults(false).parse(sst_tSalesforceOutput_1_2_fisrt.getSchemaValue()));

				props_tSalesforceOutput_1.connection.userPassword.setValue("useAuth", false);

				props_tSalesforceOutput_1.connection.proxy.userPassword.setValue("useAuth", false);

				props_tSalesforceOutput_1.connection.referencedComponent.setValue("referenceType",
						org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE);

				props_tSalesforceOutput_1.connection.referencedComponent.setValue("componentInstanceId",
						"tSalesforceConnection_1");

				props_tSalesforceOutput_1.connection.referencedComponent.setValue("referenceDefinitionName",
						"tSalesforceConnection");

				props_tSalesforceOutput_1.module.setValue("moduleName", "Account");

				props_tSalesforceOutput_1.module.connection.userPassword.setValue("useAuth", false);

				props_tSalesforceOutput_1.module.connection.proxy.userPassword.setValue("useAuth", false);

				props_tSalesforceOutput_1.module.connection.referencedComponent.setValue("referenceType",
						org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE);

				props_tSalesforceOutput_1.module.connection.referencedComponent.setValue("componentInstanceId",
						"tSalesforceConnection_1");

				props_tSalesforceOutput_1.module.connection.referencedComponent.setValue("referenceDefinitionName",
						"tSalesforceConnection");

				class SchemaSettingTool_tSalesforceOutput_1_3_fisrt {

					String getSchemaValue() {

						StringBuilder s = new StringBuilder();

						a("{\"type\":\"record\",", s);

						a("\"name\":\"mapping\",\"fields\":[{", s);

						a("\"name\":\"Name\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbType\":\"\",\"talend.field.dbColumnName\":\"Name\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"Name\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ExternalId__c\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbType\":\"\",\"talend.field.dbColumnName\":\"ExternalId__c\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"ExternalId__c\",\"di.column.relatedEntity\":\"\"}],\"di.table.name\":\"mapping\",\"di.table.label\":\"mapping\"}",
								s);

						return s.toString();

					}

					void a(String part, StringBuilder strB) {
						strB.append(part);
					}

				}

				SchemaSettingTool_tSalesforceOutput_1_3_fisrt sst_tSalesforceOutput_1_3_fisrt = new SchemaSettingTool_tSalesforceOutput_1_3_fisrt();

				props_tSalesforceOutput_1.module.main.setValue("schema", new org.apache.avro.Schema.Parser()
						.setValidateDefaults(false).parse(sst_tSalesforceOutput_1_3_fisrt.getSchemaValue()));

				if (org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE == props_tSalesforceOutput_1.connection.referencedComponent.referenceType
						.getValue()) {
					final String referencedComponentInstanceId_tSalesforceOutput_1 = props_tSalesforceOutput_1.connection.referencedComponent.componentInstanceId
							.getStringValue();
					if (referencedComponentInstanceId_tSalesforceOutput_1 != null) {
						org.talend.daikon.properties.Properties referencedComponentProperties_tSalesforceOutput_1 = (org.talend.daikon.properties.Properties) globalMap
								.get(referencedComponentInstanceId_tSalesforceOutput_1
										+ "_COMPONENT_RUNTIME_PROPERTIES");
						props_tSalesforceOutput_1.connection.referencedComponent
								.setReference(referencedComponentProperties_tSalesforceOutput_1);
					}
				}
				if (org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE == props_tSalesforceOutput_1.module.connection.referencedComponent.referenceType
						.getValue()) {
					final String referencedComponentInstanceId_tSalesforceOutput_1 = props_tSalesforceOutput_1.module.connection.referencedComponent.componentInstanceId
							.getStringValue();
					if (referencedComponentInstanceId_tSalesforceOutput_1 != null) {
						org.talend.daikon.properties.Properties referencedComponentProperties_tSalesforceOutput_1 = (org.talend.daikon.properties.Properties) globalMap
								.get(referencedComponentInstanceId_tSalesforceOutput_1
										+ "_COMPONENT_RUNTIME_PROPERTIES");
						props_tSalesforceOutput_1.module.connection.referencedComponent
								.setReference(referencedComponentProperties_tSalesforceOutput_1);
					}
				}
				globalMap.put("tSalesforceOutput_1_COMPONENT_RUNTIME_PROPERTIES", props_tSalesforceOutput_1);
				globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
				globalMap.put("TALEND_COMPONENTS_VERSION", "0.37.41");
				java.net.URL mappings_url_tSalesforceOutput_1 = this.getClass().getResource("/xmlMappings");
				globalMap.put("tSalesforceOutput_1_MAPPINGS_URL", mappings_url_tSalesforceOutput_1);

				org.talend.components.api.container.RuntimeContainer container_tSalesforceOutput_1 = new org.talend.components.api.container.RuntimeContainer() {
					public Object getComponentData(String componentId, String key) {
						return globalMap.get(componentId + "_" + key);
					}

					public void setComponentData(String componentId, String key, Object data) {
						globalMap.put(componentId + "_" + key, data);
					}

					public String getCurrentComponentId() {
						return "tSalesforceOutput_1";
					}

					public Object getGlobalData(String key) {
						return globalMap.get(key);
					}
				};

				int nb_line_tSalesforceOutput_1 = 0;

				org.talend.components.api.component.ConnectorTopology topology_tSalesforceOutput_1 = null;
				topology_tSalesforceOutput_1 = org.talend.components.api.component.ConnectorTopology.INCOMING;

				org.talend.daikon.runtime.RuntimeInfo runtime_info_tSalesforceOutput_1 = def_tSalesforceOutput_1
						.getRuntimeInfo(org.talend.components.api.component.runtime.ExecutionEngine.DI,
								props_tSalesforceOutput_1, topology_tSalesforceOutput_1);
				java.util.Set<org.talend.components.api.component.ConnectorTopology> supported_connector_topologies_tSalesforceOutput_1 = def_tSalesforceOutput_1
						.getSupportedConnectorTopologies();

				org.talend.components.api.component.runtime.RuntimableRuntime componentRuntime_tSalesforceOutput_1 = (org.talend.components.api.component.runtime.RuntimableRuntime) (Class
						.forName(runtime_info_tSalesforceOutput_1.getRuntimeClassName()).newInstance());
				org.talend.daikon.properties.ValidationResult initVr_tSalesforceOutput_1 = componentRuntime_tSalesforceOutput_1
						.initialize(container_tSalesforceOutput_1, props_tSalesforceOutput_1);

				if (initVr_tSalesforceOutput_1
						.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
					throw new RuntimeException(initVr_tSalesforceOutput_1.getMessage());
				}

				if (componentRuntime_tSalesforceOutput_1 instanceof org.talend.components.api.component.runtime.ComponentDriverInitialization) {
					org.talend.components.api.component.runtime.ComponentDriverInitialization compDriverInitialization_tSalesforceOutput_1 = (org.talend.components.api.component.runtime.ComponentDriverInitialization) componentRuntime_tSalesforceOutput_1;
					compDriverInitialization_tSalesforceOutput_1.runAtDriver(container_tSalesforceOutput_1);
				}

				org.talend.components.api.component.runtime.SourceOrSink sourceOrSink_tSalesforceOutput_1 = null;
				if (componentRuntime_tSalesforceOutput_1 instanceof org.talend.components.api.component.runtime.SourceOrSink) {
					sourceOrSink_tSalesforceOutput_1 = (org.talend.components.api.component.runtime.SourceOrSink) componentRuntime_tSalesforceOutput_1;
					if (doesNodeBelongToRequest_tSalesforceOutput_1) {
						org.talend.daikon.properties.ValidationResult vr_tSalesforceOutput_1 = sourceOrSink_tSalesforceOutput_1
								.validate(container_tSalesforceOutput_1);
						if (vr_tSalesforceOutput_1
								.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
							throw new RuntimeException(vr_tSalesforceOutput_1.getMessage());
						}
					}
				}

				org.talend.codegen.enforcer.IncomingSchemaEnforcer incomingEnforcer_tSalesforceOutput_1 = null;
				if (sourceOrSink_tSalesforceOutput_1 instanceof org.talend.components.api.component.runtime.Sink) {
					org.talend.components.api.component.runtime.Sink sink_tSalesforceOutput_1 = (org.talend.components.api.component.runtime.Sink) sourceOrSink_tSalesforceOutput_1;
					org.talend.components.api.component.runtime.WriteOperation writeOperation_tSalesforceOutput_1 = sink_tSalesforceOutput_1
							.createWriteOperation();
					if (doesNodeBelongToRequest_tSalesforceOutput_1) {
						writeOperation_tSalesforceOutput_1.initialize(container_tSalesforceOutput_1);
					}
					writer_tSalesforceOutput_1 = writeOperation_tSalesforceOutput_1
							.createWriter(container_tSalesforceOutput_1);
					if (doesNodeBelongToRequest_tSalesforceOutput_1) {
						writer_tSalesforceOutput_1.open("tSalesforceOutput_1");
					}

					resourceMap.put("writer_tSalesforceOutput_1", writer_tSalesforceOutput_1);
				} // end of "sourceOrSink_tSalesforceOutput_1 instanceof ...Sink"
				org.talend.components.api.component.Connector c_tSalesforceOutput_1 = null;
				for (org.talend.components.api.component.Connector currentConnector : props_tSalesforceOutput_1
						.getAvailableConnectors(null, false)) {
					if (currentConnector.getName().equals("MAIN")) {
						c_tSalesforceOutput_1 = currentConnector;
						break;
					}
				}
				org.apache.avro.Schema designSchema_tSalesforceOutput_1 = props_tSalesforceOutput_1
						.getSchema(c_tSalesforceOutput_1, false);
				incomingEnforcer_tSalesforceOutput_1 = new org.talend.codegen.enforcer.IncomingSchemaEnforcer(
						designSchema_tSalesforceOutput_1);

				java.lang.Iterable<?> outgoingMainRecordsList_tSalesforceOutput_1 = new java.util.ArrayList<Object>();
				java.util.Iterator outgoingMainRecordsIt_tSalesforceOutput_1 = null;

				/**
				 * [tSalesforceOutput_1 begin ] stop
				 */

				/**
				 * [tLogRow_1 begin ] start
				 */

				sh("tLogRow_1");

				s(currentComponent = "tLogRow_1");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0, "mapping");

				int tos_count_tLogRow_1 = 0;

				if (log.isDebugEnabled())
					log.debug("tLogRow_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_tLogRow_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_tLogRow_1 = new StringBuilder();
							log4jParamters_tLogRow_1.append("Parameters:");
							log4jParamters_tLogRow_1.append("BASIC_MODE" + " = " + "false");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("TABLE_PRINT" + " = " + "true");
							log4jParamters_tLogRow_1.append(" | ");
							log4jParamters_tLogRow_1.append("VERTICAL" + " = " + "false");
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

				class Util_tLogRow_1 {

					String[] des_top = { ".", ".", "-", "+" };

					String[] des_head = { "|=", "=|", "-", "+" };

					String[] des_bottom = { "'", "'", "-", "+" };

					String name = "";

					java.util.List<String[]> list = new java.util.ArrayList<String[]>();

					int[] colLengths = new int[2];

					public void addRow(String[] row) {

						for (int i = 0; i < 2; i++) {
							if (row[i] != null) {
								colLengths[i] = Math.max(colLengths[i], row[i].length());
							}
						}
						list.add(row);
					}

					public void setTableName(String name) {

						this.name = name;
					}

					public StringBuilder format() {

						StringBuilder sb = new StringBuilder();

						sb.append(print(des_top));

						int totals = 0;
						for (int i = 0; i < colLengths.length; i++) {
							totals = totals + colLengths[i];
						}

						// name
						sb.append("|");
						int k = 0;
						for (k = 0; k < (totals + 1 - name.length()) / 2; k++) {
							sb.append(' ');
						}
						sb.append(name);
						for (int i = 0; i < totals + 1 - name.length() - k; i++) {
							sb.append(' ');
						}
						sb.append("|\n");

						// head and rows
						sb.append(print(des_head));
						for (int i = 0; i < list.size(); i++) {

							String[] row = list.get(i);

							java.util.Formatter formatter = new java.util.Formatter(new StringBuilder());

							StringBuilder sbformat = new StringBuilder();
							sbformat.append("|%1$-");
							sbformat.append(colLengths[0]);
							sbformat.append("s");

							sbformat.append("|%2$-");
							sbformat.append(colLengths[1]);
							sbformat.append("s");

							sbformat.append("|\n");

							formatter.format(sbformat.toString(), (Object[]) row);

							sb.append(formatter.toString());
							if (i == 0)
								sb.append(print(des_head)); // print the head
						}

						// end
						sb.append(print(des_bottom));
						return sb;
					}

					private StringBuilder print(String[] fillChars) {
						StringBuilder sb = new StringBuilder();
						// first column
						sb.append(fillChars[0]);
						for (int i = 0; i < colLengths[0] - fillChars[0].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[3]);

						// last column
						for (int i = 0; i < colLengths[1] - fillChars[1].length() + 1; i++) {
							sb.append(fillChars[2]);
						}
						sb.append(fillChars[1]);
						sb.append("\n");
						return sb;
					}

					public boolean isTableEmpty() {
						if (list.size() > 1)
							return false;
						return true;
					}
				}
				Util_tLogRow_1 util_tLogRow_1 = new Util_tLogRow_1();
				util_tLogRow_1.setTableName("tLogRow_1");
				util_tLogRow_1.addRow(new String[] { "Name", "ExternalId__c", });
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

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0, "row1");

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
				int count_row1_tMap_1 = 0;

// ###############################        

// ###############################
// # Vars initialization
				class Var__tMap_1__Struct {
				}
				Var__tMap_1__Struct Var__tMap_1 = new Var__tMap_1__Struct();
// ###############################

// ###############################
// # Outputs initialization
				int count_mapping_tMap_1 = 0;

				mappingStruct mapping_tmp = new mappingStruct();
// ###############################

				/**
				 * [tMap_1 begin ] stop
				 */

				/**
				 * [tFixedFlowInput_1 begin ] start
				 */

				sh("tFixedFlowInput_1");

				s(currentComponent = "tFixedFlowInput_1");

				int tos_count_tFixedFlowInput_1 = 0;

				if (enableLogStash) {
					talendJobLog.addCM("tFixedFlowInput_1", "tFixedFlowInput_1", "tFixedFlowInput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_tFixedFlowInput_1 = 0;
				List<row1Struct> cacheList_tFixedFlowInput_1 = new java.util.ArrayList<row1Struct>();
				row1 = new row1Struct();
				row1.Name = "TestniAccount";
				row1.ExternalId__c = "123456789";
				cacheList_tFixedFlowInput_1.add(row1);
				for (int i_tFixedFlowInput_1 = 0; i_tFixedFlowInput_1 < 1; i_tFixedFlowInput_1++) {
					for (row1Struct tmpRow_tFixedFlowInput_1 : cacheList_tFixedFlowInput_1) {
						nb_line_tFixedFlowInput_1++;
						row1 = tmpRow_tFixedFlowInput_1;

						/**
						 * [tFixedFlowInput_1 begin ] stop
						 */

						/**
						 * [tFixedFlowInput_1 main ] start
						 */

						s(currentComponent = "tFixedFlowInput_1");

						tos_count_tFixedFlowInput_1++;

						/**
						 * [tFixedFlowInput_1 main ] stop
						 */

						/**
						 * [tFixedFlowInput_1 process_data_begin ] start
						 */

						s(currentComponent = "tFixedFlowInput_1");

						/**
						 * [tFixedFlowInput_1 process_data_begin ] stop
						 */

						/**
						 * [tMap_1 main ] start
						 */

						s(currentComponent = "tMap_1");

						if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

								, "row1", "tFixedFlowInput_1", "tFixedFlowInput_1", "tFixedFlowInput", "tMap_1",
								"tMap_1", "tMap"

						)) {
							talendJobLogProcess(globalMap);
						}

						if (log.isTraceEnabled()) {
							log.trace("row1 - " + (row1 == null ? "" : row1.toLogString()));
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

							mapping = null;

// # Output table : 'mapping'
							count_mapping_tMap_1++;

							mapping_tmp.Name = row1.Name;
							mapping_tmp.ExternalId__c = row1.ExternalId__c;
							mapping = mapping_tmp;
							log.debug("tMap_1 - Outputting the record " + count_mapping_tMap_1
									+ " of the output table 'mapping'.");

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

// Start of branch "mapping"
						if (mapping != null) {

							/**
							 * [tLogRow_1 main ] start
							 */

							s(currentComponent = "tLogRow_1");

							if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

									, "mapping", "tMap_1", "tMap_1", "tMap", "tLogRow_1", "tLogRow_1", "tLogRow"

							)) {
								talendJobLogProcess(globalMap);
							}

							if (log.isTraceEnabled()) {
								log.trace("mapping - " + (mapping == null ? "" : mapping.toLogString()));
							}

///////////////////////		

							String[] row_tLogRow_1 = new String[2];

							if (mapping.Name != null) { //
								row_tLogRow_1[0] = String.valueOf(mapping.Name);

							} //

							if (mapping.ExternalId__c != null) { //
								row_tLogRow_1[1] = String.valueOf(mapping.ExternalId__c);

							} //

							util_tLogRow_1.addRow(row_tLogRow_1);
							nb_line_tLogRow_1++;
							log.info("tLogRow_1 - Content of row " + nb_line_tLogRow_1 + ": "
									+ TalendString.unionString("|", row_tLogRow_1));
//////

//////                    

///////////////////////    			

							row3 = mapping;

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
							 * [tSalesforceOutput_1 main ] start
							 */

							s(currentComponent = "tSalesforceOutput_1");

							if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

									, "row3", "tLogRow_1", "tLogRow_1", "tLogRow", "tSalesforceOutput_1",
									"tSalesforceOutput_1", "tSalesforceOutput"

							)) {
								talendJobLogProcess(globalMap);
							}

							if (log.isTraceEnabled()) {
								log.trace("row3 - " + (row3 == null ? "" : row3.toLogString()));
							}

							if (incomingEnforcer_tSalesforceOutput_1 != null) {
								incomingEnforcer_tSalesforceOutput_1.createNewRecord();
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tSalesforceOutput_1 != null && incomingEnforcer_tSalesforceOutput_1
									.getRuntimeSchema().getField("Name") != null) {
								incomingEnforcer_tSalesforceOutput_1.put("Name", row3.Name);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tSalesforceOutput_1 != null && incomingEnforcer_tSalesforceOutput_1
									.getRuntimeSchema().getField("ExternalId__c") != null) {
								incomingEnforcer_tSalesforceOutput_1.put("ExternalId__c", row3.ExternalId__c);
							}

							org.apache.avro.generic.IndexedRecord data_tSalesforceOutput_1 = null;
							if (incomingEnforcer_tSalesforceOutput_1 != null) {
								data_tSalesforceOutput_1 = incomingEnforcer_tSalesforceOutput_1.getCurrentRecord();
							}

							if (writer_tSalesforceOutput_1 != null && data_tSalesforceOutput_1 != null) {
								writer_tSalesforceOutput_1.write(data_tSalesforceOutput_1);
							}

							nb_line_tSalesforceOutput_1++;

							tos_count_tSalesforceOutput_1++;

							/**
							 * [tSalesforceOutput_1 main ] stop
							 */

							/**
							 * [tSalesforceOutput_1 process_data_begin ] start
							 */

							s(currentComponent = "tSalesforceOutput_1");

							/**
							 * [tSalesforceOutput_1 process_data_begin ] stop
							 */

							/**
							 * [tSalesforceOutput_1 process_data_end ] start
							 */

							s(currentComponent = "tSalesforceOutput_1");

							/**
							 * [tSalesforceOutput_1 process_data_end ] stop
							 */

							/**
							 * [tLogRow_1 process_data_end ] start
							 */

							s(currentComponent = "tLogRow_1");

							/**
							 * [tLogRow_1 process_data_end ] stop
							 */

						} // End of branch "mapping"

						/**
						 * [tMap_1 process_data_end ] start
						 */

						s(currentComponent = "tMap_1");

						/**
						 * [tMap_1 process_data_end ] stop
						 */

						/**
						 * [tFixedFlowInput_1 process_data_end ] start
						 */

						s(currentComponent = "tFixedFlowInput_1");

						/**
						 * [tFixedFlowInput_1 process_data_end ] stop
						 */

						/**
						 * [tFixedFlowInput_1 end ] start
						 */

						s(currentComponent = "tFixedFlowInput_1");

					}
				}
				cacheList_tFixedFlowInput_1.clear();
				globalMap.put("tFixedFlowInput_1_NB_LINE", nb_line_tFixedFlowInput_1);

				ok_Hash.put("tFixedFlowInput_1", true);
				end_Hash.put("tFixedFlowInput_1", System.currentTimeMillis());

				/**
				 * [tFixedFlowInput_1 end ] stop
				 */

				/**
				 * [tMap_1 end ] start
				 */

				s(currentComponent = "tMap_1");

// ###############################
// # Lookup hashes releasing
// ###############################      
				log.debug("tMap_1 - Written records count in the table 'mapping': " + count_mapping_tMap_1 + ".");

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, "row1", 2, 0,
						"tFixedFlowInput_1", "tFixedFlowInput_1", "tFixedFlowInput", "tMap_1", "tMap_1", "tMap",
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

				java.io.PrintStream consoleOut_tLogRow_1 = null;
				if (globalMap.get("tLogRow_CONSOLE") != null) {
					consoleOut_tLogRow_1 = (java.io.PrintStream) globalMap.get("tLogRow_CONSOLE");
				} else {
					consoleOut_tLogRow_1 = new java.io.PrintStream(new java.io.BufferedOutputStream(System.out));
					globalMap.put("tLogRow_CONSOLE", consoleOut_tLogRow_1);
				}

				consoleOut_tLogRow_1.println(util_tLogRow_1.format().toString());
				consoleOut_tLogRow_1.flush();
//////
				globalMap.put("tLogRow_1_NB_LINE", nb_line_tLogRow_1);
				if (log.isInfoEnabled())
					log.info("tLogRow_1 - " + ("Printed row count: ") + (nb_line_tLogRow_1) + ("."));

///////////////////////    			

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, "mapping", 2, 0,
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

				/**
				 * [tSalesforceOutput_1 end ] start
				 */

				s(currentComponent = "tSalesforceOutput_1");

// end of generic

				resourceMap.put("finish_tSalesforceOutput_1", Boolean.TRUE);

				java.util.Map<String, Object> resultMap_tSalesforceOutput_1 = null;
				if (writer_tSalesforceOutput_1 != null) {
					org.talend.components.api.component.runtime.Result resultObject_tSalesforceOutput_1 = (org.talend.components.api.component.runtime.Result) writer_tSalesforceOutput_1
							.close();
					resultMap_tSalesforceOutput_1 = writer_tSalesforceOutput_1.getWriteOperation()
							.finalize(java.util.Arrays.<org.talend.components.api.component.runtime.Result>asList(
									resultObject_tSalesforceOutput_1), container_tSalesforceOutput_1);
				}
				if (resultMap_tSalesforceOutput_1 != null) {
					for (java.util.Map.Entry<String, Object> entry_tSalesforceOutput_1 : resultMap_tSalesforceOutput_1
							.entrySet()) {
						switch (entry_tSalesforceOutput_1.getKey()) {
						case org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE:
							container_tSalesforceOutput_1.setComponentData("tSalesforceOutput_1", "ERROR_MESSAGE",
									entry_tSalesforceOutput_1.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_TOTAL_RECORD_COUNT:
							container_tSalesforceOutput_1.setComponentData("tSalesforceOutput_1", "NB_LINE",
									entry_tSalesforceOutput_1.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_SUCCESS_RECORD_COUNT:
							container_tSalesforceOutput_1.setComponentData("tSalesforceOutput_1", "NB_SUCCESS",
									entry_tSalesforceOutput_1.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_REJECT_RECORD_COUNT:
							container_tSalesforceOutput_1.setComponentData("tSalesforceOutput_1", "NB_REJECT",
									entry_tSalesforceOutput_1.getValue());
							break;
						default:
							StringBuilder studio_key_tSalesforceOutput_1 = new StringBuilder();
							for (int i_tSalesforceOutput_1 = 0; i_tSalesforceOutput_1 < entry_tSalesforceOutput_1
									.getKey().length(); i_tSalesforceOutput_1++) {
								char ch_tSalesforceOutput_1 = entry_tSalesforceOutput_1.getKey()
										.charAt(i_tSalesforceOutput_1);
								if (Character.isUpperCase(ch_tSalesforceOutput_1) && i_tSalesforceOutput_1 > 0) {
									studio_key_tSalesforceOutput_1.append('_');
								}
								studio_key_tSalesforceOutput_1.append(ch_tSalesforceOutput_1);
							}
							container_tSalesforceOutput_1.setComponentData("tSalesforceOutput_1",
									studio_key_tSalesforceOutput_1.toString().toUpperCase(java.util.Locale.ENGLISH),
									entry_tSalesforceOutput_1.getValue());
							break;
						}
					}
				}

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, "row3", 2, 0,
						"tLogRow_1", "tLogRow_1", "tLogRow", "tSalesforceOutput_1", "tSalesforceOutput_1",
						"tSalesforceOutput", "output")) {
					talendJobLogProcess(globalMap);
				}

				ok_Hash.put("tSalesforceOutput_1", true);
				end_Hash.put("tSalesforceOutput_1", System.currentTimeMillis());

				/**
				 * [tSalesforceOutput_1 end ] stop
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
				 * [tFixedFlowInput_1 finally ] start
				 */

				s(currentComponent = "tFixedFlowInput_1");

				/**
				 * [tFixedFlowInput_1 finally ] stop
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

				/**
				 * [tSalesforceOutput_1 finally ] start
				 */

				s(currentComponent = "tSalesforceOutput_1");

// finally of generic

				if (resourceMap.get("finish_tSalesforceOutput_1") == null) {
					if (resourceMap.get("writer_tSalesforceOutput_1") != null) {
						try {
							((org.talend.components.api.component.runtime.Writer) resourceMap
									.get("writer_tSalesforceOutput_1")).close();
						} catch (java.io.IOException e_tSalesforceOutput_1) {
							String errorMessage_tSalesforceOutput_1 = "failed to release the resource in tSalesforceOutput_1 :"
									+ e_tSalesforceOutput_1.getMessage();
							System.err.println(errorMessage_tSalesforceOutput_1);
						}
					}
				}

				/**
				 * [tSalesforceOutput_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tFixedFlowInput_1_SUBPROCESS_STATE", 1);
	}

	public static class row2Struct implements routines.system.IPersistableRow<row2Struct> {
		final static byte[] commonByteArrayLock_SALESFORCETALEND_Input = new byte[0];
		static byte[] commonByteArray_SALESFORCETALEND_Input = new byte[0];

		public String LastName;

		public String getLastName() {
			return this.LastName;
		}

		public Boolean LastNameIsNullable() {
			return false;
		}

		public Boolean LastNameIsKey() {
			return false;
		}

		public Integer LastNameLength() {
			return 80;
		}

		public Integer LastNamePrecision() {
			return null;
		}

		public String LastNameDefault() {

			return null;

		}

		public String LastNameComment() {

			return "";

		}

		public String LastNamePattern() {

			return "";

		}

		public String LastNameOriginalDbColumnName() {

			return "LastName";

		}

		public String FirstName;

		public String getFirstName() {
			return this.FirstName;
		}

		public Boolean FirstNameIsNullable() {
			return true;
		}

		public Boolean FirstNameIsKey() {
			return false;
		}

		public Integer FirstNameLength() {
			return 40;
		}

		public Integer FirstNamePrecision() {
			return null;
		}

		public String FirstNameDefault() {

			return null;

		}

		public String FirstNameComment() {

			return "";

		}

		public String FirstNamePattern() {

			return "";

		}

		public String FirstNameOriginalDbColumnName() {

			return "FirstName";

		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_SALESFORCETALEND_Input.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Input.length == 0) {
						commonByteArray_SALESFORCETALEND_Input = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Input = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_SALESFORCETALEND_Input, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Input, 0, length, utf8Charset);
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
				if (length > commonByteArray_SALESFORCETALEND_Input.length) {
					if (length < 1024 && commonByteArray_SALESFORCETALEND_Input.length == 0) {
						commonByteArray_SALESFORCETALEND_Input = new byte[1024];
					} else {
						commonByteArray_SALESFORCETALEND_Input = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_SALESFORCETALEND_Input, 0, length);
				strReturn = new String(commonByteArray_SALESFORCETALEND_Input, 0, length, utf8Charset);
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

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Input) {

				try {

					int length = 0;

					this.LastName = readString(dis);

					this.FirstName = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_SALESFORCETALEND_Input) {

				try {

					int length = 0;

					this.LastName = readString(dis);

					this.FirstName = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// String

				writeString(this.LastName, dos);

				// String

				writeString(this.FirstName, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// String

				writeString(this.LastName, dos);

				// String

				writeString(this.FirstName, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("LastName=" + LastName);
			sb.append(",FirstName=" + FirstName);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			if (LastName == null) {
				sb.append("<null>");
			} else {
				sb.append(LastName);
			}

			sb.append("|");

			if (FirstName == null) {
				sb.append("<null>");
			} else {
				sb.append(FirstName);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row2Struct other) {

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

	public void tFixedFlowInput_2Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tFixedFlowInput_2_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("tFixedFlowInput_2", "9bW8U2_");

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

				row2Struct row2 = new row2Struct();

				/**
				 * [tSalesforceOutput_2 begin ] start
				 */

				sh("tSalesforceOutput_2");

				s(currentComponent = "tSalesforceOutput_2");

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0, "row2");

				int tos_count_tSalesforceOutput_2 = 0;

				if (enableLogStash) {
					talendJobLog.addCM("tSalesforceOutput_2", "tSalesforceOutput_2", "tSalesforceOutput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				boolean doesNodeBelongToRequest_tSalesforceOutput_2 = 0 == 0;
				@SuppressWarnings("unchecked")
				java.util.Map<String, Object> restRequest_tSalesforceOutput_2 = (java.util.Map<String, Object>) globalMap
						.get("restRequest");
				String currentTRestRequestOperation_tSalesforceOutput_2 = (String) (restRequest_tSalesforceOutput_2 != null
						? restRequest_tSalesforceOutput_2.get("OPERATION")
						: null);

				org.talend.components.api.component.ComponentDefinition def_tSalesforceOutput_2 = new org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputDefinition();

				org.talend.components.api.component.runtime.Writer writer_tSalesforceOutput_2 = null;
				org.talend.components.api.component.runtime.Reader reader_tSalesforceOutput_2 = null;

				org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputProperties props_tSalesforceOutput_2 = (org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputProperties) def_tSalesforceOutput_2
						.createRuntimeProperties();
				props_tSalesforceOutput_2.setValue("extendInsert", true);

				props_tSalesforceOutput_2.setValue("ceaseForError", true);

				props_tSalesforceOutput_2.setValue("commitLevel", 200);

				props_tSalesforceOutput_2.setValue("dataTimeUTC", true);

				props_tSalesforceOutput_2.setValue("logFileName", "");

				props_tSalesforceOutput_2.setValue("outputAction",
						org.talend.components.salesforce.SalesforceOutputProperties.OutputAction.INSERT);

				class SchemaSettingTool_tSalesforceOutput_2_1_fisrt {

					String getSchemaValue() {

						StringBuilder s = new StringBuilder();

						a("{\"type\":\"record\",", s);

						a("\"name\":\"tSalesforceOutput_2\",\"fields\":[{", s);

						a("\"name\":\"LastName\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"LastName\",\"talend.field.dbColumnName\":\"LastName\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"80\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"LastName\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"FirstName\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"FirstName\",\"talend.field.dbColumnName\":\"FirstName\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"FirstName\",\"di.column.relatedEntity\":\"\"}],\"di.table.name\":\"tSalesforceOutput_2\",\"di.table.label\":\"tSalesforceOutput_2\"}",
								s);

						return s.toString();

					}

					void a(String part, StringBuilder strB) {
						strB.append(part);
					}

				}

				SchemaSettingTool_tSalesforceOutput_2_1_fisrt sst_tSalesforceOutput_2_1_fisrt = new SchemaSettingTool_tSalesforceOutput_2_1_fisrt();

				props_tSalesforceOutput_2.schemaFlow.setValue("schema", new org.apache.avro.Schema.Parser()
						.setValidateDefaults(false).parse(sst_tSalesforceOutput_2_1_fisrt.getSchemaValue()));

				class SchemaSettingTool_tSalesforceOutput_2_2_fisrt {

					String getSchemaValue() {

						StringBuilder s = new StringBuilder();

						a("{\"type\":\"record\",", s);

						a("\"name\":\"rejectOutput\",\"fields\":[{", s);

						a("\"name\":\"LastName\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"LastName\",\"talend.field.dbColumnName\":\"LastName\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"80\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"LastName\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"FirstName\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"FirstName\",\"talend.field.dbColumnName\":\"FirstName\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"FirstName\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"errorCode\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"errorFields\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"errorMessage\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"}],\"di.table.name\":\"tSalesforceOutput_2\",\"di.table.label\":\"tSalesforceOutput_2\"}",
								s);

						return s.toString();

					}

					void a(String part, StringBuilder strB) {
						strB.append(part);
					}

				}

				SchemaSettingTool_tSalesforceOutput_2_2_fisrt sst_tSalesforceOutput_2_2_fisrt = new SchemaSettingTool_tSalesforceOutput_2_2_fisrt();

				props_tSalesforceOutput_2.schemaReject.setValue("schema", new org.apache.avro.Schema.Parser()
						.setValidateDefaults(false).parse(sst_tSalesforceOutput_2_2_fisrt.getSchemaValue()));

				props_tSalesforceOutput_2.connection.userPassword.setValue("useAuth", false);

				props_tSalesforceOutput_2.connection.proxy.userPassword.setValue("useAuth", false);

				props_tSalesforceOutput_2.connection.referencedComponent.setValue("referenceType",
						org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE);

				props_tSalesforceOutput_2.connection.referencedComponent.setValue("componentInstanceId",
						"tSalesforceConnection_1");

				props_tSalesforceOutput_2.connection.referencedComponent.setValue("referenceDefinitionName",
						"tSalesforceConnection");

				props_tSalesforceOutput_2.module.setValue("moduleName", "Contact");

				props_tSalesforceOutput_2.module.connection.userPassword.setValue("useAuth", false);

				props_tSalesforceOutput_2.module.connection.proxy.userPassword.setValue("useAuth", false);

				props_tSalesforceOutput_2.module.connection.referencedComponent.setValue("referenceType",
						org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE);

				props_tSalesforceOutput_2.module.connection.referencedComponent.setValue("componentInstanceId",
						"tSalesforceConnection_1");

				props_tSalesforceOutput_2.module.connection.referencedComponent.setValue("referenceDefinitionName",
						"tSalesforceConnection");

				class SchemaSettingTool_tSalesforceOutput_2_3_fisrt {

					String getSchemaValue() {

						StringBuilder s = new StringBuilder();

						a("{\"type\":\"record\",", s);

						a("\"name\":\"tSalesforceOutput_2\",\"fields\":[{", s);

						a("\"name\":\"LastName\",\"type\":\"string\",\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"LastName\",\"talend.field.dbColumnName\":\"LastName\",\"di.column.talendType\":\"id_String\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"80\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"LastName\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"FirstName\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"AVRO_TECHNICAL_KEY\":\"FirstName\",\"talend.field.dbColumnName\":\"FirstName\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.table.label\":\"FirstName\",\"di.column.relatedEntity\":\"\"}],\"di.table.name\":\"tSalesforceOutput_2\",\"di.table.label\":\"tSalesforceOutput_2\"}",
								s);

						return s.toString();

					}

					void a(String part, StringBuilder strB) {
						strB.append(part);
					}

				}

				SchemaSettingTool_tSalesforceOutput_2_3_fisrt sst_tSalesforceOutput_2_3_fisrt = new SchemaSettingTool_tSalesforceOutput_2_3_fisrt();

				props_tSalesforceOutput_2.module.main.setValue("schema", new org.apache.avro.Schema.Parser()
						.setValidateDefaults(false).parse(sst_tSalesforceOutput_2_3_fisrt.getSchemaValue()));

				if (org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE == props_tSalesforceOutput_2.connection.referencedComponent.referenceType
						.getValue()) {
					final String referencedComponentInstanceId_tSalesforceOutput_2 = props_tSalesforceOutput_2.connection.referencedComponent.componentInstanceId
							.getStringValue();
					if (referencedComponentInstanceId_tSalesforceOutput_2 != null) {
						org.talend.daikon.properties.Properties referencedComponentProperties_tSalesforceOutput_2 = (org.talend.daikon.properties.Properties) globalMap
								.get(referencedComponentInstanceId_tSalesforceOutput_2
										+ "_COMPONENT_RUNTIME_PROPERTIES");
						props_tSalesforceOutput_2.connection.referencedComponent
								.setReference(referencedComponentProperties_tSalesforceOutput_2);
					}
				}
				if (org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE == props_tSalesforceOutput_2.module.connection.referencedComponent.referenceType
						.getValue()) {
					final String referencedComponentInstanceId_tSalesforceOutput_2 = props_tSalesforceOutput_2.module.connection.referencedComponent.componentInstanceId
							.getStringValue();
					if (referencedComponentInstanceId_tSalesforceOutput_2 != null) {
						org.talend.daikon.properties.Properties referencedComponentProperties_tSalesforceOutput_2 = (org.talend.daikon.properties.Properties) globalMap
								.get(referencedComponentInstanceId_tSalesforceOutput_2
										+ "_COMPONENT_RUNTIME_PROPERTIES");
						props_tSalesforceOutput_2.module.connection.referencedComponent
								.setReference(referencedComponentProperties_tSalesforceOutput_2);
					}
				}
				globalMap.put("tSalesforceOutput_2_COMPONENT_RUNTIME_PROPERTIES", props_tSalesforceOutput_2);
				globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
				globalMap.put("TALEND_COMPONENTS_VERSION", "0.37.41");
				java.net.URL mappings_url_tSalesforceOutput_2 = this.getClass().getResource("/xmlMappings");
				globalMap.put("tSalesforceOutput_2_MAPPINGS_URL", mappings_url_tSalesforceOutput_2);

				org.talend.components.api.container.RuntimeContainer container_tSalesforceOutput_2 = new org.talend.components.api.container.RuntimeContainer() {
					public Object getComponentData(String componentId, String key) {
						return globalMap.get(componentId + "_" + key);
					}

					public void setComponentData(String componentId, String key, Object data) {
						globalMap.put(componentId + "_" + key, data);
					}

					public String getCurrentComponentId() {
						return "tSalesforceOutput_2";
					}

					public Object getGlobalData(String key) {
						return globalMap.get(key);
					}
				};

				int nb_line_tSalesforceOutput_2 = 0;

				org.talend.components.api.component.ConnectorTopology topology_tSalesforceOutput_2 = null;
				topology_tSalesforceOutput_2 = org.talend.components.api.component.ConnectorTopology.INCOMING;

				org.talend.daikon.runtime.RuntimeInfo runtime_info_tSalesforceOutput_2 = def_tSalesforceOutput_2
						.getRuntimeInfo(org.talend.components.api.component.runtime.ExecutionEngine.DI,
								props_tSalesforceOutput_2, topology_tSalesforceOutput_2);
				java.util.Set<org.talend.components.api.component.ConnectorTopology> supported_connector_topologies_tSalesforceOutput_2 = def_tSalesforceOutput_2
						.getSupportedConnectorTopologies();

				org.talend.components.api.component.runtime.RuntimableRuntime componentRuntime_tSalesforceOutput_2 = (org.talend.components.api.component.runtime.RuntimableRuntime) (Class
						.forName(runtime_info_tSalesforceOutput_2.getRuntimeClassName()).newInstance());
				org.talend.daikon.properties.ValidationResult initVr_tSalesforceOutput_2 = componentRuntime_tSalesforceOutput_2
						.initialize(container_tSalesforceOutput_2, props_tSalesforceOutput_2);

				if (initVr_tSalesforceOutput_2
						.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
					throw new RuntimeException(initVr_tSalesforceOutput_2.getMessage());
				}

				if (componentRuntime_tSalesforceOutput_2 instanceof org.talend.components.api.component.runtime.ComponentDriverInitialization) {
					org.talend.components.api.component.runtime.ComponentDriverInitialization compDriverInitialization_tSalesforceOutput_2 = (org.talend.components.api.component.runtime.ComponentDriverInitialization) componentRuntime_tSalesforceOutput_2;
					compDriverInitialization_tSalesforceOutput_2.runAtDriver(container_tSalesforceOutput_2);
				}

				org.talend.components.api.component.runtime.SourceOrSink sourceOrSink_tSalesforceOutput_2 = null;
				if (componentRuntime_tSalesforceOutput_2 instanceof org.talend.components.api.component.runtime.SourceOrSink) {
					sourceOrSink_tSalesforceOutput_2 = (org.talend.components.api.component.runtime.SourceOrSink) componentRuntime_tSalesforceOutput_2;
					if (doesNodeBelongToRequest_tSalesforceOutput_2) {
						org.talend.daikon.properties.ValidationResult vr_tSalesforceOutput_2 = sourceOrSink_tSalesforceOutput_2
								.validate(container_tSalesforceOutput_2);
						if (vr_tSalesforceOutput_2
								.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
							throw new RuntimeException(vr_tSalesforceOutput_2.getMessage());
						}
					}
				}

				org.talend.codegen.enforcer.IncomingSchemaEnforcer incomingEnforcer_tSalesforceOutput_2 = null;
				if (sourceOrSink_tSalesforceOutput_2 instanceof org.talend.components.api.component.runtime.Sink) {
					org.talend.components.api.component.runtime.Sink sink_tSalesforceOutput_2 = (org.talend.components.api.component.runtime.Sink) sourceOrSink_tSalesforceOutput_2;
					org.talend.components.api.component.runtime.WriteOperation writeOperation_tSalesforceOutput_2 = sink_tSalesforceOutput_2
							.createWriteOperation();
					if (doesNodeBelongToRequest_tSalesforceOutput_2) {
						writeOperation_tSalesforceOutput_2.initialize(container_tSalesforceOutput_2);
					}
					writer_tSalesforceOutput_2 = writeOperation_tSalesforceOutput_2
							.createWriter(container_tSalesforceOutput_2);
					if (doesNodeBelongToRequest_tSalesforceOutput_2) {
						writer_tSalesforceOutput_2.open("tSalesforceOutput_2");
					}

					resourceMap.put("writer_tSalesforceOutput_2", writer_tSalesforceOutput_2);
				} // end of "sourceOrSink_tSalesforceOutput_2 instanceof ...Sink"
				org.talend.components.api.component.Connector c_tSalesforceOutput_2 = null;
				for (org.talend.components.api.component.Connector currentConnector : props_tSalesforceOutput_2
						.getAvailableConnectors(null, false)) {
					if (currentConnector.getName().equals("MAIN")) {
						c_tSalesforceOutput_2 = currentConnector;
						break;
					}
				}
				org.apache.avro.Schema designSchema_tSalesforceOutput_2 = props_tSalesforceOutput_2
						.getSchema(c_tSalesforceOutput_2, false);
				incomingEnforcer_tSalesforceOutput_2 = new org.talend.codegen.enforcer.IncomingSchemaEnforcer(
						designSchema_tSalesforceOutput_2);

				java.lang.Iterable<?> outgoingMainRecordsList_tSalesforceOutput_2 = new java.util.ArrayList<Object>();
				java.util.Iterator outgoingMainRecordsIt_tSalesforceOutput_2 = null;

				/**
				 * [tSalesforceOutput_2 begin ] stop
				 */

				/**
				 * [tFixedFlowInput_2 begin ] start
				 */

				sh("tFixedFlowInput_2");

				s(currentComponent = "tFixedFlowInput_2");

				int tos_count_tFixedFlowInput_2 = 0;

				if (enableLogStash) {
					talendJobLog.addCM("tFixedFlowInput_2", "tFixedFlowInput_2", "tFixedFlowInput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				int nb_line_tFixedFlowInput_2 = 0;
				List<row2Struct> cacheList_tFixedFlowInput_2 = new java.util.ArrayList<row2Struct>();
				row2 = new row2Struct();
				row2.LastName = "Cont";
				row2.FirstName = "Testni";
				cacheList_tFixedFlowInput_2.add(row2);
				for (int i_tFixedFlowInput_2 = 0; i_tFixedFlowInput_2 < 1; i_tFixedFlowInput_2++) {
					for (row2Struct tmpRow_tFixedFlowInput_2 : cacheList_tFixedFlowInput_2) {
						nb_line_tFixedFlowInput_2++;
						row2 = tmpRow_tFixedFlowInput_2;

						/**
						 * [tFixedFlowInput_2 begin ] stop
						 */

						/**
						 * [tFixedFlowInput_2 main ] start
						 */

						s(currentComponent = "tFixedFlowInput_2");

						tos_count_tFixedFlowInput_2++;

						/**
						 * [tFixedFlowInput_2 main ] stop
						 */

						/**
						 * [tFixedFlowInput_2 process_data_begin ] start
						 */

						s(currentComponent = "tFixedFlowInput_2");

						/**
						 * [tFixedFlowInput_2 process_data_begin ] stop
						 */

						/**
						 * [tSalesforceOutput_2 main ] start
						 */

						s(currentComponent = "tSalesforceOutput_2");

						if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

								, "row2", "tFixedFlowInput_2", "tFixedFlowInput_2", "tFixedFlowInput",
								"tSalesforceOutput_2", "tSalesforceOutput_2", "tSalesforceOutput"

						)) {
							talendJobLogProcess(globalMap);
						}

						if (log.isTraceEnabled()) {
							log.trace("row2 - " + (row2 == null ? "" : row2.toLogString()));
						}

						if (incomingEnforcer_tSalesforceOutput_2 != null) {
							incomingEnforcer_tSalesforceOutput_2.createNewRecord();
						}
						// skip the put action if the input column doesn't appear in component runtime
						// schema
						if (incomingEnforcer_tSalesforceOutput_2 != null && incomingEnforcer_tSalesforceOutput_2
								.getRuntimeSchema().getField("LastName") != null) {
							incomingEnforcer_tSalesforceOutput_2.put("LastName", row2.LastName);
						}
						// skip the put action if the input column doesn't appear in component runtime
						// schema
						if (incomingEnforcer_tSalesforceOutput_2 != null && incomingEnforcer_tSalesforceOutput_2
								.getRuntimeSchema().getField("FirstName") != null) {
							incomingEnforcer_tSalesforceOutput_2.put("FirstName", row2.FirstName);
						}

						org.apache.avro.generic.IndexedRecord data_tSalesforceOutput_2 = null;
						if (incomingEnforcer_tSalesforceOutput_2 != null) {
							data_tSalesforceOutput_2 = incomingEnforcer_tSalesforceOutput_2.getCurrentRecord();
						}

						if (writer_tSalesforceOutput_2 != null && data_tSalesforceOutput_2 != null) {
							writer_tSalesforceOutput_2.write(data_tSalesforceOutput_2);
						}

						nb_line_tSalesforceOutput_2++;

						tos_count_tSalesforceOutput_2++;

						/**
						 * [tSalesforceOutput_2 main ] stop
						 */

						/**
						 * [tSalesforceOutput_2 process_data_begin ] start
						 */

						s(currentComponent = "tSalesforceOutput_2");

						/**
						 * [tSalesforceOutput_2 process_data_begin ] stop
						 */

						/**
						 * [tSalesforceOutput_2 process_data_end ] start
						 */

						s(currentComponent = "tSalesforceOutput_2");

						/**
						 * [tSalesforceOutput_2 process_data_end ] stop
						 */

						/**
						 * [tFixedFlowInput_2 process_data_end ] start
						 */

						s(currentComponent = "tFixedFlowInput_2");

						/**
						 * [tFixedFlowInput_2 process_data_end ] stop
						 */

						/**
						 * [tFixedFlowInput_2 end ] start
						 */

						s(currentComponent = "tFixedFlowInput_2");

					}
				}
				cacheList_tFixedFlowInput_2.clear();
				globalMap.put("tFixedFlowInput_2_NB_LINE", nb_line_tFixedFlowInput_2);

				ok_Hash.put("tFixedFlowInput_2", true);
				end_Hash.put("tFixedFlowInput_2", System.currentTimeMillis());

				/**
				 * [tFixedFlowInput_2 end ] stop
				 */

				/**
				 * [tSalesforceOutput_2 end ] start
				 */

				s(currentComponent = "tSalesforceOutput_2");

// end of generic

				resourceMap.put("finish_tSalesforceOutput_2", Boolean.TRUE);

				java.util.Map<String, Object> resultMap_tSalesforceOutput_2 = null;
				if (writer_tSalesforceOutput_2 != null) {
					org.talend.components.api.component.runtime.Result resultObject_tSalesforceOutput_2 = (org.talend.components.api.component.runtime.Result) writer_tSalesforceOutput_2
							.close();
					resultMap_tSalesforceOutput_2 = writer_tSalesforceOutput_2.getWriteOperation()
							.finalize(java.util.Arrays.<org.talend.components.api.component.runtime.Result>asList(
									resultObject_tSalesforceOutput_2), container_tSalesforceOutput_2);
				}
				if (resultMap_tSalesforceOutput_2 != null) {
					for (java.util.Map.Entry<String, Object> entry_tSalesforceOutput_2 : resultMap_tSalesforceOutput_2
							.entrySet()) {
						switch (entry_tSalesforceOutput_2.getKey()) {
						case org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE:
							container_tSalesforceOutput_2.setComponentData("tSalesforceOutput_2", "ERROR_MESSAGE",
									entry_tSalesforceOutput_2.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_TOTAL_RECORD_COUNT:
							container_tSalesforceOutput_2.setComponentData("tSalesforceOutput_2", "NB_LINE",
									entry_tSalesforceOutput_2.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_SUCCESS_RECORD_COUNT:
							container_tSalesforceOutput_2.setComponentData("tSalesforceOutput_2", "NB_SUCCESS",
									entry_tSalesforceOutput_2.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_REJECT_RECORD_COUNT:
							container_tSalesforceOutput_2.setComponentData("tSalesforceOutput_2", "NB_REJECT",
									entry_tSalesforceOutput_2.getValue());
							break;
						default:
							StringBuilder studio_key_tSalesforceOutput_2 = new StringBuilder();
							for (int i_tSalesforceOutput_2 = 0; i_tSalesforceOutput_2 < entry_tSalesforceOutput_2
									.getKey().length(); i_tSalesforceOutput_2++) {
								char ch_tSalesforceOutput_2 = entry_tSalesforceOutput_2.getKey()
										.charAt(i_tSalesforceOutput_2);
								if (Character.isUpperCase(ch_tSalesforceOutput_2) && i_tSalesforceOutput_2 > 0) {
									studio_key_tSalesforceOutput_2.append('_');
								}
								studio_key_tSalesforceOutput_2.append(ch_tSalesforceOutput_2);
							}
							container_tSalesforceOutput_2.setComponentData("tSalesforceOutput_2",
									studio_key_tSalesforceOutput_2.toString().toUpperCase(java.util.Locale.ENGLISH),
									entry_tSalesforceOutput_2.getValue());
							break;
						}
					}
				}

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, "row2", 2, 0,
						"tFixedFlowInput_2", "tFixedFlowInput_2", "tFixedFlowInput", "tSalesforceOutput_2",
						"tSalesforceOutput_2", "tSalesforceOutput", "output")) {
					talendJobLogProcess(globalMap);
				}

				ok_Hash.put("tSalesforceOutput_2", true);
				end_Hash.put("tSalesforceOutput_2", System.currentTimeMillis());

				/**
				 * [tSalesforceOutput_2 end ] stop
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
				 * [tFixedFlowInput_2 finally ] start
				 */

				s(currentComponent = "tFixedFlowInput_2");

				/**
				 * [tFixedFlowInput_2 finally ] stop
				 */

				/**
				 * [tSalesforceOutput_2 finally ] start
				 */

				s(currentComponent = "tSalesforceOutput_2");

// finally of generic

				if (resourceMap.get("finish_tSalesforceOutput_2") == null) {
					if (resourceMap.get("writer_tSalesforceOutput_2") != null) {
						try {
							((org.talend.components.api.component.runtime.Writer) resourceMap
									.get("writer_tSalesforceOutput_2")).close();
						} catch (java.io.IOException e_tSalesforceOutput_2) {
							String errorMessage_tSalesforceOutput_2 = "failed to release the resource in tSalesforceOutput_2 :"
									+ e_tSalesforceOutput_2.getMessage();
							System.err.println(errorMessage_tSalesforceOutput_2);
						}
					}
				}

				/**
				 * [tSalesforceOutput_2 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tFixedFlowInput_2_SUBPROCESS_STATE", 1);
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
		final Input InputClass = new Input();

		int exitCode = InputClass.runJobInTOS(args);
		if (exitCode == 0) {
			log.info("TalendJob: 'Input' - Done.");
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
		log.info("TalendJob: 'Input' - Start.");

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
		org.slf4j.MDC.put("_jobRepositoryId", "__kNg4PA7Ee-2rNEZYaVnnw");
		org.slf4j.MDC.put("_compiledAtTimestamp", "2025-02-24T12:57:37.103949400Z");

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
			java.io.InputStream inContext = Input.class.getClassLoader()
					.getResourceAsStream("salesforcetalend/input_0_1/contexts/" + contextStr + ".properties");
			if (inContext == null) {
				inContext = Input.class.getClassLoader()
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
		log.info("TalendJob: 'Input' - Started.");
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
			System.out.println((endUsedMemory - startUsedMemory) + " bytes memory increase when running : Input");
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
		log.info("TalendJob: 'Input' - Finished - status: " + status + " returnCode: " + returnCode);

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
 * 163542 characters generated by Talend Cloud Data Management Platform on the
 * 24. veljače 2025. u 13:57:37 CET
 ************************************************************************************************/