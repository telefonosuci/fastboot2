package it.bamboolab.fastboot.core;

import it.bamboolab.fastboot.context.ApplicationProperties;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;

public class Client {

	private static Scanner scanner;

	public static void main(String[] args) {
		
		if(args.length > 0) {
			
			String action = args[0];

            switch (action) {
                case "-version":
                    printVersion();
                    break;
                case "-listmac":
                    printMacs();
                    break;
                case "-newkey":
                    setKey();
                    break;
                default:
                    System.out.println("Invalid argument");
                    break;
            }
            
		} else{
			System.out.println("Invalid arguments number");
        }
	}
	
	public static void setKey() {
		
		scanner = new Scanner(System.in);
		
		System.out.println("Hello fuckers");
		System.out.print("Enter a new key: ");
		String key = scanner.next();
		System.out.println(String.format("New key is %s", key));
	}

	public static void printVersion() {
		
		System.out.println("Java version: " + System.getProperty("java.version"));
		System.out.println("OS name: " + System.getProperty("os.name"));
		System.out.println("OS version: " + System.getProperty("os.version"));
		System.out.println("Arch: " + System.getProperty("os.arch"));
		System.out.println("Java home: " + System.getProperty("java.home"));
		System.out.println("Fastbootversion: " + ApplicationProperties.FB_VERSION);
		System.out.println("File encoding: " + System.getProperty("file.encoding"));
		
	}

	public static void printMacs() {

		try {

			Enumeration<NetworkInterface> networkInterfaceEnum = NetworkInterface.getNetworkInterfaces();

			while (networkInterfaceEnum.hasMoreElements()) {

				NetworkInterface networkInterface = networkInterfaceEnum.nextElement();

				byte[] macAddress = networkInterface.getHardwareAddress();

				if (macAddress != null && macAddress.length > 0) {

					StringBuilder sb = new StringBuilder();
					
					for (int i = 0; i < macAddress.length; i++) {
						sb.append(String.format("%02X%s", macAddress[i], (i < macAddress.length - 1) ? "-" : ""));
					}

					String currentMacAddress = sb.toString();

					System.out.println("MacAddress " + networkInterface.getDisplayName() + ": " + currentMacAddress);
				}
			}

		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

}
