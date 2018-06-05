package com.oracle.test;

import java.util.Map;

import org.omg.CORBA.portable.ApplicationException;

import com.oracle.bean.A;
import com.oracle.bean.B;
import com.oracle.bean.C;
import com.oracle.config.Bean;
import com.oracle.config.parse.ConfigManager;
import com.oracle.main.BeanFactory;
import com.oracle.main.ClassPathApplicationContext;

public class Test {

	@org.junit.Test
	public void fun1(){
		Map<String,Bean> config = ConfigManager.getConfig("/applicationContext.xml");
		System.out.println(config);
	}
	
	@org.junit.Test
	public void fun2(){
		BeanFactory bf = new ClassPathApplicationContext("/applicationContext.xml");
		A a  = (A) bf.getBean("A");
		System.out.println(a.getName());
		B b =(B) bf.getBean("B");
		System.out.println(b.getA().getName());
		C c =(C) bf.getBean("C");
		System.out.println(c.getB().getA().getName());
	}
}
