/**
 * 
 */
package com.wangyin.ci.performance;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

/**
 * @author wyhubingyin
 * @date 2014年7月29日
 */
public class NmonUtil {
	private static final int MAX_WAIT_TIME=10*1000;
	private static String user;
	private static String pass;
	private static String key_file;
	private static int port;
	
	public static void init(String user,String pass,String keyFile,String port){
		NmonUtil.user=user;
		NmonUtil.pass=pass;
		NmonUtil.key_file=keyFile;
		NmonUtil.port=Integer.parseInt(port);
	}

	private static ConcurrentHashMap<String, CounterEntity> map=new ConcurrentHashMap<String, CounterEntity>();
	
	public synchronized static void closeNmonAndDownLoad(String workspace,PrintStream ps, String name) throws JSchException, InterruptedException,  IOException{
		CounterEntity counterEntity=map.get(name);
		ps.println("isEnd : "+counterEntity.isEnd());
		if(counterEntity.isStart()&&!counterEntity.isEnd()&&counterEntity.getMap().size()>0){
			ps.println("endNmon");
			File workFile=new File(workspace);
			if(!workFile.exists()){
				ps.println("close nmon ...cz nmon dir is not exist,create nmon dir");
				workFile.mkdirs();
			}
			//kill nmon &&download nmon
			for(Entry<String, String> serverString:counterEntity.getMap().entrySet()){
		        JSchChannel client = new JSchChannel(serverString.getKey(), user, port, MAX_WAIT_TIME, key_file, pass);
		        client.exec("pgrep nmon | xargs sudo kill -9");
		        try {
		        	ps.println("nmon file : "+serverString.getValue()+" copy to locate");
					client.cp(serverString.getValue(), workspace+File.separator+serverString.getValue());
				} catch (SftpException e) {
					ps.println(ExceptionUtil.exception2String(e));
				}
		        client.close();
			}
			counterEntity.setEnd(true);
			counterEntity.setStart(false);
			counterEntity.getMap().clear();
		}
	}
	
	
	public static int getSumCount(String name) {
		return map.get(name).getSumCount().get();
	}

	public static void initExecuteCount(String name){
		map.get(name).getExecuteCount().set(0);
	}

	public static void incrementSum(String name){
		if(!map.containsKey(name)){
			map.putIfAbsent(name, new CounterEntity());
		}
		map.get(name).getSumCount().incrementAndGet();
	}
	
	public static void incrementExecute(String name){
		map.get(name).getExecuteCount().incrementAndGet();
	}
	
	
	public static void incrementExecuteInit(String name){
		map.get(name).getExecuteInit().incrementAndGet();
	}
	
	
	public static int getExecuteInit(String name) {
		return map.get(name).getExecuteInit().get();
	}
	
	//start nmon
	public synchronized static void startNmon(PerformanceRunner runner, String[] serverList,PrintStream ps,String name) throws JSchException {
		CounterEntity counterEntity=map.get(name);
		ps.println("isStart : "+counterEntity.isStart());
		if(!counterEntity.isStart()){
	      	DateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
	      	String date=dateFormat.format(new Date());
	      	String mode="";
	      	if("Loops".equals(runner.getRunMode())){
	      		mode="L";
	      	}else if ("Duration".equals(runner.getRunMode())) {
	      		mode="D";
				}
	      	date=date+"_"+runner.getThreads()+"T"+"_"+runner.getRunModeParams()+mode;
	      	
	      	for(String server:serverList){
	      		server=server.trim();
	      		ps.println("nmon monitor server ： "+server);
	      		counterEntity.getMap().put(server, server.replace(".", "-")+"_"+date+".nmon");
	      	}
			ps.println("startNmon");
			for(Entry<String, String> serverString:counterEntity.getMap().entrySet()){
		        JSchChannel client = new JSchChannel(serverString.getKey(), user, port, MAX_WAIT_TIME, key_file, pass);
		        String execString=client.exec("source /etc/profile;nmon -F "+serverString.getValue()+" -t -s 3");
		        ps.println(execString);
		        client.close();
			}
			counterEntity.setStart(true);
			counterEntity.setEnd(false);
		}
	}
	
	public static void reset(String name){
			if(map.containsKey(name)){
				CounterEntity counterEntity=map.get(name);
				counterEntity.getExecuteCount().set(0);
				counterEntity.getSumCount().set(0);
				counterEntity.getExecuteInit().set(0);
				counterEntity.getMap().clear();
				counterEntity.setEnd(false);
				counterEntity.setStart(false);
			}
	}

	public static int getExecuteCount(String name) {
		return map.get(name).getExecuteCount().get();
	}


	public static void initExecuteInit(String name) {
		map.get(name).getExecuteInit().set(0);
	}
	
}
