/*-
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ingogriebsch.spring.hateoas.siren;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.applyProperties;
import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.extractProperties;
import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import com.github.ingogriebsch.spring.hateoas.siren.support.Capital;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Data;

class BeanUtilsTest {

    @Nested
    class Instantiate {

        @Test
        void should_throw_exception_if_input_is_null() {
            assertThatThrownBy(() -> instantiate(null, null, null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_create_string_object() {
            String expected = "String";
            assertThat(instantiate(String.class, new Class[] { String.class }, new Object[] { expected })).isEqualTo(expected);
        }

        @Test
        void should_create_pojo() {
            Pojo pojo = new Pojo("Peter");
            assertThat(instantiate(Pojo.class, new Class[] { String.class }, new Object[] { pojo.getName() })).isEqualTo(pojo);
        }
    }

    @Nested
    class ExtractProperties {

        @Test
        void should_throw_exception_if_input_is_null() {
            assertThatThrownBy(() -> extractProperties(null, (String[]) null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_return_map_containing_properties() {
            Capital object = new Capital("Sacramento");
            Map<String, Object> propertyValues =
                of(new SimpleEntry<>("links", newArrayList()), new SimpleEntry<>("name", object.getName()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            assertThat(extractProperties(object)).isEqualTo(propertyValues);
        }

        @Test
        void should_return_map_containing_properties_filtered_by_excludes() {
            Capital object = new Capital("Sacramento");
            Map<String, Object> propertyValues =
                of(new SimpleEntry<>("name", object.getName())).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            assertThat(extractProperties(object, "links")).isEqualTo(propertyValues);
        }
    }

    @Nested
    class ApplyProperties {

        @Test
        void should_throw_exception_if_input_is_null() {
            assertThatThrownBy(() -> applyProperties(null, null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_set_simple_property_on_pojo() {
            Pojo pojo = new Pojo("Paul");
            assertThat(applyProperties(new Pojo("Peter"), singletonMap("name", pojo.getName()))).isEqualTo(pojo);
        }
    }

    @AllArgsConstructor
    @Data
    private static class Pojo {

        private String name;
    }
}
