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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marco Ruiz
 */
public final class WeakReferenceFinalizable extends WeakReference<Object> implements Disposable {
	
	private static final Set<WeakReferenceFinalizable> finalizers = new HashSet<>();
	
	public static WeakReferenceFinalizable createFinalizer(Object referent, Runnable finalizer) {
		WeakReferenceFinalizable finalizable = new WeakReferenceFinalizable(referent, finalizer);
		finalizers.add(finalizable);
		return finalizable;
	}
	
	private Runnable finalizerCommand;
	
	private WeakReferenceFinalizable(Object referent, Runnable finalizer) {
		this(referent, finalizer, ReferencesQueueProcessor.getInstance().getReferenceQueue());
	}
	
	private WeakReferenceFinalizable(Object referent, Runnable finalizer, ReferenceQueue<Object> queue) {
		super(referent, queue);
		this.finalizerCommand = finalizer;
	}
	
	public void dispose() {
		finalizerCommand.run();
	    clear();
	    finalizers.remove(this);
	}
}
