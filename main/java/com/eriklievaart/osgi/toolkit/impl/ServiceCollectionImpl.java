package com.eriklievaart.osgi.toolkit.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.toolkit.lang.api.check.Check;

/**
 * Utility class for finding and invoking services.
 *
 * @param <E>
 *            type under which the services are registered.
 */
public class ServiceCollectionImpl<E> implements ServiceCollection<E> {

	private BundleContext context;
	private Class<E> type;
	private String filter;

	public ServiceCollectionImpl(BundleContext context, Class<E> type) {
		Check.noneNull(context, type);
		this.context = context;
		this.type = type;
		this.filter = String.format("(objectclass=%s)", type.getName());
	}

	private List<E> list() {
		List<E> result = new ArrayList<>();
		try {
			for (ServiceReference<E> reference : context.getServiceReferences(type, filter)) {
				result.add(context.getService(reference));
			}

		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public <T> T anyReturns(Function<E, T> consumer) {
		Iterator<E> iterator = list().iterator();
		while (iterator.hasNext()) {
			E e = iterator.next();
			if (e != null) {
				return consumer.apply(e);
			}
		}
		return null;
	}

	@Override
	public <T> T oneReturns(Function<E, T> function) {
		List<E> list = list();
		int matches = list.size();
		Check.isFalse(matches < 1, "Cannot find service for type $", type);
		Check.isFalse(matches > 1, "Multiple services for type $", type);
		E service = list.get(0);
		Check.notNull(service, "Service found, but was not available <null>");
		return function.apply(service);
	}

	@Override
	public void oneCall(Consumer<E> consumer) {
		List<E> list = list();
		int matches = list.size();
		Check.isFalse(matches < 1, "Cannot find service for type $", type);
		Check.isFalse(matches > 1, "Multiple services for type $", type);
		E service = list.get(0);
		Check.notNull(service, "Service found, but was not available <null>");
		consumer.accept(service);
	}

	@Override
	public void allCall(Consumer<E> consumer) {
		Iterator<E> iterator = list().iterator();
		while (iterator.hasNext()) {
			E e = iterator.next();
			if (e != null) {
				consumer.accept(e);
			}
		}
	}
}
