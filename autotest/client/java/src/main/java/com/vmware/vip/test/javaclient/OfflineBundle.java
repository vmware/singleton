package com.vmware.vip.test.javaclient;

public class OfflineBundle extends Bundle{
	private static OfflineBundle bundle;

	private OfflineBundle() {
	}
	public static synchronized OfflineBundle getInstance() {
        if (bundle == null) {
        	bundle = new OfflineBundle();
        }
        return bundle;
    }
}
