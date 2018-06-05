package com.oracle.config.parse;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.oracle.config.Bean;
import com.oracle.config.Property;

public class ConfigManager {

	// 读取xml==》并返回结果
	public static Map<String, Bean> getConfig(String path) {
		// 用于返回的map对象
		Map<String, Bean> map = new HashMap<String, Bean>();
		// dom4j实现
		// 1创建解析器
		SAXReader reader = new SAXReader();
		// 2加载配置文件==document对象
		InputStream is = ConfigManager.class.getResourceAsStream(path);
		Document doc = null;
		try {
			doc = reader.read(is);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("请检查xml配置是否正确！");
		}
		// 3定义xpath表达式，取出所有bean元素
		String xpath = "//bean";
		// 4对bean元素进行遍历
		List<Element> list = doc.selectNodes(xpath);
		if (list != null) {
			for (Element element : list) {
				// 将bean元素的name、class属性封装到bean对象中
				Bean bean = new Bean();
				String name = element.attributeValue("name");
				String className = element.attributeValue("class");
				String scope = element.attributeValue("scope");
				bean.setName(name);
				bean.setClassName(className);
				if (scope != null) {
					bean.setScope(scope);
				}
				// 获取bean元素中的所有property子元素，将属性封装到property中
				List<Element> children = element.elements("property");
				if (children != null) {
					for (Element child : children) {
						Property prop = new Property();
						String pName = child.attributeValue("name");
						String pValue = child.attributeValue("value");
						String pRef = child.attributeValue("ref");
						// 将property对象封装到bean对象
						prop.setName(pName);
						prop.setValue(pValue);
						prop.setRef(pRef);
						bean.getProperties().add(prop);
					}
				}
				// 将bean对象封装到map中
				map.put(name, bean);
			}

		}
		// 5返回map
		return map;
	}
}
