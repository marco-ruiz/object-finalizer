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
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marco Ruiz
 */
public abstract class AbstractCommandWithWeakArgs implements Runnable {

	public static List<WeakReference<Object>> createWeakReferences(Object... args) {
		return Stream.of(args)
			.map(arg -> (arg == null) ? null : new WeakReference<Object>(arg))
			.collect(Collectors.toList());
	}
	
	protected List<WeakReference<Object>> weakRefs = new ArrayList<>();

	public AbstractCommandWithWeakArgs(Object... args) {
		weakRefs = createWeakReferences(args);
	}

	public void run() {
		boolean allArgsAvailable = weakRefs.stream()
				.filter(Objects::nonNull)
				.map(WeakReference::get)
				.allMatch(Objects::nonNull);
		
		if (allArgsAvailable) {
			List<Object> refs = weakRefs.stream()
					.map(weak -> (weak == null) ? null : weak.get())
					.collect(Collectors.toList());
			
			runWithArgs(refs);
		}
	}

	protected abstract void runWithArgs(List<Object> refs);
	
	@SuppressWarnings("unchecked")
	protected <ARG_T> ARG_T getCasted(List<Object> list, int index) {
		return (ARG_T) list.get(index);
	}

	//===============================
	// FOUR ARGUMENTS VERSION
	//===============================
	
	@FunctionalInterface
	public static interface ConsumerWithArgs4<ARG1_T, ARG2_T, ARG3_T, ARG4_T> {
		void process(ARG1_T arg1, ARG2_T arg2, ARG3_T arg3, ARG4_T arg4);
	}
	
	public static class CmdWithWeakArgs4<ARG1_T, ARG2_T, ARG3_T, ARG4_T> extends AbstractCommandWithWeakArgs {
		
		private ConsumerWithArgs4<ARG1_T, ARG2_T, ARG3_T, ARG4_T> commandWithArgs;

		public CmdWithWeakArgs4(ConsumerWithArgs4<ARG1_T, ARG2_T, ARG3_T, ARG4_T> cmdConsumer, ARG1_T arg1, ARG2_T arg2, ARG3_T arg3, ARG4_T arg4) {
			super(arg1, arg2, arg3, arg4);
			commandWithArgs = cmdConsumer;
		}

		protected void runWithArgs(List<Object> refs) {
			commandWithArgs.process(getCasted(refs, 0), getCasted(refs, 1), getCasted(refs, 2), getCasted(refs, 3));
		}
	}
	
	//===============================
	// THREE ARGUMENTS VERSION
	//===============================
	
	@FunctionalInterface
	public static interface ConsumerWithArgs3<ARG1_T, ARG2_T, ARG3_T> {
		void process(ARG1_T arg1, ARG2_T arg2, ARG3_T arg3);
	}
	
	public static class CmdWithWeakArgs3<ARG1_T, ARG2_T, ARG3_T> extends AbstractCommandWithWeakArgs {
		
		private ConsumerWithArgs3<ARG1_T, ARG2_T, ARG3_T> commandWithArgs;

		public CmdWithWeakArgs3(ConsumerWithArgs3<ARG1_T, ARG2_T, ARG3_T> cmdConsumer, ARG1_T arg1, ARG2_T arg2, ARG3_T arg3) {
			super(arg1, arg2, arg3);
			commandWithArgs = cmdConsumer;
		}

		protected void runWithArgs(List<Object> refs) {
			commandWithArgs.process(getCasted(refs, 0), getCasted(refs, 1), getCasted(refs, 2));
		}
	}
	
	//===============================
	// TWO ARGUMENTS VERSION
	//===============================
	
	public static class CmdWithWeakArgs2<ARG1_T, ARG2_T> extends AbstractCommandWithWeakArgs {
		
		private BiConsumer<ARG1_T, ARG2_T> commandWithArgs;

		public CmdWithWeakArgs2(BiConsumer<ARG1_T, ARG2_T> cmdConsumer, ARG1_T arg1, ARG2_T arg2) {
			super(arg1, arg2);
			commandWithArgs = cmdConsumer;
		}

		protected void runWithArgs(List<Object> refs) {
			commandWithArgs.accept(getCasted(refs, 0), getCasted(refs, 1));
		}
	}
	
	//===============================
	// ONE ARGUMENT VERSION
	//===============================
	
	public static class CmdWithWeakArgs1<ARG1_T> extends AbstractCommandWithWeakArgs {
		
		private Consumer<ARG1_T> commandWithArgs;

		public CmdWithWeakArgs1(Consumer<ARG1_T> cmdConsumer, ARG1_T arg1) {
			super(arg1);
			commandWithArgs = cmdConsumer;
		}

		protected void runWithArgs(List<Object> refs) {
			commandWithArgs.accept(getCasted(refs, 0));
		}
	}
}
