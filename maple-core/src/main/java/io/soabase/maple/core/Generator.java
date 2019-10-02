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
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.bytebuddy.implementation.MethodCall.invoke;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

public class Generator {
    private final Map<Key, GeneratedMetaInstance> metaInstanceCache = new ConcurrentHashMap<>();
    private final Map<Class, Generated> generatedCache = new ConcurrentHashMap<>();
    private static final Set<String> reservedMethodNames = Collections.unmodifiableSet(
            Stream.of(Instance.class.getMethods()).map(Method::getName).collect(Collectors.toSet())
    );
    private static final Method setValueAtIndexMethod;

    static {
        try {
            setValueAtIndexMethod = Instance.class.getMethod("internalSetValueAtIndex", Integer.TYPE, Object.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find internal method", e);
        }
    }

    public static Set<String> getReservedMethodNames() {
        return reservedMethodNames;
    }

    public void clearCache() {
        metaInstanceCache.clear();
        generatedCache.clear();
    }

    private static class Generated<T> {
        final Class generatedClass;
        final InstanceFactory<T> instanceFactory;

        Generated(Class generatedClass, InstanceFactory<T> instanceFactory) {
            this.generatedClass = generatedClass;
            this.instanceFactory = instanceFactory;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> GeneratedMetaInstance<T> generate(Names names, Class<T> schemaClass, ClassLoader classLoader, MapleFormatter formatter) {
        return metaInstanceCache.computeIfAbsent(new Key(schemaClass, formatter), __ -> {
            Generated<T> generated = generatedCache.computeIfAbsent(schemaClass, ___ -> {
                ByteBuddy byteBuddy = new ByteBuddy();
                Class generatedClass = internalGenerate(byteBuddy, schemaClass, classLoader, toSet(names));
                InstanceFactory<T> instanceFactory = generateInstanceFactory(byteBuddy, generatedClass);
                return new Generated(generatedClass, instanceFactory);
            });
            return new GeneratedMetaInstance<>(generated.generatedClass, generated.instanceFactory, names, formatter);
        });
    }

    private List<String> toSet(Names names) {
        return IntStream.range(0, names.qty())
                .mapToObj(names::nthName)
                .collect(Collectors.toList());
    }

    private Class internalGenerate(ByteBuddy byteBuddy, Class schemaClass, ClassLoader classLoader, List<String> names) {
        DynamicType.Builder builder = byteBuddy.subclass(Instance.class).implement(schemaClass);
        for (Method method : schemaClass.getMethods()) {
            if (method.isBridge() || method.isSynthetic() || method.isDefault() || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            int thisIndex = names.indexOf(method.getName());
            Implementation methodCall = invoke(setValueAtIndexMethod)
                    .with(thisIndex)
                    .withArgument(0)
                    .andThen(FixedValue.self());
            ElementMatcher<?> matcher = named(method.getName())
                    .and(takesArguments(method.getParameterTypes()[0]));
            builder = builder.method(matcher).intercept(methodCall);
        }
        return builder.make().load(classLoader).getLoaded();
    }

    private <T> InstanceFactory generateInstanceFactory(ByteBuddy byteBuddy, Class<T> clazz) {
        try {
            DynamicType.Builder<InstanceFactory> builder = byteBuddy
                    .subclass(InstanceFactory.class)
                    .method(named("newInstance")).intercept(MethodDelegation.toConstructor(clazz));
            return builder.make().load(clazz.getClassLoader()).getLoaded().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't not create InstanceFactory for: " + clazz.getName(), e);
        }
    }
}
