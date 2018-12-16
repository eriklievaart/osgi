package com.eriklievaart.osgi.toolkit.api;

import java.util.function.Consumer;
import java.util.function.Function;

import com.eriklievaart.toolkit.lang.api.AssertionException;

public interface ServiceCollection<E> {

	/**
	 * Invoke the function on the first implementation of the contract found, if there is one.
	 *
	 * @param consumer
	 *            lambda with the code to invoke on the service.
	 * @return the result of a call on the service or null if the service could not be found.
	 */
	public <T> T anyReturns(Function<E, T> consumer);

	/**
	 * Expects exactly one implementation of the service to be present and invokes it with the lambda.
	 *
	 * @param function
	 *            lambda with the code to invoke on the service.
	 * @return the result of a call on the service.
	 * @throws AssertionException
	 *             if there is not exactly one implementation.
	 */
	public <T> T oneReturns(Function<E, T> function);

	/**
	 * Expects exactly one implementation of the service to be present and invokes it with the lambda.
	 *
	 * @param consumer
	 *            lambda with the code to invoke on the service.
	 * @throws AssertionException
	 *             if there is not exactly one implementation.
	 */
	public void oneCall(Consumer<E> consumer);

	/**
	 * Invoke the function on all implementations of the contract found.
	 *
	 * @param consumer
	 *            lambda with the code to invoke on the services.
	 */
	public void allCall(Consumer<E> consumer);
}
