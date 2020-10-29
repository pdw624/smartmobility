package kr.tracom.smps.common;

import org.springframework.context.ApplicationContext;

public class BeanUtil {
	public static Object getBean(Class<?> classType) {
		ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
		return applicationContext.getBean(classType);
	}
}
