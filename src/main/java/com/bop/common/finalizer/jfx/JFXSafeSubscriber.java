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

import java.util.Map;
import java.util.WeakHashMap;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

/**
 * @author Marco Ruiz
 */
public interface JFXSafeSubscriber<SUB_T> {

	static final Map<Object, JFXSafeSubscriptionTemplate<?>> jfxSubscriberTemplates = new WeakHashMap<>();
	
	default <EV_T extends Event> void addEventHandlerSafely(Node publisher, EventType<EV_T> eventType, EventHandler<? super EV_T> observer) {
		getJFXSafeSubscriptionTemplate().addEventHandlerSafely(publisher, eventType, observer);
	}

	default void addListenerSafely(Observable publisher, InvalidationListener observer) {
		getJFXSafeSubscriptionTemplate().addInvalidationListenerSafely(publisher, observer);
	}

	default <VAL_T> void addListenerSafely(ObservableValue<VAL_T> publisher, ChangeListener<? super VAL_T> observer) {
		getJFXSafeSubscriptionTemplate().addChangeListenerSafely(publisher, observer);
	}

	default <VAL_T> void removeObservers() {
		getJFXSafeSubscriptionTemplate().removeObservers();
	}

	@SuppressWarnings("unchecked")
	default JFXSafeSubscriptionTemplate<SUB_T> getJFXSafeSubscriptionTemplate() {
		return (JFXSafeSubscriptionTemplate<SUB_T>) 
				jfxSubscriberTemplates.computeIfAbsent(this, JFXSafeSubscriptionTemplate::new);
	}
}
