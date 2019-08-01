package com.vmware.singleton.api;

import java.util.HashMap;
import java.util.List;

public class Api {
	private String name;
	private HashMap<String, ApiParameter> paramMap;
	private String groupName;
	private String path;
	private String method;
	private String desciption;
	public Api(String groupName, String name, String path, String desciption,
			String method, List<ApiParameter> params) {
		this.name = name;
		this.method = method;
		setParamMap(params);
		this.groupName = groupName;
		this.path = path;
		this.desciption = desciption;
	}
	public String getName() {
		return name;
	}
	public String getGroupName() {
		return groupName;
	}
	public String getMethod() {
		return method.toUpperCase();
	}
	public String getPath() {
		return path;
	}
	public String getDesciption() {
		return desciption;
	}
	public HashMap<String, ApiParameter> getParamMap() {
		return paramMap;
	}
	private void setParamMap(List<ApiParameter> paramMap) {
		this.paramMap = new HashMap<String, ApiParameter>();
		for (ApiParameter apiParameter: paramMap) {
			this.paramMap.put(apiParameter.getName(), apiParameter);
		}
	}
}
