package it.bamboolab.fastboot.context;

public class ApplicationProperties {

	public static final String FB_VERSION = "1.0.0";
	
	public static String fileSeparator = System.getProperty("file.separator");
	public static String lineSeparator = System.getProperty("line.separator");

	private String username;
	private String password;

	private int rmiPort;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRmiPort() {
		return rmiPort;
	}

	public void setRmiPort(int rmiPort) {
		this.rmiPort = rmiPort;
	}
}
