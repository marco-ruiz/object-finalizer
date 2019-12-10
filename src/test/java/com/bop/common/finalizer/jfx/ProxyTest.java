/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bop.common.finalizer.jfx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Marco Ruiz
 */
public class ProxyTest implements InvocationHandler {
	
	public static Object createProxy(Object target) {
		Class<? extends Object> clazz = target.getClass();
		return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new ProxyTest());		
	}
	
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    	// PROXIED OBJECTS COMPARISON
//    	return Proxy.getProxiedObject(proxy).equals(Proxy.getProxiedObject(args[0]));
    	
    	// DYNAMIC PROXIES COMPARISON
//    	return proxy.equals(args[0]);
    	
    	return null;
    }
    
    public static void main(String[] args) {
    	Object proxied = createProxy(new Object());
    	System.out.println(proxied.equals(proxied));
    }
}


