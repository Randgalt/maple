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
package io.soabase.maple.api.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be used with {@link io.soabase.maple.api.MapleFormatter}s that use Jackson
 * (e.g. {@link io.soabase.maple.formatters.ModelFormatter}. Fields annotated with this
 * will not get logged. For example, mark password fields or any other security sensitive fields. If you use this
 * annotation, you must pass an {@link com.fasterxml.jackson.databind.ObjectMapper} registered via
 * {@link io.soabase.maple.formatters.DoNotLogAnnotationIntrospector#register(com.fasterxml.jackson.databind.ObjectMapper)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@JacksonAnnotation
public @interface DoNotLog {
}
