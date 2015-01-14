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
public class JMeterJavaRunner extends PerformanceRunner {
	
	
	private String jmxName;
    private String className;
    public static final String PERFORMANCE_REPORTS_DIRECTORY = "performance-reports";
    public static final String JMETER_GET_DEFAULT_PARAMETERS_METHOD_NAME="getDefaultParameters";
    
	@Extension
	public static class DescriptorImpl extends PerformanceRunnerDescriptor {
		@Override
		public String getDisplayName() {
			return "JMeter Java";
		}
		
		public FormValidation doCheckClassName(@QueryParameter String value) throws IOException, ServletException {
	        if (value.length() > 0&&!value.matches("^\\w+(\\.\\w+)+$"))
	        	return FormValidation.error("className Paramter is incorrect ,pls check!");
	        return FormValidation.ok();
	    }
	}

	@DataBoundConstructor
	public JMeterJavaRunner(String threads,String runMode, String runModeParams, String parameters,String className,String ramp_time,String jmxName) {
		super(threads, runMode,runModeParams, parameters,ramp_time);
		this.className=className;
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
		ps.println("runModeParams : " + getRunModeParams());
		ps.println("jmx file : "+getJmxName());
		ps.println("threads : " + getThreads());
		ps.println("className : "+getClassName());
		ps.println("Paramters : " + getParameters());
		ps.println("ramp_time :" +getRamp_time());
		PerformanceParamter paramter = new PerformanceParamter(getJmxName(),getThreads(),getRunMode(), getRunModeParams(), getParameters(),getClassName(),getRamp_time());
		
		
		executeJmeter(build, launcher, listener,paramter,srcJmeterPath);
	}

	public String getClassName() {
		return className;
	}

	public String getJmxName() {
		return jmxName;
	}

	public void setJmxName(String jmxName) {
		this.jmxName = jmxName;
	}

	
	
	
	
}
