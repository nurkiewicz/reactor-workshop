package com.nurkiewicz.reactor.samples;

import java.util.AbstractCollection;
import java.util.Iterator;

import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheCollectionAdapter<T> extends AbstractCollection<T> {

	private static final Logger log = LoggerFactory.getLogger(CacheCollectionAdapter.class);

	private final Cache<T, Boolean> cache;

	public CacheCollectionAdapter(Cache<T, Boolean> cache) {
		this.cache = cache;
	}

	@Override
	public Iterator<T> iterator() {
		return cache.asMap().keySet().iterator();
	}

	@Override
	public int size() {
		return cache.asMap().size();
	}

	@Override
	public boolean contains(Object o) {
		final boolean result = cache.getIfPresent((T)o) != null;
		log.debug("GET {} -> {}", o, result);
		return result;
	}

	@Override
	public boolean add(T t) {
		final boolean newElem = cache.getIfPresent(t) == null;
		cache.put(t, true);
		cache.cleanUp();
		log.debug("PUT {} -> {}", t, newElem);
		return newElem;
	}


}
