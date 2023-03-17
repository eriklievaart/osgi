package com.eriklievaart.osgi.toolkit.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import com.eriklievaart.osgi.toolkit.api.listener.SimpleServiceListener;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class ServiceListenerWrapper<E> implements ServiceListener {

	private BundleContext context;
	private SimpleServiceListener<E> delegate;

	public ServiceListenerWrapper(BundleContext context, SimpleServiceListener<E> delegate) {
		Check.noneNull(context, delegate);
		this.context = context;
		this.delegate = delegate;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void serviceChanged(ServiceEvent event) {
		E service = (E) context.getService(event.getServiceReference());

		if (event.getType() == ServiceEvent.REGISTERED) {
			delegate.register(service);
		}
		if (event.getType() == ServiceEvent.UNREGISTERING) {
			delegate.unregistering(service);
		}
		if (event.getType() == ServiceEvent.MODIFIED) {
			delegate.modified(service);
		}
		if (event.getType() == ServiceEvent.MODIFIED_ENDMATCH) {
			delegate.modifiedEndMatch(service);
		}
	}
}
