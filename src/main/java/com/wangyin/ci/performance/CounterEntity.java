/**
 * 
 */
package com.wangyin.ci.performance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**Thread counter
 * @author wyhubingyin
 * @date 2014年9月10日
 */
public class CounterEntity {
	private boolean isStart=false;
	private boolean isEnd=false;
	private AtomicInteger sumCount=new AtomicInteger(0);
	private AtomicInteger executeCount=new AtomicInteger(0);
	
	private AtomicInteger executeInit=new AtomicInteger(0);
	private Map<String, String> map=new HashMap<String, String>();
	
	
	public CounterEntity() {
	}
	
	public boolean isStart() {
		return isStart;
	}
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	public boolean isEnd() {
		return isEnd;
	}
	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
	public AtomicInteger getSumCount() {
		return sumCount;
	}
	public void setSumCount(AtomicInteger sumCount) {
		this.sumCount = sumCount;
	}
	public AtomicInteger getExecuteCount() {
		return executeCount;
	}
	public void setExecuteCount(AtomicInteger executeCount) {
		this.executeCount = executeCount;
	}
	public AtomicInteger getExecuteInit() {
		return executeInit;
	}
	public void setExecuteInit(AtomicInteger executeInit) {
		this.executeInit = executeInit;
	}
	public Map<String, String> getMap() {
		return map;
	}
	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	
	
	
	
	
	
}
