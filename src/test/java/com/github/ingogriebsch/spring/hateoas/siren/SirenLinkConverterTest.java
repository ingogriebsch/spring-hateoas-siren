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

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ingogriebsch.spring.hateoas.siren.support.StaticMessageResolver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.support.MappingUtils;
import org.springframework.hateoas.support.WebMvcEmployeeController;

class SirenLinkConverterTest {

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(INDENT_OUTPUT, true);
    }

    @Nested
    class Ctor {

        @Test
        void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> new SirenLinkConverter(null));
        }
    }

    @Nested
    class To {

        @Test
        void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> new SirenLinkConverter(DEFAULTS_ONLY).to(null));
        }

        @Test
        void containing_link_with_href_and_rel() throws IOException {
            Link source = new Link("/employees/1", SELF);
            SirenNavigables expected = read("navigables/containing_link_with_href_and_rel.json");

            SirenLinkConverter converter = new SirenLinkConverter(DEFAULTS_ONLY);
            SirenNavigables actual = converter.to(newArrayList(source));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void containing_link_with_title_from_input() throws IOException {
            Link source = new Link("/employees/1", SELF).withTitle("title");
            SirenNavigables expected = read("navigables/containing_link_with_title.json");

            SirenLinkConverter converter = new SirenLinkConverter(DEFAULTS_ONLY);
            SirenNavigables actual = converter.to(newArrayList(source));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void containing_link_with_title_from_message_resolver() throws IOException {
            Link source = new Link("/employees/1", SELF);
            SirenNavigables expected = read("navigables/containing_link_with_title.json");

            SirenLinkConverter converter = new SirenLinkConverter(StaticMessageResolver.of("title"));
            SirenNavigables actual = converter.to(newArrayList(source));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void containing_link_with_title_from_input_even_if_available_through_message_resolver() throws IOException {
            Link source = new Link("/employees/1", SELF).withTitle("title");
            SirenNavigables expected = read("navigables/containing_link_with_title.json");

            SirenLinkConverter converter = new SirenLinkConverter(StaticMessageResolver.of("something"));
            SirenNavigables actual = converter.to(newArrayList(source));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void containing_link_and_action_representing_link_and_affordance() throws IOException {
            WebMvcEmployeeController controller = methodOn(WebMvcEmployeeController.class);
            Link source = linkTo(controller.findOne(1)).withSelfRel().andAffordance(afford(controller.updateEmployee(null, 1)));
            SirenNavigables expected = read("navigables/containing_link_and_action_representing_link_with_affordance.json");

            SirenLinkConverter converter = new SirenLinkConverter(DEFAULTS_ONLY);
            SirenNavigables actual = converter.to(newArrayList(source));

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class From {

        @Test
        void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> new SirenLinkConverter(DEFAULTS_ONLY).from(null));
        }

        @Test
        void containing_link_with_href_and_rel() throws IOException {
            SirenNavigables source = read("navigables/containing_link_with_href_and_rel.json");
            List<Link> expected = newArrayList(new Link("/employees/1", SELF));

            SirenLinkConverter converter = new SirenLinkConverter(DEFAULTS_ONLY);
            List<Link> actual = converter.from(source);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void containing_link_with_title() throws IOException {
            SirenNavigables source = read("navigables/containing_link_with_title.json");
            List<Link> expected = newArrayList(new Link("/employees/1", SELF).withTitle("title"));

            SirenLinkConverter converter = new SirenLinkConverter(DEFAULTS_ONLY);
            List<Link> actual = converter.from(source);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void containing_link_with_type() throws IOException {
            SirenNavigables source = read("navigables/containing_link_with_type.json");
            List<Link> expected = newArrayList(new Link("/employees/1", SELF).withType(APPLICATION_JSON_VALUE));

            SirenLinkConverter converter = new SirenLinkConverter(DEFAULTS_ONLY);
            List<Link> actual = converter.from(source);

            assertThat(actual).isEqualTo(expected);
        }
    }

    private SirenNavigables read(String sourceFilename) throws IOException {
        return read(sourceFilename, SirenNavigables.class);
    }

    private <T> T read(String sourceFilename, Class<T> type) throws IOException {
        return objectMapper.readValue(readResource(sourceFilename), type);
    }

    private String readResource(String sourceFilename) throws IOException {
        return MappingUtils.read(new ClassPathResource(sourceFilename, getClass()));
    }
}
