/**
 * Copyright 2019 Jordan Zimmerman
 *
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
package io.soabase.maple.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

class Loader<T> {
    private final T instance;

    Loader(Class<T> clazz, T defaultValue) {
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        Iterator<T> iterator = loader.iterator();
        instance = iterator.hasNext() ? iterator.next() : defaultValue;
        if (iterator.hasNext()) {
            System.err.println("Service Loader found multiple instances. Using: " + instance.getClass().getName());
        }
    }

    T instance() {
        return instance;
    }
}
