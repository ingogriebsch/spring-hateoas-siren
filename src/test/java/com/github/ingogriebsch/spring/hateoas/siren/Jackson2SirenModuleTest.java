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
import com.github.ingogriebsch.spring.hateoas.siren.support.Person;
import com.github.ingogriebsch.spring.hateoas.siren.support.PersonModel;
import com.github.ingogriebsch.spring.hateoas.siren.support.ResourceReader;
import com.github.ingogriebsch.spring.hateoas.siren.support.SimpleObjectProvider;

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
            new SimpleObjectProvider<>(new SirenEntityClassProvider() {
            });

        SimpleObjectProvider<SirenEntityRelProvider> sirenEntityRelProvider =
            new SimpleObjectProvider<>(new SirenEntityRelProvider() {
            });

        SirenMediaTypeConfiguration sirenMediaTypeConfiguration = new SirenMediaTypeConfiguration(sirenConfiguration,
            representationModelFactories, sirenEntityClassProvider, sirenEntityRelProvider, DEFAULTS_ONLY);

        objectMapper = sirenMediaTypeConfiguration.configureObjectMapper(new ObjectMapper());
        objectMapper.configure(INDENT_OUTPUT, true);
    }

    @Nested
    class Collection {

        @Nested
        class Serialize {

            @Test
            void containing_entitymodel_containing_pojo_and_self_link() throws Exception {
                CollectionModel<?> source =
                    new CollectionModel<>(newArrayList(new EntityModel<>(new Person("Peter", 33), new Link("/persons/1", SELF))));
                String expected = readResource("collection/containing_entitymodel_containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                CollectionModel<?> source = new CollectionModel<>(newArrayList(new EntityModel<>(new Person("Peter", 33))));
                String expected = readResource("collection/containing_entitymodel_containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodels() throws Exception {
                List<EntityModel<Person>> content = newArrayList( //
                    new EntityModel<>(new Person("Peter", 33), new Link("/persons/1", "person")), //
                    new EntityModel<>(new Person("Paul", 44), new Link("/persons/2", "person")), //
                    new EntityModel<>(new Person("Sarah", 55), new Link("/persons/3", "person")), //
                    new EntityModel<>(new Person("Stephanie", 66), new Link("/persons/4", "person")) //
                );
                CollectionModel<EntityModel<Person>> source = new CollectionModel<>(content, new Link("/persons", SELF));
                String expected = readResource("collection/containing_entitymodels.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                CollectionModel<Person> source =
                    new CollectionModel<>(newArrayList(new Person("Peter", 33)), new Link("/persons", SELF));
                String expected = readResource("collection/containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                CollectionModel<Person> source = new CollectionModel<>(newArrayList(new Person("Peter", 33)));
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
                CollectionModel<?> source = new CollectionModel<>(newArrayList(), new Link("/persons", SELF));
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
                EntityModel<Person> entityModel = new EntityModel<>(new Person("Peter", 33), new Link("/persons/1", SELF));
                CollectionModel<EntityModel<Person>> expected = new CollectionModel<>(newArrayList(entityModel));

                CollectionModel<EntityModel<Person>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Person>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                String source = readResource("collection/containing_entitymodel_containing_pojo.json");
                EntityModel<Person> entityModel = new EntityModel<>(new Person("Peter", 33));
                CollectionModel<EntityModel<Person>> expected = new CollectionModel<>(newArrayList(entityModel));

                CollectionModel<EntityModel<Person>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Person>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodels() throws Exception {
                String source = readResource("collection/containing_entitymodels.json");
                List<EntityModel<Person>> content = newArrayList( //
                    new EntityModel<>(new Person("Peter", 33), new Link("/persons/1", "person")), //
                    new EntityModel<>(new Person("Paul", 44), new Link("/persons/2", "person")), //
                    new EntityModel<>(new Person("Sarah", 55), new Link("/persons/3", "person")), //
                    new EntityModel<>(new Person("Stephanie", 66), new Link("/persons/4", "person")) //
                );
                CollectionModel<EntityModel<Person>> expected = new CollectionModel<>(content, new Link("/persons", SELF));

                CollectionModel<EntityModel<Person>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Person>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                String source = readResource("collection/containing_pojo_and_self_link.json");
                CollectionModel<Person> expected =
                    new CollectionModel<>(newArrayList(new Person("Peter", 33)), new Link("/persons", SELF));

                CollectionModel<Person> actual = read(source, new TypeReference<CollectionModel<Person>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                String source = readResource("collection/containing_pojo.json");
                CollectionModel<Person> expected = new CollectionModel<>(newArrayList(new Person("Peter", 33)));

                CollectionModel<Person> actual = read(source, new TypeReference<CollectionModel<Person>>() {
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
                CollectionModel<?> expected = new CollectionModel<>(newArrayList(), new Link("/persons", SELF));

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
                EntityModel<EntityModel<Person>> source = new EntityModel<>(new EntityModel<>(new Person("Peter", 33)));
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
                EntityModel<Person> contained =
                    new EntityModel<>(new Person("Peter", 33), new Link("/departments/6/persons/1", SELF));
                EntityModel<EntityModel<Person>> source = new EntityModel<>(contained, new Link("/departments/6/persons", SELF));
                String expected = readResource("entity/containing_link_and_entitymodel_containing_pojo_and_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                EntityModel<Person> source = new EntityModel<>(new Person("Peter", 33), new Link("/persons/1", SELF));
                String expected = readResource("entity/containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                EntityModel<Person> source = new EntityModel<>(new Person("Peter", 33));
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
                    typeFactory.constructParametricType(EntityModel.class, Person.class));

                EntityModel<EntityModel<Person>> expected = new EntityModel<>(new EntityModel<>(new Person("Peter", 33)));

                EntityModel<EntityModel<Person>> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_integer() throws Exception {
                String source = readResource("entity/containing_integer.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Integer.class);
                EntityModel<Integer> expected = new EntityModel<>(42);

                EntityModel<Person> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_and_entitymodel_containing_pojo_and_link() throws Exception {
                String source = readResource("entity/containing_link_and_entitymodel_containing_pojo_and_link.json");

                TypeFactory typeFactory = objectMapper.getTypeFactory();
                JavaType expectedType = typeFactory.constructParametricType(EntityModel.class,
                    typeFactory.constructParametricType(EntityModel.class, Person.class));

                EntityModel<Person> contained =
                    new EntityModel<>(new Person("Peter", 33), new Link("/departments/6/persons/1", SELF));
                EntityModel<EntityModel<Person>> expected =
                    new EntityModel<>(contained, new Link("/departments/6/persons", SELF));

                EntityModel<EntityModel<Person>> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                String source = readResource("entity/containing_pojo_and_self_link.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Person.class);
                EntityModel<Person> expected = new EntityModel<>(new Person("Peter", 33), new Link("/persons/1", SELF));

                EntityModel<Person> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                String source = readResource("entity/containing_pojo.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Person.class);
                EntityModel<Person> expected = new EntityModel<>(new Person("Peter", 33));

                EntityModel<Person> actual = read(source, expectedType);
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
                EntityModel<Person> entityModel = new EntityModel<>(new Person("Peter", 33), new Link("/persons/1", SELF));
                PagedModel<EntityModel<Person>> source = new PagedModel<>(newArrayList(entityModel), new PageMetadata(20, 0, 1));
                String expected = readResource("paged/containing_entitymodel_containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                EntityModel<Person> entityModel = new EntityModel<>(new Person("Peter", 33));
                PagedModel<EntityModel<Person>> source = new PagedModel<>(newArrayList(entityModel), new PageMetadata(20, 0, 1));
                String expected = readResource("paged/containing_entitymodel_containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                PagedModel<Person> source = new PagedModel<>(newArrayList(new Person("Peter", 33)), new PageMetadata(20, 0, 1));
                String expected = readResource("paged/containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void with_self_link() throws Exception {
                PagedModel<?> source = new PagedModel<>(newArrayList(), new PageMetadata(20, 0, 0),
                    enhance(new Link("/persons", SELF), pagingParams(0, 20)));
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
                EntityModel<Person> entityModel = new EntityModel<>(new Person("Peter", 33), new Link("/persons/1", SELF));
                CollectionModel<EntityModel<Person>> expected = new CollectionModel<>(newArrayList(entityModel));

                CollectionModel<EntityModel<Person>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Person>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                String source = readResource("paged/containing_entitymodel_containing_pojo.json");
                EntityModel<Person> entityModel = new EntityModel<>(new Person("Peter", 33));
                PagedModel<EntityModel<Person>> expected =
                    new PagedModel<>(newArrayList(entityModel), new PageMetadata(20, 0, 1));

                PagedModel<EntityModel<Person>> actual = read(source, new TypeReference<PagedModel<EntityModel<Person>>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                String source = readResource("paged/containing_pojo.json");
                PagedModel<Person> expected = new PagedModel<>(newArrayList(new Person("Peter", 33)), new PageMetadata(20, 0, 1));

                PagedModel<Person> actual = read(source, new TypeReference<PagedModel<Person>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void with_self_link() throws Exception {
                String source = readResource("paged/with_self_link.json");
                PagedModel<?> expected = new PagedModel<>(newArrayList(), new PageMetadata(20, 0, 0),
                    enhance(new Link("/persons", SELF), pagingParams(0, 20)));

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
                Link link = of(new Link("/persons/1", SELF)).afford(DELETE).withName("delete").toLink();
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
                Link link = of(new Link("/persons/1", SELF)).afford(PUT).withInput(Person.class).withName("update").toLink();
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representation/containing_link_with_update_affordance.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_with_update_and_delete_affordances() throws Exception {
                Link link = of(new Link("/persons/1", SELF)).afford(PUT).withInput(Person.class).withName("update")
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
                RepresentationModel<?> source = new RepresentationModel<>(newArrayList(new Link("/persons", SELF),
                    new Link("/about", ABOUT), new Link("/help", HELP), new Link("/license", LICENSE)));
                String expected = readResource("representation/containing_links.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_link() throws Exception {
                RepresentationModel<?> source = new PersonModel("Peter", 33);
                source.add(new Link("/person", SELF));
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
                RepresentationModel<?> expected = new RepresentationModel<>(newArrayList(new Link("/persons", SELF),
                    new Link("/about", ABOUT), new Link("/help", HELP), new Link("/license", LICENSE)));

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_link() throws Exception {
                String source = readResource("representation/having_properties_containing_link.json");
                RepresentationModel<?> expected = new PersonModel("Peter", 33);
                expected.add(new Link("/person", SELF));

                RepresentationModel<?> actual = read(source, PersonModel.class);
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
        return ResourceReader.read(new ClassPathResource(sourceFilename, getClass()));
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
