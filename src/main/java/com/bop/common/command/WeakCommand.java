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

package com.bop.common.command;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marco Ruiz
 */
public class WeakCommand implements Runnable {
	
	private final Consumer<CastedList> command;
	protected final List<WeakReference<?>> dependencies = new ArrayList<>();

	@SafeVarargs
	public WeakCommand(Consumer<CastedList> command, Object... dependencies) {
		this.command = command;
		addDependencies(dependencies);
	}
	
	public void addDependencies(Object... dependencies) {
		this.dependencies.addAll(Stream.of(dependencies).map(WeakReference::new).collect(Collectors.toList()));
	}

	public void run() {
		CastedList depList = extractDependenciesListFromWeaks();
		if (!depList.contains(null))
			command.accept(depList);
	}

	protected CastedList extractDependenciesListFromWeaks() {
		return dependencies.stream()
				.map(WeakReference::get)
				.collect(CastedList::new, CastedList::add, CastedList::addAll);
	}
	
	@SuppressWarnings("serial")
	public static class CastedList extends ArrayList<Object> {
		
		@SuppressWarnings("unchecked")
		public <RET_T> RET_T getCasted(int index) {
			return (RET_T) get(index);
		}
	}
	
}
