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

import static com.github.ingogriebsch.spring.hateoas.siren.support.ResourceReader.read;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.IanaLinkRelations.APPENDIX;
import static org.springframework.hateoas.IanaLinkRelations.SELF;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

class SirenLinkDiscovererTest {

    private static final SirenLinkDiscoverer discoverer = new SirenLinkDiscoverer();

    @Nested
    class FindLinkWithRel {

        @Nested
        class FromString {

            @Test
            void should_return_empty_optional_if_not_available() throws IOException {
                String source = read(resource("representation-model/containing_links.json"));
                assertThat(discoverer.findLinkWithRel(APPENDIX, source)).isEmpty();
            }

            @Test
            void should_return_self_link() throws IOException {
                String source = read(resource("representation-model/containing_links.json"));

                Optional<Link> link = discoverer.findLinkWithRel(SELF, source);
                assertThat(link).map(Link::getHref).hasValue("/persons");
            }

            @ParameterizedTest
            @CsvSource(value = { "self,/persons", "about,/about", "help,/help", "license,/license" })
            void should_return_matching_link(LinkRelation rel, String href) throws IOException {
                String source = read(resource("representation-model/containing_links.json"));

                Optional<Link> link = discoverer.findLinkWithRel(rel, source);
                assertThat(link).map(Link::getHref).hasValue(href);
            }
        }

        @Nested
        class FromInputStream {

            @Test
            void should_return_empty_optional_if_not_available() throws IOException {
                try (InputStream source = source("representation-model/containing_links.json")) {
                    assertThat(discoverer.findLinkWithRel(APPENDIX, source)).isEmpty();
                }
            }

            @Test
            void should_return_self_link() throws IOException {
                try (InputStream source = source("representation-model/containing_links.json")) {
                    Optional<Link> link = discoverer.findLinkWithRel(SELF, source);
                    assertThat(link).map(Link::getHref).hasValue("/persons");
                }
            }

            @ParameterizedTest
            @CsvSource(value = { "self,/persons", "about,/about", "help,/help", "license,/license" })
            void should_return_matching_link(LinkRelation rel, String href) throws IOException {
                try (InputStream source = source("representation-model/containing_links.json")) {
                    Optional<Link> link = discoverer.findLinkWithRel(rel, source);
                    assertThat(link).map(Link::getHref).hasValue(href);
                }
            }
        }

    }

    @Nested
    class FindLinksWithRel {

        @Nested
        class FromString {

            @Test
            void should_return_empty_optional_if_not_available() throws IOException {
                String source = read(resource("collection-model/containing_entitymodels.json"));
                assertThat(discoverer.findLinksWithRel(APPENDIX, source)).isEmpty();
            }

            @Test
            void should_return_self_link() throws IOException {
                String source = read(resource("collection-model/containing_entitymodels.json"));

                assertThat(discoverer.findLinksWithRel(SELF, source)) //
                    .extracting("href") //
                    .containsExactlyInAnyOrder("/persons");
            }

            @Test
            void should_return_person_links() throws IOException {
                String source = read(resource("collection-model/containing_entitymodels.json"));

                assertThat(discoverer.findLinksWithRel("person", source)) //
                    .extracting("href") //
                    .containsExactlyInAnyOrder("/persons/1", "/persons/2", "/persons/3", "/persons/4");
            }
        }

        @Nested
        class FromInputStream {

            @Test
            void should_return_empty_optional_if_not_available() throws IOException {
                try (InputStream source = source("collection-model/containing_entitymodels.json")) {
                    assertThat(discoverer.findLinkWithRel(APPENDIX, source)).isEmpty();
                }
            }

            @Test
            void should_return_self_link() throws IOException {
                try (InputStream source = source("collection-model/containing_entitymodels.json")) {
                    assertThat(discoverer.findLinksWithRel(SELF, source)) //
                        .extracting("href") //
                        .containsExactlyInAnyOrder("/persons");
                }
            }

            @Test
            void should_return_person_links() throws IOException {
                try (InputStream source = source("collection-model/containing_entitymodels.json")) {
                    assertThat(discoverer.findLinksWithRel("person", source)) //
                        .extracting("href") //
                        .containsExactlyInAnyOrder("/persons/1", "/persons/2", "/persons/3", "/persons/4");
                }
            }
        }
    }

    private InputStream source(String path) throws IOException {
        return resource(path).getInputStream();
    }

    private Resource resource(String path) {
        return new ClassPathResource(path, getClass());
    }

}
