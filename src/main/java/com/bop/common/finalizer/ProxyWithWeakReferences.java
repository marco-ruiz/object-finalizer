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

package com.bop.common.finalizer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Marco Ruiz
 */
public class ProxyWithWeakReferences<OBJ_T> implements InvocationHandler {
	
	@SuppressWarnings("unchecked")
	public static <OBJ_T> OBJ_T create(OBJ_T target, Class<? super OBJ_T> clazz, Object... refsToWeaken) {
		ProxyWithWeakReferences<OBJ_T> handler = new ProxyWithWeakReferences<OBJ_T>(target, refsToWeaken);
		return (OBJ_T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, handler);		
	}

	@SuppressWarnings("unchecked")
	public static <OBJ_T> OBJ_T create(OBJ_T target, Object... refsToWeaken) {
		Class<? extends Object> targetClazz = target.getClass();
		ProxyWithWeakReferences<OBJ_T> handler = new ProxyWithWeakReferences<OBJ_T>(target, refsToWeaken);
		return (OBJ_T) Proxy.newProxyInstance(targetClazz.getClassLoader(), targetClazz.getInterfaces(), handler);		
	}
	
    private OBJ_T target;
    private WeakFields weakFields;
	
	private ProxyWithWeakReferences(OBJ_T target, Object... referencesToWeaken) {
        this.target = target;
        this.weakFields = new WeakFields(target, referencesToWeaken);
        this.weakFields.clearWeakFields();
	}
 
    @Override
    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    	if (!weakFields.areWeakFieldsValid()) return null;
    	
    	Object result = null;
    	weakFields.resetWeakFields();
        try {
			result = method.invoke(target, args);
		} catch (Exception e) {
			throw e;
		} finally {
			weakFields.clearWeakFields();
		}
        
        return result;
    }
}

