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

import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;

class PagedModelFactoryTest {

    @Test
    void create_should_throw_exception_if_input_is_null() {
        PagedModelFactory factory = new PagedModelFactory() {
        };

        assertThatThrownBy(() -> factory.create(null, null, null, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_should_throw_exception_if_type_is_not_matching() {
        PagedModelFactory factory = new PagedModelFactory() {
        };

        JavaType type = defaultInstance().constructSimpleType(String.class, null);
        List<Link> links = newArrayList();
        List<Object> content = newArrayList();
        PageMetadata metadata = new PageMetadata(0, 0, 0);

        assertThatThrownBy(() -> factory.create(type, links, content, metadata)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_should_return_paged_model_containing_given_links() {
        PagedModelFactory factory = new PagedModelFactory() {
        };

        JavaType type = defaultInstance().constructParametricType(PagedModel.class, String.class);
        Iterable<Link> links = newArrayList(Link.of("href1"), Link.of("href2"));
        PagedModel<?> model = factory.create(type, links, newArrayList(), new PageMetadata(0, 0, 0));

        assertThat(model).isNotNull();
        assertThat(model.getLinks()).isNotNull();
        assertThat(model.getLinks().toList()).isEqualTo(links);
    }
}
