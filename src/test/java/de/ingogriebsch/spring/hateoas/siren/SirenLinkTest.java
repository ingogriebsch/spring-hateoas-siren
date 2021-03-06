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

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.hateoas.IanaLinkRelations.ABOUT;

import de.ingogriebsch.spring.hateoas.siren.SirenLink.SirenLinkBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SirenLinkTest {

    @Nested
    class Builder {

        @Test
        void should_throw_exception_if_href_is_not_given() {
            SirenLinkBuilder builder = SirenLink.builder().rels(newArrayList());
            assertThatThrownBy(() -> builder.build()).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_return_link_containing_empty_rels_if_not_explicitely_defined() {
            SirenLink link = SirenLink.builder().href("/api").build();
            assertThat(link.getRels()).isNotNull().isEmpty();
        }
    }

    @Nested
    class TitleResolvable {

        @Test
        void should_return_codes_respecting_given_name() {
            SirenLink.TitleResolvable titleResolvable = SirenLink.TitleResolvable.of(ABOUT);
            assertThat(titleResolvable.getCodes()).containsExactly("_link.about.title", "_link.default.title");
        }
    }

}
