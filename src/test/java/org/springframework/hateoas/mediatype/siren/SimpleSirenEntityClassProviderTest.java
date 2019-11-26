/*-
 * #%L
 * Spring HATEOAS Siren sample
 * %%
 * Copyright (C) 2018 - 2019 Ingo Griebsch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.springframework.hateoas.mediatype.siren;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.support.EmployeeResource;

class SimpleSirenEntityClassProviderTest {

    private static final SirenEntityClassProvider sirenEntityClassProvider = new SimpleSirenEntityClassProvider();

    @Test
    void get_should_throw_exception_if_input_is_null() {
        assertThrows(IllegalArgumentException.class, () -> sirenEntityClassProvider.get(null));
    }

    @Test
    void get_should_return_single_class_on_representation_model() {
        assertThat(sirenEntityClassProvider.get(new RepresentationModel<>())).containsExactly("representation");
    }

    @Test
    void get_should_return_single_class_on_object_extending_representation_model() {
        assertThat(sirenEntityClassProvider.get(new EmployeeResource("Peter"))).containsExactly("representation");
    }

    @Test
    void get_should_return_single_class_on_entity_model() {
        assertThat(sirenEntityClassProvider.get(new EntityModel<>("content"))).containsExactly("entity");
    }

    @Test
    void get_should_return_single_class_on_collection_model() {
        assertThat(sirenEntityClassProvider.get(new CollectionModel<>(newArrayList()))).containsExactly("collection");
    }

    @Test
    void get_should_return_single_class_on_paged_model() {
        assertThat(sirenEntityClassProvider.get(new PagedModel<>(newArrayList(), new PageMetadata(0, 0, 0))))
            .containsExactly("paged");
    }

}
