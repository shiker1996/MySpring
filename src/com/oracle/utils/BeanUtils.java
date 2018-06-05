package com.oracle.utils;

import java.lang.reflect.Method;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class BeanUtils {

	public static Method getWriteMethod(Object beanObj, String name) {
		// TODO Auto-generated method stub
		Method method = null;
		//使用内省技术来实现该方法
		//1分析bean对象beaninfo
		
		try {
			BeanInfo info = Introspector.getBeanInfo(beanObj.getClass());
			//2根据beaninfo获得所有属性的描述器
			PropertyDescriptor[] pds = info.getPropertyDescriptors();
			//3遍历这些属性描述器
			if(pds!=null){
				for(PropertyDescriptor pd : pds){
					//判断当前遍历的描述器属性是否是要找的属性
					//获得当前描述器描述的属性名称
					String pname=pd.getName();
					//使用要找的属性名称与当前描述的属性名称比对
					if(pname.equals(name)){
						//获得属性的set方法
						method = pd.getWriteMethod();
					}
				}
			}
			//4返回找到的set方法
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//如果没有找到，则抛出异常
		if(method==null){
			throw new RuntimeException("请检查属性"+name+"的set方法是否创建");
		}
		return method;
	}

}
