package com.vmware.singleton.api;

public class ApiParameter {
	private String name;
	private boolean required;
	private String desc;
	private String paramType;
	private String dataType;
	public ApiParameter(String name, boolean required, String desc, String paramType, String dataType) {
		setName(name);
		setRequired(required);
		setDesc(desc);
		setType(paramType);
		setDataType(dataType);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getRequired() {
		return required;
	}
	public void setRequired(Boolean required) {
		this.required = required;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getType() {
		return paramType;
	}
	public void setType(String type) {
		this.paramType = type;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
