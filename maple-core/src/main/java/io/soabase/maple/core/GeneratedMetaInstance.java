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
package io.soabase.maple.core;

import io.soabase.maple.api.MapleFormatter;
import io.soabase.maple.api.Names;
import io.soabase.maple.api.NamesValues;
import io.soabase.maple.spi.MetaInstance;

class GeneratedMetaInstance<T> implements MetaInstance<T> {
    private final Class<T> generatedClass;
    private final InstanceFactory<T> instanceFactory;
    private final Names names;
    private final MapleFormatter formatter;

    GeneratedMetaInstance(Class<T> generatedClass, InstanceFactory<T> instanceFactory, Names names, MapleFormatter formatter) {
        this.generatedClass = generatedClass;
        this.instanceFactory = instanceFactory;
        this.names = names;
        this.formatter = formatter;
    }

    @Override
    public T newSchemaInstance() {
        try {
            T instance = instanceFactory.newInstance();
            ((Instance) instance).arguments = new Object[names.qty()];
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Could not allocate schema instance: " + generatedClass.getName(), e);
        }
    }

    @Override
    public Names schemaNames() {
        return names;
    }

    @Override
    public MapleFormatter formatter() {
        return formatter;
    }

    @Override
    public NamesValues toNamesValues(T instance) {
        Object[] arguments = ((Instance) instance).arguments;
        return new NamesValues() {
            @Override
            public int qty() {
                return names.qty();
            }

            @Override
            public String nthName(int n) {
                return names.nthName(n);
            }

            @Override
            public Object nthValue(int n) {
                return arguments[n];
            }

            @Override
            public boolean nthIsRequired(int n) {
                return names.nthIsRequired(n);
            }
        };
    }
}
