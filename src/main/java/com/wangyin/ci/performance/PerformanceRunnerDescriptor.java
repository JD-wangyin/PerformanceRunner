package com.wangyin.ci.performance;

import java.io.IOException;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.FormValidation;

/**
 * 
 * @author wyhubingyin wyliangxiaowu
 * @date 2014年6月24日
 */
public abstract class PerformanceRunnerDescriptor extends Descriptor<PerformanceRunner> {


    
    public static DescriptorExtensionList<PerformanceRunner, PerformanceRunnerDescriptor> all() {
        return Hudson.getInstance().<PerformanceRunner, PerformanceRunnerDescriptor> getDescriptorList(
                PerformanceRunner.class);
    }
    
    public FormValidation doCheckThreads(@QueryParameter String value) throws IOException, ServletException {
        if (value.length() == 0)
            return FormValidation.error("Please set one thread at least");
        if (!value.matches("^\\d{1,5}$"))
        	return FormValidation.error("Please set thread in values(1-99999)");
        return FormValidation.ok();
    }
	
	public FormValidation doCheckRunModeParams(@QueryParameter String value) throws IOException, ServletException {
        if (value.length() == 0)
            return FormValidation.error("Please set value!");
        if (!value.matches("^\\d{1,5}|-1$"))
        	return FormValidation.error("Please set RunModeParams in values(1-99999)");
        return FormValidation.ok();
    }
	
	public FormValidation doCheckParamters(@QueryParameter String value) throws IOException, ServletException {
        if (value.length()!=0&&!value.matches("^[^;=]+=[^;=]*(;[^;=]+=[^;=]*)*$"))
        	return FormValidation.error("Please set paramter as a=1 or a=1;b=2");
        return FormValidation.ok();
    }
	
	public FormValidation doCheckRamp_time(@QueryParameter String value) throws IOException, ServletException {
        if (value.length()!=0&&!value.matches("^\\d{1,5}$"))
        	return FormValidation.error("Please set ramp_time in values(0-99999)");
        return FormValidation.ok();
    }

	@Override
	public boolean configure(StaplerRequest req, JSONObject json) throws hudson.model.Descriptor.FormException {
		save();
		return super.configure(req, json);
	}
    
}
