package it.bamboolab.fastboot.rmi;

import javax.management.remote.JMXConnector;

import org.apache.log4j.Logger;


import java.io.FileInputStream;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

public class Client {

    private static String domainName = "it.bamboolab";
    private static String typeName = "MXBean";
    private static String objName = domainName + ":type=" + typeName;
    private static String host = "localhost";

    private static JMXManager jmxManager = null;

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Invalid number of arguments");
            System.exit(-1);
        }

        try {

            Properties prop = new Properties();
            InputStream input = new FileInputStream("cfg/fastboot.properties");
            prop.load(input);

            int port = Integer.parseInt(prop.getProperty("fastboot.application.rmi.port"));

            String action = args[0];
            String arg1 = "";

            if (args.length > 1)
                arg1 = args[1];

            jmxManager = JMXManager.getInstance();

            JMXConnector jmxc = jmxManager.getRMIConnectorClient(host, port);
            MXBean mxbean = jmxManager.getServiceMXBeanViaProxies(jmxc, objName);

            switch (action) {

                case "-stop":
                    try {
                        String name = mxbean.getServiceName();
                        System.out.println("Stop command sent to " + name);
                        //logger.info("Stop command sent to " + name);
                        mxbean.stop();

                    } catch (Exception e) {
                        if (e.getMessage() != null)
                            System.err.println(e.getMessage());
                    } finally {
                        System.out.println("Fastboot has been shut down");
                    }

                    break;

                case "-status":

                    try {
                        System.out.println("Status:\n" + mxbean.getStatus());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case "-traceon":

                    if (arg1.equals(""))
                        arg1 = "ERROR";

                    arg1 = arg1.toUpperCase();

                    if (arg1.equals("DEBUG") ||
                        arg1.equals("ALL") ||
                        arg1.equals("ERROR") ||
                        arg1.equals("FATAL") ||
                        arg1.equals("INFO") ||
                        arg1.equals("OFF") ||
                        arg1.equals("TRACE") ||
                        arg1.equals("WARN")) {

                        System.out.println(mxbean.traceon(arg1));

                    } else {
                        System.out.println("Invalid level");
                    }

                    break;

                case "-traceoff":
                    System.out.println(mxbean.traceoff());
                    break;
                case "-usrdump":
                    System.out.println(mxbean.usrDump(arg1));
                    break;
                case "-heapdump":
                    System.out.println(mxbean.heapDump(arg1));
                    break;
                case "-refresh":
                    System.out.println(mxbean.refresh());
                    break;
                default:
                    System.out.println("Invalid argument");
                    break;
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } catch (Throwable e) {
            System.err.println(e.getMessage());
        }
    }
}