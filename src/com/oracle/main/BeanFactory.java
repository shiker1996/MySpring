package com.oracle.main;

public interface BeanFactory {

	//根据bean的那么获得bean对象的方法
	Object getBean(String beanName);
}
