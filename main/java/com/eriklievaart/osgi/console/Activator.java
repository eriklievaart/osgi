package com.eriklievaart.osgi.console;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.toolkit.logging.api.appender.Appender;
import com.eriklievaart.toolkit.logging.api.appender.ConsoleAppender;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		addServiceWithCleanup(Appender.class, new ConsoleAppender());
	}
}
