package com.vmware.singleton.api.test.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListDiffer {
	private List<Object> source;
	private List<Object> redundantList;
	private List<Object> missingList;
	public ListDiffer(List<Object> source) {
		this.source = source;
		this.redundantList = new ArrayList<Object>();
		this.missingList = new ArrayList<Object>();
	}
	public void compare(List<Object> target) {
		List<Object> remainder  = new ArrayList<Object>();
		remainder.addAll(source);
		Iterator<Object> targetIterator = target.iterator();

		while (targetIterator.hasNext()) {
			String currComponent = targetIterator.next().toString();
			boolean found = false;
			for (int i = 0; i < remainder.size(); i++) {
				if (currComponent.equals(remainder.get(i))) {
					found = true;
					remainder.remove(i);
					break;
				}
			}
			if (!found) {
				missingList.add(currComponent);
			}
		}
		setRedundantList(remainder);
	}
	public List<Object> getRedundantList() {
		return redundantList;
	}
	public List<Object> getMissingList() {
		return missingList;
	}
	private void setRedundantList(List<Object> redundantList) {
		this.redundantList = redundantList;
	}

	public boolean hasNoMissing() {
		return this.missingList.isEmpty();
	}

	public boolean hasNoRedundant() {
		return this.redundantList.isEmpty();
	}
}
