package com.wangyin.ci.performance;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.util.FormValidation;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
/**
 * @author wyhubingyin
 * @date 2014年6月18日
 */
public class JMeterHTTPRunner extends PerformanceRunner {
	private String domain;
	private String port;
	private String path;
	private String method;
	private String jmxName;

	@Extension
	public static class DescriptorImpl extends PerformanceRunnerDescriptor {
		@Override
		public String getDisplayName() {
			return "JMeter HTTP";
		}
		
		public FormValidation doCheckDomain(@QueryParameter String value) throws IOException, ServletException {
	        if (value.startsWith("http://")||value.startsWith("https://"))
	        	return FormValidation.error("domain cant start with 'http://' or 'https://'. e.g: http://www.baidu.com ,pls set www.baidu.com !");
	        return FormValidation.ok();
	    }
	}

	@DataBoundConstructor
	public JMeterHTTPRunner(String threads,String runMode, String runModeParams, String parameters, String ramp_time,String method, String domain, String port, String path,String jmxName) {
		super(threads, runMode,runModeParams, parameters,ramp_time);
		this.method = method;
		this.domain = domain;
		this.path = path;
		this.port = port;
		if("$default".equals(jmxName)||"".equals(jmxName)){
			this.jmxName=jmxName;
		}else{
			this.jmxName=jmxName.endsWith(".jmx")?jmxName:jmxName+".jmx";
		}
		
	}



	@Override
	public void execute(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener,FilePath srcJmeterPath) throws Exception {

		PrintStream ps = listener.getLogger();
		ps.println("runMode : "+getRunMode());
		ps.println("runModeParams :" +getRunModeParams());
		ps.println("jmx file : "+getJmxName());
		ps.println("threads :" + getThreads());
		ps.println("Paramters :" + getParameters());
		ps.println("port :" + getPort());
		ps.println("domain :" + getDomain());
		ps.println("path :" + getPath());
		ps.println("ramp_time :" +getRamp_time());
		ps.println("method :" + method);
		
		PerformanceParamter paramter = new PerformanceParamter(getJmxName(),getThreads(), getRunMode(),getRunModeParams(), getParameters(), getDomain(), getPath(), getPort(), getMethod(),getRamp_time());
		
		
		executeJmeter(build, launcher, listener,paramter,srcJmeterPath);
	}

	public String getDomain() {
		return domain;
	}

	public String getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

	public String getMethod() {
		return method;
	}

	public String getJmxName() {
		return jmxName;
	}
	
	
}
