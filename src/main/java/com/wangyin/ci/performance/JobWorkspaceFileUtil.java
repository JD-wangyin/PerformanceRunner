/**
 * 
 */
package com.wangyin.ci.performance;

import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author wyhubingyin
 * @date 2014年7月30日
 */
public class JobWorkspaceFileUtil {
	private static ConcurrentHashMap<String, Boolean> map=new ConcurrentHashMap<String, Boolean>();
	
	public synchronized static void create(String fileString,PrintStream ps,String name){
		if(!map.containsKey(name)){
			map.putIfAbsent(name,false);
		}
		
		boolean isCreate=map.get(name);
		ps.println("isCreate : "+ isCreate);
		if(!isCreate){
			File file=new File(fileString);
			boolean isSuccess=false;
			if(file.exists()){
				ps.println("nmon dir is existed,clear it");
				for(File f:file.listFiles()){
					f.delete();
				}
			}else{
				isSuccess=file.mkdirs();
				ps.println("mkdirs success? : "+ isSuccess);
			}
			
			map.put(name, true);
		}
	} 
	
	public static void reset(String name){
		if(!"".equals(name)){
			map.put(name,false);
		}
			
	}
	
}
