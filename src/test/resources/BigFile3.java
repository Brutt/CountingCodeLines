/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jakarta.servlet;

import java.io.IOException;

/**
 * A filter is an object that performs filtering tasks on either the request to
 * a resource (a servlet or static content), or on the response from a resource,
 * or both. <br>
 * <br>
 * Filters perform filtering in the <code>doFilter</code> method. Every Filter
 * has access to a FilterConfig object from which it can obtain its
 * initialization parameters, a reference to the ServletContext which it can
 * use, for example, to load resources needed for filtering tasks.
 * <p>
 * Filters are configured in the deployment descriptor of a web application
 * <p>
 * Examples that have been identified for this design are<br>
 * 1) Authentication Filters <br>
 * 2) Logging and Auditing Filters <br>
 * 3) Image conversion Filters <br>
 * 4) Data compression Filters <br>
 * 5) Encryption Filters <br>
 * 6) Tokenizing Filters <br>
 * 7) Filters that trigger resource access events <br>
 * 8) XSL/T filters <br>
 * 9) Mime-type chain Filter <br>
 *
 * @since Servlet 2.3
 */
public interface Filter {

    /**
     * Called by the web container to indicate to a filter that it is being
     * placed into service. The servlet container calls the init method exactly
     * once after instantiating the filter. The init method must complete
     * successfully before the filter is asked to do any filtering work.
     * <p>
     * The web container cannot place the filter into service if the init method
     * either:
     * <ul>
     * <li>Throws a ServletException</li>
     * <li>Does not return within a time period defined by the web
     *     container</li>
     * </ul>
     * The default implementation is a NO-OP.
     *
     * @param filterConfig The configuration information associated with the
     *                     filter instance being initialised
     *
     * @throws ServletException if the initialisation fails
     */
    public default void init(FilterConfig filterConfig) throws ServletException {}

    /**
     * The <code>doFilter</code> method of the Filter is called by the container
     * each time a request/response pair is passed through the chain due to a
     * client request for a resource at the end of the chain. The FilterChain
     * passed in to this method allows the Filter to pass on the request and
     * response to the next entity in the chain.
     * <p>
     * A typical implementation of this method would follow the following
     * pattern:- <br>
     * 1. Examine the request<br>
     * 2. Optionally wrap the request object with a custom implementation to
     * filter content or headers for input filtering <br>
     * 3. Optionally wrap the response object with a custom implementation to
     * filter content or headers for output filtering <br>
     * 4. a) <strong>Either</strong> invoke the next entity in the chain using
     * the FilterChain object (<code>chain.doFilter()</code>), <br>
     * 4. b) <strong>or</strong> not pass on the request/response pair to the
     * next entity in the filter chain to block the request processing<br>
     * 5. Directly set headers on the response after invocation of the next
     * entity in the filter chain.
     *
     * @param request  The request to process
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this
     *                 filter to pass the request and response to for further
     *                 processing
     *
     * @throws IOException if an I/O error occurs during this filter's
     *                     processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException;

    /**
     * Called by the web container to indicate to a filter that it is being
     * taken out of service. This method is only called once all threads within
     * the filter's doFilter method have exited or after a timeout period has
     * passed. After the web container calls this method, it will not call the
     * doFilter method again on this instance of the filter. <br>
     * <br>
     *
     * This method gives the filter an opportunity to clean up any resources
     * that are being held (for example, memory, file handles, threads) and make
     * sure that any persistent state is synchronized with the filter's current
     * state in memory.
     *
     * The default implementation is a NO-OP.
     */
    public default void destroy() {}
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}


/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jakarta.servlet;

        import java.io.IOException;

/**
 * A filter is an object that performs filtering tasks on either the request to
 * a resource (a servlet or static content), or on the response from a resource,
 * or both. <br>
 * <br>
 * Filters perform filtering in the <code>doFilter</code> method. Every Filter
 * has access to a FilterConfig object from which it can obtain its
 * initialization parameters, a reference to the ServletContext which it can
 * use, for example, to load resources needed for filtering tasks.
 * <p>
 * Filters are configured in the deployment descriptor of a web application
 * <p>
 * Examples that have been identified for this design are<br>
 * 1) Authentication Filters <br>
 * 2) Logging and Auditing Filters <br>
 * 3) Image conversion Filters <br>
 * 4) Data compression Filters <br>
 * 5) Encryption Filters <br>
 * 6) Tokenizing Filters <br>
 * 7) Filters that trigger resource access events <br>
 * 8) XSL/T filters <br>
 * 9) Mime-type chain Filter <br>
 *
 * @since Servlet 2.3
 */
public interface Filter {

    /**
     * Called by the web container to indicate to a filter that it is being
     * placed into service. The servlet container calls the init method exactly
     * once after instantiating the filter. The init method must complete
     * successfully before the filter is asked to do any filtering work.
     * <p>
     * The web container cannot place the filter into service if the init method
     * either:
     * <ul>
     * <li>Throws a ServletException</li>
     * <li>Does not return within a time period defined by the web
     *     container</li>
     * </ul>
     * The default implementation is a NO-OP.
     *
     * @param filterConfig The configuration information associated with the
     *                     filter instance being initialised
     *
     * @throws ServletException if the initialisation fails
     */
    public default void init(FilterConfig filterConfig) throws ServletException {}

    /**
     * The <code>doFilter</code> method of the Filter is called by the container
     * each time a request/response pair is passed through the chain due to a
     * client request for a resource at the end of the chain. The FilterChain
     * passed in to this method allows the Filter to pass on the request and
     * response to the next entity in the chain.
     * <p>
     * A typical implementation of this method would follow the following
     * pattern:- <br>
     * 1. Examine the request<br>
     * 2. Optionally wrap the request object with a custom implementation to
     * filter content or headers for input filtering <br>
     * 3. Optionally wrap the response object with a custom implementation to
     * filter content or headers for output filtering <br>
     * 4. a) <strong>Either</strong> invoke the next entity in the chain using
     * the FilterChain object (<code>chain.doFilter()</code>), <br>
     * 4. b) <strong>or</strong> not pass on the request/response pair to the
     * next entity in the filter chain to block the request processing<br>
     * 5. Directly set headers on the response after invocation of the next
     * entity in the filter chain.
     *
     * @param request  The request to process
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this
     *                 filter to pass the request and response to for further
     *                 processing
     *
     * @throws IOException if an I/O error occurs during this filter's
     *                     processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException;

    /**
     * Called by the web container to indicate to a filter that it is being
     * taken out of service. This method is only called once all threads within
     * the filter's doFilter method have exited or after a timeout period has
     * passed. After the web container calls this method, it will not call the
     * doFilter method again on this instance of the filter. <br>
     * <br>
     *
     * This method gives the filter an opportunity to clean up any resources
     * that are being held (for example, memory, file handles, threads) and make
     * sure that any persistent state is synchronized with the filter's current
     * state in memory.
     *
     * The default implementation is a NO-OP.
     */
    public default void destroy() {}
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}


/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jakarta.servlet;

        import java.io.IOException;

/**
 * A filter is an object that performs filtering tasks on either the request to
 * a resource (a servlet or static content), or on the response from a resource,
 * or both. <br>
 * <br>
 * Filters perform filtering in the <code>doFilter</code> method. Every Filter
 * has access to a FilterConfig object from which it can obtain its
 * initialization parameters, a reference to the ServletContext which it can
 * use, for example, to load resources needed for filtering tasks.
 * <p>
 * Filters are configured in the deployment descriptor of a web application
 * <p>
 * Examples that have been identified for this design are<br>
 * 1) Authentication Filters <br>
 * 2) Logging and Auditing Filters <br>
 * 3) Image conversion Filters <br>
 * 4) Data compression Filters <br>
 * 5) Encryption Filters <br>
 * 6) Tokenizing Filters <br>
 * 7) Filters that trigger resource access events <br>
 * 8) XSL/T filters <br>
 * 9) Mime-type chain Filter <br>
 *
 * @since Servlet 2.3
 */
public interface Filter {

    /**
     * Called by the web container to indicate to a filter that it is being
     * placed into service. The servlet container calls the init method exactly
     * once after instantiating the filter. The init method must complete
     * successfully before the filter is asked to do any filtering work.
     * <p>
     * The web container cannot place the filter into service if the init method
     * either:
     * <ul>
     * <li>Throws a ServletException</li>
     * <li>Does not return within a time period defined by the web
     *     container</li>
     * </ul>
     * The default implementation is a NO-OP.
     *
     * @param filterConfig The configuration information associated with the
     *                     filter instance being initialised
     *
     * @throws ServletException if the initialisation fails
     */
    public default void init(FilterConfig filterConfig) throws ServletException {}

    /**
     * The <code>doFilter</code> method of the Filter is called by the container
     * each time a request/response pair is passed through the chain due to a
     * client request for a resource at the end of the chain. The FilterChain
     * passed in to this method allows the Filter to pass on the request and
     * response to the next entity in the chain.
     * <p>
     * A typical implementation of this method would follow the following
     * pattern:- <br>
     * 1. Examine the request<br>
     * 2. Optionally wrap the request object with a custom implementation to
     * filter content or headers for input filtering <br>
     * 3. Optionally wrap the response object with a custom implementation to
     * filter content or headers for output filtering <br>
     * 4. a) <strong>Either</strong> invoke the next entity in the chain using
     * the FilterChain object (<code>chain.doFilter()</code>), <br>
     * 4. b) <strong>or</strong> not pass on the request/response pair to the
     * next entity in the filter chain to block the request processing<br>
     * 5. Directly set headers on the response after invocation of the next
     * entity in the filter chain.
     *
     * @param request  The request to process
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this
     *                 filter to pass the request and response to for further
     *                 processing
     *
     * @throws IOException if an I/O error occurs during this filter's
     *                     processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException;

    /**
     * Called by the web container to indicate to a filter that it is being
     * taken out of service. This method is only called once all threads within
     * the filter's doFilter method have exited or after a timeout period has
     * passed. After the web container calls this method, it will not call the
     * doFilter method again on this instance of the filter. <br>
     * <br>
     *
     * This method gives the filter an opportunity to clean up any resources
     * that are being held (for example, memory, file handles, threads) and make
     * sure that any persistent state is synchronized with the filter's current
     * state in memory.
     *
     * The default implementation is a NO-OP.
     */
    public default void destroy() {}
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}


/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jakarta.servlet;

        import java.io.IOException;

/**
 * A filter is an object that performs filtering tasks on either the request to
 * a resource (a servlet or static content), or on the response from a resource,
 * or both. <br>
 * <br>
 * Filters perform filtering in the <code>doFilter</code> method. Every Filter
 * has access to a FilterConfig object from which it can obtain its
 * initialization parameters, a reference to the ServletContext which it can
 * use, for example, to load resources needed for filtering tasks.
 * <p>
 * Filters are configured in the deployment descriptor of a web application
 * <p>
 * Examples that have been identified for this design are<br>
 * 1) Authentication Filters <br>
 * 2) Logging and Auditing Filters <br>
 * 3) Image conversion Filters <br>
 * 4) Data compression Filters <br>
 * 5) Encryption Filters <br>
 * 6) Tokenizing Filters <br>
 * 7) Filters that trigger resource access events <br>
 * 8) XSL/T filters <br>
 * 9) Mime-type chain Filter <br>
 *
 * @since Servlet 2.3
 */
public interface Filter {

    /**
     * Called by the web container to indicate to a filter that it is being
     * placed into service. The servlet container calls the init method exactly
     * once after instantiating the filter. The init method must complete
     * successfully before the filter is asked to do any filtering work.
     * <p>
     * The web container cannot place the filter into service if the init method
     * either:
     * <ul>
     * <li>Throws a ServletException</li>
     * <li>Does not return within a time period defined by the web
     *     container</li>
     * </ul>
     * The default implementation is a NO-OP.
     *
     * @param filterConfig The configuration information associated with the
     *                     filter instance being initialised
     *
     * @throws ServletException if the initialisation fails
     */
    public default void init(FilterConfig filterConfig) throws ServletException {}

    /**
     * The <code>doFilter</code> method of the Filter is called by the container
     * each time a request/response pair is passed through the chain due to a
     * client request for a resource at the end of the chain. The FilterChain
     * passed in to this method allows the Filter to pass on the request and
     * response to the next entity in the chain.
     * <p>
     * A typical implementation of this method would follow the following
     * pattern:- <br>
     * 1. Examine the request<br>
     * 2. Optionally wrap the request object with a custom implementation to
     * filter content or headers for input filtering <br>
     * 3. Optionally wrap the response object with a custom implementation to
     * filter content or headers for output filtering <br>
     * 4. a) <strong>Either</strong> invoke the next entity in the chain using
     * the FilterChain object (<code>chain.doFilter()</code>), <br>
     * 4. b) <strong>or</strong> not pass on the request/response pair to the
     * next entity in the filter chain to block the request processing<br>
     * 5. Directly set headers on the response after invocation of the next
     * entity in the filter chain.
     *
     * @param request  The request to process
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this
     *                 filter to pass the request and response to for further
     *                 processing
     *
     * @throws IOException if an I/O error occurs during this filter's
     *                     processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException;

    /**
     * Called by the web container to indicate to a filter that it is being
     * taken out of service. This method is only called once all threads within
     * the filter's doFilter method have exited or after a timeout period has
     * passed. After the web container calls this method, it will not call the
     * doFilter method again on this instance of the filter. <br>
     * <br>
     *
     * This method gives the filter an opportunity to clean up any resources
     * that are being held (for example, memory, file handles, threads) and make
     * sure that any persistent state is synchronized with the filter's current
     * state in memory.
     *
     * The default implementation is a NO-OP.
     */
    public default void destroy() {}
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}


/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

        import java.util.AbstractMap;
        import java.util.AbstractSet;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Set;

        import org.apache.tomcat.util.res.StringManager;

/**
 * A Map implementation that uses case-insensitive (using {@link
 * Locale#ENGLISH}) strings as keys.
 * <p>
 * Keys must be instances of {@link String}. Note that this means that
 * <code>null</code> keys are not permitted.
 * <p>
 * This implementation is not thread-safe.
 *
 * @param <V> Type of values placed in this Map.
 */
public class CaseInsensitiveKeyMap<V> extends AbstractMap<String,V> {

    private static final StringManager sm =
            StringManager.getManager(CaseInsensitiveKeyMap.class);

    private final Map<Key,V> map = new HashMap<>();


    @Override
    public V get(Object key) {
        return map.get(Key.getInstance(key));
    }


    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return map.put(caseInsensitiveKey, value);
    }


    /**
     * {@inheritDoc}
     * <p>
     * <b>Use this method with caution</b>. If the input Map contains duplicate
     * keys when the keys are compared in a case insensitive manner then some
     * values will be lost when inserting via this method.
     */
    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }


    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(Key.getInstance(key));
    }


    @Override
    public V remove(Object key) {
        return map.remove(Key.getInstance(key));
    }


    @Override
    public Set<Entry<String, V>> entrySet() {
        return new EntrySet<>(map.entrySet());
    }


    private static class EntrySet<V> extends AbstractSet<Entry<String,V>> {

        private final Set<Entry<Key,V>> entrySet;

        public EntrySet(Set<Map.Entry<Key,V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Entry<String,V>> iterator() {
            return new EntryIterator<>(entrySet.iterator());
        }

        @Override
        public int size() {
            return entrySet.size();
        }
    }


    private static class EntryIterator<V> implements Iterator<Entry<String,V>> {

        private final Iterator<Entry<Key,V>> iterator;

        public EntryIterator(Iterator<Entry<Key,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<String,V> next() {
            Entry<Key,V> entry = iterator.next();
            return new EntryImpl<>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }


    private static class EntryImpl<V> implements Entry<String,V> {

        private final String key;
        private final V value;

        public EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Key {

        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return lcKey.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String) o);
            }
            return null;
        }
    }
}

/**
 * A filter is an object that performs filtering tasks on either the request to
 * a resource (a servlet or static content), or on the response from a resource,
 * or both. <br>
 * <br>
 * Filters perform filtering in the <code>doFilter</code> method. Every Filter
 * has access to a FilterConfig object from which it can obtain its
 * initialization parameters, a reference to the ServletContext which it can
 * use, for example, to load resources needed for filtering tasks.
 * <p>
 * Filters are configured in the deployment descriptor of a web application
 * <p>
 * Examples that have been identified for this design are<br>
 * 1) Authentication Filters <br>
 * 2) Logging and Auditing Filters <br>
 * 3) Image conversion Filters <br>
 * 4) Data compression Filters <br>
 * 5) Encryption Filters <br>
 * 6) Tokenizing Filters <br>
 * 7) Filters that trigger resource access events <br>
 * 8) XSL/T filters <br>
 * 9) Mime-type chain Filter <br>
 *
 * @since Servlet 2.3
 */