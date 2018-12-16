package com.eriklievaart.osgi.toolkit.impl;

import java.util.Map;
import java.util.function.BiConsumer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.eriklievaart.osgi.toolkit.api.listener.SimpleBundleListener;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class BundleListenerWrapper implements BundleListener {
	private LogTemplate log = new LogTemplate(getClass());

	private static Map<Integer, BiConsumer<SimpleBundleListener, Bundle>> lambdas = NewCollection.hashMap();

	private SimpleBundleListener delegate;

	static {
		lambdas.put(BundleEvent.INSTALLED, (listener, bundle) -> listener.installed(bundle));
		lambdas.put(BundleEvent.RESOLVED, (listener, bundle) -> listener.resolved(bundle));
		lambdas.put(BundleEvent.STARTED, (listener, bundle) -> listener.started(bundle));
		lambdas.put(BundleEvent.STARTING, (listener, bundle) -> listener.starting(bundle));
		lambdas.put(BundleEvent.STOPPED, (listener, bundle) -> listener.stopped(bundle));
		lambdas.put(BundleEvent.STOPPING, (listener, bundle) -> listener.stopping(bundle));
		lambdas.put(BundleEvent.UNRESOLVED, (listener, bundle) -> listener.unresolved(bundle));
		lambdas.put(BundleEvent.UNINSTALLED, (listener, bundle) -> listener.uninstalled(bundle));
		lambdas.put(BundleEvent.UPDATED, (listener, bundle) -> listener.updated(bundle));
		lambdas.put(BundleEvent.LAZY_ACTIVATION, (listener, bundle) -> listener.lazyActivation(bundle));
	}

	public BundleListenerWrapper(SimpleBundleListener delegate) {
		Check.notNull(delegate);
		this.delegate = delegate;
	}

	@Override
	public void bundleChanged(BundleEvent event) {
		int type = event.getType();

		if (lambdas.containsKey(type)) {
			lambdas.get(type).accept(delegate, event.getBundle());
		} else {
			log.warn("Unknown BundleEvent %", type);
		}
	}
}