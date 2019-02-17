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

import com.bop.common.command.AbstractCommandWithWeakArgs.CmdWithWeakArgs2;
import com.bop.common.command.AbstractCommandWithWeakArgs.CmdWithWeakArgs3;
import com.bop.common.finalizer.SafeSubscriptionTemplate;

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
public class JFXSafeSubscriptionTemplate<SUB_T> extends SafeSubscriptionTemplate<SUB_T> {
	
	public JFXSafeSubscriptionTemplate(SUB_T subscriber) {
		super(subscriber);
	}
	
	public <EV_T extends Event> void addEventHandlerSafely(Node publisher, EventType<EV_T> eventType, EventHandler<? super EV_T> observer) {
		addSafely(publisher, observer, 
				(pub, obs) -> pub.addEventHandler(eventType, obs), 
				(pub, obs) -> new CmdWithWeakArgs3<>(Node::removeEventHandler, pub, eventType, obs));
	}

	public void addInvalidationListenerSafely(Observable publisher, InvalidationListener observer) {
		addSafely(publisher, observer, 
				Observable::addListener, 
				(pub, obs) -> new CmdWithWeakArgs2<>(Observable::removeListener, pub, obs));
	}

	public <VAL_T> void addChangeListenerSafely(ObservableValue<VAL_T> publisher, ChangeListener<? super VAL_T> observer) {
		addSafely(publisher, observer, 
				ObservableValue<VAL_T>::addListener, 
				(pub, obs) -> new CmdWithWeakArgs2<>(ObservableValue<VAL_T>::removeListener, pub, obs));
	}
}

