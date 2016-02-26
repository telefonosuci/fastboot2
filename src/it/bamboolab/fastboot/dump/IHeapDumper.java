package it.bamboolab.fastboot.dump;

public interface IHeapDumper {
	public void dumpHeap(String heapOutputFile, boolean live);
}
