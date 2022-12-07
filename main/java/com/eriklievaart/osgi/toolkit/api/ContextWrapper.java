package com.eriklievaart.osgi.toolkit.api;

import java.io.File;
import java.util.function.Consumer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;

import com.eriklievaart.osgi.toolkit.api.listener.SimpleBundleListener;
import com.eriklievaart.osgi.toolkit.api.listener.SimpleServiceListener;
import com.eriklievaart.osgi.toolkit.impl.BundleListenerWrapper;
import com.eriklievaart.osgi.toolkit.impl.OsgiFilter;
import com.eriklievaart.osgi.toolkit.impl.ServiceCollectionImpl;
import com.eriklievaart.osgi.toolkit.impl.ServiceListenerWrapper;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;

/**
 * Convenience class for interacting with the {@link BundleContext} without the plumbing.
 */
public class ContextWrapper {

	private final BundleContext context;

	public ContextWrapper(BundleContext context) {
		this.context = context;
	}

	/**
	 * Find services by their registered type.
	 *
	 * @param type
	 *            Type the services are registered under.
	 * @return all matching services.
	 */
	public <E> ServiceCollection<E> getServiceCollection(Class<E> type) {
		return new ServiceCollectionImpl<>(context, type);
	}

	/**
	 * Get the directory containing the bundle of this BundleContext.
	 */
	public File getBundleDir() {
		String location = context.getBundle().getLocation().replaceFirst("^[^:]++:", "");
		return new File(location).getParentFile();
	}

	/**
	 * Get the parent of the directory containing the bundle of this BundleContext.
	 */
	public File getBundleParentDir() {
		return getBundleDir().getParentFile();
	}

	public BundleListener addBundleListener(SimpleBundleListener listener) {
		BundleListenerWrapper result = new BundleListenerWrapper(listener);
		context.addBundleListener(result);
		return result;
	}

	/**
	 * Add a ServiceListener with the more easy to use {@link SimpleServiceListener} interface.
	 */
	public ServiceListener addServiceListener(SimpleServiceListener<Object> listener) {
		ServiceListenerWrapper<Object> result = new ServiceListenerWrapper<>(context, listener);
		context.addServiceListener(result);
		return result;
	}

	/**
	 * Add a ServiceListener with the more easy to use {@link SimpleServiceListener} interface.
	 *
	 * @param type
	 *            only listen for events of services of the specified type.
	 */
	public <E> ServiceListenerWrapper<E> addServiceListener(Class<E> type, SimpleServiceListener<E> listener) {
		try {
			ServiceListenerWrapper<E> result = new ServiceListenerWrapper<>(context, listener);
			context.addServiceListener(result, OsgiFilter.byType(type));
			return result;

		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get a file in a location relative to the bundle directory's parent.
	 */
	public File getProjectFile(String path) {
		return new File(getBundleParentDir(), path);
	}

	/**
	 * Tests whether a property is set or not.
	 */
	public boolean hasProperty(String key) {
		return context.getProperty(key) != null;
	}

	/**
	 * Load a property or return the preset if the property is not available.
	 */
	public String getPropertyString(String key, String preset) {
		String value = context.getProperty(key);
		return value == null ? preset : value;
	}

	/**
	 * Invoke the consumer with the value of the property. Only invokes the consumer if the property is configured.
	 */
	public void getPropertyStringOptional(String key, Consumer<String> consumer) {
		String value = context.getProperty(key);
		if (value != null) {
			consumer.accept(value);
		}
	}

	/**
	 * Load a property or return the preset if the property is not available.
	 */
	public boolean getPropertyBoolean(String key, boolean preset) {
		String value = context.getProperty(key);
		if (Str.isBlank(value)) {
			return preset;
		}
		return value.trim().toLowerCase().equals("true");
	}

	/**
	 * Load a property or return the preset if the property is not available.
	 */
	public int getPropertyInt(String key, int preset) {
		String value = context.getProperty(key);
		if (Str.isBlank(value)) {
			return preset;
		}
		Check.matches(value, "\\d++", "property % should be a number, was %", key, value);
		return Integer.parseInt(value);
	}

	/**
	 * Load a property or return the preset if the property is not available.
	 */
	public long getPropertyLong(String key, long preset) {
		String value = context.getProperty(key);
		if (Str.isBlank(value)) {
			return preset;
		}
		Check.matches(value, "\\d++", "property % should be a number, was %", key, value);
		return Long.parseLong(value);
	}
}
