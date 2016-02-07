package it.bamboolab.fastboot.rmi;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;


public class JMXManager {

	private static final Logger logger = Logger.getLogger(JMXManager.class);
	
	private static final String SYNC_OBJ = JMXManager.class.getName();
	private static final String URL_PREFIX = "service:jmx:rmi:///jndi/rmi://";
	private static final String URL_POSTFIX = "/fastbootService";
	private static JMXManager jmxManager = null;
	private JMXConnectorServer jmxConnectorServer = null;
	private MBeanServer beanServer;

	private JMXManager() {

		super();
		beanServer = ManagementFactory.getPlatformMBeanServer();
	}

	public static JMXManager getInstance() {
		
		synchronized (SYNC_OBJ) {

			if (jmxManager == null) {
				jmxManager = new JMXManager();
			}
			return jmxManager;
		}
	}

	public void startJmxServer(String host, int port) throws Exception {

		if (port > 0) {

			try {
				
				MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

				// Create the RMI registry on port
				LocateRegistry.createRegistry(port);

				// Build a URL which tells the RMIConnectorServer to bind to the
				// RMIRegistry running on port .
				JMXServiceURL url = new JMXServiceURL(URL_PREFIX + host + ":" + port + URL_POSTFIX);

				jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbeanServer);
				jmxConnectorServer.start();

				logger.info("JMX Server create with url: '" + url.toString() + "'");

			} catch (IOException e) {

				throw new Exception("Start command failed: port " + port + " already in use. The process could be already started");
			}
		}
	}

	public void stopJmxServer() {

		if (jmxConnectorServer != null && jmxConnectorServer.isActive()) {

			logger.info("Stopping JMX Server '" + jmxConnectorServer.getAddress() + "'");

			try {
				jmxConnectorServer.stop();
			} catch (IOException e) {
				logger.error("Error during JMX server stop", e);
			}
		}
	}

	public void registryMBeanServer(Object obj, String objName) throws Exception {
		
		ObjectName name = new ObjectName(objName);
		beanServer.registerMBean(obj, name);
	}

	public JMXConnector getRMIConnectorClient(String host, int port) throws Exception {

		System.out.println("Connecting to JMX service: " + URL_PREFIX + host + ":" + port + URL_POSTFIX);

		JMXConnector jmxc = null;

		try {
			JMXServiceURL url = new JMXServiceURL(URL_PREFIX + host + ":" + port + URL_POSTFIX);
			jmxc = JMXConnectorFactory.connect(url, null);
		} catch (IOException e) {
			String STOP_FAIL = "Connection refuse on port " + port + " . The process could be already stopped. ";
			throw new Exception(STOP_FAIL, e);
		}

		return jmxc;
	}

	public MXBean getServiceMXBeanViaProxies(JMXConnector jmxc, String objName) throws Exception {
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		ObjectName mbeanName = new ObjectName(objName);
		return JMX.newMXBeanProxy(mbsc, mbeanName, MXBean.class, true);
	}
}
