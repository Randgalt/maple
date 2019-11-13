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

import io.soabase.maple.api.Names;
import io.soabase.maple.api.Specialization;
import io.soabase.maple.api.annotations.MdcDefaultValue;
import io.soabase.maple.api.annotations.Required;
import io.soabase.maple.api.annotations.SortOrder;
import io.soabase.maple.api.exceptions.InvalidSchemaException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class StandardNamesBuilder {
    public static Names build(Class<?> schemaClass, Collection<String> reservedNames) {
        if (!schemaClass.isInterface()) {
            throw new InvalidSchemaException("Schema must be an interface. Schema: " + schemaClass.getName());
        }

        class Entry implements Comparable<Entry> {
            private final String name;
            private final String rawName;
            private final Set<Specialization> specializations;
            private final int sortValue;

            private Entry(String name, String rawName, Set<Specialization> specializations, int sortValue) {
                this.name = name;
                this.rawName = rawName;
                this.specializations = Collections.unmodifiableSet(specializations);
                this.sortValue = sortValue;
            }

            @Override
            public int compareTo(Entry o) {
                int diff = sortValue - o.sortValue;
                if (diff == 0) {
                    diff = name.compareTo(o.name);
                }
                return diff;
            }
        }

        List<Entry> entries = new ArrayList<>();
        for (Method method : schemaClass.getMethods()) {
            if (method.isBridge() || method.isSynthetic() || method.isDefault() || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (!method.getReturnType().isAssignableFrom(schemaClass)) {
                throw new InvalidSchemaException("Schema methods must return " + schemaClass.getSimpleName() +
                        " or a subclass of it. Method: " + method.getName());
            }
            if (method.getParameterCount() != 1) {
                throw new InvalidSchemaException("Schema methods must take exactly 1 argument. Method: " + method.getName());
            }
            if (reservedNames.contains(method.getName())) {
                throw new InvalidSchemaException("Schema method name is reserved for internal use. Name: " + method.getName());
            }

            Set<Specialization> specializations = new HashSet<>();
            if (method.getAnnotation(Required.class) != null) {
                specializations.add(Specialization.REQUIRED);
            }
            if (method.getAnnotation(MdcDefaultValue.class) != null) {
                specializations.add(Specialization.DEFAULT_FROM_MDC);
            }

            SortOrder sortOrder = method.getAnnotation(SortOrder.class);
            int sortOrderValue = (sortOrder != null) ? sortOrder.value() : Short.MAX_VALUE;
            entries.add(new Entry(method.getName(), method.toGenericString(), specializations, sortOrderValue));
        }
        Collections.sort(entries);
        return new Names() {
            @Override
            public String nthName(int n) {
                return entries.get(n).name;
            }

            @Override
            public String nthRawName(int n) {
                return entries.get(n).rawName;
            }

            @Override
            public int qty() {
                return entries.size();
            }

            @Override
            public Set<Specialization> nthSpecializations(int n) {
                return entries.get(n).specializations;
            }
        };
    }

    private StandardNamesBuilder() {
    }
}
