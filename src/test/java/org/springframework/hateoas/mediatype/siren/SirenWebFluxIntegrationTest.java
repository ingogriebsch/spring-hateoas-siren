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
package org.springframework.hateoas.mediatype.siren;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;
import static org.springframework.hateoas.mediatype.siren.MediaTypes.SIREN_JSON;
import static org.springframework.hateoas.support.JsonPathUtils.jsonPath;
import static org.springframework.hateoas.support.MappingUtils.read;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToApplicationContext;

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
import org.springframework.hateoas.config.WebClientConfigurer;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.support.WebFluxEmployeeController;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
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
        WebFluxEmployeeController.reset();
    }

    @Test
    void all() throws Exception {
        ResponseSpec response = testClient.get().uri("http://localhost/employees").accept(SIREN_JSON).exchange();
        response.expectStatus().isOk().expectHeader().contentType(SIREN_JSON);

        response.expectBody(String.class) //
            .value(jsonPath("$.properties.size", is(2))) //
            .value(jsonPath("$.class[0]", is("collection"))) //
            .value(jsonPath("$.entities[0].properties.name", is("Frodo Baggins"))) //
            .value(jsonPath("$.entities[0].properties.role", is("ring bearer"))) //
            .value(jsonPath("$.entities[0].links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.entities[0].links[0].href", is("http://localhost/employees/0"))) //
            .value(jsonPath("$.entities[0].links[1].rel[0]", is("employees"))) //
            .value(jsonPath("$.entities[0].links[1].href", is("http://localhost/employees"))) //
            .value(jsonPath("$.entities[1].properties.name", is("Bilbo Baggins"))) //
            .value(jsonPath("$.entities[1].properties.role", is("burglar"))) //
            .value(jsonPath("$.entities[1].links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.entities[1].links[0].href", is("http://localhost/employees/1"))) //
            .value(jsonPath("$.entities[1].links[1].rel[0]", is("employees"))) //
            .value(jsonPath("$.entities[1].links[1].href", is("http://localhost/employees"))) //
            .value(jsonPath("$.links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.links[0].href", is("http://localhost/employees")));
    }

    @Test
    void search() throws Exception {
        String name = "Frodo";
        ResponseSpec response = testClient.get().uri(b -> {
            return b.scheme("http").host("localhost").path("/employees/search").queryParam("name", name).build();
        }).accept(SIREN_JSON).exchange();
        response.expectStatus().isOk().expectHeader().contentType(SIREN_JSON);

        response.expectBody(String.class) //
            .value(jsonPath("$.properties.size", is(1))) //
            .value(jsonPath("$.class[0]", is(not(empty())))) //
            .value(jsonPath("$.entities[0].class[0]", is(not(empty())))) //
            .value(jsonPath("$.entities[0].properties.name", is("Frodo Baggins"))) //
            .value(jsonPath("$.entities[0].properties.role", is("ring bearer"))) //
            .value(jsonPath("$.entities[0].links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.entities[0].links[0].href", is("http://localhost/employees/0")))
            .value(jsonPath("$.entities[0].links[1].rel[0]", is("employees"))) //
            .value(jsonPath("$.entities[0].links[1].href", is("http://localhost/employees"))) //
            .value(jsonPath("$.links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.links[0].href", is("http://localhost/employees"))); //
    }

    @Test
    void findOne() throws Exception {
        ResponseSpec response = testClient.get().uri("http://localhost/employees/0").accept(SIREN_JSON).exchange();
        response.expectStatus().isOk().expectHeader().contentType(SIREN_JSON);

        response.expectBody(String.class) //
            .value(jsonPath("$.properties.name", is("Frodo Baggins"))) //
            .value(jsonPath("$.properties.role", is("ring bearer"))) //
            .value(jsonPath("$.class[0]", is(not(empty())))) //
            .value(jsonPath("$.links[0].rel[0]", is("self"))) //
            .value(jsonPath("$.links[0].href", is("http://localhost/employees/0"))) //
            .value(jsonPath("$.links[1].rel[0]", is("employees"))) //
            .value(jsonPath("$.links[1].href", is("http://localhost/employees")));
    }

    @Test
    void newEmployee() throws Exception {
        ResponseSpec response = testClient.post().uri("http://localhost/employees").contentType(SIREN_JSON)
            .bodyValue(read(new ClassPathResource("new-employee.json", getClass()))).exchange();

        response.expectStatus().isCreated() //
            .expectHeader().valueEquals(LOCATION, "http://localhost/employees/2");
    }

    @Test
    void updateEmployee() throws Exception {
        ResponseSpec response = testClient.put().uri("http://localhost/employees/0").contentType(SIREN_JSON)
            .bodyValue(read(new ClassPathResource("update-employee.json", getClass()))).exchange();

        response.expectStatus().isNoContent() //
            .expectHeader().valueEquals(LOCATION, "http://localhost/employees/0");
    }

    @Test
    void partiallyUpdateEmployee() throws Exception {
        ResponseSpec response = testClient.patch().uri("http://localhost/employees/0").contentType(SIREN_JSON)
            .bodyValue(read(new ClassPathResource("update-employee.json", getClass()))).exchange();

        response.expectStatus().isNoContent() //
            .expectHeader().valueEquals(LOCATION, "http://localhost/employees/0");
    }

    @Configuration
    @EnableWebFlux
    @EnableHypermediaSupport(type = HAL)
    static class TestConfig {

        @Bean
        WebFluxEmployeeController employeeController() {
            return new WebFluxEmployeeController();
        }

        @Bean
        SirenMediaTypeConfiguration sirenMediaTypeConfiguration(ObjectProvider<SirenConfiguration> sirenConfiguration,
            ObjectProvider<SirenEntityClassProvider> sirenEntityClassProvider, MessageResolver messageResolver) {
            return new SirenMediaTypeConfiguration(sirenConfiguration, sirenEntityClassProvider, messageResolver);
        }

        @Bean
        WebTestClient webTestClient(WebClientConfigurer webClientConfigurer, ApplicationContext ctx) {
            return bindToApplicationContext(ctx).build().mutate()
                .exchangeStrategies(webClientConfigurer.hypermediaExchangeStrategies()).build();
        }
    }

}
