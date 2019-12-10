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

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * @author Marco Ruiz
 */
public class SafeSubscriptionTemplate {
	
	private final WeakReference<Object> weakSubscriber;
	private final Set<WeakReferenceFinalizable> finalizables = Collections.newSetFromMap(new WeakHashMap<WeakReferenceFinalizable, Boolean>());
	
	protected SafeSubscriptionTemplate(Object subscriber) {
		this.weakSubscriber = new WeakReference<>(subscriber);
	}
	
	public <PUB_T, OBS_T> void addSafely(PUB_T publisher, OBS_T observer, 
			BiConsumer<PUB_T, OBS_T> registerer, BiFunction<PUB_T, OBS_T, Runnable> unregistererFactory) {

		observer = WeakRefsInvocationHandler.create(observer, weakSubscriber.get());
		registerer.accept(publisher, observer);
		Runnable unregistererAsync = unregistererFactory.apply(publisher, observer);
		finalizables.add(WeakReferenceFinalizable.createFinalizer(weakSubscriber.get(), unregistererAsync));
	}

	public void removeObservers() {
		finalizables.forEach(obj -> obj.dispose());
		finalizables.clear();
	}
}

