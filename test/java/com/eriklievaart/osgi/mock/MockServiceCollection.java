package com.eriklievaart.osgi.mock;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class MockServiceCollection<E> implements ServiceCollection<E> {

	private List<E> collection = NewCollection.list();

	public MockServiceCollection(E service) {
		collection.add(service);
	}

	public MockServiceCollection(Collection<E> services) {
		collection.addAll(services);
	}

	public static <E> MockServiceCollection<E> of(E service) {
		return new MockServiceCollection<>(service);
	}

	@Override
	public <T> T anyReturns(Function<E, T> consumer) {
		return collection.isEmpty() ? null : consumer.apply(collection.get(0));
	}

	@Override
	public <T> T oneReturns(Function<E, T> function) {
		CheckCollection.isSize(collection, 1);
		return function.apply(collection.get(0));
	}

	@Override
	public void oneCall(Consumer<E> consumer) {
		CheckCollection.isSize(collection, 1);
		consumer.accept(collection.get(0));
	}

	@Override
	public void allCall(Consumer<E> consumer) {
		for (E service : collection) {
			consumer.accept(service);
		}
	}
}
