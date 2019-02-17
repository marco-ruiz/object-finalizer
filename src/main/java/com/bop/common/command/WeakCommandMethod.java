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
import java.util.function.BiConsumer;

/**
 * @author Marco Ruiz
 */
public class WeakCommandMethod<OWNER_T> extends WeakCommand {
	
	protected WeakReference<OWNER_T> weakOwner;
	BiConsumer<OWNER_T, CastedList> command;
	
	@SafeVarargs
	public WeakCommandMethod(BiConsumer<OWNER_T, CastedList> command, OWNER_T owner, Object... dependencies) {
		super(null, dependencies);
		this.command = command;
		this.weakOwner = new WeakReference<>(owner);
	}
	
	public void run() {
		OWNER_T owner = weakOwner.get();
		CastedList depList = extractDependenciesListFromWeaks();
		if (owner != null && !depList.contains(null))
			command.accept(owner, depList);
	}
}
