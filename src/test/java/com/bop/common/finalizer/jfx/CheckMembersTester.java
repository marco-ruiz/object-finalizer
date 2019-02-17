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

import com.bop.common.finalizer.ProxyWithWeakReferences;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


/**
 * @author Marco Ruiz
 */
public class CheckMembersTester {
	
	public static CheckMembersTester getThis() {
		return null;
	}
	
	
	public static void main(String[] args) {
		new CheckMembersTester().test();
		
	}


	private void test() {
//		ChangeListener<String> listener = (ov, oldVal, newVal) -> this.print(newVal);
		ChangeListener<String> listener = this::changed;
//		ChangeListener<String> listener = new CL(this);
		
//		ChangeListener<String> proxy = WeakReferencingProxy.create(listener, ChangeListener.class, this);
		tryChanged(listener);
		ChangeListener<String> proxy = ProxyWithWeakReferences.create(listener, this);
		tryChanged(listener);
		
		ChangeListener<String> listener2 = (ov, oldVal, newVal) -> getThis().print(newVal);
		tryChanged(proxy);
		tryChanged(listener);
	}


	private void tryChanged(ChangeListener<String> listener) {
		try {
			listener.changed(null, null, String.valueOf(listener));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		this.print(newValue);
	}
	
	public void print(String msg) {
		System.out.println(msg);
	}
	
	
}

