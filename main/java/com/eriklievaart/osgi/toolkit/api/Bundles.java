package com.eriklievaart.osgi.toolkit.api;

import org.osgi.framework.Bundle;

import com.eriklievaart.toolkit.lang.api.FormattedException;

public class Bundles {

	public static BundleStatus getState(Bundle bundle) {
		switch (bundle.getState()) {

		case Bundle.ACTIVE:
			return BundleStatus.ACTIVE;
		case Bundle.INSTALLED:
			return BundleStatus.INSTALLED;
		case Bundle.RESOLVED:
			return BundleStatus.RESOLVED;
		case Bundle.STARTING:
			return BundleStatus.STARTING;
		case Bundle.STOPPING:
			return BundleStatus.STOPPING;
		case Bundle.UNINSTALLED:
			return BundleStatus.UNINSTALLED;

		default:
			throw new FormattedException("Unknown state %", bundle.getState());
		}
	}
}
