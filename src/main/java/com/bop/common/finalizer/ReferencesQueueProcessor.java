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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Marco Ruiz
 */
public class ReferencesQueueProcessor {

	private static ReferencesQueueProcessor queueProcessor = new ReferencesQueueProcessor();
	
	public static ReferencesQueueProcessor getInstance() {
		if (queueProcessor == null)
			queueProcessor = new ReferencesQueueProcessor();
		return queueProcessor;
	}
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final ReferenceQueue<Object> referenceQueue;

	private ReferencesQueueProcessor() {
		this(new ReferenceQueue<Object>());
	}
	
	private ReferencesQueueProcessor(ReferenceQueue<Object> queue) {
		this.referenceQueue = queue;
		this.executor.submit(this::disposeFinalizersInQueue);
	}

	public ReferenceQueue<Object> getReferenceQueue() {
		return referenceQueue;
	}

	private void disposeFinalizersInQueue() {
		while (true) {
			try {
				Reference<?> weakRef = referenceQueue.remove();
				if (weakRef != null && weakRef instanceof Disposable)
					((Disposable)weakRef).dispose();
			} catch (InterruptedException e) {
				// Ignore remove interruption: keep retrying
			}
		}
	}
	
	public void shutdown() {
		executor.shutdown();
	}
}
