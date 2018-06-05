package com.oracle.main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.oracle.config.Bean;
import com.oracle.config.Property;
import com.oracle.config.parse.ConfigManager;
import com.oracle.utils.BeanUtils;

public class ClassPathApplicationContext implements BeanFactory {

	private Map<String, Bean> config;
	// 使用map做spring的容器
	private Map<String, Object> context = new HashMap<String, Object>();

	public ClassPathApplicationContext(String path) {
		// TODO Auto-generated constructor stub
		// 1读取配置文件获得需要初始化的bean信息
		config = ConfigManager.getConfig(path);
		// 2遍历初始化bean
		if (config != null) {
			for (Entry<String, Bean> en : config.entrySet()) {
				// 获得配置中的bean信息
				String beanName = en.getKey();
				Bean bean = en.getValue();

				// createBean方法中也会向context中放入bean
				Object existBean = context.get(beanName);
				// 判断容器中是否已经存在可这个bean
				// 并且bean的scope属性为单例模式
				if (existBean == null && bean.getScope().equals("singleton")) {
					// 根据bean配置创建bean对象
					Object beanObj = createBean(bean);
					// 3将初始化好的bean放入容器中
					context.put(beanName, beanObj);
				}
			}
		}

	}

	private Object createBean(Bean bean) {
		// TODO Auto-generated method stub
		// 1获得要创建的bean的class
		String className = bean.getClassName();
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("请检查您bean的class配置是否正确:" + className);
		}
		// 获得class后，将class对应的对象创建出来
		Object beanObj = null;
		try {
			beanObj = clazz.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("您的bean没有空参构造！" + className);
		}
		// 2需要获得bean的属性，将其注入
		if (bean.getProperties() != null) {
			for (Property prop : bean.getProperties()) {
				// 注入分两种情况
				String name = prop.getName();
				// 使用属性方式2：使用beanutils工具类完成属性注入
				String value = prop.getValue();
				if (value != null) {
					Map<String, String[]> paramMap = new HashMap<>();
					paramMap.put(name, new String[] { value });
					try {
						org.apache.commons.beanutils.BeanUtils.populate(
								beanObj, paramMap);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new RuntimeException("属性值转换异常");
					}

				}
				if (prop.getRef() != null) {
					Method setMethod = BeanUtils.getWriteMethod(beanObj, name);
					Object existBean = context.get(prop.getRef());
					if (existBean == null) {
						existBean = createBean(config.get(prop.getRef()));
						// 将创建好的bean放入容器中
						if (config.get(prop.getRef()).getScope()
								.equals("singleton")) {
							context.put(prop.getRef(), existBean);
						}
					}
					try {
						setMethod.invoke(beanObj, existBean);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new RuntimeException("您的bean属性" + name
								+ "没有对应的方法，或者方法不正确" + className);
					}
				}
				/*
				 * Method setMethod = BeanUtils.getWriteMethod(beanObj, name);
				 * // 创建一个需要注入到bean中的属性 Object param = null; if (prop.getValue()
				 * != null) { // 1value属性注入 String value = prop.getValue(); //
				 * 根据属性名称获得注入属性的set方法 // 调用set方法注入即可 param = value; } //
				 * 2其它bean的注入 if (prop.getRef() != null) {
				 * 
				 * Object existBean = context.get(prop.getRef());
				 * 
				 * if (existBean == null) { existBean =
				 * createBean(config.get(prop.getRef())); // 将创建好的bean放入容器中 if
				 * (config.get(prop.getRef()).getScope() .equals("singleton")) {
				 * context.put(prop.getRef(), existBean); } } param = existBean;
				 * } try { setMethod.invoke(beanObj, param); } catch (Exception
				 * e) { // TODO Auto-generated catch block e.printStackTrace();
				 * throw new RuntimeException("您的bean属性" + name +
				 * "没有对应的方法，或者方法不正确" + className); }
				 */
			}
		}

		return beanObj;
	}

	// 希望在ClasssPathApplicationContext一创建就初始化spring容器
	@Override
	public Object getBean(String beanName) {
		// TODO Auto-generated method stub
		Object bean = context.get(beanName);
		// 如果bean的scope配置为prototype。那么context中就不会包含该bean对象
		if (bean == null) {
			// 创建bean对象
			bean = createBean(config.get(beanName));
		}
		return bean;
	}

}
