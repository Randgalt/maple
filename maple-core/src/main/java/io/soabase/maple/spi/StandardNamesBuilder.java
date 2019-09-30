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

import io.soabase.maple.api.exceptions.InvalidSchemaException;
import io.soabase.maple.api.Names;
import io.soabase.maple.api.annotations.Required;
import io.soabase.maple.api.annotations.SortOrder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class StandardNamesBuilder {
    public static Names build(Class<?> schemaClass, Collection<String> reservedNames) {
        if (!schemaClass.isInterface()) {
            throw new InvalidSchemaException("Schema must be an interface. Schema: " + schemaClass.getName());
        }

        Set<String> requiredNames = new HashSet<>();
        List<String> schemaNames = new ArrayList<>();
        Set<String> usedMethods = new HashSet<>();
        Map<String, Integer> schemaNameToSortOrder = new HashMap<>();
        for (Method method : schemaClass.getMethods()) {
            if (method.isBridge() || method.isSynthetic() || method.isDefault() || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (isUsedMethod(usedMethods, method)) {
                throw new InvalidSchemaException("Schema method names must be unique. Duplicate: " + method.getName());
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
            if (method.getAnnotation(Required.class) != null) {
                requiredNames.add(method.getName());
            }
            schemaNames.add(method.getName());

            SortOrder sortOrder = method.getAnnotation(SortOrder.class);
            int sortOrderValue = (sortOrder != null) ? sortOrder.value() : Short.MAX_VALUE;
            schemaNameToSortOrder.put(method.getName(), sortOrderValue);
        }
        schemaNames.sort((name1, name2) -> compareSchemaNames(schemaNameToSortOrder, name1, name2));
        Set<Integer> requireds = requiredNames.stream().map(schemaNames::indexOf).collect(Collectors.toSet());
        return new Names() {
            @Override
            public String nthName(int n) {
                return schemaNames.get(n);
            }

            @Override
            public int qty() {
                return schemaNames.size();
            }

            @Override
            public boolean nthIsRequired(int n) {
                return requireds.contains(n);
            }
        };
    }

    private static boolean isUsedMethod(Set<String> usedMethods, Method method) {
        if (method.getParameterCount() == 1) {
            return !usedMethods.add(method.getName());
        }
        return false;
    }

    private static int compareSchemaNames(Map<String, Integer> schemaNameToSortOrder, String name1, String name2) {
        int sortValue1 = schemaNameToSortOrder.get(name1);
        int sortValue2 = schemaNameToSortOrder.get(name2);
        int diff = sortValue1 - sortValue2;
        if (diff == 0) {
            diff = name1.compareTo(name2);
        }
        return diff;
    }

    private StandardNamesBuilder() {
    }
}
