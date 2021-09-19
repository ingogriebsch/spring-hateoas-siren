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

import static java.util.Collections.singletonMap;

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
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
import de.ingogriebsch.spring.hateoas.siren.support.Capital;
import de.ingogriebsch.spring.hateoas.siren.support.Country;
import de.ingogriebsch.spring.hateoas.siren.support.Person;
import de.ingogriebsch.spring.hateoas.siren.support.PersonModel;
import de.ingogriebsch.spring.hateoas.siren.support.ResourceReader;
import de.ingogriebsch.spring.hateoas.siren.support.State;
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
        SirenConfiguration configuration = new SirenConfiguration().withEntityAndCollectionModelSubclassingEnabled(true);

        SirenMediaTypeConfiguration sirenMediaTypeConfiguration = SirenMediaTypeConfiguration.of(DEFAULTS_ONLY, configuration,
            SirenEntityClassProvider.DEFAULT_INSTANCE, SirenEntityRelProvider.DEFAULT_INSTANCE,
            new TypeBasedSirenActionFieldTypeConverter(), RepresentationModelFactories.DEFAULT_INSTANCE);

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
                    CollectionModel.of(newArrayList(EntityModel.of(new Person("Peter", 33), Link.of("/persons/1", SELF))));
                String expected = readResource("collection-model/containing_entitymodel_containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                CollectionModel<?> source = CollectionModel.of(newArrayList(EntityModel.of(new Person("Peter", 33))));
                String expected = readResource("collection-model/containing_entitymodel_containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodels() throws Exception {
                List<EntityModel<Person>> content = newArrayList( //
                    EntityModel.of(new Person("Peter", 33), Link.of("/persons/1", "person")), //
                    EntityModel.of(new Person("Paul", 44), Link.of("/persons/2", "person")), //
                    EntityModel.of(new Person("Sarah", 55), Link.of("/persons/3", "person")), //
                    EntityModel.of(new Person("Stephanie", 66), Link.of("/persons/4", "person")) //
                );
                CollectionModel<EntityModel<Person>> source = CollectionModel.of(content, Link.of("/persons", SELF));
                String expected = readResource("collection-model/containing_entitymodels.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                CollectionModel<Person> source =
                    CollectionModel.of(newArrayList(new Person("Peter", 33)), Link.of("/persons", SELF));
                String expected = readResource("collection-model/containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                CollectionModel<Person> source = CollectionModel.of(newArrayList(new Person("Peter", 33)));
                String expected = readResource("collection-model/containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_entitymodel_containing_representationmodel() throws Exception {
                Country source = new Country("America", newArrayList(new State("California", new Capital("Sacramento"))));
                String expected =
                    readResource("collection-model/having_properties_containing_entitymodel_containing_representationmodel.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void with_self_link() throws Exception {
                CollectionModel<?> source = CollectionModel.of(newArrayList(), Link.of("/persons", SELF));
                String expected = readResource("collection-model/with_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_content() throws Exception {
                CollectionModel<?> source = CollectionModel.of(newArrayList());
                String expected = readResource("collection-model/without_content.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Deserialize {

            @Test
            void containing_entitymodel_containing_pojo_and_self_link() throws Exception {
                String source = readResource("collection-model/containing_entitymodel_containing_pojo_and_self_link.json");
                EntityModel<Person> entityModel = EntityModel.of(new Person("Peter", 33), Link.of("/persons/1", SELF));
                CollectionModel<EntityModel<Person>> expected = CollectionModel.of(newArrayList(entityModel));

                CollectionModel<EntityModel<Person>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Person>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                String source = readResource("collection-model/containing_entitymodel_containing_pojo.json");
                EntityModel<Person> entityModel = EntityModel.of(new Person("Peter", 33));
                CollectionModel<EntityModel<Person>> expected = CollectionModel.of(newArrayList(entityModel));

                CollectionModel<EntityModel<Person>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Person>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodels() throws Exception {
                String source = readResource("collection-model/containing_entitymodels.json");
                List<EntityModel<Person>> content = newArrayList( //
                    EntityModel.of(new Person("Peter", 33), Link.of("/persons/1", "person")), //
                    EntityModel.of(new Person("Paul", 44), Link.of("/persons/2", "person")), //
                    EntityModel.of(new Person("Sarah", 55), Link.of("/persons/3", "person")), //
                    EntityModel.of(new Person("Stephanie", 66), Link.of("/persons/4", "person")) //
                );
                CollectionModel<EntityModel<Person>> expected = CollectionModel.of(content, Link.of("/persons", SELF));

                CollectionModel<EntityModel<Person>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Person>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                String source = readResource("collection-model/containing_pojo_and_self_link.json");
                CollectionModel<Person> expected =
                    CollectionModel.of(newArrayList(new Person("Peter", 33)), Link.of("/persons", SELF));

                CollectionModel<Person> actual = read(source, new TypeReference<CollectionModel<Person>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                String source = readResource("collection-model/containing_pojo.json");
                CollectionModel<Person> expected = CollectionModel.of(newArrayList(new Person("Peter", 33)));

                CollectionModel<Person> actual = read(source, new TypeReference<CollectionModel<Person>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_entitymodel_containing_representationmodel() throws Exception {
                String source =
                    readResource("collection-model/having_properties_containing_entitymodel_containing_representationmodel.json");
                Country expected = new Country("America", newArrayList(new State("California", new Capital("Sacramento"))));

                Country actual = read(source, Country.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void with_self_link() throws Exception {
                String source = readResource("collection-model/with_self_link.json");
                CollectionModel<?> expected = CollectionModel.of(newArrayList(), Link.of("/persons", SELF));

                CollectionModel<?> actual = read(source, CollectionModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_content() throws Exception {
                String source = readResource("collection-model/without_content.json");
                CollectionModel<?> expected = CollectionModel.of(newArrayList());

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
                EntityModel<EntityModel<Person>> source = EntityModel.of(EntityModel.of(new Person("Peter", 33)));
                String expected = readResource("entity-model/containing_entitymodel_containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_integer() throws Exception {
                EntityModel<Integer> source = EntityModel.of(42);
                String expected = readResource("entity-model/containing_integer.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_and_entitymodel_containing_pojo_and_link() throws Exception {
                EntityModel<Person> contained =
                    EntityModel.of(new Person("Peter", 33), Link.of("/departments/6/persons/1", SELF));
                EntityModel<EntityModel<Person>> source = EntityModel.of(contained, Link.of("/departments/6/persons", SELF));
                String expected = readResource("entity-model/containing_link_and_entitymodel_containing_pojo_and_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                EntityModel<Person> source = EntityModel.of(new Person("Peter", 33), Link.of("/persons/1", SELF));
                String expected = readResource("entity-model/containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                EntityModel<Person> source = EntityModel.of(new Person("Peter", 33));
                String expected = readResource("entity-model/containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_string() throws Exception {
                EntityModel<String> source = EntityModel.of("Something");
                String expected = readResource("entity-model/containing_string.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_representationmodel() throws Exception {
                State source = new State("California", new Capital("Sacramento"));
                String expected = readResource("entity-model/having_properties_containing_representationmodel.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Deserialize {

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                String source = readResource("entity-model/containing_entitymodel_containing_pojo.json");

                TypeFactory typeFactory = objectMapper.getTypeFactory();
                JavaType expectedType = typeFactory.constructParametricType(EntityModel.class,
                    typeFactory.constructParametricType(EntityModel.class, Person.class));

                EntityModel<EntityModel<Person>> expected = EntityModel.of(EntityModel.of(new Person("Peter", 33)));

                EntityModel<EntityModel<Person>> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_integer() throws Exception {
                String source = readResource("entity-model/containing_integer.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Integer.class);
                EntityModel<Integer> expected = EntityModel.of(42);

                EntityModel<Person> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_and_entitymodel_containing_pojo_and_link() throws Exception {
                String source = readResource("entity-model/containing_link_and_entitymodel_containing_pojo_and_link.json");

                TypeFactory typeFactory = objectMapper.getTypeFactory();
                JavaType expectedType = typeFactory.constructParametricType(EntityModel.class,
                    typeFactory.constructParametricType(EntityModel.class, Person.class));

                EntityModel<Person> contained =
                    EntityModel.of(new Person("Peter", 33), Link.of("/departments/6/persons/1", SELF));
                EntityModel<EntityModel<Person>> expected = EntityModel.of(contained, Link.of("/departments/6/persons", SELF));

                EntityModel<EntityModel<Person>> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo_and_self_link() throws Exception {
                String source = readResource("entity-model/containing_pojo_and_self_link.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Person.class);
                EntityModel<Person> expected = EntityModel.of(new Person("Peter", 33), Link.of("/persons/1", SELF));

                EntityModel<Person> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                String source = readResource("entity-model/containing_pojo.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, Person.class);
                EntityModel<Person> expected = EntityModel.of(new Person("Peter", 33));

                EntityModel<Person> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_string() throws Exception {
                String source = readResource("entity-model/containing_string.json");
                JavaType expectedType = objectMapper.getTypeFactory().constructParametricType(EntityModel.class, String.class);
                EntityModel<String> expected = EntityModel.of("Something");

                EntityModel<String> actual = read(source, expectedType);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_representationmodel() throws Exception {
                String source = readResource("entity-model/having_properties_containing_representationmodel.json");
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
                EntityModel<Person> entityModel = EntityModel.of(new Person("Peter", 33), Link.of("/persons/1", SELF));
                PagedModel<EntityModel<Person>> source = PagedModel.of(newArrayList(entityModel), new PageMetadata(20, 0, 1));
                String expected = readResource("paged-model/containing_entitymodel_containing_pojo_and_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                EntityModel<Person> entityModel = EntityModel.of(new Person("Peter", 33));
                PagedModel<EntityModel<Person>> source = PagedModel.of(newArrayList(entityModel), new PageMetadata(20, 0, 1));
                String expected = readResource("paged-model/containing_entitymodel_containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                PagedModel<Person> source = PagedModel.of(newArrayList(new Person("Peter", 33)), new PageMetadata(20, 0, 1));
                String expected = readResource("paged-model/containing_pojo.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void with_self_link() throws Exception {
                PagedModel<?> source = PagedModel.of(newArrayList(), new PageMetadata(20, 0, 0),
                    enhance(Link.of("/persons", SELF), pagingParams(0, 20)));
                String expected = readResource("paged-model/with_self_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_content() throws Exception {
                PagedModel<?> source = PagedModel.of(newArrayList(), new PageMetadata(20, 0, 0));
                String expected = readResource("paged-model/without_content.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

        }

        @Nested
        class Deserialize {

            @Test
            void containing_entitymodel_containing_pojo_and_self_link() throws Exception {
                String source = readResource("paged-model/containing_entitymodel_containing_pojo_and_self_link.json");
                EntityModel<Person> entityModel = EntityModel.of(new Person("Peter", 33), Link.of("/persons/1", SELF));
                CollectionModel<EntityModel<Person>> expected = CollectionModel.of(newArrayList(entityModel));

                CollectionModel<EntityModel<Person>> actual =
                    read(source, new TypeReference<CollectionModel<EntityModel<Person>>>() {
                    });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entitymodel_containing_pojo() throws Exception {
                String source = readResource("paged-model/containing_entitymodel_containing_pojo.json");
                EntityModel<Person> entityModel = EntityModel.of(new Person("Peter", 33));
                PagedModel<EntityModel<Person>> expected = PagedModel.of(newArrayList(entityModel), new PageMetadata(20, 0, 1));

                PagedModel<EntityModel<Person>> actual = read(source, new TypeReference<PagedModel<EntityModel<Person>>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_pojo() throws Exception {
                String source = readResource("paged-model/containing_pojo.json");
                PagedModel<Person> expected = PagedModel.of(newArrayList(new Person("Peter", 33)), new PageMetadata(20, 0, 1));

                PagedModel<Person> actual = read(source, new TypeReference<PagedModel<Person>>() {
                });
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void with_self_link() throws Exception {
                String source = readResource("paged-model/with_self_link.json");
                PagedModel<?> expected = PagedModel.of(newArrayList(), new PageMetadata(20, 0, 0),
                    enhance(Link.of("/persons", SELF), pagingParams(0, 20)));

                PagedModel<?> actual = read(source, PagedModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_content() throws Exception {
                String source = readResource("paged-model/without_content.json");
                PagedModel<?> expected = PagedModel.of(newArrayList(), new PageMetadata(20, 0, 0));

                PagedModel<?> actual = read(source, PagedModel.class);
                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Nested
    class Representation {

        @Nested
        @SuppressWarnings("deprecation")
        class Serialize {

            @Test
            void containing_link_with_delete_affordance() throws Exception {
                Link link = of(Link.of("/persons/1", SELF)).afford(DELETE).withName("delete").toLink();
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representation-model/containing_link_with_delete_affordance.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_with_title() throws Exception {
                Link link = Link.of("/about", ABOUT).withTitle("about");
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representation-model/containing_link_with_title.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_with_update_affordance() throws Exception {
                Link link = of(Link.of("/persons/1", SELF)).afford(PUT).withInput(Person.class)
                    .withInputMediaType(APPLICATION_JSON).withName("update").toLink();
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representation-model/containing_link_with_update_affordance.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link_with_update_and_delete_affordances() throws Exception {
                Link link = of(Link.of("/persons/1", SELF)).afford(PUT).withInput(Person.class)
                    .withInputMediaType(APPLICATION_JSON).withName("update").andAfford(DELETE).withName("delete").toLink();
                RepresentationModel<?> source = new RepresentationModel<>(link);
                String expected = readResource("representation-model/containing_link_with_update_and_delete_affordances.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>(Link.of("/about", ABOUT));
                String expected = readResource("representation-model/containing_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_links() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>(newArrayList(Link.of("/persons", SELF),
                    Link.of("/about", ABOUT), Link.of("/help", HELP), Link.of("/license", LICENSE)));
                String expected = readResource("representation-model/containing_links.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_link() throws Exception {
                RepresentationModel<?> source = new PersonModel("Peter", 33);
                source.add(Link.of("/person", SELF));
                String expected = readResource("representation-model/having_properties_containing_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_links() throws Exception {
                RepresentationModel<?> source = new RepresentationModel<>();
                String expected = readResource("representation-model/without_links.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class Deserialize {

            @Test
            void containing_link() throws Exception {
                String source = readResource("representation-model/containing_link.json");
                RepresentationModel<?> expected = new RepresentationModel<>(Link.of("/about", ABOUT));

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_links() throws Exception {
                String source = readResource("representation-model/containing_links.json");
                RepresentationModel<?> expected = new RepresentationModel<>(newArrayList(Link.of("/persons", SELF),
                    Link.of("/about", ABOUT), Link.of("/help", HELP), Link.of("/license", LICENSE)));

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void having_properties_containing_link() throws Exception {
                String source = readResource("representation-model/having_properties_containing_link.json");
                RepresentationModel<?> expected = new PersonModel("Peter", 33);
                expected.add(Link.of("/person", SELF));

                RepresentationModel<?> actual = read(source, PersonModel.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void without_links() throws Exception {
                String source = readResource("representation-model/without_links.json");
                RepresentationModel<?> expected = new RepresentationModel<>();

                RepresentationModel<?> actual = read(source, RepresentationModel.class);
                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Nested
    class Siren {

        @Nested
        class Serialize {

            @Test
            void containing_class_and_link_and_title_and_representation_models_as_entities_and_properties() throws Exception {
                RepresentationModel<?> source =
                    SirenModelBuilder.sirenModel().classes("departement").properties(singletonMap("name", "Development"))
                        .entities(new PersonModel("Peter", 33), new PersonModel("Paul", 44)).title("Development")
                        .linksAndActions(Link.of("/departement", SELF)).build();

                String expected = readResource(
                    "siren-model/containing_class_and_link_and_title_and_representation_models_as_entities_and_properties.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_class_and_link_and_title() throws Exception {
                RepresentationModel<?> source = SirenModelBuilder.sirenModel().classes("about")
                    .linksAndActions(Link.of("/about", ABOUT)).title("about").build();
                String expected = readResource("siren-model/containing_class_and_link_and_title.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_class_and_link() throws Exception {
                RepresentationModel<?> source =
                    SirenModelBuilder.sirenModel().classes("about").linksAndActions(Link.of("/about", ABOUT)).build();
                String expected = readResource("siren-model/containing_class_and_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_entity_model_as_entities() throws Exception {
                RepresentationModel<?> source =
                    SirenModelBuilder.sirenModel().classes("country").properties(singletonMap("name", "United States of America"))
                        .entities("states", new State("Pennsylvania", new Capital("Philadelphia"))).build();
                String expected = readResource("siren-model/containing_entity_model_as_entities.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_link() throws Exception {
                RepresentationModel<?> source = SirenModelBuilder.sirenModel().linksAndActions(Link.of("/about", ABOUT)).build();
                String expected = readResource("siren-model/containing_link.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_properties() throws Exception {
                RepresentationModel<?> source =
                    SirenModelBuilder.sirenModel().classes("representation").properties(new Person("Peter", 33)).build();
                String expected = readResource("siren-model/containing_properties.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_representation_model_as_entities_with_rel() throws Exception {
                RepresentationModel<?> source =
                    SirenModelBuilder.sirenModel().entities("child", new PersonModel("Peter", 33)).build();
                String expected = readResource("siren-model/containing_representation_model_as_entities_with_rel.json");

                String actual = write(source);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            void containing_representation_model_as_entities() throws Exception {
                RepresentationModel<?> source =
                    SirenModelBuilder.sirenModel().classes("representation").entities(new PersonModel("Peter", 33)).build();
                String expected = readResource("siren-model/containing_representation_model_as_entities.json");

                String actual = write(source);
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
        return Link.of(of(builder.build().toString()), SELF);
    }
}
