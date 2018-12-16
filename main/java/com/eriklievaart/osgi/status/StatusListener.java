package com.eriklievaart.osgi.status;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

public class StatusListener implements BundleListener {

	private BundleContext context;

	public StatusListener(BundleContext context) {
		this.context = context;
	}

	@Override
	public void bundleChanged(BundleEvent event) {
		int t = event.getType();

		if (t == BundleEvent.INSTALLED) {
			printMessage("installed " + event.getBundle().getSymbolicName());
		}
		if (t == BundleEvent.STARTED) {
			printMessage("started " + event.getBundle().getSymbolicName());
		}
		if (t == BundleEvent.STOPPED) {
			printMessage("stopped " + event.getBundle().getSymbolicName());
		}
	}

	private void printMessage(String message) {
		System.out.println(String.format("%s (%s/%s)", message, getActiveCount(), context.getBundles().length));
		printOfflineBundles();
	}

	private void printOfflineBundles() {
		for (Bundle bundle : context.getBundles()) {
			if (bundle.getState() != Bundle.ACTIVE) {
				System.out.println("* offline " + bundle.getSymbolicName());
			}
		}
	}

	private int getActiveCount() {
		int active = 0;
		for (Bundle bundle : context.getBundles()) {
			if (bundle.getState() == Bundle.ACTIVE) {
				active++;
			}
		}
		return active;
	}
}
