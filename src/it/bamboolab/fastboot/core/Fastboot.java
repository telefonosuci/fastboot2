package it.bamboolab.fastboot.core;

import it.bamboolab.fastboot.context.ApplicationState;

import org.apache.log4j.Logger;

public class Fastboot {

	private static Logger logger = Logger.getLogger(Fastboot.class);
	
	
	public static void main(String[] args) {
			
		while(ApplicationState.on){
			
			logger.debug("Look, I'm running!");
			
			try {
		
				Thread.sleep(1000);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void stop(){
		
		ApplicationState.on=false;
		
	}
}
