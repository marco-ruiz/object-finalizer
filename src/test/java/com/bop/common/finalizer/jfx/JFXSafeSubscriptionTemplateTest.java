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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

/**
 * @author Marco Ruiz
 */
public class JFXSafeSubscriptionTemplateTest {//implements JFXSafeSubscriber {

	private static void gc(boolean gc) {
		if (gc) System.gc();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
	}
	
	private String initialValue = "Hello ";
	
	private StringProperty publisher;
	private StringProperty observerProperty;
	private ChangeListener<? super String> listener;
	private JFXSafeSubscriber subscriber;

	@Before
	public void before() {
		subscriber = new JFXSafeSubscriber() {};
		publisher = new SimpleStringProperty(initialValue);
		observerProperty = new SimpleStringProperty(initialValue);
		listener = (ov, oldVal, newVal) -> observerProperty.set(newVal);
		
		subscriber.addListenerSafely(publisher, listener);
		
		assertEquals(initialValue, publisher.get());
		assertEquals(initialValue, observerProperty.get());

		initialValue += "World";
		publisher.set(initialValue);
		assertEquals(initialValue, observerProperty.get());
	}
	
	@After
	public void after() {
		publisher = null;
		observerProperty = null;
		subscriber = null;
		gc(true);
	}
	
	@Test
	public void whenPublisherSet_thenReceiverSet() {
		String value = initialValue + " Again";
		
		gc(false);
		publisher.set(value);
		assertEquals(value, observerProperty.get());
	}
	
	@Test
	public void whenGC_thenStillReceiverSet() {
		String value = initialValue + " Again";
		
		gc(true);
		publisher.set(value);
		assertEquals(value, observerProperty.get());
	}
	
	@Test
	public void whenSubscriberRemoved_thenReceiverNotSet() {
		String value = initialValue + " Again";

		subscriber = null;
		gc(true);
		publisher.set(value);
		assertEquals(initialValue, observerProperty.get());
	}
	
	@Test
	public void whenObserversRemoved_thenReceiverNotSet() {
		String value = initialValue + " Again";
		
		subscriber.removeObservers();
//		gc(true);
		publisher.set(value);
		assertEquals(initialValue, observerProperty.get());
	}
}
