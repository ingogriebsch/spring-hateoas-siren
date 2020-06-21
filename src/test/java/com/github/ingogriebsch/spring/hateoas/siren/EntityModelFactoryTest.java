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

import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JavaType;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

class EntityModelFactoryTest {

    @Test
    void create_should_throw_exception_if_input_is_null() {
        EntityModelFactory factory = new EntityModelFactory() {
        };

        assertThatThrownBy(() -> factory.create(null, null, null, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void create_should_throw_exception_if_type_is_not_matching() {
        EntityModelFactory factory = new EntityModelFactory() {
        };

        JavaType type = defaultInstance().constructSimpleType(String.class, null);
        assertThatThrownBy(() -> factory.create(type, newArrayList(), new Object(), null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_should_return_entity_model_containing_given_links() {
        EntityModelFactory factory = new EntityModelFactory() {
        };

        JavaType type = defaultInstance().constructParametricType(EntityModel.class, String.class);
        Iterable<Link> links = newArrayList(Link.of("href1"), Link.of("href2"));
        EntityModel<?> model = factory.create(type, links, "content", null);

        assertThat(model).isNotNull();
        assertThat(model.getLinks()).isNotNull();
        assertThat(model.getLinks().toList()).isEqualTo(links);
    }

}
