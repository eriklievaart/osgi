package com.eriklievaart.osgi.toolkit.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.lang.api.check.Check;

/**
 * Utility class for finding and invoking services.
 *
 * @param <E>
 *            type under which the services are registered.
 */
public class ServiceCollection<E> {

	private BundleContext context;
	private Class<E> type;
	private String filter;

	public ServiceCollection(BundleContext context, Class<E> type) {
		Check.notNull(context, type);
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

	/**
	 * Invoke the function on the first implementation of the contract found, if there is one.
	 *
	 * @param consumer
	 *            lambda with the code to invoke on the service.
	 * @return the result of a call on the service or null iff the service could not be found.
	 */
	public <T> T withAny(Function<E, T> consumer) {
		Iterator<E> iterator = list().iterator();
		while (iterator.hasNext()) {
			E e = iterator.next();
			if (e != null) {
				return consumer.apply(e);
			}
		}
		return null;
	}

	/**
	 * Invoke the function on all implementations of the contract found.
	 *
	 * @param consumer
	 *            lambda with the code to invoke on the services.
	 */
	public void withAll(Consumer<E> consumer) {
		Iterator<E> iterator = list().iterator();
		while (iterator.hasNext()) {
			E e = iterator.next();
			if (e != null) {
				consumer.accept(e);
			}
		}
	}

	/**
	 * Expects exactly one implementation of the service to be present and invokes it with the lambda.
	 *
	 * @param consumer
	 *            lambda with the code to invoke on the service.
	 * @return the result of a call on the service or null iff the service could not be found.
	 * @throws AssertionException
	 *             iff there is not exactly one implementation.
	 */
	public <T> T withOne(Function<E, T> consumer) {
		List<E> list = list();
		int matches = list.size();
		Check.isFalse(matches < 1, "Cannot find service for type $", type);
		Check.isFalse(matches > 1, "Multiple services for type $", type);
		E service = list.get(0);
		Check.notNull(service, "Service found, but was not available <null>");
		return consumer.apply(service);
	}
}
