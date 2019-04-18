package com.eriklievaart.osgi.toolkit.api;

import java.util.List;
import java.util.function.Consumer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import com.eriklievaart.osgi.toolkit.api.listener.SimpleServiceListener;
import com.eriklievaart.osgi.toolkit.impl.OsgiFilter;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

/**
 * Convenience class for creating an OSGI whiteboard.
 *
 * @param <E>
 *            type of services to support.
 */
public class Whiteboard<E> implements ServiceListener {
	private LogTemplate log = new LogTemplate(getClass());

	protected final BundleContext context;
	private final String filter;
	private Class<E> type;

	private List<SimpleServiceListener<E>> listeners = NewCollection.concurrentList();

	public Whiteboard(BundleContext context, Class<E> type) {
		this.type = type;
		this.context = context;
		this.filter = OsgiFilter.byType(type);
		init();
	}

	private void init() {
		try {
			context.addServiceListener(this, filter);
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Register a handler for the whiteboard.
	 *
	 * @param listener
	 *            this listener will receive updates when a service registers or unregisters. On registration it is also
	 *            called once for every implementation already available.
	 */
	public void addListener(final SimpleServiceListener<E> listener) {
		listeners.add(listener);
		try {
			new ContextWrapper(context).getServiceCollection(type).allCall(s -> {
				log.trace("$ -> $", listener.getClass().getSimpleName(), s);
				listener.register(s);
			});
		} catch (Exception e) {
			listeners.remove(listener);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Convenience method for events that only need to be handled on registering.
	 */
	public void onRegister(Consumer<E> consumer) {
		addListener(new SimpleServiceListener<E>() {
			@Override
			public void register(E service) {
				consumer.accept(service);
			}

			@Override
			public void unregistering(E service) {
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public void serviceChanged(ServiceEvent event) {
		E arg = (E) context.getService(event.getServiceReference());

		for (SimpleServiceListener<E> listener : listeners) {
			if (event.getType() == ServiceEvent.REGISTERED) {
				listener.register(arg);
			}
			if (event.getType() == ServiceEvent.UNREGISTERING) {
				listener.unregistering(arg);
			}
			if (event.getType() == ServiceEvent.MODIFIED) {
				listener.modified(arg);
			}
			if (event.getType() == ServiceEvent.MODIFIED_ENDMATCH) {
				listener.modifiedEndMatch(arg);
			}
		}
	}

	/**
	 * Properly clean up the whiteboard.
	 */
	public void shutdown() {
		log.debug("removing listener for type $", type);
		context.removeServiceListener(this);
	}
}