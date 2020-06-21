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

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.ingogriebsch.spring.hateoas.siren.support.PersonModel;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.RepresentationModel;

class SirenEntityClassProviderTest {

    private static final SirenEntityClassProvider provider = new SirenEntityClassProvider() {
    };

    @Test
    void get_should_throw_exception_if_input_is_null() {
        assertThrows(NullPointerException.class, () -> provider.get(null));
    }

    @Test
    void get_should_return_single_class_on_representation_model() {
        assertThat(provider.get(new RepresentationModel<>())).containsExactly("representation");
    }

    @Test
    void get_should_return_single_class_on_object_extending_representation_model() {
        assertThat(provider.get(new PersonModel("Peter", 33))).containsExactly("representation");
    }

    @Test
    void get_should_return_single_class_on_entity_model() {
        assertThat(provider.get(EntityModel.of("content"))).containsExactly("entity");
    }

    @Test
    void get_should_return_single_class_on_collection_model() {
        assertThat(provider.get(CollectionModel.of(newArrayList()))).containsExactly("collection");
    }

    @Test
    void get_should_return_single_class_on_paged_model() {
        assertThat(provider.get(PagedModel.of(newArrayList(), new PageMetadata(0, 0, 0)))).containsExactly("paged");
    }

}
