package it.bamboolab.fastboot.dump;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.lang.management.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ThreadDumper implements IThreadDumper {

	private static final Logger logger = Logger.getLogger(ThreadDumper.class);
	
	private static final String NEW_LINE = System.getProperty("line.separator");

	private static final String FIELD_SEPARATOR = ",";

	private static final String KEY_SEPARATOR = "\\";

	private static final String BANNER = "--------------------------------------------------------------------------------";

	private static final int STACK_TRACE_MAX_DEPTH = 100;

	private Writer writer = null;

	public ThreadDumper(Writer writer)
	{
		this.writer = writer;
	}

	public static String getPID()
	{
		return ManagementFactory.getRuntimeMXBean().getName();
	}

	public static long getJREStartTime()
	{
		return ManagementFactory.getRuntimeMXBean().getStartTime();
	}

	public void resetWriter(Writer writer)
	{
		closeWriter();

		this.writer = writer;
	}

	public Writer getWriter()
	{
		return writer;
	}

	public void closeWriter()
	{
		if (writer != null)
		{
			try
			{
				writer.close();
			}
			catch (IOException e)
			{
				logger.error(e.getMessage());
			}
		}
	}

	public void insertBanner(String title) throws IOException
	{
		write(getBanner(title));
	}

	public void insertNewLine() throws IOException
	{
		writer.write(NEW_LINE);
	}

	public void insertString(String str) throws IOException
	{
		writeln(str);
	}

	public void dumpObjectBean(Object bean, String prefixString) throws IOException
	{
		if (bean instanceof Map)
		{
			writeln(printMapInfoAsLine((Map<?, ?>) bean, prefixString));
		}
		else if (bean instanceof List)
		{
			writeln(printListInfoAsLine((List<?>) bean, prefixString));
		}
		else if (bean instanceof Object[])
		{
			writeln(printArrayInfoAsLine((Object[]) bean, prefixString));
		}
		else
		{
			writeln(printBeanInfoAsLine(bean, prefixString));
		}

		writer.flush();
	}

	public void dumpEnvVariables() throws IOException
	{
		String className = "EnvVariables";
		write(getBanner(className));

		Map<?, ?> envMap = System.getenv();
		dumpObjectBean(envMap, className + KEY_SEPARATOR);
	}

	public void dumpClassLoadingMXBean() throws IOException
	{
		String className = "ClassLoadingMXBean";
		write(getMXBanner(className));

		ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();

		writeln(printBeanInfoAsLine(classLoadingMXBean, className + KEY_SEPARATOR));

		writer.flush();
	}

	public void dumpCompilationMXBean() throws IOException
	{
		String className = "CompilationMXBean";
		write(getMXBanner(className));

		CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();

		writeln(printBeanInfoAsLine(compilationMXBean, className + KEY_SEPARATOR));

		writer.flush();
	}

	public void dumpGarbageCollectorMXBean() throws IOException
	{
		String className = "GarbageCollectorMXBean";
		write(getMXBanner(className));

		List<GarbageCollectorMXBean> garbageCollectorMXBeanList = ManagementFactory
				.getGarbageCollectorMXBeans();
		Iterator<GarbageCollectorMXBean> it = garbageCollectorMXBeanList.iterator();
		while (it.hasNext())
		{
			GarbageCollectorMXBean garbageCollectorMXBean = it.next();
			String name = garbageCollectorMXBean.getName();

			writeln(printBeanInfoAsLine(garbageCollectorMXBean, className + KEY_SEPARATOR, name
					+ KEY_SEPARATOR));
		}
		writer.flush();
	}

	public void dumpMemoryMXBean() throws IOException
	{
		String className = "MemoryMXBean";
		write(getMXBanner(className));

		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		writeln(printBeanInfoAsLine(memoryMXBean, className + KEY_SEPARATOR));

		MemoryUsage memory = memoryMXBean.getHeapMemoryUsage();
		writeln(printBeanInfoAsLine(memory, className + KEY_SEPARATOR + "HeapMemory" + KEY_SEPARATOR));

		memory = memoryMXBean.getNonHeapMemoryUsage();
		writeln(printBeanInfoAsLine(memory, className + KEY_SEPARATOR + "NonHeapMemory" + KEY_SEPARATOR));

		writer.flush();
	}

	public void dumpMemoryManagerMXBean() throws IOException
	{
		String className = "MemoryManagerMXBean";
		write(getMXBanner(className));

		List<MemoryManagerMXBean> memoryManagerMXBeanList = ManagementFactory.getMemoryManagerMXBeans();
		Iterator<MemoryManagerMXBean> it = memoryManagerMXBeanList.iterator();
		while (it.hasNext())
		{
			MemoryManagerMXBean memoryManagerMXBean = it.next();
			String name = memoryManagerMXBean.getName();

			writeln(printBeanInfoAsLine(memoryManagerMXBean, className + KEY_SEPARATOR, name + KEY_SEPARATOR));

			Object[] memoryPoolNames = memoryManagerMXBean.getMemoryPoolNames();
			writeln(printArrayInfoAsLine(memoryPoolNames, className + KEY_SEPARATOR, name + KEY_SEPARATOR
					+ "MemoryPoolNames="));
		}

		writer.flush();
	}

	public void dumpMemoryPoolMXBean() throws IOException
	{
		String className = "MemoryPoolMXBean";
		write(getMXBanner(className));

		List<MemoryPoolMXBean> MemoryPoolMXBeanList = ManagementFactory.getMemoryPoolMXBeans();

		Iterator<MemoryPoolMXBean> it = MemoryPoolMXBeanList.iterator();

		while (it.hasNext())
		{
			MemoryPoolMXBean memoryPoolMXBean = it.next();
			String name = memoryPoolMXBean.getName();
			writeln(printBeanInfoAsLine(memoryPoolMXBean, className + KEY_SEPARATOR, name + KEY_SEPARATOR));

			MemoryUsage memoryUsage = memoryPoolMXBean.getCollectionUsage();
			writeln(printBeanInfoAsLine(memoryUsage, className + KEY_SEPARATOR, name + KEY_SEPARATOR
					+ "CollectionUsage" + KEY_SEPARATOR));

			Object[] memoryManagerNames = memoryPoolMXBean.getMemoryManagerNames();
			writeln(printArrayInfoAsLine(memoryManagerNames, className + KEY_SEPARATOR, name + KEY_SEPARATOR
					+ "MemoryManagerNames="));

			memoryUsage = memoryPoolMXBean.getPeakUsage();
			writeln(printBeanInfoAsLine(memoryUsage, className + KEY_SEPARATOR, name + KEY_SEPARATOR
					+ "PeakUsage" + KEY_SEPARATOR));

			memoryUsage = memoryPoolMXBean.getUsage();
			writeln(printBeanInfoAsLine(memoryUsage, className + KEY_SEPARATOR, name + KEY_SEPARATOR
					+ "Usage" + KEY_SEPARATOR));

			writeln(className + KEY_SEPARATOR + name + KEY_SEPARATOR + "Type="
					+ memoryPoolMXBean.getType().toString());
		}

		writer.flush();
	}

	public void dumpOperatingSystemMXBean() throws IOException
	{
		String className = "OperatingSystemMXBean";
		write(getMXBanner(className));

		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		writeln(printBeanInfoAsLine(operatingSystemMXBean, className + KEY_SEPARATOR));

		writer.flush();
	}

	public void dumpRuntimeMXBean() throws IOException
	{
		String className = "RuntimeMXBean";
		write(getMXBanner(className));

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		writeln(printBeanInfoAsLine(runtimeMXBean, className + KEY_SEPARATOR));

		Map<?, ?> systemPropertiesMap = runtimeMXBean.getSystemProperties();
		writeln(printMapInfoAsLine(systemPropertiesMap, className + KEY_SEPARATOR + "SystemProperties"
				+ KEY_SEPARATOR));

		List<?> inputArgumentsList = runtimeMXBean.getInputArguments();
		writeln(printListInfoAsLine(inputArgumentsList, className + KEY_SEPARATOR, "InputArguments="));

		writer.flush();
	}

	public void dumpThreadMXBean() throws IOException
	{
		String className = "ThreadMXBean";
		write(getMXBanner(className));

		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

		writeln(printBeanInfoAsLine(threadMXBean, className + KEY_SEPARATOR));

		long[] monitorDeadlockedThreads = threadMXBean.findMonitorDeadlockedThreads();

		writeln(printLongArrayInfoAsLine(monitorDeadlockedThreads, className + KEY_SEPARATOR,
				"MonitorDeadlockedThreads="));

		ThreadInfo[] threadInfo = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(),
				STACK_TRACE_MAX_DEPTH);
		for (int i = 0; i < threadInfo.length; i++)
		{
			String name = threadInfo[i].getThreadName();

			writeln(printBeanInfoAsLine(threadInfo[i], className + KEY_SEPARATOR, name + KEY_SEPARATOR));

			StackTraceElement[] stackTraceElement = threadInfo[i].getStackTrace();
			writeln(printBeanStackTraceAsLine(stackTraceElement, className + KEY_SEPARATOR, name
					+ KEY_SEPARATOR + "StackTraceElement" + KEY_SEPARATOR));
		}

		writer.flush();

	}

	public void dumpAllMXBean() throws IOException
	{
		dumpLicenceInfo();
		insertNewLine();
		insertNewLine();
		dumpEnvVariables();
		insertNewLine();
		insertNewLine();
		dumpOperatingSystemMXBean();
		insertNewLine();
		insertNewLine();
		dumpRuntimeMXBean();
		insertNewLine();
		insertNewLine();
		dumpThreadMXBean();
		insertNewLine();
		insertNewLine();
		dumpMemoryMXBean();
		insertNewLine();
		insertNewLine();
		dumpMemoryManagerMXBean();
		insertNewLine();
		insertNewLine();
		dumpMemoryPoolMXBean();
		insertNewLine();
		insertNewLine();
		dumpGarbageCollectorMXBean();
		insertNewLine();
		insertNewLine();
		dumpCompilationMXBean();
		insertNewLine();
		insertNewLine();
		dumpClassLoadingMXBean();
		insertNewLine();
		insertNewLine();
		write(BANNER);
		insertNewLine();
		write(BANNER);
	}

	private void dumpLicenceInfo() throws IOException {
		//TODO licence info
		write(getBanner("Licence info"));
		//writeln(Licence.print());
	}

	private static String printMapInfoAsLine(Map<?, ?> beanMap)
	{
		return printMapInfoAsLine(beanMap, null, null);
	}

	private static String printMapInfoAsLine(Map<?, ?> beanMap, String prefixString)
	{
		return printMapInfoAsLine(beanMap, prefixString, null);
	}

	private static String printMapInfoAsLine(Map<?, ?> beanMap, String prefixString, String name)
	{
		Iterator<?> it = beanMap.entrySet().iterator();
		StringBuffer sbValue = new StringBuffer();
		while (it.hasNext())
		{
			Map.Entry entry = (Map.Entry) it.next();
			if (prefixString != null && prefixString.length() > 0)
			{
				sbValue.append(prefixString);
			}
			if (name != null && name.length() > 0)
			{
				sbValue.append(name);
			}
			sbValue.append(entry.getKey());
			sbValue.append("=");
			sbValue.append(entry.getValue());
			sbValue.append(NEW_LINE);
		}
		String values = sbValue.toString();
		if (values.length() > NEW_LINE.length())
		{
			values = sbValue.substring(0, sbValue.length() - NEW_LINE.length());
		}
		return values;
	}

	private static String printListInfoAsLine(List<?> beanList)
	{
		return printListInfoAsLine(beanList, null, null);
	}

	private static String printListInfoAsLine(List<?> beanList, String prefixString)
	{
		return printListInfoAsLine(beanList, prefixString, null);
	}

	private static String printListInfoAsLine(List<?> beanList, String prefixString, String name)
	{
		if (beanList == null)
		{
			return null;
		}

		Iterator<?> it = beanList.iterator();
		StringBuffer sbValue = new StringBuffer();

		if (prefixString != null && prefixString.length() > 0)
		{
			sbValue.append(prefixString);
		}
		if (name != null && name.length() > 0)
		{
			sbValue.append(name);
		}

		while (it.hasNext())
		{
			sbValue.append(it.next());
			sbValue.append(FIELD_SEPARATOR);
		}
		String values = sbValue.toString();
		if (values.length() > FIELD_SEPARATOR.length())
		{
			values = sbValue.substring(0, sbValue.length() - FIELD_SEPARATOR.length());
		}
		return values;
	}

	private static String printArrayInfoAsLine(Object[] beanArray)
	{
		return printArrayInfoAsLine(beanArray, null, null);
	}

	private static String printArrayInfoAsLine(Object[] beanArray, String prefixString)
	{
		return printArrayInfoAsLine(beanArray, prefixString, null);
	}

	private static String printArrayInfoAsLine(Object[] beanArray, String prefixString, String name)
	{
		if (beanArray == null)
		{
			return null;
		}
		StringBuffer sbValue = new StringBuffer();

		if (prefixString != null && prefixString.length() > 0)
		{
			sbValue.append(prefixString);
		}
		if (name != null && name.length() > 0)
		{
			sbValue.append(name);
		}
		for (int i = 0; i < beanArray.length; i++)
		{
			sbValue.append(beanArray[i]);
			sbValue.append(FIELD_SEPARATOR);
		}
		String values = sbValue.toString();
		if (values.length() > FIELD_SEPARATOR.length())
		{
			values = sbValue.substring(0, sbValue.length() - FIELD_SEPARATOR.length());
		}
		return values;
	}

	private static String printLongArrayInfoAsLine(long[] beanArray)
	{
		return printLongArrayInfoAsLine(beanArray, null, null);
	}

	private static String printLongArrayInfoAsLine(long[] beanArray, String prefixString)
	{
		return printLongArrayInfoAsLine(beanArray, prefixString, null);
	}

	private static String printLongArrayInfoAsLine(long[] beanArray, String prefixString, String name)
	{
		StringBuffer sbValue = new StringBuffer();

		if (prefixString != null && prefixString.length() > 0)
		{
			sbValue.append(prefixString);
		}
		if (name != null && name.length() > 0)
		{
			sbValue.append(name);
		}

		if (beanArray == null)
		{
			return sbValue.toString();
		}

		for (int i = 0; i < beanArray.length; i++)
		{
			sbValue.append(beanArray[i]);
			sbValue.append(FIELD_SEPARATOR);
		}
		String values = sbValue.toString();
		if (values.length() > FIELD_SEPARATOR.length())
		{
			values = sbValue.substring(0, sbValue.length() - FIELD_SEPARATOR.length());
		}
		return values;
	}

	private static String printBeanInfoAsLine(Object bean)
	{

		return printBeanInfoAsLine(bean, null, null);
	}

	private static String printBeanInfoAsLine(Object bean, String prefixString)
	{

		return printBeanInfoAsLine(bean, prefixString, null);
	}

	private static String printBeanInfoAsLine(Object bean, String prefixString, String name)
	{
		if (bean == null)
		{
			return null;
		}
		List<?> getters = findGetMethods(bean.getClass());
		Object propertyValue = null;
		String propertyName = null;
		Method method = null;
		StringBuffer sbValue = new StringBuffer();

		for (int i = 0; i < getters.size(); i++)
		{
			method = (Method) getters.get(i);

			propertyName = method.getName().substring(3);
			if (prefixString != null && prefixString.length() > 0)
			{
				sbValue.append(prefixString);
			}
			if (name != null && name.length() > 0)
			{
				sbValue.append(name);
			}
			sbValue.append(propertyName);
			sbValue.append("=");

			try
			{
				if (!method.isAccessible())
				{
					method.setAccessible(true);
				}

				propertyValue = method.invoke(bean, null);

				if (propertyValue != null)
				{
					sbValue.append(propertyValue);
				}

			}
			catch (IllegalArgumentException e)
			{
				logger.error("Error invoking method " + method.getName() + ": " + e.getMessage());
			}
			catch (IllegalAccessException e)
			{
				logger.error("Error invoking method " + method.getName() + ": " + e.getMessage());
			}
			catch (InvocationTargetException e)
			{
				if (!(e.getCause() instanceof UnsupportedOperationException))
				{
					logger.error("Error invoking method " + method.getName() + ": " + e.getMessage());
				}
			}
			sbValue.append(NEW_LINE);
		}

		String values = sbValue.toString();
		if (values.length() > NEW_LINE.length())
		{
			values = sbValue.substring(0, sbValue.length() - NEW_LINE.length()).trim();
		}

		return values;
	}

	private static String printBeanStackTraceAsLine(StackTraceElement[] beanArray)
	{
		return printBeanStackTraceAsLine(beanArray, null, null);
	}

	private static String printBeanStackTraceAsLine(StackTraceElement[] beanArray, String prefixString)
	{
		return printBeanStackTraceAsLine(beanArray, prefixString, null);
	}

	private static String printBeanStackTraceAsLine(StackTraceElement[] beanArray, String prefixString,
			String name)
	{
		if (beanArray == null)
		{
			return null;
		}

		MessageFormat stackFmt = new MessageFormat("{0}.{3}({1}:{2})");
		List<?> getters = null;
		Object propertyValue = null;
		Method method = null;
		Object[] stackObj = new Object[stackFmt.getFormats().length];

		StringBuffer sbValue = new StringBuffer();

		for (int k = 0; k < beanArray.length; k++)
		{
			if (getters == null)
			{
				getters = findGetMethods(beanArray[k].getClass());
			}

			if (prefixString != null && prefixString.length() > 0)
			{
				sbValue.append(prefixString);
			}

			if (name != null && name.length() > 0)
			{
				sbValue.append(name);
			}

			for (int i = 0; i < getters.size(); i++)
			{
				method = (Method) getters.get(i);

				try
				{
					if (!method.isAccessible())
					{
						method.setAccessible(true);
					}
					propertyValue = method.invoke(beanArray[k], null);

					if (propertyValue != null)
					{
						stackObj[i] = propertyValue;
					}
				}
				catch (IllegalArgumentException e)
				{
					logger.error("Error invoking method " + method.getName() + ": " + e.getMessage());
				}
				catch (IllegalAccessException e)
				{
					logger.error("Error invoking method " + method.getName() + ": " + e.getMessage());
				}
				catch (InvocationTargetException e)
				{

					if (!(e.getCause() instanceof UnsupportedOperationException))
					{
						logger.error("Error invoking method " + method.getName() + ": " + e.getMessage());
					}
				}

			}

			sbValue.append(stackFmt.format(stackObj));
			sbValue.append(NEW_LINE);
		}

		String values = sbValue.toString();
		if (values.length() > NEW_LINE.length())
		{
			values = sbValue.substring(0, sbValue.length() - NEW_LINE.length());
		}
		return values;
	}

	private static List<Method> findGetMethods(Class klass)
	{
		List<Method> getMethodList = new ArrayList<Method>();
		Method[] allMethods = klass.getMethods();
		for (int i = 0; i < allMethods.length; i++)
		{
			Method method = allMethods[i];
			boolean isGetMethod = false;
			String methodName = method.getName();

			if (methodName.startsWith("get") && !methodName.equals("getClass"))
			{

				Type type = method.getGenericReturnType();

				isGetMethod = isPrimitiveType(type) && Modifier.isPublic(method.getModifiers())
						&& !Modifier.isStatic(method.getModifiers())
						&& (method.getParameterTypes().length == 0);
				
				if (logger.isDebugEnabled() && !isGetMethod)
				{
					logger.debug("The 'get' method '"
									+ klass
									+ "."
									+ method.getName()
									+ " will be ignored because it is not public or it has no return type or it requires some input parameters");
				}
			}

			if (isGetMethod)
			{
				getMethodList.add(method);
			}
		}
		return getMethodList;
	}

	private static boolean isPrimitiveType(Type type)
	{
		boolean isPrimitive = false;
		if (type.equals(Byte.TYPE))
		{
			isPrimitive = true;
		}
		else if (type.equals(Character.TYPE))
		{
			isPrimitive = true;
		}
		else if (type.equals(Character.TYPE))
		{
			isPrimitive = true;
		}
		else if (type.equals(Short.TYPE))
		{
			isPrimitive = true;
		}
		else if (type.equals(Integer.TYPE))
		{
			isPrimitive = true;
		}
		else if (type.equals(Long.TYPE))
		{
			isPrimitive = true;
		}
		else if (type.equals(Float.TYPE))
		{
			isPrimitive = true;
		}
		else if (type.equals(Double.TYPE))
		{
			isPrimitive = true;
		}
		else if (type.equals(Boolean.TYPE))
		{
			isPrimitive = true;
		}
		else if (type.equals(String.class))
		{
			isPrimitive = true;
		}
		return isPrimitive;

	}

	private String getMXBanner(String className)
	{
		StringBuffer banner = new StringBuffer(BANNER);
		banner.append(NEW_LINE);
		banner.append("--- " + className);
		banner.append(NEW_LINE);
		banner.append("--- http://java.sun.com/j2se/1.5.0/docs/api/java/lang/management/" + className
				+ ".html");
		banner.append(NEW_LINE);
		banner.append(NEW_LINE);
		return banner.toString();
	}

	private String getBanner(String title)
	{
		StringBuffer banner = new StringBuffer(BANNER);
		banner.append(NEW_LINE);
		banner.append("--- " + title);
		banner.append(NEW_LINE);
		banner.append(NEW_LINE);
		return banner.toString();
	}

	private void write(String message) throws IOException
	{
		if (message != null && message.length() > 0)
		{
			writer.write(message);
		}
	}

	private void writeln(String message) throws IOException
	{
		if (message != null && message.length() > 0)
		{
			writer.write(message);
			writer.write(NEW_LINE);
		}
	}
}



