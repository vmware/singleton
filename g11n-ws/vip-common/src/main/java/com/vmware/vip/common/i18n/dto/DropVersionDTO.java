/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DropVersionDTO implements Serializable {

	private static final long serialVersionUID = -4987924773801493875L;

	public String getDropId() {
		return dropId;
	}

	public void setDropId(String dropId) {
		this.dropId = dropId;
	}

	public List<ComponentVersionDTO> getComponentList() {
		return componentList;
	}

	public void setComponentList(List<ComponentVersionDTO> componentList) {
		this.componentList = componentList;
	}
	
	public ComponentVersionDTO createComponentVersionDTO() {
		return new ComponentVersionDTO();
	}

	private String dropId = "";
	private List<ComponentVersionDTO> componentList = new ArrayList<ComponentVersionDTO>();

	public class ComponentVersionDTO implements Serializable{
		

		private static final long serialVersionUID = -7450709802882333626L;

		public String getComponentName() {
			return componentName;
		}

		public void setComponentName(String componentName) {
			this.componentName = componentName;
		}

		public List<VersionDTO> getVersionList() {
			return versionList;
		}

		public void setVersionList(List<VersionDTO> versionList) {
			this.versionList = versionList;
		}
        
		public VersionDTO createVersionDTO() {
			return new VersionDTO();
		}
		
		private String componentName = "";
		private List<VersionDTO> versionList = new ArrayList<VersionDTO>();

		public class VersionDTO  implements Serializable{
		
			private static final long serialVersionUID = 5228872934941503453L;
			
			
			public String getLocale() {
				return locale;
			}

			public void setLocale(String locale) {
				this.locale = locale;
			}

			public String getVersion() {
				return version;
			}

			public void setVersion(String version) {
				this.version = version;
			}

			private String locale = "";
			private String version = "";
		}
	}
}
