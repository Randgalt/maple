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
package io.soabase.maple;

import io.soabase.maple.api.*;
import io.soabase.maple.api.exceptions.InvalidSchemaException;
import io.soabase.maple.api.exceptions.MissingSchemaValueException;
import io.soabase.maple.core.Generator;
import io.soabase.maple.formatters.StandardFormatter;
import io.soabase.maple.schema.*;
import io.soabase.maple.schema.invalid.*;
import io.soabase.maple.spi.MapleSpi;
import io.soabase.maple.spi.MetaInstance;
import io.soabase.maple.spi.StandardNamesBuilder;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TestGeneration {
    private final Generator generator = new Generator();
    private final StandardFormatter formatter = new StandardFormatter();

    @Test
    void testBasicSchemaGeneration() {
        Names names = buildNames(BasicSchema.class);
        MetaInstance<BasicSchema> metaInstance = generate(names, BasicSchema.class);
        assertThat(metaInstance.schemaNames()).isSameAs(names);
        assertThat(metaInstance.formatter().getClass()).isEqualTo(StandardFormatter.class);

        BasicSchema instance = metaInstance.newSchemaInstance();
        instance.name("test").qty(10);
        NamesValues namesValues = metaInstance.toNamesValues(instance);
        assertThat(namesValues.qty()).isEqualTo(2);
        assertThat(namesValues.nthName(0)).isEqualTo("name");
        assertThat(namesValues.nthName(1)).isEqualTo("qty");
        assertThat(namesValues.nthValue(0)).isEqualTo("test");
        assertThat(namesValues.nthValue(1)).isEqualTo(10);
    }

    @Test
    void testInvalidSchema() {
        Stream.of(BadReturnType.class,
                InvalidMethod.class,
                CannotBeAClass.class,
                MustTake1ArgumentB.class,
                MustReturnRightTypeInheritance.class
        ).forEach(clazz -> assertThatThrownBy(() -> buildNames(clazz)).isInstanceOf(InvalidSchemaException.class));
    }

    @Test
    void testMultiMethodDefaults() {
        Names names = buildNames(MultiMethodDefaults.class);
        MetaInstance<MultiMethodDefaults> metaInstance = generate(names, MultiMethodDefaults.class);

        MultiMethodDefaults instance = metaInstance.newSchemaInstance();
        instance.name("n").foo(10).name(10.20).name("hey");
        NamesValues namesValues = metaInstance.toNamesValues(instance);
        assertThat(namesValues.qty()).isEqualTo(1);
        assertThat(namesValues.nthName(0)).isEqualTo("name");
        assertThat(namesValues.nthValue(0)).isEqualTo("hey");

        instance = metaInstance.newSchemaInstance();
        instance.name("n").foo(10).name(10.20);
        namesValues = metaInstance.toNamesValues(instance);
        assertThat(namesValues.qty()).isEqualTo(1);
        assertThat(namesValues.nthName(0)).isEqualTo("name");
        assertThat(namesValues.nthValue(0)).isEqualTo("foo(double): " + 10.20);
    }

    @Test
    void testRequired() {
        Names names = buildNames(HasRequired.class);
        assertThat(names.nthSpecializations(1).contains(Specialization.REQUIRED)).isTrue();

        MetaInstance<HasRequired> metaInstance = generate(names, HasRequired.class);
        HasRequired instance = metaInstance.newSchemaInstance();
        instance.name("test");

        boolean saveProductionMode = MapleSpi.instance().getProductionMode();
        try {
            MapleSpi.instance().setProductionMode(false);
            NamesValues namesValues = metaInstance.toNamesValues(instance);
            assertThatThrownBy(() -> MapleSpi.instance().applySpecializations(namesValues)).isInstanceOf(MissingSchemaValueException.class);
        } catch (Exception e) {
            MapleSpi.instance().setProductionMode(saveProductionMode);
        }
    }

    @Test
    void testMdcDefaultValue() {
        Names names = buildNames(HasMdcDefault.class);
        assertThat(names.nthSpecializations(0).contains(Specialization.DEFAULT_FROM_MDC)).isTrue();
    }

    @Test
    void testDuplicates() {
        Names names = buildNames(Duplicates.class);
        assertThat(names.qty()).isEqualTo(2);
        assertThat(names.nthName(0)).isEqualTo("id");
        assertThat(names.nthName(1)).isEqualTo("id");
        assertThat(names.nthRawName(0)).isNotEqualTo(names.nthRawName(1));
    }

    @Test
    void testSortOrder() {
        Names names = buildNames(HasSortOrder.class);
        assertThat(names.qty()).isEqualTo(4);
        assertThat(names.nthName(0)).isEqualTo("d");
        assertThat(names.nthName(1)).isEqualTo("a");
        assertThat(names.nthName(2)).isEqualTo("b");
        assertThat(names.nthName(3)).isEqualTo("c");
    }

    @Test
    void testStreamingNamesValues() {
        Names names = buildNames(BasicSchema.class);
        MetaInstance<BasicSchema> metaInstance = generate(names, BasicSchema.class);
        Statement<BasicSchema> statement = s -> s.name("n").qty(10);
        String result = statement.toNamesValues(metaInstance).stream()
                .map(nameValue -> nameValue.name() + "=" + nameValue.value())
                .collect(Collectors.joining("|"));
        assertThat(result).isEqualTo("name=n|qty=10");
    }

    @Test
    void testCaching() {
        Names names = buildNames(BasicSchema.class);
        MetaInstance<BasicSchema> metaInstance1 = generate(names, BasicSchema.class);
        MetaInstance<BasicSchema> metaInstance2 = generate(names, BasicSchema.class);
        assertThat(metaInstance1).isSameAs(metaInstance2);

        generator.clearCache();

        MetaInstance<BasicSchema> metaInstance3 = generate(names, BasicSchema.class);
        assertThat(metaInstance3).isNotSameAs(metaInstance1);
        assertThat(metaInstance3).isNotSameAs(metaInstance2);
    }

    @Test
    void testCachingWithFormatters() {
        Names names = buildNames(BasicSchema.class);

        MapleFormatter altFormatter = (logger, loggerName, namesValues, mainMessage, t) -> {};

        MetaInstance<BasicSchema> metaInstance1 = generator.generate(names, BasicSchema.class, ClassLoader.getSystemClassLoader(), formatter);
        MetaInstance<BasicSchema> metaInstance2 = generator.generate(names, BasicSchema.class, ClassLoader.getSystemClassLoader(), altFormatter);
        assertThat(metaInstance1).isNotSameAs(metaInstance2);
        assertThat(metaInstance1.newSchemaInstance().getClass()).isSameAs(metaInstance2.newSchemaInstance().getClass());

        generator.clearCache();

        MetaInstance<BasicSchema> metaInstance3 = generator.generate(names, BasicSchema.class, ClassLoader.getSystemClassLoader(), formatter);
        assertThat(metaInstance3).isNotSameAs(metaInstance1);
        assertThat(metaInstance3).isNotSameAs(metaInstance2);
        assertThat(metaInstance3.newSchemaInstance().getClass()).isNotSameAs(metaInstance1.newSchemaInstance().getClass());
        assertThat(metaInstance3.newSchemaInstance().getClass()).isNotSameAs(metaInstance2.newSchemaInstance().getClass());
    }

    private <T> MetaInstance<T> generate(Names names, Class<T> clazz) {
        return generator.generate(names, clazz, ClassLoader.getSystemClassLoader(), formatter);
    }

    private Names buildNames(Class<?> clazz) {
        return StandardNamesBuilder.build(clazz, Generator.getReservedMethodNames());
    }
}
