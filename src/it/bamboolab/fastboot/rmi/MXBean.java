package it.bamboolab.fastboot.rmi;

public interface MXBean {

    public String getServiceName();

    public void stop();

    public String getStatus();
    
	public String usrDump(String dir);

    public String heapDump(String dir);

    public String traceon(String level);
	
	public String traceoff();

	public String refresh();

}
