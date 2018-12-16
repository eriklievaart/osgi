package com.eriklievaart.osgi.toolkit.api;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;

import com.eriklievaart.osgi.toolkit.api.listener.SimpleBundleListener;
import com.eriklievaart.osgi.toolkit.api.listener.SimpleServiceListener;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public abstract class ActivatorWrapper implements BundleActivator {
	private LogTemplate log = new LogTemplate(getClass());

	private BundleContext context;

	private List<Whiteboard<?>> whiteboards = NewCollection.concurrentList();
	private List<BundleListener> bundleListeners = NewCollection.concurrentList();
	private List<ServiceListener> serviceListeners = NewCollection.concurrentList();
	private List<ServiceRegistration<?>> services = NewCollection.concurrentList();

	@Override
	public final void start(BundleContext bc) throws Exception {
		this.context = bc;
		log.info("starting bundle %", bc.getBundle().getSymbolicName());
		init(getBundleContext());
	}

	@Override
	public final void stop(BundleContext bc) throws Exception {
		for (ServiceRegistration<?> registration : services) {
			registration.unregister();
		}
		for (Whiteboard<?> whiteboard : whiteboards) {
			whiteboard.shutdown();
		}
		for (ServiceListener listener : serviceListeners) {
			log.debug("removing listener $", listener);
			context.removeServiceListener(listener);
		}
		for (BundleListener listener : bundleListeners) {
			log.debug("removing listener $", listener);
			context.removeBundleListener(listener);
		}
		shutdown();
	}

	public Dictionary<String, ?> dictionary(String key, Object value) {
		Hashtable<String, Object> map = new Hashtable<>();
		map.put(key, value);
		return map;
	}

	public <E> ServiceRegistration<E> addServiceWithCleanup(Class<E> type, E service) {
		return addServiceWithCleanup(type, service, new Hashtable<>());
	}

	public <E> ServiceRegistration<E> addServiceWithCleanup(Class<E> type, E service, Dictionary<String, ?> d) {
		ServiceRegistration<E> registration = context.registerService(type, service, d);
		services.add(registration);
		return registration;
	}

	public <E> Whiteboard<E> addWhiteboardWithCleanup(Class<E> type) {
		Whiteboard<E> whiteboard = new Whiteboard<>(getBundleContext(), type);
		whiteboards.add(whiteboard);
		return whiteboard;
	}

	public <E> Whiteboard<E> addWhiteboardWithCleanup(Class<E> type, SimpleServiceListener<E> listener) {
		Whiteboard<E> whiteboard = addWhiteboardWithCleanup(type);
		whiteboard.addListener(listener);
		return whiteboard;
	}

	public void addServiceListenerWithCleanup(SimpleServiceListener<Object> listener) {
		serviceListeners.add(getContextWrapper().addServiceListener(listener));
	}

	public <E> void addServiceListenerWithCleanup(Class<E> type, SimpleServiceListener<E> listener) {
		serviceListeners.add(getContextWrapper().addServiceListener(type, listener));
	}

	public void addBundleListenerWithCleanup(SimpleBundleListener listener) {
		bundleListeners.add(getContextWrapper().addBundleListener(listener));
	}

	public BundleContext getBundleContext() {
		return context;
	}

	public ContextWrapper getContextWrapper() {
		return new ContextWrapper(context);
	}

	@SuppressWarnings("hiding")
	protected abstract void init(BundleContext context) throws Exception;

	protected void shutdown() throws Exception {
	}
}
