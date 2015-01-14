package com.wangyin.ci.performance;



import java.io.PrintStream;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Hudson;









import hudson.tasks.BatchFile;
import hudson.tasks.CommandInterpreter;
import hudson.tasks.Shell;

import org.kohsuke.stapler.DataBoundConstructor;
/**
 * 
 * @author wyhubingyin
 * @date 2014年7月25日
 */
public abstract class PerformanceRunner implements Describable<PerformanceRunner>, ExtensionPoint {
    /**
     * GLOB patterns that specify the performance report.
     */
	private String threads;
	private String runMode;
	private String runModeParams;
	private String parameters;
	private String ramp_time;
	
	private String jobName;

    @DataBoundConstructor
    public PerformanceRunner(String threads, String runMode,String runModeParams, String parameters,String ramp_time) {
		this.threads = threads;
		this.runMode=runMode;
		this.runModeParams=runModeParams;
		this.parameters = parameters;
		this.ramp_time=ramp_time;
	}
    
   

    public PerformanceRunnerDescriptor getDescriptor() {
        return (PerformanceRunnerDescriptor) Hudson.getInstance().getDescriptorOrDie(getClass());
    }

    
    
	public String getThreads() {
		return threads;
	}

	public void setThreads(String threads) {
		this.threads = threads;
	}

    public String getParameters() {
		return parameters;
	}
    
    public String getRunModeParams() {
		return runModeParams;
	}


	public String getRamp_time() {
		return ramp_time;
	}
    
    
    
	public String getRunMode() {
		return runMode;
	}

	/**
     * All registered implementations.
     */
    public static ExtensionList<PerformanceRunner> all() {
        return Hudson.getInstance().getExtensionList(PerformanceRunner.class);
    }
    
	
	public abstract void execute(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, FilePath srcJmeterPath) throws Exception ;
		
    protected void executeJmeter(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, PerformanceParamter paramter, FilePath srcJmeterPath) throws Exception{
 		
	PrintStream ps = listener.getLogger();		
	
	FilePath[] jmxFilePaths=srcJmeterPath.list("*.jmx");
		int jmxSize=jmxFilePaths.length;
		ps.println("jmx files size :" + jmxSize);
		final String jmxFileString=paramter.getJmxName();

		if(jmxSize>0){
			if("$default".equals(jmxFileString)||"".equals(jmxFileString)){
			if (jmxSize> 1) {
				throw new Exception("jmx Name is ${default} , so jmx file only one! but ur 'jmx' files size is :" + jmxSize);
			}else{
				if(this instanceof JMeterJavaRunner){
					JMeterUtil.modifyJavaJmx(jmxFilePaths[0], paramter);
				}else if(this instanceof JMeterHTTPRunner){
					JMeterUtil.modifyHttpJmx(jmxFilePaths[0], paramter);
				}
				ps.println("modify jmx complete");
			}
			}else{
				if(jmxSize>1){
					for(FilePath filePath:jmxFilePaths){
						if(!filePath.getName().equals(jmxFileString)){
							filePath.delete();
						}
					}
					jmxFilePaths=srcJmeterPath.list("*.jmx");
				}
				
				if(jmxFilePaths.length==0){
					throw new Exception("jmx Name is "+jmxFileString+" , can't find this jmx!");
				}else{
					//modify jmx
					if(this instanceof JMeterJavaRunner){
						JMeterUtil.modifyJavaJmx(jmxFilePaths[0], paramter);
					}else if(this instanceof JMeterHTTPRunner){
						JMeterUtil.modifyHttpJmx(jmxFilePaths[0], paramter);
					}
					ps.println("modify jmx complete");
				}
			}
		}else if(jmxSize==0){
			//create jmx
			String fileName="Test";
			fileName=fileName+"-"+paramter.getThreads()+"T-"+paramter.getRunModeParams()+paramter.getModeLorP()+".jmx";
			FilePath jmxPath=new FilePath(srcJmeterPath,fileName);
			ps.println("create jmx start...");
			if(this instanceof JMeterJavaRunner){
				JMeterUtil.createJavaJmx(jmxPath,paramter);
			}else if(this instanceof JMeterHTTPRunner){
				JMeterUtil.createHttpJmx(jmxPath,paramter);
			}
			ps.println("create jmx complete...");
		}
		//invoke performance test
    	String verifyCommandString = "mvn jmeter:jmeter";
    	CommandInterpreter verifyInterpreter=null;
 		if (launcher.isUnix()) {
 			String sourceCommandString = "source /etc/profile";
 			String allCommandString = sourceCommandString+";"+verifyCommandString+";";
 			verifyInterpreter=new Shell(allCommandString);
 		} else {
 			verifyInterpreter = new BatchFile(verifyCommandString);
 		}
 		NmonUtil.incrementExecuteInit(jobName);
		int i=0;
		//wait all machine init complete
		while(true){
			int sum=NmonUtil.getSumCount(jobName);
			int execute=NmonUtil.getExecuteInit(jobName);
			ps.println("executers sum counts: "+ sum);
			ps.println("executer completed counts : "+ execute);
			if(sum==execute){
				ps.println("All executers runs init complete!");
				break;
			}
			Thread.sleep(1000);
			ps.println("Wait for the other executers init.....");
			if(i++==300){
				throw new Exception("Wait for the other executers init timeout! due to download jar or workspace timeout!");
			}
		}
 		verifyInterpreter.perform(build, launcher, listener);
    }



	public String getJobName() {
		return jobName;
	}



	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
    
    
}
