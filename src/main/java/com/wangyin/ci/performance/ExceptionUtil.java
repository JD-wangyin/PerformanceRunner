/**
 * 
 */
package com.wangyin.ci.performance;

/**
 * @author wyhubingyin
 * @date 2014年7月22日
 */
public class ExceptionUtil {
	public static String exception2String(Throwable e){
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(e.getClass().getName());
		sBuilder.append(": ");
		sBuilder.append(e.getMessage());
		sBuilder.append("\n");
		for(StackTraceElement element:e.getStackTrace()){
			sBuilder.append("\tat ");
			sBuilder.append(element.getClassName());
			sBuilder.append(".");
			sBuilder.append(element.getMethodName());
			sBuilder.append("(");
			sBuilder.append(element.getFileName());
			sBuilder.append(":");
			sBuilder.append(element.getLineNumber());
			sBuilder.append(")\n");
		}
		if(e.getCause()!=null){
			sBuilder.append("Caused   by: ");
			sBuilder.append(exception2String(e.getCause()));
		}
		return sBuilder.toString();
	}
}
