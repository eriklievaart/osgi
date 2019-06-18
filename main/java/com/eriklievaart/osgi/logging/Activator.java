package com.eriklievaart.osgi.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.ContextWrapper;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogConfig;
import com.eriklievaart.toolkit.logging.api.appender.Appender;
import com.eriklievaart.toolkit.logging.api.appender.ConsoleAppender;
import com.eriklievaart.toolkit.logging.api.appender.SimpleFileAppender;

public class Activator extends ActivatorWrapper {

	private static final String ACTIVE = "true";
	private static final String SERVICE_PROPERTY = "com.eriklievaart.osgi.logging.service";
	private static final String CONSOLE_PROPERTY = "com.eriklievaart.osgi.logging.console";
	private static final String FILE_PROPERTY = "com.eriklievaart.osgi.logging.file";

	@Override
	protected void init(BundleContext context) throws Exception {
		List<Appender> appenders = NewCollection.list();

		addServiceAppender(getContextWrapper(), appenders);
		addConsoleAppender(getContextWrapper(), appenders);
		addFileAppender(getContextWrapper(), appenders);

		LogConfig.setDefaultAppenders(appenders);

		for (Appender appender : LogConfig.getDefaultAppenders()) {
			System.out.println(Str.sub("configured appender: $", appender));
		}
	}

	private void addServiceAppender(ContextWrapper wrapper, List<Appender> appenders) {
		String property = wrapper.getPropertyString(SERVICE_PROPERTY, ACTIVE);
		if (Str.isEqual(property, ACTIVE)) {
			appenders.add(new ServiceAppender(getServiceCollection(Appender.class)));
		}
	}

	private void addConsoleAppender(ContextWrapper wrapper, List<Appender> appenders) {
		String property = wrapper.getPropertyString(CONSOLE_PROPERTY, ACTIVE);
		if (Str.isEqual(property, ACTIVE)) {
			appenders.add(new ConsoleAppender());
		}
	}

	private void addFileAppender(ContextWrapper wrapper, List<Appender> appenders) {
		wrapper.getPropertyStringOptional(FILE_PROPERTY, path -> {
			try {
				appenders.add(new SimpleFileAppender(new File(path)));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
