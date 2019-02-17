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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Marco Ruiz
 */
public class FieldsAccessor {
	
	private Object target;

	public FieldsAccessor(Object target) {
		this.target = target;
	}
	
	public List<Field> getFieldsList() {
		return Arrays.asList(target.getClass().getDeclaredFields());
	}
	
	public Set<Object> getReferences() {
		return getFieldsList().stream().map(field -> getFieldValue(field, false)).collect(Collectors.toSet());
	}

	public Object getFieldValue(Field field, boolean permanentlyAccessible) {
		try {
			boolean accessible = field.isAccessible();
			if (!accessible)
				field.setAccessible(true);
			Object result = field.get(target);
			if (!accessible && !permanentlyAccessible) 
				field.setAccessible(false);
			return result;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setFieldValue(Field field, Object value) {
		try {
			field.set(target, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
