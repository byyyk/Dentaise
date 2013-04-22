package settings;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {

    private static ApplicationContext context;

    @Override
    public void onStart(Application app) {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    public static <T> T getBean(Class<T> beanClass) {
    	if (context == null) {
    		throw new IllegalStateException("Spring application context not initialized!");
    	} else {
    		return context.getBean(beanClass);
    	}
    }
    
    public static Object getBean(String name) {
    	if (context == null) {
    		throw new IllegalStateException("Spring application context not initialized!");
    	} else {
    		return context.getBean(name);
    	}
    }
}
