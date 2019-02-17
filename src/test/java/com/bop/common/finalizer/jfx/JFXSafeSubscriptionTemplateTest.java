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

/**
 * @author Marco Ruiz
 */
public class JFXSafeSubscriptionTemplateTest implements JFXSafeSubscriber<JFXSafeSubscriptionTemplateTest> {

	private static void sleep(boolean gc) {
		if (gc) System.gc();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
	}
	
	private String initialValue = "World ";
	
	private StringProperty publisher;
	private StringProperty receiver;
	private TestSubscriber subscriber;

	@Before
	public void before() {
		publisher = new SimpleStringProperty(initialValue);
		receiver = new SimpleStringProperty(initialValue);
		subscriber = new TestSubscriber();
		subscriber.subscribe(publisher, receiver);
		assertEquals(initialValue, publisher.get());
		assertEquals(initialValue, receiver.get());

		initialValue += "Hello";
		publisher.set(initialValue);
		assertEquals(initialValue, receiver.get());
	}
	
	@After
	public void after() {
		publisher = null;
		receiver = null;
		subscriber = null;
		sleep(true);
	}
	
	@Test
	public void whenPublisherSet_thenReceiverSet() {
		String value = initialValue + " Again";
		
		sleep(false);
		publisher.set(value);
		assertEquals(value, receiver.get());
	}
	
	@Test
	public void whenGC_thenStillReceiverSet() {
		String value = initialValue + " Again";
		
		sleep(true);
		publisher.set(value);
		assertEquals(value, receiver.get());
	}
	
	@Test
	public void whenSubscriberRemoved_thenReceiverNotSet() {
		String value = initialValue + " Again";

		subscriber = null;
		sleep(true);
		publisher.set(value);
		assertEquals(initialValue, receiver.get());
	}
	
	@Test
	public void whenObserversRemoved_thenReceiverNotSet() {
		String value = initialValue + " Again";
		
		subscriber.removeObservers();
		publisher.set(value);
		assertEquals(initialValue, receiver.get());
	}
}
