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

import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import de.ingogriebsch.spring.hateoas.siren.support.Country;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

class CollectionModelFactoryTest {

    @Test
    void create_should_throw_exception_if_input_is_null() {
        CollectionModelFactory factory = new CollectionModelFactory() {
        };

        assertThatThrownBy(() -> factory.create(null, null, null, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_should_throw_exception_if_type_is_not_matching() {
        CollectionModelFactory factory = new CollectionModelFactory() {
        };

        JavaType type = defaultInstance().constructSimpleType(String.class, null);
        assertThatThrownBy(() -> factory.create(type, newArrayList(), newArrayList(), null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_should_return_collection_model_containing_given_links() {
        CollectionModelFactory factory = new CollectionModelFactory() {
        };

        JavaType type = defaultInstance().constructParametricType(CollectionModel.class, String.class);
        Iterable<Link> links = newArrayList(Link.of("href1"), Link.of("href2"));
        CollectionModel<?> model = factory.create(type, links, newArrayList(), null);

        assertThat(model).isNotNull();
        assertThat(model.getLinks()).isNotNull();
        assertThat(model.getLinks().toList()).isEqualTo(links);
    }

    @Test
    void create_should_return_collection_model_having_given_properties() {
        CollectionModelFactory factory = new CollectionModelFactory() {
        };

        JavaType type = defaultInstance().constructSimpleType(Country.class, null);
        Map<String, Object> properties = singletonMap("name", "America");
        CollectionModel<?> model = factory.create(type, newArrayList(), newArrayList(), properties);

        assertThat(model).isNotNull().asInstanceOf(type(Country.class)).hasFieldOrPropertyWithValue("name", "America");
    }

    @Test
    void create_should_return_collection_model_not_having_properties_if_properties_not_given() {
        CollectionModelFactory factory = new CollectionModelFactory() {
        };

        JavaType type = defaultInstance().constructSimpleType(Country.class, null);
        CollectionModel<?> model = factory.create(type, newArrayList(), newArrayList(), null);

        assertThat(model).isNotNull().asInstanceOf(type(Country.class)).hasAllNullFieldsOrPropertiesExcept("content", "links");
    }
}
