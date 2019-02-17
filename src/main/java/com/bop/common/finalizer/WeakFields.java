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
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marco Ruiz
 */
public class WeakFields {
	
	private FieldsAccessor fieldsAccessor;
	private Map<Field, WeakReference<Object>> dependencies;

	public WeakFields(Object target, Object... referencesToWeaken) {
		fieldsAccessor = new FieldsAccessor(target);
		List<Object> toWeaken = Stream.of(referencesToWeaken)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		dependencies = fieldsAccessor.getFieldsList().stream()
				.filter(field -> toWeaken.contains(fieldsAccessor.getFieldValue(field, false)))
				.collect(Collectors.toMap(
						Function.identity(), 
						field -> new WeakReference<>(fieldsAccessor.getFieldValue(field, true))));
	}

	public void clearWeakFields() {
		dependencies.entrySet().forEach(dep -> fieldsAccessor.setFieldValue(dep.getKey(), null));
	}
	
	public void resetWeakFields() {
		dependencies.entrySet().forEach(dep -> fieldsAccessor.setFieldValue(dep.getKey(), dep.getValue().get()));
	}
	
	public boolean areWeakFieldsValid() {
		return dependencies.values().stream()
				.map(WeakReference::get)
				.allMatch(Objects::nonNull); 
	}
}
