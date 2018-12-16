package com.eriklievaart.osgi.toolkit.api.listener;

/**
 * A contract for listening to Service events that do not require casting or checking ints.
 *
 * @param <E>
 *            Type of the service.
 */
public interface SimpleServiceListener<E> {

	/**
	 * Handler for the REGISTERED event.
	 *
	 * @see org.osgi.framework.ServiceEvent#REGISTERED
	 */
	void register(E service);

	/**
	 * Handler for the UNREGISTERING event.
	 *
	 * @see org.osgi.framework.ServiceEvent#UNREGISTERING
	 */
	void unregistering(E service);

	/**
	 * Handler for the MODIFIED event (optional).
	 *
	 * @see org.osgi.framework.ServiceEvent#MODIFIED
	 */
	@SuppressWarnings("unused")
	default void modified(E service) {
	}

	/**
	 * Handler for the MODIFIED_ENDMATCH event (optional).
	 *
	 * @see org.osgi.framework.ServiceEvent#MODIFIED_ENDMATCH
	 */
	@SuppressWarnings("unused")
	default void modifiedEndMatch(E service) {
	}
}
