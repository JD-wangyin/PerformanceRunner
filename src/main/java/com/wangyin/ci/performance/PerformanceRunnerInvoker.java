package com.wangyin.ci.performance;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import hudson.tasks.CommandInterpreter;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;






/**
 * 
 * Performan Runner Plugin Main Class
 * @author wyhubingyin wyliangxiaowu
 * @date 2014年6月24日
 */
public class PerformanceRunnerInvoker extends Builder {
    public static FileLogger logger = null;
    
    
    @Extension
    public static class DescriptorImpl extends Descriptor<Builder> {
    	private String sshUser;
		private String sshPassWord;
		private String sshKeyFile;
		private String sshPort;
		
		
		public DescriptorImpl() {
			load();
		}
    	
        @Override
        public String getDisplayName() {
            return "Invoke Performance Runner";
        }


        public List<PerformanceRunnerDescriptor> getRunnerDescriptors() {
            return PerformanceRunnerDescriptor.all();
        }
        
        public FormValidation doCheckNmon_serverlist(@QueryParameter String value) throws IOException, ServletException {
            if (value.length()!=0&&!value.matches("^([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\s*)(\\s*\n\\s*[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\s*)*$"))
            	return FormValidation.error("Parameter is incorrect,Please check");
            return FormValidation.ok();
        }

		public String getSshUser() {
			return sshUser;
		}

		public void setSshUser(String sshUser) {
			this.sshUser = sshUser;
		}

		public String getSshPassWord() {
			return sshPassWord;
		}

		public void setSshPassWord(String sshPassWord) {
			this.sshPassWord = sshPassWord;
		}

		public String getSshKeyFile() {
			return sshKeyFile;
		}

		public void setSshKeyFile(String sshKeyFile) {
			this.sshKeyFile = sshKeyFile;
		}

		public String getSshPort() {
			return sshPort;
		}

		public void setSshPort(String sshPort) {
			this.sshPort = sshPort;
		}
        
		
       
        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws hudson.model.Descriptor.FormException {
                 this.setSshUser(formData.getString("sshUser"));
                 this.setSshPassWord(formData.getString("sshPassWord"));
                 this.setSshKeyFile(formData.getString("sshKeyFile"));
                 this.setSshPort(formData.getString("sshPort"));
        	 save();
        	return super.configure(req, formData);
        }

    }

    /**
     * Configured performance runners.
     */
    private List<PerformanceRunner> runners;
    
    private String nmon_serverlist;
    
    private String threadStepSize;
    
    private String finalThreads;
    
    private String intervalTime;
    
    public String getSshUser() {
		return getDescriptor().getSshUser();
	}

	public String getSshPassWord() {
		return getDescriptor().getSshPassWord();
	}

	public String getSshKeyFile() {
		return getDescriptor().getSshKeyFile();
	}

	public String getSshPort() {
		if(getDescriptor().getSshPort() == null || getDescriptor().getSshPort().length() == 0){
			return "22";
		}
		return getDescriptor().getSshPort();
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}
    
    @DataBoundConstructor
    public PerformanceRunnerInvoker(List<? extends PerformanceRunner> runners,String nmon_serverlist,String threadStepSize,String finalThreads ,String intervalTime) {

        if (runners == null) {
            runners = Collections.emptyList();
        }
        this.runners = new ArrayList<PerformanceRunner>(runners);
        this.nmon_serverlist=nmon_serverlist;
        this.threadStepSize=threadStepSize;
        this.finalThreads=finalThreads;
        this.intervalTime=intervalTime;
    }
    
    //main method
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)  {
    	logger = new FileLogger("PerformanceRunnerInvoker");
    	
    	PrintStream ps = listener.getLogger();
    	//NmonUtil init。add SSH config
    	NmonUtil.init(getSshUser(),getSshPassWord(),getSshKeyFile(),getSshPort());
    	ps.println("Nmon init complete");
    	String jobName="";
    	String path=build.getRootDir().toString();
    	
    	jobName=path.substring(path.indexOf("jobs")+"jobs".length()+1);
		jobName=jobName.substring(0,jobName.indexOf(File.separator));
		//performance machine counts++
    	NmonUtil.incrementSum(jobName);
    	
        if(build.getResult()==Result.FAILURE){
        	return false;
        }
        
       
        PerformanceRunner runner=null;
        String baseThreadsString="";
        FilePath srcJmeterPath=null;
        try {
        ps.println("runner size ："+ runners.size());
        ps.println("nmon_serverlist ： "+nmon_serverlist);
        ps.println("threadStepSize : " +threadStepSize);
        ps.println("finalThreads : " +finalThreads);
        ps.println("intervalTime : "+intervalTime);
        String[] serverList=null;
        String nmonDir="";
        boolean hasNmon=false;
        //judge has nmon
        if(nmon_serverlist!=null&&nmon_serverlist.length()>0){
        	 serverList=nmon_serverlist.split("\n");
        	 hasNmon=true;
        }
        ps.println("nmon monitor :"+hasNmon);
        //mkdir nmon
        String jobdir=path.substring(0,path.indexOf("jobs")+"jobs".length());
		nmonDir=jobdir+File.separator+jobName+File.separator+"workspace"+File.separator+"nmon";
		ps.println("nmonDir : "+nmonDir);
		ps.println("clear nmonDir ...");
		JobWorkspaceFileUtil.create(nmonDir,ps,jobName);
		
        if(runners.size()>1){
        	 ps.println(new Exception("choose runner only 1!"));
        	 build.setResult(Result.FAILURE);
        	return false;
        }
        //mvn clean package
        String cleanCommandString="mvn clean package";
 		 String allCommandString = null;
 		if (launcher.isUnix()) {
 			String sourceCommandString = "source /etc/profile";
 			allCommandString = sourceCommandString+";"+cleanCommandString+";";
 			CommandInterpreter allCommandInterpreter=new Shell(allCommandString);
 			allCommandInterpreter.perform(build, launcher, listener);
 		} else {
 			 CommandInterpreter cleanInterpreter=new BatchFile(cleanCommandString);
 			 cleanInterpreter.perform(build, launcher, listener);
 		}
 		//get Runner
 		 	runner=runners.get(0);
 		 	runner.setJobName(jobName);
 		 	baseThreadsString=runner.getThreads();
 		 	
 		 	srcJmeterPath=new FilePath(build.getModuleRoot(),"src/test/jmeter/");
 		 //multi scnece invoke !
 		 	if(threadStepSize!=null&&!threadStepSize.isEmpty()&&finalThreads!=null&!finalThreads.isEmpty()){
 		 		
 		 		ps.println("Base Test Start...");
 		 		int step=Integer.parseInt(threadStepSize);
 		 		int finalThread=Integer.parseInt(finalThreads);
 		 		int baseThreads=Integer.parseInt(baseThreadsString);
 		 		if(baseThreads>finalThread){
 		 			throw new Exception("Your finalThread < baseTread, pls check !");
 		 		}
 		 		for(int i=baseThreads;i<=finalThread;i+=step){
 		 			ps.println("Threads : " +i+" Run...");
 		 			runner.setThreads(String.valueOf(i));
 		 			executePerformance(hasNmon,runner,build,launcher,listener,serverList,nmonDir,srcJmeterPath,jobName);
 		 			if(intervalTime!=null&&!intervalTime.isEmpty()){
 		 				if(i!=finalThread){
 		 					ps.println("Sleep :" +intervalTime);
 		 					Thread.sleep(Integer.parseInt(intervalTime)*1000);
 		 				}
 		 				
 		 			}
 		 			
 		 		}
 		 	}else{
 		 		// single scene invoke
 		 		ps.println("Single Test Start...");
 		 		executePerformance(hasNmon,runner,build,launcher,listener,serverList,nmonDir,srcJmeterPath,jobName);
 		 	}
 		 	Thread.sleep(3000);
		} catch (Exception e) {
			ps.println(ExceptionUtil.exception2String(e));
			build.setResult(Result.FAILURE);
		}finally{
			ps.println("reset....");
			if(runner!=null){
				runner.setThreads(baseThreadsString);
			}
			JobWorkspaceFileUtil.reset(jobName);
			NmonUtil.reset(jobName);
			
			if(srcJmeterPath!=null){
				ps.println("delete all jmx start ....");
				try {
					FilePath[] jmxFilePaths=srcJmeterPath.list("*.jmx");
					for(FilePath filePath:jmxFilePaths){
						boolean deleteBoolean=filePath.delete();
						ps.println("delete jmx ? "+deleteBoolean);
					}
				} catch (IOException e) {
					ps.println(ExceptionUtil.exception2String(e));
				} catch (InterruptedException e) {
					ps.println(ExceptionUtil.exception2String(e));
				}
				ps.println("delete all jmx complete ....");
			}
			
		}
        
        return true;
		
    }



	/**
	 * @param serverList 
	 * @param listener 
	 * @param launcher 
	 * @param build 
	 * @param runner 
	 * @param hasNmon 
	 * @param nmonDir 
	 * @param srcJmeterPath 
	 * @throws Exception 
	 * 
	 */
	private void executePerformance(boolean hasNmon, PerformanceRunner runner, AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, String[] serverList, String nmonDir, FilePath srcJmeterPath,String jobName) throws Exception {
		PrintStream ps = listener.getLogger();
		NmonUtil.initExecuteCount(jobName);
		
      	if(hasNmon){
      	NmonUtil.startNmon(runner,serverList,ps,jobName);
    	ps.println("nmon monitor start");
      	}
      	runner.execute(build, launcher, listener,srcJmeterPath);
      	NmonUtil.incrementExecute(jobName);
      	int n=0;
      	//等待所有机器均执行完此次性能场景
		while(true){
			int sum=NmonUtil.getSumCount(jobName);
			int execute=NmonUtil.getExecuteCount(jobName);
			ps.println("executers sum counts: "+sum);
			ps.println("executer completed counts : "+ execute);
			if(sum==execute){
				ps.println("All executers runs complete!");
				break;
			}
			Thread.sleep(1000);
			ps.println("Wait for the other executers is running over...");
			if(n++==60){
				break;
			}
		}
   		if(hasNmon){
   			ps.println("nmon monitor stop");
      		NmonUtil.closeNmonAndDownLoad(nmonDir,ps,jobName);
   		}
   		NmonUtil.initExecuteInit(jobName);
		
	}


	public List<PerformanceRunner> getRunners() {
		return runners;
	}


	public String getNmon_serverlist() {
		return nmon_serverlist;
	}




	public String getThreadStepSize() {
		return threadStepSize;
	}


	public String getFinalThreads() {
		return finalThreads;
	}


	public String getIntervalTime() {
		return intervalTime;
	}
    
	
    
    
}
