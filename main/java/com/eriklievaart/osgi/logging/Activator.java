package com.eriklievaart.osgi.logging;

import java.util.logging.LogRecord;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.toolkit.logging.api.LogConfig;
import com.eriklievaart.toolkit.logging.api.appender.AbstractAppender;
import com.eriklievaart.toolkit.logging.api.appender.Appender;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		ServiceCollection<Appender> sc = getServiceCollection(Appender.class);
		LogConfig.setDefaultAppenders(new AbstractAppender() {
			@Override
			public void append(LogRecord record) {
				sc.allCall(appender -> {
					try {
						appender.append(record);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		});
	}
}
