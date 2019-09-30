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
package io.soabase.maple.formatters;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.soabase.maple.api.annotations.DoNotLog;

/**
 * You just add an instance of this to the {@link com.fasterxml.jackson.databind.ObjectMapper}
 * used for your Jackson-based {@link io.soabase.maple.api.MapleFormatter}.
 *
 * @see DoNotLog
 */
public class DoNotLogAnnotationIntrospector extends AnnotationIntrospector {
    /**
     * Register a DoNotLogAnnotationIntrospector in the given mapper
     *
     * @param mapper mapper to register
     */
    public static void register(ObjectMapper mapper) {
        Module module = new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.appendAnnotationIntrospector(new DoNotLogAnnotationIntrospector());
            }
        };
        mapper.registerModule(module);
    }

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return m.hasAnnotation(DoNotLog.class);
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }
}
