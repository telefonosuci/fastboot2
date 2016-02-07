package it.bamboolab.fastboot.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContextProvider implements ApplicationContextAware {

    public static ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");

    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        context = ac;
    }

    public ApplicationContext getApplicationContext() {
        return context;
    }
}