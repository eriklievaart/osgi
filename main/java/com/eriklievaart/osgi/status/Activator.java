package com.eriklievaart.osgi.status;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private StatusListener listener;

	@Override
	public void start(BundleContext context) throws Exception {
		listener = new StatusListener(context);
		context.addBundleListener(listener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		context.removeBundleListener(listener);
	}
}
