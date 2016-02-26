package it.bamboolab.fastboot.dump;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

public class HeapDumper implements IHeapDumper {

    //public static final String RCS_ID = "$Id: HeapDumper.java,v 1.2 2010/04/02 14:29:38 cu0060 Exp $";

    // This is the name of the HotSpot Diagnostic MBean
    private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";

    private Class clazz = null; // to hold clazz for the HotSpotDiagnosticMXBean

    // field to store the hotspot diagnostic MBean Object to not
    // have problems if com.sun.management does not exist
    private volatile Object hotspotMBean;

    public HeapDumper() throws UnsupportedOperationException {
        super();
        this.hotspotMBean = getHotspotMBean();
    }

    /**
     * Call this method from your application whenever you want to dump the heap snapshot into a file.
     *
     * @param heapOutputFile name of the heap dump file
     * @param live           flag that tells whether to dump only the live objects
     */
    public void dumpHeap(String heapOutputFile, boolean live) throws UnsupportedOperationException {
        try {
            // simulate following call
            // hotspotMBean.dumpHeap(heapOutputFile, live);
            Method m = clazz.getMethod("dumpHeap", new Class[]{String.class, boolean.class});
            m.invoke(hotspotMBean, new Object[]{heapOutputFile, live});

        } catch (Throwable e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    // get the hotspot diagnostic MBean from the
    // platform MBean server
    private Object getHotspotMBean() throws UnsupportedOperationException {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();

            // try to load the class describing this object
            clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");

            Object bean = ManagementFactory.newPlatformMXBeanProxy(server, HOTSPOT_BEAN_NAME, clazz);

            return bean;

        } catch (Throwable e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

}