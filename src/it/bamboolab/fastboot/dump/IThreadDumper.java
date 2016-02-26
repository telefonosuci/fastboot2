package it.bamboolab.fastboot.dump;
import java.io.IOException;
import java.io.Writer;

public interface IThreadDumper
{
	public void dumpObjectBean(Object bean, String prefixString) throws IOException;

	public void dumpThreadMXBean() throws IOException;

	public void dumpRuntimeMXBean() throws IOException;

	public void dumpOperatingSystemMXBean() throws IOException;

	public void dumpMemoryPoolMXBean() throws IOException;

	public void dumpMemoryMXBean() throws IOException;

	public void dumpMemoryManagerMXBean() throws IOException;

	public void dumpGarbageCollectorMXBean() throws IOException;

	public void dumpCompilationMXBean() throws IOException;

	public void dumpClassLoadingMXBean() throws IOException;

	public void dumpAllMXBean() throws IOException;

	public void insertBanner(String title) throws IOException;

	public void insertNewLine() throws IOException;

	public void insertString(String str) throws IOException;

	public Writer getWriter();

	public void resetWriter(Writer writer);

	public void closeWriter();
}

