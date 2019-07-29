package com.vmware.singleton.api;

public class Product {
	private String name;
	private String version;
	public Product(String name, String ver) {
		setName(name);
		setVersion(ver);
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

}
