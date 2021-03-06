/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.internal;

import java.util.function.Supplier;

// Thanks Holger!
// https://stackoverflow.com/questions/35331327/does-java-8-have-cached-support-for-suppliers
public class MemoizingSupplier<T> implements Supplier<T> {
    private final Supplier<T> create;

    private Supplier<T> delegate = this::init;
    private boolean initialized;

    private MemoizingSupplier(Supplier<T> create) {
        this.create = create;
    }

    public static <T> MemoizingSupplier<T> of(Supplier<T> supplier) {
        return new MemoizingSupplier<>(supplier);
    }

    @Override
    public T get() {
        return delegate.get();
    }

    private T init() { // NOPMD
        synchronized (this) {
            if (!initialized) {
                T result = create.get();
                initialized = true;
                delegate = () -> result;
            }
            return delegate.get();
        }
    }
}
