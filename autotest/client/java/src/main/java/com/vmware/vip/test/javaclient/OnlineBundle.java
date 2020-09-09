package com.vmware.vip.test.javaclient;

public class OnlineBundle extends Bundle{
	private static OnlineBundle bundle;

	private OnlineBundle() {
	}
	public static synchronized OnlineBundle getInstance() {
        if (bundle == null) {
        	bundle = new OnlineBundle();
        }
        return bundle;
    }
}
