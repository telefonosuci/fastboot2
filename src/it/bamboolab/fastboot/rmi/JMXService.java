package it.bamboolab.fastboot.rmi;

import it.bamboolab.fastboot.core.Fastboot;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class JMXService implements MXBean {
	
	private static Logger logger = Logger.getLogger(JMXService.class);

    public JMXService(String serviceName) {}

    public String getServiceName() {
    	return "Fastboot Service";
    }

    public void stop() {
    	Fastboot.stop();
	}

	public String getStatus() {
		
		String ret = "";
		return ret;
	}
	
	public String usrDump(String dir) {

		String ret = "";
		return ret;
	}

	public String heapDump(String dir) {

		String ret = "An error occured executing heapdump request.";
		return ret;
	}

	@Override
	public String traceon(String level) {
		Logger.getRootLogger().setLevel(Level.toLevel(level));
		return "Logger level set to " + Level.toLevel(level).toString();
	}

	@Override
	public String traceoff() {
		Logger.getRootLogger().setLevel(Level.WARN);
		return "Logger level set to WARNING (default)";
	}

	@Override
	public String refresh() {		
		return "Application refreshed";
	}
}
