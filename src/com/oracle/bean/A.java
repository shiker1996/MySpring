package com.oracle.bean;

public class A {

	private int name;

	public int getName() {
		return name;
	}

	public void setName(int name) {
		this.name = name;
	}

	public A() {
		System.out.println("A被创建了");
	}
	
}
