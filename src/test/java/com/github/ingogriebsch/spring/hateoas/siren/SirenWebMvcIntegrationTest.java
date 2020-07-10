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

import static com.github.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;
import static com.github.ingogriebsch.spring.hateoas.siren.support.ResourceReader.read;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.github.ingogriebsch.spring.hateoas.siren.support.WebMvcPersonController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ContextConfiguration
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
class SirenWebMvcIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = webAppContextSetup(context).build();
        WebMvcPersonController.reset();
    }

    @Test
    void findAll() throws Exception {
        ResultActions result = mockMvc.perform(get("/persons").accept(SIREN_JSON));
        result.andExpect(status().isOk());

        result.andExpect(jsonPath("$.properties").doesNotExist()) //
            .andExpect(jsonPath("$.class[0]", is(not(empty())))) //
            .andExpect(jsonPath("$.entities[0].class[0]", is(not(empty())))) //
            .andExpect(jsonPath("$.entities[0].properties.name", is("Peter"))) //
            .andExpect(jsonPath("$.entities[0].properties.age", is(33))) //
            .andExpect(jsonPath("$.entities[0].links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.entities[0].links[0].href", is("http://localhost/persons/0"))) //
            .andExpect(jsonPath("$.entities[0].links[1].rel[0]", is("persons"))) //
            .andExpect(jsonPath("$.entities[0].links[1].href", is("http://localhost/persons"))) //
            .andExpect(jsonPath("$.entities[1].properties.name", is("Paul"))) //
            .andExpect(jsonPath("$.entities[1].properties.age", is(44))) //
            .andExpect(jsonPath("$.entities[1].links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.entities[1].links[0].href", is("http://localhost/persons/1"))) //
            .andExpect(jsonPath("$.entities[1].links[1].rel[0]", is("persons"))) //
            .andExpect(jsonPath("$.entities[1].links[1].href", is("http://localhost/persons"))) //
            .andExpect(jsonPath("$.entities[2].properties.name", is("Mary"))) //
            .andExpect(jsonPath("$.entities[2].properties.age", is(55))) //
            .andExpect(jsonPath("$.entities[2].links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.entities[2].links[0].href", is("http://localhost/persons/2"))) //
            .andExpect(jsonPath("$.entities[2].links[1].rel[0]", is("persons"))) //
            .andExpect(jsonPath("$.entities[2].links[1].href", is("http://localhost/persons"))) //
            .andExpect(jsonPath("$.links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.links[0].href", is("http://localhost/persons")));
    }

    @Test
    void search() throws Exception {
        ResultActions result = mockMvc.perform(get("/persons/search").param("name", "Peter").accept(SIREN_JSON));
        result.andExpect(status().isOk());

        result.andExpect(jsonPath("$.properties").doesNotExist()) //
            .andExpect(jsonPath("$.class[0]", is(not(empty())))) //
            .andExpect(jsonPath("$.entities[0].class[0]", is(not(empty())))) //
            .andExpect(jsonPath("$.entities[0].properties.name", is("Peter"))) //
            .andExpect(jsonPath("$.entities[0].links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.entities[0].links[0].href", is("http://localhost/persons/0"))) //
            .andExpect(jsonPath("$.entities[0].links[1].rel[0]", is("persons"))) //
            .andExpect(jsonPath("$.entities[0].links[1].href", is("http://localhost/persons"))) //
            .andExpect(jsonPath("$.links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.links[0].href", is("http://localhost/persons")));
    }

    @Test
    void findOne() throws Exception {
        ResultActions result = mockMvc.perform(get("/persons/0").accept(SIREN_JSON));
        result.andExpect(status().isOk());

        result.andExpect(jsonPath("$.properties.name", is("Peter"))) //
            .andExpect(jsonPath("$.properties.age", is(33))) //
            .andExpect(jsonPath("$.class[0]", is(not(empty())))) //
            .andExpect(jsonPath("$.links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.links[0].href", is("http://localhost/persons/0"))) //
            .andExpect(jsonPath("$.links[1].rel[0]", is("persons"))) //
            .andExpect(jsonPath("$.links[1].href", is("http://localhost/persons")));
    }

    @Test
    void insert() throws Exception {
        String specBasedJson = read(new ClassPathResource("insert_person.json", getClass()));

        ResultActions result = mockMvc.perform(post("/persons").content(specBasedJson).contentType(SIREN_JSON));
        result.andExpect(status().isCreated()).andExpect(header().stringValues(LOCATION, "http://localhost/persons/3"));
    }

    @Test
    void update() throws Exception {
        String specBasedJson = read(new ClassPathResource("update_person.json", getClass()));

        ResultActions result = mockMvc.perform(put("/persons/0").content(specBasedJson).contentType(SIREN_JSON));
        result.andExpect(status().isNoContent()).andExpect(header().stringValues(LOCATION, "http://localhost/persons/0"));
    }

    @Test
    void patch() throws Exception {
        String specBasedJson = read(new ClassPathResource("patch_person.json", getClass()));

        ResultActions result =
            mockMvc.perform(MockMvcRequestBuilders.patch("/persons/0").content(specBasedJson).contentType(SIREN_JSON));
        result.andExpect(status().isNoContent()).andExpect(header().stringValues(LOCATION, "http://localhost/persons/0"));
    }

    @Configuration
    @EnableWebMvc
    @EnableHypermediaSupport(type = HAL)
    static class TestConfig {

        @Bean
        WebMvcPersonController personController() {
            return new WebMvcPersonController();
        }

        @Bean
        SirenMediaTypeConfiguration sirenMediaTypeConfiguration(ObjectProvider<SirenConfiguration> configuration,
            ObjectProvider<RepresentationModelFactories> representationModelFactories,
            ObjectProvider<SirenEntityClassProvider> entityClassProvider,
            ObjectProvider<SirenEntityRelProvider> entityRelProvider, MessageResolver messageResolver) {
            return new SirenMediaTypeConfiguration(configuration, representationModelFactories, entityClassProvider,
                entityRelProvider, messageResolver);
        }
    }

}
