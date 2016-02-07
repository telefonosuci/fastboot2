package it.bamboolab.fastboot.rmi;

import org.apache.log4j.Logger;

public class JMXServer {

    private static Logger logger = Logger.getLogger(JMXServer.class);

	private static String domainName = "it.bamboolab";
    private static String typeName = "MXBean";
    private static String objName = domainName + ":type=" + typeName;
    private static String host = "localhost";
    private static int port = 3210;
    
    public static JMXManager jmxManager = null;

    private MXBean srvMXBean = null;

    public JMXServer() {
	    jmxManager = JMXManager.getInstance();
    }
    
    public void start(){
		try {
			
		    jmxManager.startJmxServer(host, port);
		    srvMXBean = new JMXService("fastbootService");
		    jmxManager.registryMBeanServer(srvMXBean, objName);
		
		} catch(Exception e){
			logger.error("Error during JMX start", e);
		}
    }
}
