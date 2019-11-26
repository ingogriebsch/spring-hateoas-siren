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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;
import static org.springframework.hateoas.mediatype.siren.MediaTypes.SIREN_JSON;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

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
import org.springframework.hateoas.support.MappingUtils;
import org.springframework.hateoas.support.WebMvcEmployeeController;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
        WebMvcEmployeeController.reset();
    }

    @Test
    void all() throws Exception {
        ResultActions result = mockMvc.perform(get("/employees").accept(SIREN_JSON));
        result.andExpect(status().isOk());

        result.andExpect(jsonPath("$.properties.size", is(2))) //
            .andExpect(jsonPath("$.class[0]", is(not(empty())))) //
            .andExpect(jsonPath("$.entities[0].class[0]", is(not(empty())))) //
            .andExpect(jsonPath("$.entities[0].properties.name", is("Frodo Baggins"))) //
            .andExpect(jsonPath("$.entities[0].properties.role", is("ring bearer"))) //
            .andExpect(jsonPath("$.entities[0].links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.entities[0].links[0].href", is("http://localhost/employees/0"))) //
            .andExpect(jsonPath("$.entities[0].links[1].rel[0]", is("employees"))) //
            .andExpect(jsonPath("$.entities[0].links[1].href", is("http://localhost/employees"))) //
            .andExpect(jsonPath("$.entities[1].properties.name", is("Bilbo Baggins"))) //
            .andExpect(jsonPath("$.entities[1].properties.role", is("burglar"))) //
            .andExpect(jsonPath("$.entities[1].links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.entities[1].links[0].href", is("http://localhost/employees/1"))) //
            .andExpect(jsonPath("$.entities[1].links[1].rel[0]", is("employees"))) //
            .andExpect(jsonPath("$.entities[1].links[1].href", is("http://localhost/employees"))) //
            .andExpect(jsonPath("$.links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.links[0].href", is("http://localhost/employees")));
    }

    @Test
    void search() throws Exception {
        ResultActions result = mockMvc.perform(get("/employees/search").param("name", "Frodo").accept(SIREN_JSON));
        result.andExpect(status().isOk());

        result.andExpect(jsonPath("$.properties.size", is(1))) //
            .andExpect(jsonPath("$.class[0]", is(not(empty())))) //
            .andExpect(jsonPath("$.entities[0].class[0]", is(not(empty())))) //
            .andExpect(jsonPath("$.entities[0].properties.name", is("Frodo Baggins"))) //
            .andExpect(jsonPath("$.entities[0].properties.role", is("ring bearer"))) //
            .andExpect(jsonPath("$.entities[0].links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.entities[0].links[0].href", is("http://localhost/employees/0"))) //
            .andExpect(jsonPath("$.entities[0].links[1].rel[0]", is("employees"))) //
            .andExpect(jsonPath("$.entities[0].links[1].href", is("http://localhost/employees"))) //
            .andExpect(jsonPath("$.links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.links[0].href", is("http://localhost/employees")));
    }

    @Test
    void findOne() throws Exception {
        ResultActions result = mockMvc.perform(get("/employees/0").accept(SIREN_JSON));
        result.andExpect(status().isOk());

        result.andExpect(jsonPath("$.properties.name", is("Frodo Baggins"))) //
            .andExpect(jsonPath("$.properties.role", is("ring bearer"))) //
            .andExpect(jsonPath("$.class[0]", is(not(empty())))) //
            .andExpect(jsonPath("$.links[0].rel[0]", is("self"))) //
            .andExpect(jsonPath("$.links[0].href", is("http://localhost/employees/0"))) //
            .andExpect(jsonPath("$.links[1].rel[0]", is("employees"))) //
            .andExpect(jsonPath("$.links[1].href", is("http://localhost/employees")));
    }

    @Test
    void newEmployee() throws Exception {
        String specBasedJson = MappingUtils.read(new ClassPathResource("new-employee.json", getClass()));

        ResultActions result = mockMvc.perform(post("/employees").content(specBasedJson).contentType(SIREN_JSON));
        result.andExpect(status().isCreated()).andExpect(header().stringValues(LOCATION, "http://localhost/employees/2"));
    }

    @Test
    void updateEmployee() throws Exception {
        String specBasedJson = MappingUtils.read(new ClassPathResource("update-employee.json", getClass()));

        ResultActions result = mockMvc.perform(put("/employees/0").content(specBasedJson).contentType(SIREN_JSON));
        result.andExpect(status().isNoContent()).andExpect(header().stringValues(LOCATION, "http://localhost/employees/0"));
    }

    @Test
    void partiallyUpdateEmployee() throws Exception {
        String specBasedJson = MappingUtils.read(new ClassPathResource("partially-update-employee.json", getClass()));

        ResultActions result = mockMvc.perform(patch("/employees/0").content(specBasedJson).contentType(SIREN_JSON));
        result.andExpect(status().isNoContent()).andExpect(header().stringValues(LOCATION, "http://localhost/employees/0"));
    }

    @Configuration
    @EnableWebMvc
    @EnableHypermediaSupport(type = HAL)
    static class TestConfig {

        @Bean
        WebMvcEmployeeController employeeController() {
            return new WebMvcEmployeeController();
        }

        @Bean
        SirenMediaTypeConfiguration sirenMediaTypeConfiguration(ObjectProvider<SirenConfiguration> sirenConfiguration,
            ObjectProvider<SirenEntityClassProvider> sirenEntityClassProvider, MessageResolver messageResolver) {
            return new SirenMediaTypeConfiguration(sirenConfiguration, sirenEntityClassProvider, messageResolver);
        }
    }

}
