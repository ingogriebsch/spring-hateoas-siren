/*-
 * #%L
 * Spring HATEOAS Siren
 * %%
 * Copyright (C) 2018 - 2020 Ingo Griebsch
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
package com.github.ingogriebsch.spring.hateoas.siren;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.support.Employee;

class SirenEntityTest {

    @Nested
    class Builder {

        @Test
        void should_return_instance_having_null_or_empty_or_default_references_if_not_defined_explicitely() {
            SirenEntity sirenEntity = SirenEntity.builder().build();
            assertThat(sirenEntity.getActions()).isEmpty();
            assertThat(sirenEntity.getClasses()).isNull();
            assertThat(sirenEntity.getEntities()).isEmpty();
            assertThat(sirenEntity.getLinks()).isEmpty();
            assertThat(sirenEntity.getProperties()).isNull();
            assertThat(sirenEntity.getRels()).isEmpty();
            assertThat(sirenEntity.getTitle()).isNull();
        }
    }

    @Nested
    class TitleResolvable {

        @Test
        void should_return_codes_respecting_given_name() {
            SirenEntity.TitleResolvable titleResolvable = SirenEntity.TitleResolvable.of(Employee.class);
            assertThat(titleResolvable.getCodes()).containsExactly("_entity." + Employee.class.getName() + ".title",
                "_entity." + Employee.class.getSimpleName() + ".title", "_entity.default.title");
        }
    }

}
