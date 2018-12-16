package com.eriklievaart.osgi.toolkit.api.listener;

import org.osgi.framework.Bundle;

@SuppressWarnings("unused")
public interface SimpleBundleListener {

	void installed(Bundle bundle);

	void started(Bundle bundle);

	void stopped(Bundle bundle);

	default void starting(Bundle bundle) {
	}

	default void stopping(Bundle bundle) {
	}

	default void resolved(Bundle bundle) {
	}

	default void unresolved(Bundle bundle) {
	}

	default void uninstalled(Bundle bundle) {
	}

	default void updated(Bundle bundle) {
	}

	default void lazyActivation(Bundle bundle) {
	}
}
