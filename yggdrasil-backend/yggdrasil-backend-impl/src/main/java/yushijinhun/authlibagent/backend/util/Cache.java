package yushijinhun.authlibagent.backend.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import yushijinhun.authlibagent.backend.api.AlreadyDeletedException;

public class Cache<K, V> {

	private class ExpirableObjectProxyWithKey implements InvocationHandler {

		K key;
		V srcObj;

		ExpirableObjectProxyWithKey(K key, V srcObj) {
			this.key = key;
			this.srcObj = srcObj;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object ret;
			try {
				ret = method.invoke(srcObj, args);

				if (method.getName().equals("delete")) {
					expire();
				}
			} catch (InvocationTargetException e) {
				Throwable e1 = e.getTargetException();
				if (e1 instanceof AlreadyDeletedException) {
					expire();
				}
				throw e1;
			}

			return ret;
		}


		void expire() {
			cahces.remove(key);
		}

	}

	private Map<K, V> cahces = Collections.synchronizedMap(new WeakHashMap<>());
	private Object lock = new Object();
	private Function<K, V> producer;

	public Cache(Function<K, V> producer) {
		this.producer = producer;
	}

	public V get(K key) {
		V val = cahces.get(key);
		if (val == null) {
			synchronized (lock) {
				val = cahces.get(key);
				if (val == null) {
					V produce = producer.apply(key);
					if (produce != null) {
						val = wrap(key, producer.apply(key));
						cahces.put(key, val);
					}
				}
			}
		}
		return val;
	}

	public V remove(K key) {
		return cahces.remove(key);
	}

	@SuppressWarnings("unchecked")
	private V wrap(K key, V val) {
		return ((V) Proxy.newProxyInstance(getClass().getClassLoader(), val.getClass().getInterfaces(), new ExpirableObjectProxyWithKey(key, val)));
	}

}
