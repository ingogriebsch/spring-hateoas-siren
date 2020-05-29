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
import static com.google.common.collect.Maps.newTreeMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.IanaLinkRelations.ABOUT;
import static org.springframework.hateoas.IanaLinkRelations.HELP;
import static org.springframework.hateoas.IanaLinkRelations.LICENSE;
import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.UriTemplate.of;
import static org.springframework.hateoas.mediatype.Affordances.of;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.support.Employee;
import org.springframework.hateoas.support.EmployeeResource;
import org.springframework.hateoas.support.MappingUtils;
import org.springframework.hateoas.support.SimpleObjectProvider;
import org.springframework.web.util.UriComponentsBuilder;

class Jackson2SirenModuleTest {

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        SimpleObjectProvider<SirenConfiguration> sirenConfiguration = new SimpleObjectProvider<>(new SirenConfiguration());

        SimpleObjectProvider<RepresentationModelFactories> representationModelFactories =
            new SimpleObjectProvider<>(new RepresentationModelFactories() {
            });

        SimpleObjectProvider<SirenEntityClassProvider> sirenEntityClassProvider =
            new SimpleObjectProvider<>(new SimpleSirenEntityClassProvider());

        SirenMediaTypeConfiguration sirenMediaTypeConfiguration = new SirenMediaTypeConfiguration(sirenConfiguration,
            representationModelFactories, sirenEntityClassProvider, DEFAULTS_ONLY);

        objectMapper = sirenMediaTypeConfiguration.configureObjectMapper(new ObjectMapper());
        objectMapper.configure(INDENT_OUTPUT, true);
    }

    @Nested
    class Collection {

        @Nested
        class Serialize {

            @Test
            void containing_entitymodel_containing_pojo_and_self_link() throws Exception {
                CollectionModel<?> source = new CollectionModel<>(
                    newArrayList(new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF))));
                String expected = readResource("collection/containing_entitymodel_containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                CollectionModel<?> source =
                    new CollectionModel<>(newArrayList(new EntityModel<>(new Employee("Peter", "Carpenter"))));
                String expected = readResource("collection/containing_entitymodel_containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodels() throws Exception {
                List<EntityModel<Employee>> content = newArrayList( //
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", "employee")), //
                    new EntityModel<>(new Employee("Paul", "Craftsman"), new Link("/employees/2", "employee")), //
                    new EntityModel<>(new Employee("Sarah", "Baker"), new Link("/employees/3", "employee")), //
                    new EntityModel<>(new Employee("Stephanie", "Mechanic"), new Link("/employees/4", "employee")) //
                );
                CollectionModel<EntityModel<Employee>> source = new CollectionModel<>(content, new Link("/employees", SELF));
                String expected = readResource("collection/containing_entitymodels.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                CollectionModel<Employee> source =
                    new CollectionModel<>(newArrayList(new Employee("Peter", "Carpenter")), new Link("/employees", SELF));
                String expected = readResource("collection/containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                CollectionModel<Employee> source = new CollectionModel<>(newArrayList(new Employee("Peter", "Carpenter")));
                String expected = readResource("collection/containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_entitymodel_containing_representationmodel() throws Exception {
                Country source = new Country("America", newArrayList(new State("California", new Capital("Sacramento"))));
                String expected =
                    readResource("collection/having_properties_containing_entitymodel_containing_representationmodel.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void with_self_link() throws Exception {
                CollectionModel<?> source = new CollectionModel<>(newArrayList(), new Link("/employees", SELF));
                String expected = readResource("collection/with_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_content() throws Exception {
                CollectionModel<?> source = new CollectionModel<>(newArrayList());
                String expected = readResource("collection/without_content.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Deserialize {

            @Test
            void containing_entitymodel_containing_pojo_and_self_link() throws Exception {
                String source = readResource("collection/containing_entitymodel_containing_pojo_and_self_link.json");
                EntityModel<Employee> entityModel =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF));
                CollectionModel<EntityModel<Employee>> expected = new CollectionModel<>(newArrayList(entityModel));

                CollectionModel<EntityModel<Employee>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Employee>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                String source = readResource("collection/containing_entitymodel_containing_pojo.json");
                EntityModel<Employee> entityModel = new EntityModel<>(new Employee("Peter", "Carpenter"));
                CollectionModel<EntityModel<Employee>> expected = new CollectionModel<>(newArrayList(entityModel));

                CollectionModel<EntityModel<Employee>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Employee>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodels() throws Exception {
                String source = readResource("collection/containing_entitymodels.json");
                List<EntityModel<Employee>> content = newArrayList( //
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", "employee")), //
                    new EntityModel<>(new Employee("Paul", "Craftsman"), new Link("/employees/2", "employee")), //
                    new EntityModel<>(new Employee("Sarah", "Baker"), new Link("/employees/3", "employee")), //
                    new EntityModel<>(new Employee("Stephanie", "Mechanic"), new Link("/employees/4", "employee")) //
                );
                CollectionModel<EntityModel<Employee>> expected = new CollectionModel<>(content, new Link("/employees", SELF));

                CollectionModel<EntityModel<Employee>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Employee>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                String source = readResource("collection/containing_pojo_and_self_link.json");
                CollectionModel<Employee> expected =
                    new CollectionModel<>(newArrayList(new Employee("Peter", "Carpenter")), new Link("/employees", SELF));

                CollectionModel<Employee> actual = read(source, new TypeReference<CollectionModel<Employee>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                String source = readResource("collection/containing_pojo.json");
                CollectionModel<Employee> expected = new CollectionModel<>(newArrayList(new Employee("Peter", "Carpenter")));

                CollectionModel<Employee> actual = read(source, new TypeReference<CollectionModel<Employee>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_entitymodel_containing_representationmodel() throws Exception {
                String source =
                    readResource("collection/having_properties_containing_entitymodel_containing_representationmodel.json");
                Country expected = new Country("America", newArrayList(new State("California", new Capital("Sacramento"))));

                Country actual = read(source, Country.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void with_self_link() throws Exception {
                String source = readResource("collection/with_self_link.json");
                CollectionModel<?> expected = new CollectionModel<>(newArrayList(), new Link("/employees", SELF));

                CollectionModel<?> actual = read(source, CollectionModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_content() throws Exception {
                String source = readResource("collection/without_content.json");
                CollectionModel<?> expected = new CollectionModel<>(newArrayList());

                CollectionModel<?> actual = read(source, CollectionModel.class);
                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Nested
    class Entity {

        @Nested
        class Serialize {

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                EntityModel<EntityModel<Employee>> source =
                    new EntityModel<>(new EntityModel<>(new Employee("Peter", "Carpenter")));
                String expected = readResource("entity/containing_entitymodel_containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_integer() throws Exception {
                EntityModel<Integer> source = new EntityModel<>(42);
                String expected = readResource("entity/containing_integer.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_and_entitymodel_containing_pojo_and_link() throws Exception {
                EntityModel<Employee> contained =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/departments/6/employees/1", SELF));
                EntityModel<EntityModel<Employee>> source =
                    new EntityModel<>(contained, new Link("/departments/6/employees", SELF));
                String expected = readResource("entity/containing_link_and_entitymodel_containing_pojo_and_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                EntityModel<Employee> source =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF));
                String expected = readResource("entity/containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                EntityModel<Employee> source = new EntityModel<>(new Employee("Peter", "Carpenter"));
                String expected = readResource("entity/containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_string() throws Exception {
                EntityModel<String> source = new EntityModel<>("Something");
                String expected = readResource("entity/containing_string.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_representationmodel() throws Exception {
                State source = new State("California", new Capital("Sacramento"));
                String expected = readResource("entity/having_properties_containing_representationmodel.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Deserialize {

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                String source = readResource("entity/containing_entitymodel_containing_pojo.json");

                TypeFactory typeFactory = objectMapper.getTypeFactory();
                JavaType expectedType = typeFactory.constructParametricType(EntityModel.class,
                    typeFactory.constructParametricType(EntityModel.class, Employee.class));

                EntityModel<EntityModel<Employee>> expected =
                    new EntityModel<>(new EntityModel<>(new Employee("Peter", "Carpenter")));

                EntityModel<EntityModel<Employee>> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_integer() throws Exception {
                String source = readResource("entity/containing_integer.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Integer.class);
                EntityModel<Integer> expected = new EntityModel<>(42);

                EntityModel<Employee> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_and_entitymodel_containing_pojo_and_link() throws Exception {
                String source = readResource("entity/containing_link_and_entitymodel_containing_pojo_and_link.json");

                TypeFactory typeFactory = objectMapper.getTypeFactory();
                JavaType expectedType = typeFactory.constructParametricType(EntityModel.class,
                    typeFactory.constructParametricType(EntityModel.class, Employee.class));

                EntityModel<Employee> contained =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/departments/6/employees/1", SELF));
                EntityModel<EntityModel<Employee>> expected =
                    new EntityModel<>(contained, new Link("/departments/6/employees", SELF));

                EntityModel<EntityModel<Employee>> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                String source = readResource("entity/containing_pojo_and_self_link.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Employee.class);
                EntityModel<Employee> expected =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF));

                EntityModel<Employee> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                String source = readResource("entity/containing_pojo.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Employee.class);
                EntityModel<Employee> expected = new EntityModel<>(new Employee("Peter", "Carpenter"));

                EntityModel<Employee> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_string() throws Exception {
                String source = readResource("entity/containing_string.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, String.class);
                EntityModel<String> expected = new EntityModel<>("Something");

                EntityModel<String> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_representationmodel() throws Exception {
                String source = readResource("entity/having_properties_containing_representationmodel.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructSimpleType(State.class, null);
                State expected = new State("California", new Capital("Sacramento"));

                State actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Nested
    class Paged {

        @Nested
        class Serialize {

            @Test
            void containing_entitymodel_containing_pojo_and_self_link() throws Exception {
                EntityModel<Employee> entityModel =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF));
                PagedModel<EntityModel<Employee>> source =
                    new PagedModel<>(newArrayList(entityModel), new PageMetadata(20, 0, 1));
                String expected = readResource("paged/containing_entitymodel_containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                EntityModel<Employee> entityModel = new EntityModel<>(new Employee("Peter", "Carpenter"));
                PagedModel<EntityModel<Employee>> source =
                    new PagedModel<>(newArrayList(entityModel), new PageMetadata(20, 0, 1));
                String expected = readResource("paged/containing_entitymodel_containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                PagedModel<Employee> source =
                    new PagedModel<>(newArrayList(new Employee("Peter", "Carpenter")), new PageMetadata(20, 0, 1));
                String expected = readResource("paged/containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void with_self_link() throws Exception {
                PagedModel<?> source = new PagedModel<>(newArrayList(), new PageMetadata(20, 0, 0),
                    enhance(new Link("/employees", SELF), pagingParams(0, 20)));
                String expected = readResource("paged/with_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_content() throws Exception {
                PagedModel<?> source = new PagedModel<>(newArrayList(), new PageMetadata(20, 0, 0));
                String expected = readResource("paged/without_content.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

        }

        @Nested
        class Deserialize {

            @Test
            void containing_entitymodel_containing_pojo_and_self_link() throws Exception {
                String source = readResource("paged/containing_entitymodel_containing_pojo_and_self_link.json");
                EntityModel<Employee> entityModel =
                    new EntityModel<>(new Employee("Peter", "Carpenter"), new Link("/employees/1", SELF));
                CollectionModel<EntityModel<Employee>> expected = new CollectionModel<>(newArrayList(entityModel));

                CollectionModel<EntityModel<Employee>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Employee>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                String source = readResource("paged/containing_entitymodel_containing_pojo.json");
                EntityModel<Employee> entityModel = new EntityModel<>(new Employee("Peter", "Carpenter"));
                PagedModel<EntityModel<Employee>> expected =
                    new PagedModel<>(newArrayList(entityModel), new PageMetadata(20, 0, 1));

                PagedModel<EntityModel<Employee>> actual = read(source, new TypeReference<PagedModel<EntityModel<Employee>>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                String source = readResource("paged/containing_pojo.json");
                PagedModel<Employee> expected =
                    new PagedModel<>(newArrayList(new Employee("Peter", "Carpenter")), new PageMetadata(20, 0, 1));

                PagedModel<Employee> actual = read(source, new TypeReference<PagedModel<Employee>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void with_self_link() throws Exception {
                String source = readResource("paged/with_self_link.json");
                PagedModel<?> expected = new PagedModel<>(newArrayList(), new PageMetadata(20, 0, 0),
                    enhance(new Link("/employees", SELF), pagingParams(0, 20)));

                PagedModel<?> actual = read(source, PagedModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_content() throws Exception {
                String source = readResource("paged/without_content.json");
                PagedModel<?> expected = new PagedModel<>(newArrayList(), new PageMetadata(20, 0, 0));

                PagedModel<?> actual = read(source, PagedModel.class);
                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Nested
    class Representation {

        @Nested
        class Serialize {

            @Test
            void containing_link_with_delete_affordance() throws Exception {
                Link link = of(new Link("/employees/1", SELF)).afford(DELETE).withName("delete").toLink();
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representation/containing_link_with_delete_affordance.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_with_title() throws Exception {
                Link link = new Link("/about", ABOUT).withTitle("about");
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representation/containing_link_with_title.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_with_update_affordance() throws Exception {
                Link link = of(new Link("/employees/1", SELF)).afford(PUT).withInput(Employee.class).withName("update").toLink();
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representation/containing_link_with_update_affordance.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_with_update_and_delete_affordances() throws Exception {
                Link link = of(new Link("/employees/1", SELF)).afford(PUT).withInput(Employee.class).withName("update")
                    .andAfford(DELETE).withName("delete").toLink();
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representation/containing_link_with_update_and_delete_affordances.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>(new Link("/about", ABOUT));
                String expected = readResource("representation/containing_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_links() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>(newArrayList(new Link("/employees", SELF),
                    new Link("/about", ABOUT), new Link("/help", HELP), new Link("/license", LICENSE)));
                String expected = readResource("representation/containing_links.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_link() throws Exception {
                RepresentationModel<?> source = new EmployeeResource("Peter");
                source.add(new Link("/employee", SELF));
                String expected = readResource("representation/having_properties_containing_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_links() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>();
                String expected = readResource("representation/without_links.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Deserialize {

            @Test
            void containing_link() throws Exception {
                String source = readResource("representation/containing_link.json");
                RepresentationModel<?> expected = new RepresentationModel<>(new Link("/about", ABOUT));

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_links() throws Exception {
                String source = readResource("representation/containing_links.json");
                RepresentationModel<?> expected = new RepresentationModel<>(newArrayList(new Link("/employees", SELF),
                    new Link("/about", ABOUT), new Link("/help", HELP), new Link("/license", LICENSE)));

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_link() throws Exception {
                String source = readResource("representation/having_properties_containing_link.json");
                RepresentationModel<?> expected = new EmployeeResource("Peter");
                expected.add(new Link("/employee", SELF));

                RepresentationModel<?> actual = read(source, EmployeeResource.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_links() throws Exception {
                String source = readResource("representation/without_links.json");
                RepresentationModel<?> expected = new RepresentationModel<>();

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    private String readResource(String sourceFilename) throws IOException {
        return MappingUtils.read(new ClassPathResource(sourceFilename, getClass()));
    }

    private static <T> T read(String str, TypeReference<T> type) throws Exception {
        return objectMapper.readValue(str, type);
    }

    private static <T> T read(String str, JavaType type) throws Exception {
        return objectMapper.readValue(str, type);
    }

    private static <T> T read(String str, Class<T> type) throws Exception {
        return objectMapper.readValue(str, type);
    }

    private static String write(Object object) throws Exception {
        Writer writer = new StringWriter();
        objectMapper.writeValue(writer, object);
        return writer.toString();
    }

    private static Map<String, Object> pagingParams(int page, int size) {
        Map<String, Object> params = newTreeMap();
        params.put("page", page);
        params.put("size", size);
        return params;
    }

    private static Link enhance(Link link, Map<String, Object> params) {
        UriComponentsBuilder builder = fromUri(link.getTemplate().expand());
        for (Map.Entry<String, Object> param : params.entrySet()) {
            builder.queryParam(param.getKey(), param.getValue());
        }
        return new Link(of(builder.build().toString()), SELF);
    }
}
