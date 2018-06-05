package com.oracle.bean;

public class B {

	private A a;

	public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}
	
	public B() {
		System.out.println("b被创建了");
	}
}
