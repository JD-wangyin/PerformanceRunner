package com.wangyin.ci.performance;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;



import org.kohsuke.stapler.DataBoundConstructor;
/**
 * @author wyhubingyin
 * @date 2014年6月18日
 */
public class AK47StressRunner extends PerformanceRunner  {

    @Extension
    public static class DescriptorImpl extends PerformanceRunnerDescriptor {
      @Override
      public String getDisplayName() {
        return "AK47 Stress";
      }
    }

    @DataBoundConstructor
	public AK47StressRunner(String threads, String runMode,String runModeParams, String parameters,String ramp_time) {
    	super(threads, runMode,runModeParams, parameters,ramp_time);
    }


	@Override
	public void execute(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener,FilePath srcJmeterPath) {
	}
}
