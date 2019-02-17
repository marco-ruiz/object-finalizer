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

import javafx.beans.property.StringProperty;

/**
 * @author Marco Ruiz
 */
public class TestSubscriber implements JFXSafeSubscriber<TestSubscriber> {
	
//	private StringProperty publisher;
//	private StringProperty receiver;
	
	public void subscribe(StringProperty publisher, StringProperty receiver) {
		addListenerSafely(publisher, (ov, oldVal, newVal) -> receiver.set(newVal));
	}
}