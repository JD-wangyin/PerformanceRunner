/**
 * 
 */
package com.wangyin.ci.performance;

/**
 * @author wyhubingyin
 * @date 2014年6月17日
 */
public class PerformanceParamter {
	private String jmxName = "";
	private String threads = "";

	private String runMode = "";
	private String runModeParams = "";

	private String paramters = "";

	private String ramp_time = "";

	// JMeter HTTP Params
	private String domain = "";
	private String path = "";
	private String port = "";
	private String method = "";

	// jmeter JAVA Params
	private String className = "";

	public PerformanceParamter(String jmxName, String threads, String runMode, String runModeParams, String paramters, String className, String ramp_time) {
		this.jmxName = jmxName;
		this.threads = threads;
		this.runMode = runMode;
		this.runModeParams = runModeParams;
		this.paramters = paramters;
		this.className = className;
		this.ramp_time = ramp_time;
	}

	public PerformanceParamter(String jmxName, String threads, String runMode, String runModeParams, String paramters, String domain, String path, String port, String method, String ramp_time) {
		this.jmxName = jmxName;
		this.threads = threads;
		this.runMode = runMode;
		this.runModeParams = runModeParams;
		this.paramters = paramters;
		this.domain = domain;
		this.path = path;
		this.port = port;
		this.method = method;
		this.ramp_time = ramp_time;
	}

	public PerformanceParamter() {
	}

	public String getThreads() {
		return threads;
	}

	public void setThreads(String threads) {
		this.threads = threads;
	}

	public String getParamters() {
		return paramters;
	}

	public void setParamters(String paramters) {
		this.paramters = paramters;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getRamp_time() {
		return ramp_time;
	}

	public void setRamp_time(String ramp_time) {
		this.ramp_time = ramp_time;
	}

	public String getRunMode() {
		return runMode;
	}

	public void setRunMode(String runMode) {
		this.runMode = runMode;
	}

	public String getRunModeParams() {
		return runModeParams;
	}

	public void setRunModeParams(String runModeParams) {
		this.runModeParams = runModeParams;
	}

	public String getModeLorP() {
		String mode = "";
		if ("Loops".equals(getRunMode())) {
			mode = "L";
		} else if ("Duration".equals(getRunMode())) {
			mode = "D";
		}

		return mode;
	}

	public String getJmxName() {
		return jmxName;
	}

	public void setJmxName(String jmxName) {
		this.jmxName = jmxName;
	}

}
