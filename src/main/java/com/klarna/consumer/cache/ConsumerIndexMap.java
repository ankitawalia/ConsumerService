package com.klarna.consumer.cache;

import static com.google.common.collect.Iterators.emptyIterator;
import static com.google.common.collect.Maps.immutableEntry;
import static java.lang.String.format;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

public class ConsumerIndexMap<K, V> extends AbstractMap<K, V> implements
		ConcurrentMap<K, V> {
	private final ConcurrentMap<K, Index<K>> indexes;
	private final Function<V, Index<K>> extractor;
	private final ConcurrentMultiMap<K, V> store;

	/**
	 * An {@link ConsumerIndexMap} backed by a {@link ConcurrentHashMap}.
	 * 
	 * @param extractor
	 *            Extracts the keys that are associated with the given value.
	 */
	public static <K, V> ConsumerIndexMap<K, V> create(
			Function<V, Index<K>> extractor) {
		return create(new ConcurrentMultiMap<K, V>(), extractor);
	}

	/**
	 * An {@link ConsumerIndexMap} backed by the specified map.
	 * 
	 * @param store
	 *            The backing data map to decorate.
	 * @param extractor
	 *            Extracts the keys that are associated with the given value.
	 */
	public static <K, V> ConsumerIndexMap<K, V> create(
			ConcurrentMultiMap<K, V> store, Function<V, Index<K>> extractor) {
		return create(store, new ConcurrentHashMap<K, Index<K>>(), extractor);
	}

	/**
	 * An {@link ConsumerIndexMap} backed by the specified maps.
	 * 
	 * @param store
	 *            The backing data map to decorate.
	 * @param indexes
	 *            The backing key index map to decorate.
	 * @param extractor
	 *            Extracts the keys that are associated with the given value.
	 */
	public static <K, V> ConsumerIndexMap<K, V> create(
			ConcurrentMultiMap<K, V> store, ConcurrentMap<K, Index<K>> indexes,
			Function<V, Index<K>> extractor) {
		return new ConsumerIndexMap<K, V>(store, indexes, extractor);
	}

	/**
	 * An implementation backed by the specified map.
	 * 
	 * @param store
	 *            The backing data map to decorate.
	 * @param indexes
	 *            The backing key index map to decorate.
	 * @param extractor
	 *            Extracts the keys that are associated with the given value.
	 */
	ConsumerIndexMap(ConcurrentMultiMap<K, V> store,
			ConcurrentMap<K, Index<K>> indexes, Function<V, Index<K>> extractor) {
		this.extractor = extractor;
		this.indexes = indexes;
		this.store = store;
	}

	/**
	 * The index associated with a value.
	 * 
	 */
	public static final class Index<K> {
		private final K primary;
		private final Set<K> all;
		private final Set<K> secondaries;

		/**
		 * The index keys associated with a single value.
		 * 
		 * @param primary
		 *            The primary key to the value.
		 * @param secondaries
		 *            The secondary keys to the value.
		 */
		public Index(K primary, K... secondaries) {
			this(primary, ImmutableSet.of(secondaries));
		}

		/**
		 * The index keys associated with a single value.
		 * 
		 * @param primary
		 *            The primary key to the value.
		 * @param immutableSet
		 *            The secondary keys to the value.
		 */
		public Index(K primary, ImmutableSet<K[]> immutableSet) {
			this.primary = (primary);
			this.secondaries = (Set<K>) (immutableSet);
			getSecondaries().add(getPrimary());
			this.all = getSecondaries();
		}

		/**
		 * Retrieves the primary key.
		 */
		public K getPrimary() {
			return primary;
		}

		/**
		 * Retrieves the secondary keys to index with.
		 */
		public Set<K> getSecondaries() {
			return secondaries;
		}

		/**
		 * Retrieves all of the keys.
		 */
		public Set<K> getAll() {
			return all;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return format("primary=%s, secondaries=%s", getPrimary(),
					getSecondaries());
		}
	}

	@Override
	public V putIfAbsent(K key, V value) {
		Index<K> index = checkIndex(key, value);
		return putIfAbsent(index, value);
	}

	/**
	 * Places the value into the map if absent and associates its keys.
	 * 
	 * @param index
	 *            The key associations.
	 * @param value
	 *            The value to put into the map.
	 * @return The previous value, or <tt>null</tt> if there was no mapping.
	 */
	private V putIfAbsent(Index<K> index, V value) {
		K primary = index.getPrimary();
		V old = store.putIfAbsent(primary, value);
		if (old == null) {
			addIndex(index);
		}
		return old;
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		Index<K> oldIndex = extractor.apply(oldValue);
		Index<K> newIndex = extractor.apply(newValue);
		oldIndex.getAll().addAll(newIndex.getAll());
		Set<K> keys = oldIndex.getAll();
		return replace(oldIndex, oldValue, newIndex, newValue);
	}

	/**
	 * Replaces the entry based on the primary key only if it is currently
	 * mapped to the given value.
	 * 
	 * @param oldIndex
	 *            The old key associations.
	 * @param oldValue
	 *            The expected value in the map.
	 * @param newValue
	 *            The value to put into the map.
	 * @return Whether the value was replaced.
	 */
	private boolean replace(Index<K> oldIndex, V oldValue, Index<K> newIndex,
			V newValue) {
		oldIndex.getPrimary().equals(newIndex.getPrimary());
		K primary = oldIndex.getPrimary();
		if (store.replace(primary, oldValue, newValue)) {
			updateIndex(oldIndex, newIndex);
			return true;
		}
		return false;
	}

	/**
	 * Updates the index associations for a replaced value. It is assumed that
	 * the lock is held.
	 * <p>
	 * It is expected that the primary key does not change, but that secondary
	 * key associations may have become stale. If so, the stale indexes are
	 * removed and the new indexes added to the mapping.
	 * 
	 * @param oldIndex
	 *            The old key associations.
	 * @param newIndex
	 *            The new key associations.
	 */
	protected void updateIndex(Index<K> oldIndex, Index<K> newIndex) {
		// Adds the new keys, or updates old ones, to map to the new index
		addIndex(newIndex);

		// Removes the stale keys that no longer map to the old index
		Set<K> removed = new HashSet<K>(oldIndex.getSecondaries());
		removed.removeAll(newIndex.getSecondaries());
		for (K key : removed) {
			indexes.remove(key);
		}
	}

	/**
	 * Adds the indexed keys. It is assumed that the lock is held.
	 * 
	 * @param index
	 *            The key associations.
	 */
	protected void addIndex(Index<K> index) {
		indexes.putAll(index.getAll());
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		  return new EntrySet();
	}
	
	 /**
     * An adapter that represents the association of multiple keys to a single value.
     */
    private final class EntrySet extends AbstractSet<Entry<K, V>> {
        @Override
        public void clear() {
            ConsumerIndexMap.this.clear();
        }
        @Override
        public int size() {
            return ConsumerIndexMap.this.indexes.size();
        }
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator(store.values().iterator());
        }
        @Override
        public boolean contains(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry<?, ?> entry = (Entry<?, ?>) obj;
            V value = get(entry.getKey());
            return (value != null) && (value.equals(entry.getValue()));
        }
        @Override
        public boolean add(Entry<K, V> entry) {
            return (putIfAbsent(entry.getKey(), entry.getValue()) == null);
        }
        @Override
        public boolean remove(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry<?, ?> entry = (Entry<?, ?>) obj;
            return IndexMap.this.remove(entry.getKey(), entry.getValue());
        }
        @Override
        public Object[] toArray() {
            // avoid 3-expression 'for' loop when using concurrent collections
            Collection<Entry<K, V>> entries = new ArrayList<Entry<K, V>>(size());
            for (Entry<K, V> entry : this) {
                entries.add(entry);
            }
            return entries.toArray();
        }
        @Override
        public <T> T[] toArray(T[] array) {
            // avoid 3-expression 'for' loop when using concurrent collections
            Collection<Entry<K, V>> entries = new ArrayList<Entry<K, V>>(size());
            for (Entry<K, V> entry : this) {
                entries.add(entry);
            }
            return entries.toArray(array);
        }
    }
    
    /**
     * An adapter that represents the association of multiple keys to a single value.
     */
    private final class EntryIterator implements Iterator<Entry<K, V>> {
        private final Iterator<V> values;
        private Iterator<K> keys;
        private V value;

        public EntryIterator(Iterator<V> values) {
            this.keys = emptyIterator();
            this.values = values;
        }
        public boolean hasNext() {
            return keys.hasNext() || values.hasNext();
        }
        public Entry<K, V> next() {
            if (!keys.hasNext()) {
                value = values.next();
                Index<K> index = extractor.apply(value);
                keys = index.getAll().iterator();
            }
            return immutableEntry(keys.next(), value);
        }
        public void remove() {
            IndexMap.this.removeValue(value);
        }
    }

	@Override
	public V replace(K key, V value) {
		Index<K> index = checkIndex(key, value);
		return replace(index, value);
	}
	
	/**
     * Replaces the entry based on the primary key only if it is currently mapped to some value.
     *
     * @param index The key associations.
     * @param value The value to put into the map.
     * @return      The previous value, or <tt>null</tt> if there was no mapping.
     */
    private V replace(Index<K> index, V value) {
        K primary = index.getPrimary();
            V old = store.replace(primary, value);
            if (old != null) {
                updateIndex(indexes.get(primary), index);
            }
            return old;
    }


	/**
	 * Replaces the entry based on the primary key only if it is currently
	 * mapped to some value.
	 * 
	 * @param value
	 *            The value to put into the map.
	 * @return The previous value, or <tt>null</tt> if there was no mapping.
	 */
	public V replaceValue(V value) {
		Index<K> index = extractor.apply(value);
		return replace(index, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		Index<K> index = indexes.get(key);
		if (index == null) {
			return false;
		}
		K primary = index.getPrimary();
		if (store.remove(primary, value)) {
			removeIndex(indexes.get(primary));
			return true;
		}
		return false;
	}

	/**
	 * Removes the indexed keys. It is assumed that the lock is held.
	 * 
	 * @param index
	 *            The key associations.
	 */
	protected void removeIndex(Index<K> index) {
		for (K key : index.getAll()) {
			indexes.remove(key);
		}
	}
	
	/**
     * Extracts the index from the value and asserts that the given key is a valid association.
     *
     * @param key   The key.
     * @param value The value.
     * @return      The index.
     */
    private Index<K> checkIndex(K key, V value) {
        Index<K> index = extractor.apply(value);
        return index.getAll().contains(key) ? index : null;
    }

}
