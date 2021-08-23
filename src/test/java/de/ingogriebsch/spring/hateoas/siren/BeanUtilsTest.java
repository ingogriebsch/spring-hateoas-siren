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
package de.ingogriebsch.spring.hateoas.siren;

import static java.util.Collections.singletonMap;

import static de.ingogriebsch.spring.hateoas.siren.BeanUtils.applyProperties;
import static de.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BeanUtilsTest {

    @Nested
    class Instantiate {

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
    class ApplyProperties {

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
