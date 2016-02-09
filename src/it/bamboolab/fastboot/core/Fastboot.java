package it.bamboolab.fastboot.core;

import it.bamboolab.fastboot.context.ApplicationContextProvider;
import it.bamboolab.fastboot.context.ApplicationState;

import it.bamboolab.fastboot.threads.DirectoryMonitor;
import org.apache.log4j.Logger;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class Fastboot {

	private static Logger logger = Logger.getLogger(Fastboot.class);
	
	
	public static void main(String[] args) {

		ThreadPoolTaskExecutor executor = ApplicationContextProvider.context.getBean("taskExecutor", ThreadPoolTaskExecutor.class);

		DirectoryMonitor monitor = new DirectoryMonitor();

		executor.execute(monitor);


	}
	
	public static void stop(){
		
		ApplicationState.on=false;
		
	}


}
