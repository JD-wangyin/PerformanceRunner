/**
 * 
 */
package com.wangyin.ci.performance;

import hudson.FilePath;

import java.io.StringReader;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;



/**
 * @author wyhubingyin
 * @date 2014年6月18日
 */
public class JMeterUtil {
	//modify http jmx
	public synchronized static void modifyHttpJmx(FilePath filepath, PerformanceParamter paramter) throws Exception {
		SAXReader reader=new SAXReader();
		Document doc=reader.read(filepath.read());
		if(paramter.getThreads()!=null&&paramter.getThreads().length()>0){
		List<Node> threads=doc.selectNodes("//stringProp[@name='ThreadGroup.num_threads']");
		for(Node t:threads){
			t.setText(paramter.getThreads());
		}
		}
		if(paramter.getRunModeParams()!=null&&paramter.getRunModeParams().length()>0){
		List<Node> forevers=doc.selectNodes("//boolProp[@name='LoopController.continue_forever']");
		Node scheduler=doc.selectSingleNode("//boolProp[@name='ThreadGroup.scheduler']");
		if("Loops".equals(paramter.getRunMode())){
			for(Node forever:forevers){
				forever.setText("false");
			}
			scheduler.setText("false");
			List<Node> loops=doc.selectNodes("//stringProp[@name='LoopController.loops']");
			for(Node loop:loops){
				loop.setText(paramter.getRunModeParams());
			}
		}
		if("Duration".equals(paramter.getRunMode())){
			for(Node forever:forevers){
				forever.setText("true");
			}
			scheduler.setText("true");
			List<Node> durations=doc.selectNodes("//stringProp[@name='ThreadGroup.duration']");
			for(Node duration:durations){duration.setText(paramter.getRunModeParams());
			}
		}
		}
		
	
		List<Node> ramps=doc.selectNodes("//stringProp[@name='ThreadGroup.ramp_time']");
		for(Node ramp:ramps){
			ramp.setText(paramter.getRamp_time());
		}
		
		if(paramter.getParamters().length()>0){
		String[] param=paramter.getParamters().split(";");
		for(String s:param){
			String[] nameValue=s.split("=");
			String name=nameValue[0];
			String value=nameValue.length>1?nameValue[1]:"";
			Node paramNode=doc.selectSingleNode("//elementProp[@name='"+name+"']/stringProp[@name='Argument.value']");
			if(paramNode!=null){
				paramNode.setText(value);
			}
		}
		
		}
		if(paramter.getDomain().length()>0){
			Node domain=doc.selectSingleNode("//stringProp[@name='HTTPSampler.domain']");
			if(domain!=null){
				domain.setText(paramter.getDomain());
			}
		}
		if(paramter.getPort().length()>0){
			Node port=doc.selectSingleNode("//stringProp[@name='HTTPSampler.port']");
			if(port!=null){
				port.setText(paramter.getPort());
			}
		}
		if(paramter.getPath().length()>0){
			Node path=doc.selectSingleNode("//stringProp[@name='HTTPSampler.path']");
			if(path!=null){
				path.setText(paramter.getPath());
			}
		}
		if(paramter.getMethod()!=null&&paramter.getMethod().length()>0){
			Node method=doc.selectSingleNode("//stringProp[@name='HTTPSampler.method']");
			if(method!=null){
				method.setText(paramter.getMethod());
			}
		}
		XMLWriter writer=new XMLWriter(filepath.write());
		writer.write(doc);
		writer.flush();
		writer.close();
		String fileName=filepath.getBaseName();
		if(fileName.matches("^[^-]+-\\d{1,5}T-\\d{1,5}[D,L]$")){
			fileName=fileName.split("-")[0];
		}
		fileName=fileName+"-"+paramter.getThreads()+"T-"+paramter.getRunModeParams()+paramter.getModeLorP()+".jmx";
		filepath.renameTo(new FilePath(filepath.getParent(),fileName));
		
	}
	//modify java jmx
	public synchronized static void modifyJavaJmx(FilePath filepath, PerformanceParamter paramter) throws Exception {
		SAXReader reader=new SAXReader();
		Document doc=reader.read(filepath.read());
		if(paramter.getThreads()!=null&&paramter.getThreads().length()>0){
		List<Node> threads=doc.selectNodes("//stringProp[@name='ThreadGroup.num_threads']");
		for(Node t:threads){
			t.setText(paramter.getThreads());
		}
		}
		if(paramter.getRunModeParams()!=null&&paramter.getRunModeParams().length()>0){
		List<Node> forevers=doc.selectNodes("//boolProp[@name='LoopController.continue_forever']");
		Node scheduler=doc.selectSingleNode("//boolProp[@name='ThreadGroup.scheduler']");
		if("Loops".equals(paramter.getRunMode())){
			for(Node forever:forevers){
				forever.setText("false");
			}
			scheduler.setText("false");
			List<Node> loops=doc.selectNodes("//stringProp[@name='LoopController.loops']");
			for(Node loop:loops){
				loop.setText(paramter.getRunModeParams());
			}
		}
		if("Duration".equals(paramter.getRunMode())){
			for(Node forever:forevers){
				forever.setText("true");
			}
			scheduler.setText("true");
			List<Node> durations=doc.selectNodes("//stringProp[@name='ThreadGroup.duration']");
			for(Node duration:durations){duration.setText(paramter.getRunModeParams());
			}
		}
		}
		List<Node> ramps=doc.selectNodes("//stringProp[@name='ThreadGroup.ramp_time']");
		for(Node ramp:ramps){
			ramp.setText(paramter.getRamp_time());
		}
		if(paramter.getClassName()!=null&&!paramter.getClassName().isEmpty()){
			Node className=doc.selectSingleNode("//stringProp[@name='classname']");
			className.setText(paramter.getClassName());
		}
		
		if(paramter.getParamters().length()>0){
			String[] param=paramter.getParamters().split(";");
			for(String s:param){
				String[] nameValue=s.split("=");
				String name=nameValue[0];
				String value=nameValue.length>1?nameValue[1]:"";
				Node paramNode=doc.selectSingleNode("//elementProp[@name='"+name+"']/stringProp[@name='Argument.value']");
				if(paramNode!=null){
					paramNode.setText(value);
				}
		}
		}
		
		XMLWriter writer=new XMLWriter(filepath.write());
		writer.write(doc);
		writer.flush();
		writer.close();
		
		String fileName=filepath.getBaseName();
		if(fileName.matches("^[^-]+-\\d{1,5}T-\\d{1,5}[D,L]$")){
			fileName=fileName.split("-")[0];
		}
		fileName=fileName+"-"+paramter.getThreads()+"T-"+paramter.getRunModeParams()+paramter.getModeLorP()+".jmx";
		filepath.renameTo(new FilePath(filepath.getParent(),fileName));
	}

	/**create http jmx 
	 * @param jmxPath
	 * @throws Exception 
	 */
	public synchronized static void createHttpJmx(FilePath jmxPath,PerformanceParamter paramter) throws Exception {
		SAXReader reader=new SAXReader();
		Document doc=reader.read(new StringReader(HTTP_JMX_DEMO));
		Node threads=doc.selectSingleNode("//stringProp[@name='ThreadGroup.num_threads']");
		threads.setText(paramter.getThreads());
		Node forever=doc.selectSingleNode("//boolProp[@name='LoopController.continue_forever']");
		Node scheduler=doc.selectSingleNode("//boolProp[@name='ThreadGroup.scheduler']");
		if("Loops".equals(paramter.getRunMode())){
			forever.setText("false");
			scheduler.setText("false");
			Node loops=doc.selectSingleNode("//stringProp[@name='LoopController.loops']");
			loops.setText(paramter.getRunModeParams());
		}
		if("Duration".equals(paramter.getRunMode())){
			forever.setText("true");
			scheduler.setText("true");
			Node duration=doc.selectSingleNode("//stringProp[@name='ThreadGroup.duration']");
			duration.setText(paramter.getRunModeParams());
		}
		Node ramp=doc.selectSingleNode("//stringProp[@name='ThreadGroup.ramp_time']");
		ramp.setText(paramter.getRamp_time());
		Node domain=doc.selectSingleNode("//stringProp[@name='HTTPSampler.domain']");
		domain.setText(paramter.getDomain());
		Node port=doc.selectSingleNode("//stringProp[@name='HTTPSampler.port']");
		port.setText(paramter.getPort());
		Node path=doc.selectSingleNode("//stringProp[@name='HTTPSampler.path']");
		path.setText(paramter.getPath());
		Node method=doc.selectSingleNode("//stringProp[@name='HTTPSampler.method']");
		method.setText(paramter.getMethod());
		long time=System.currentTimeMillis();
		Node start_time=doc.selectSingleNode("//longProp[@name='ThreadGroup.start_time']");
		Node endTime=doc.selectSingleNode("//longProp[@name='ThreadGroup.end_time']");
		start_time.setText(String.valueOf(time));
		endTime.setText(String.valueOf(time));
		String stringParam=paramter.getParamters();
		if(stringParam.length()>0){
			Node collectioNode=doc.selectSingleNode("//HTTPSamplerProxy/elementProp/collectionProp");
			String[] param=paramter.getParamters().split(";");
			String text="\n";
			for(String s:param){
				text+="          ";
				String[] nameValue=s.split("=");
				String name=nameValue[0];
				String value=nameValue.length>1?nameValue[1]:"";
				Document docTmp=reader.read(new StringReader(HTTP_PARAMTER_ELEMENT));
				docTmp.getRootElement().attribute("name").setValue(name);
				docTmp.selectSingleNode("//stringProp[@name='Argument.value']").setText(value);
				docTmp.selectSingleNode("//stringProp[@name='Argument.name']").setText(name);
				text+=docTmp.asXML().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", "")+"\n";
			}
			collectioNode.setText(text);
		}
		
		XMLWriter writer=new XMLWriter(jmxPath.write());
		writer.write(reader.read(new StringReader(doc.asXML().replace("&lt;", "<").replace("&gt;", ">"))));
		writer.flush();
		writer.close();
	}
	
	private final static String HTTP_PARAMTER_ELEMENT="              <elementProp name=\"\" elementType=\"HTTPArgument\">\n"
+"            <boolProp name=\"HTTPArgument.always_encode\">false</boolProp>\n"
+"            <stringProp name=\"Argument.value\"></stringProp>\n"
+"            <stringProp name=\"Argument.metadata\">=</stringProp>\n"
+"            <boolProp name=\"HTTPArgument.use_equals\">true</boolProp>\n"
+"           <stringProp name=\"Argument.name\"></stringProp>\n"
+"          </elementProp>\n";
	
	private final static String HTTP_JMX_DEMO="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+"<jmeterTestPlan version=\"1.2\" properties=\"2.5\" jmeter=\"2.10 r1533061\">\n"
			+"<hashTree>\n"
    +"<TestPlan guiclass=\"TestPlanGui\" testclass=\"TestPlan\" testname=\"测试计划\" enabled=\"true\">\n"
      +"<stringProp name=\"TestPlan.comments\"></stringProp>\n"
      +"<boolProp name=\"TestPlan.functional_mode\">false</boolProp>\n"
      +"<boolProp name=\"TestPlan.serialize_threadgroups\">false</boolProp>\n"
      +"<elementProp name=\"TestPlan.user_defined_variables\" elementType=\"Arguments\" guiclass=\"ArgumentsPanel\" testclass=\"Arguments\" testname=\"用户定义的变量\" enabled=\"true\">\n"
       +" <collectionProp name=\"Arguments.arguments\"/>\n"
      +"</elementProp>\n"
      +"<stringProp name=\"TestPlan.user_define_classpath\"></stringProp>\n"
    +"</TestPlan>\n"
    +"<hashTree>\n"
      +"<ThreadGroup guiclass=\"ThreadGroupGui\" testclass=\"ThreadGroup\" testname=\"线程组\" enabled=\"true\">\n"
        +"<stringProp name=\"ThreadGroup.on_sample_error\">continue</stringProp>\n"
        +"<elementProp name=\"ThreadGroup.main_controller\" elementType=\"LoopController\" guiclass=\"LoopControlPanel\" testclass=\"LoopController\" testname=\"循环控制器\" enabled=\"true\">\n"
          +"<boolProp name=\"LoopController.continue_forever\">false</boolProp>\n"
          +"<stringProp name=\"LoopController.loops\">1</stringProp>\n"
        +"</elementProp>\n"
       +" <stringProp name=\"ThreadGroup.num_threads\">1</stringProp>\n"
       +" <stringProp name=\"ThreadGroup.ramp_time\">1</stringProp>\n"
        +"<longProp name=\"ThreadGroup.start_time\">1402560894000</longProp>\n"
       +" <longProp name=\"ThreadGroup.end_time\">1402560894000</longProp>\n"
      +"  <boolProp name=\"ThreadGroup.scheduler\">false</boolProp>\n"
       +" <stringProp name=\"ThreadGroup.duration\"></stringProp>\n"
        +"<stringProp name=\"ThreadGroup.delay\"></stringProp>\n"
    +"  </ThreadGroup>\n"
     +" <hashTree>\n"
      +"  <HTTPSamplerProxy guiclass=\"HttpTestSampleGui\" testclass=\"HTTPSamplerProxy\" testname=\"HTTP请求\" enabled=\"true\">\n"
      +"    <elementProp name=\"HTTPsampler.Arguments\" elementType=\"Arguments\" guiclass=\"HTTPArgumentsPanel\" testclass=\"Arguments\" testname=\"用户定义的变量\" enabled=\"true\">\n"
      +"      <collectionProp name=\"Arguments.arguments\"/>\n"
       +"   </elementProp>\n"
        +"  <stringProp name=\"HTTPSampler.domain\"></stringProp>\n"
      +"    <stringProp name=\"HTTPSampler.port\"></stringProp>\n"
       +"   <stringProp name=\"HTTPSampler.connect_timeout\"></stringProp>\n"
       +"   <stringProp name=\"HTTPSampler.response_timeout\"></stringProp>\n"
        +"  <stringProp name=\"HTTPSampler.protocol\"></stringProp>\n"
        +"  <stringProp name=\"HTTPSampler.contentEncoding\"></stringProp>\n"
       +"   <stringProp name=\"HTTPSampler.path\"></stringProp>\n"
       +"   <stringProp name=\"HTTPSampler.method\">GET</stringProp>\n"
       +"   <boolProp name=\"HTTPSampler.follow_redirects\">true</boolProp>\n"
       +"   <boolProp name=\"HTTPSampler.auto_redirects\">false</boolProp>\n"
       +"   <boolProp name=\"HTTPSampler.use_keepalive\">true</boolProp>\n"
       +"  <boolProp name=\"HTTPSampler.DO_MULTIPART_POST\">false</boolProp>\n"
    		   +"   <boolProp name=\"HTTPSampler.monitor\">false</boolProp>\n"
       +"   <stringProp name=\"HTTPSampler.embedded_url_re\"></stringProp>\n"
      +"  </HTTPSamplerProxy>\n"
    +"    <hashTree/>\n"
   +"   </hashTree>\n"
  +"  </hashTree>\n"
 +" </hashTree>\n"
+"</jmeterTestPlan>\n";
	
	public final static String JAVA_SAMPLE_JMX_DEMO="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
+"<jmeterTestPlan version=\"1.2\" properties=\"2.5\" jmeter=\"2.10 r1533061\">\n"
+"  <hashTree>\n"
+"    <TestPlan guiclass=\"TestPlanGui\" testclass=\"TestPlan\" testname=\"测试计划\" enabled=\"true\">\n"
+"      <stringProp name=\"TestPlan.comments\"></stringProp>\n"
+"      <boolProp name=\"TestPlan.functional_mode\">false</boolProp>\n"
+"      <boolProp name=\"TestPlan.serialize_threadgroups\">false</boolProp>\n"
+"      <elementProp name=\"TestPlan.user_defined_variables\" elementType=\"Arguments\" guiclass=\"ArgumentsPanel\" testclass=\"Arguments\" testname=\"用户定义的变量\" enabled=\"true\">\n"
+"        <collectionProp name=\"Arguments.arguments\"/>\n"
+"      </elementProp>\n"
+"      <stringProp name=\"TestPlan.user_define_classpath\"></stringProp>\n"
+"    </TestPlan>\n"
+"    <hashTree>\n"
+"      <ThreadGroup guiclass=\"ThreadGroupGui\" testclass=\"ThreadGroup\" testname=\"线程组\" enabled=\"true\">\n"
+"        <stringProp name=\"ThreadGroup.on_sample_error\">continue</stringProp>\n"
+"        <elementProp name=\"ThreadGroup.main_controller\" elementType=\"LoopController\" guiclass=\"LoopControlPanel\" testclass=\"LoopController\" testname=\"循环控制器\" enabled=\"true\">\n"
+"          <boolProp name=\"LoopController.continue_forever\">false</boolProp>\n"
+"          <stringProp name=\"LoopController.loops\">1</stringProp>\n"
+"        </elementProp>\n"
+"        <stringProp name=\"ThreadGroup.num_threads\">1</stringProp>\n"
+"        <stringProp name=\"ThreadGroup.ramp_time\">1</stringProp>\n"
+"        <longProp name=\"ThreadGroup.start_time\">1403775974000</longProp>\n"
+"        <longProp name=\"ThreadGroup.end_time\">1403775974000</longProp>\n"
+"        <boolProp name=\"ThreadGroup.scheduler\">false</boolProp>\n"
+"        <stringProp name=\"ThreadGroup.duration\"></stringProp>\n"
+"       <stringProp name=\"ThreadGroup.delay\"></stringProp>\n"
+"     </ThreadGroup>\n"
+"     <hashTree>\n"
+"        <JavaSampler guiclass=\"JavaTestSamplerGui\" testclass=\"JavaSampler\" testname=\"Java请求\" enabled=\"true\">\n"
+"          <elementProp name=\"arguments\" elementType=\"Arguments\" guiclass=\"ArgumentsPanel\" testclass=\"Arguments\" enabled=\"true\">\n"
+"            <collectionProp name=\"Arguments.arguments\"/>\n"
+"          </elementProp>\n"
+"          <stringProp name=\"classname\"></stringProp>\n"
+"        </JavaSampler>\n"
+"        <hashTree/>\n"
+"      </hashTree>\n"
+"    </hashTree>\n"
+"  </hashTree>\n"
+"</jmeterTestPlan>\n";
	
	public final static String JAVA_SAMPLE_PARAMTER_ELEMENT="              <elementProp name=\"\" elementType=\"Argument\">\n"
+"                <stringProp name=\"Argument.name\"></stringProp>\n"
+"                <stringProp name=\"Argument.value\"></stringProp>\n"
+"                <stringProp name=\"Argument.metadata\">=</stringProp>\n"
+"              </elementProp>\n";
	



	/** create java jmx
	 * @param jmeterPath
	 * @param javaSampleParameter
	 * @throws Exception 
	 */
	public synchronized static void createJavaJmx(FilePath filepath,PerformanceParamter performanceParamter) throws Exception {
		SAXReader reader=new SAXReader();
		Document doc=reader.read(new StringReader(JAVA_SAMPLE_JMX_DEMO));
		Node threads=doc.selectSingleNode("//stringProp[@name='ThreadGroup.num_threads']");
		threads.setText(performanceParamter.getThreads());
		Node forever=doc.selectSingleNode("//boolProp[@name='LoopController.continue_forever']");
		Node scheduler=doc.selectSingleNode("//boolProp[@name='ThreadGroup.scheduler']");
		if("Loops".equals(performanceParamter.getRunMode())){
			forever.setText("false");
			scheduler.setText("false");
			Node loops=doc.selectSingleNode("//stringProp[@name='LoopController.loops']");
			loops.setText(performanceParamter.getRunModeParams());
		}
		if("Duration".equals(performanceParamter.getRunMode())){
			forever.setText("true");
			scheduler.setText("true");
			Node duration=doc.selectSingleNode("//stringProp[@name='ThreadGroup.duration']");
			duration.setText(performanceParamter.getRunModeParams());
		}
		Node ramp=doc.selectSingleNode("//stringProp[@name='ThreadGroup.ramp_time']");
		ramp.setText(performanceParamter.getRamp_time());
		long time=System.currentTimeMillis();
		Node start_time=doc.selectSingleNode("//longProp[@name='ThreadGroup.start_time']");
		Node endTime=doc.selectSingleNode("//longProp[@name='ThreadGroup.end_time']");
		start_time.setText(String.valueOf(time));
		endTime.setText(String.valueOf(time));
		Node className=doc.selectSingleNode("//stringProp[@name='classname']");
		className.setText(performanceParamter.getClassName());
		String stringParam=performanceParamter.getParamters();
		if(stringParam.length()>0){
			Node collectioNode=doc.selectSingleNode("//JavaSampler/elementProp/collectionProp");
			String[] param=stringParam.split(";");
			String text="\n";
			for(String s:param){
				text+="          ";
				String[] nameValue=s.split("=");
				String name=nameValue[0];
				String value=nameValue.length>1?nameValue[1]:"";
				Document docTmp=reader.read(new StringReader(JAVA_SAMPLE_PARAMTER_ELEMENT));
				docTmp.getRootElement().attribute("name").setValue(name);
				docTmp.selectSingleNode("//stringProp[@name='Argument.value']").setText(value);
				docTmp.selectSingleNode("//stringProp[@name='Argument.name']").setText(name);
				text+=docTmp.asXML().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", "")+"\n";
			}
			collectioNode.setText(text);
		}
		
		XMLWriter writer=new XMLWriter(filepath.write());
		
		writer.write(reader.read(new StringReader(doc.asXML().replace("&lt;", "<").replace("&gt;", ">"))));
		writer.flush();
		writer.close();
	}


	
}
