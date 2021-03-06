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

import static de.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;
import static de.ingogriebsch.spring.hateoas.siren.support.ResourceReader.read;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToApplicationContext;

import de.ingogriebsch.spring.hateoas.siren.support.WebFluxPersonController;
import lombok.RequiredArgsConstructor;
import org.assertj.core.matcher.AssertionMatcher;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.JsonPathExpectationsHelper;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.config.EnableWebFlux;

@ContextConfiguration
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
class SirenWebFluxIntegrationTest {

    @Autowired
    private WebTestClient testClient;

    @BeforeEach
    void beforeEach() {
        WebFluxPersonController.reset();
    }

    @Test
    void findAll() throws Exception {
        ResponseSpec response = testClient.get().uri("http://localhost/persons").accept(SIREN_JSON).exchange();
        response.expectStatus().isOk().expectHeader().contentType(SIREN_JSON);

        response.expectBody(String.class) //
            .value(jsonPath("$.properties").doesNotExist()) //
            .value(jsonPath("$.class[0]", is("collection"))) //
            .value(jsonPath("$.entities[0].properties.name", is("Peter"))) //
            .value(jsonPath("$.entities[0].properties.age", is(33))) //
            .value(jsonPath("$.entities[0].links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.entities[0].links[0].href", is("http://localhost/persons/0"))) //
            .value(jsonPath("$.entities[0].links[1].rel[0]", is("persons"))) //
            .value(jsonPath("$.entities[0].links[1].href", is("http://localhost/persons"))) //
            .value(jsonPath("$.entities[1].properties.name", is("Paul"))) //
            .value(jsonPath("$.entities[1].properties.age", is(44))) //
            .value(jsonPath("$.entities[1].links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.entities[1].links[0].href", is("http://localhost/persons/1"))) //
            .value(jsonPath("$.entities[1].links[1].rel[0]", is("persons"))) //
            .value(jsonPath("$.entities[1].links[1].href", is("http://localhost/persons"))) //
            .value(jsonPath("$.entities[2].properties.name", is("Mary"))) //
            .value(jsonPath("$.entities[2].properties.age", is(55))) //
            .value(jsonPath("$.entities[2].links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.entities[2].links[0].href", is("http://localhost/persons/2"))) //
            .value(jsonPath("$.entities[2].links[1].rel[0]", is("persons"))) //
            .value(jsonPath("$.entities[2].links[1].href", is("http://localhost/persons"))) //
            .value(jsonPath("$.links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.links[0].href", is("http://localhost/persons")));
    }

    @Test
    void search() throws Exception {
        String name = "Peter";
        ResponseSpec response = testClient.get().uri(b -> {
            return b.scheme("http").host("localhost").path("/persons/search").queryParam("name", name).build();
        }).accept(SIREN_JSON).exchange();
        response.expectStatus().isOk().expectHeader().contentType(SIREN_JSON);

        response.expectBody(String.class) //
            .value(jsonPath("$.properties").doesNotExist()) //
            .value(jsonPath("$.class[0]", is(not(empty())))) //
            .value(jsonPath("$.entities[0].class[0]", is(not(empty())))) //
            .value(jsonPath("$.entities[0].properties.name", is("Peter"))) //
            .value(jsonPath("$.entities[0].properties.age", is(33))) //
            .value(jsonPath("$.entities[0].links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.entities[0].links[0].href", is("http://localhost/persons/0")))
            .value(jsonPath("$.entities[0].links[1].rel[0]", is("persons"))) //
            .value(jsonPath("$.entities[0].links[1].href", is("http://localhost/persons"))) //
            .value(jsonPath("$.links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.links[0].href", is("http://localhost/persons"))); //
    }

    @Test
    void findOne() throws Exception {
        ResponseSpec response = testClient.get().uri("http://localhost/persons/0").accept(SIREN_JSON).exchange();
        response.expectStatus().isOk().expectHeader().contentType(SIREN_JSON);

        response.expectBody(String.class) //
            .value(jsonPath("$.properties.name", is("Peter"))) //
            .value(jsonPath("$.properties.age", is(33))) //
            .value(jsonPath("$.class[0]", is(not(empty())))) //
            .value(jsonPath("$.links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.links[0].href", is("http://localhost/persons/0"))) //
            .value(jsonPath("$.links[1].rel[0]", is("persons"))) //
            .value(jsonPath("$.links[1].href", is("http://localhost/persons")));
    }

    @Test
    void insert() throws Exception {
        ResponseSpec response = testClient.post().uri("http://localhost/persons").contentType(SIREN_JSON)
            .bodyValue(read(new ClassPathResource("insert_person.json", getClass()))).exchange();

        response.expectStatus().isCreated() //
            .expectHeader().valueEquals(LOCATION, "http://localhost/persons/3");
    }

    @Test
    void update() throws Exception {
        ResponseSpec response = testClient.put().uri("http://localhost/persons/0").contentType(SIREN_JSON)
            .bodyValue(read(new ClassPathResource("update_person.json", getClass()))).exchange();

        response.expectStatus().isNoContent() //
            .expectHeader().valueEquals(LOCATION, "http://localhost/persons/0");
    }

    @Test
    void patch() throws Exception {
        ResponseSpec response = testClient.patch().uri("http://localhost/persons/0").contentType(SIREN_JSON)
            .bodyValue(read(new ClassPathResource("update_person.json", getClass()))).exchange();

        response.expectStatus().isNoContent() //
            .expectHeader().valueEquals(LOCATION, "http://localhost/persons/0");
    }

    private static JsonPathAssertionMatcher jsonPath(String expression) {
        return new JsonPathAssertionMatcher(expression, null);
    }

    private static <T> AssertionMatcher<String> jsonPath(String expression, Matcher<T> matcher) {
        return new AssertionMatcher<String>() {

            @Override
            public void assertion(String actual) throws AssertionError {
                new JsonPathExpectationsHelper(expression).assertValue(actual, matcher);
            }
        };
    }

    @Configuration
    @EnableWebFlux
    @EnableHypermediaSupport(type = {})
    static class TestConfig {

        @Bean
        WebFluxPersonController personController() {
            return new WebFluxPersonController();
        }

        @Bean
        SirenMediaTypeConfiguration sirenMediaTypeConfiguration(ObjectProvider<MessageResolver> messageResolver,
            ObjectProvider<SirenConfiguration> configuration, ObjectProvider<SirenEntityClassProvider> entityClassProvider,
            ObjectProvider<SirenEntityRelProvider> entityRelProvider,
            ObjectProvider<SirenActionFieldTypeConverter> sirenActionFieldTypeConverter,
            ObjectProvider<RepresentationModelFactories> representationModelFactories) {

            return new SirenMediaTypeConfiguration(messageResolver, configuration, entityClassProvider, entityRelProvider,
                sirenActionFieldTypeConverter, representationModelFactories);
        }

        @Bean
        WebTestClient webTestClient(HypermediaWebTestClientConfigurer configurer, ApplicationContext ctx) {
            return bindToApplicationContext(ctx).build().mutateWith(configurer);
        }
    }

    @RequiredArgsConstructor
    private static class JsonPathAssertionMatcher extends AssertionMatcher<String> {

        private final String expression;
        private final Object expected;

        @Override
        public void assertion(String actual) throws AssertionError {
            JsonPathExpectationsHelper helper = new JsonPathExpectationsHelper(expression);
            if (expected == null) {
                helper.doesNotHaveJsonPath(actual);
            } else {
                helper.assertValue(actual, expected);
            }
        }

        public JsonPathAssertionMatcher doesNotExist() {
            return new JsonPathAssertionMatcher(this.expression, null);
        }
    }
}
