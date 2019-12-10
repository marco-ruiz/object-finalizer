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
import java.util.Map;
import java.util.WeakHashMap;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

/**
 * @author Marco Ruiz
 */
public class NodeProxy implements InvocationHandler {
	
	static final Map<Object, JFXSafeSubscriptionTemplate> jfxSubscriberTemplates = new WeakHashMap<>();

	@SuppressWarnings("unchecked")
	public static <OBJ_T extends Node> OBJ_T create(Node target, Object subscriber) {
		Class<? extends Object> targetClazz = target.getClass();
		NodeProxy handler = new NodeProxy(target, subscriber);
		return (OBJ_T) Proxy.newProxyInstance(targetClazz.getClassLoader(), new Class[] { Node.class }, handler);		
	}
	
    private static Method targetMethod = null;

	private static Method getTargetMethod() {
		if (targetMethod == null)
			try {
				targetMethod = Node.class.getMethod("addEventHandler", new Class[] {EventType.class, EventHandler.class});
			} catch (NoSuchMethodException | SecurityException e) {
				// Impossible: Node has "addEventHandler" method defined!
				e.printStackTrace();
			}
		return targetMethod;
	}

	private Node target;
    private Object subscriber;
	
	private NodeProxy(Node target, Object subscriber) {
        this.target = target;
        getTargetMethod();
	}
 
	private JFXSafeSubscriptionTemplate getJFXSafeSubscriptionTemplate() {
		return (JFXSafeSubscriptionTemplate) jfxSubscriberTemplates.computeIfAbsent(subscriber, JFXSafeSubscriptionTemplate::new);
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    	Object result = null;
    	
    	if (method.equals(getTargetMethod()))
    		getJFXSafeSubscriptionTemplate().addEventHandlerSafely(target, (EventType)args[0], (EventHandler)args[1]);
    	else {
	        try {
				result = method.invoke(target, args);
			} catch (Exception e) {
				throw e;
			} finally {
			}
    	}
        
        return result;
    }
}

