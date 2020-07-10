package com.github.ingogriebsch.spring.hateoas.siren;

import static java.util.stream.Collectors.toList;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Streams.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.iterable;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.springframework.hateoas.IanaLinkRelations.SELF;

import java.util.Collection;
import java.util.List;

import com.github.ingogriebsch.spring.hateoas.siren.SirenModel.EmbeddedRepresentation;
import com.github.ingogriebsch.spring.hateoas.siren.support.Capital;
import com.github.ingogriebsch.spring.hateoas.siren.support.Person;
import com.github.ingogriebsch.spring.hateoas.siren.support.PersonModel;
import com.github.ingogriebsch.spring.hateoas.siren.support.State;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

class SirenModelBuilderTest {

    @Nested
    class Builder {

        @Test
        void should_return_new_instance() {
            assertThat(SirenModelBuilder.sirenModel()).isNotNull().isNotEqualTo(SirenModelBuilder.sirenModel());
        }
    }

    @Nested
    class Build {

        @Test
        void should_return_siren_model_not_having_any_members_set() {
            RepresentationModel<?> model = SirenModelBuilder.sirenModel().build();
            assertThat(model).isNotNull().isInstanceOf(SirenModel.class);

            SirenModel sirenModel = (SirenModel) model;
            assertThat(sirenModel.getClasses()).isEmpty();
            assertThat(sirenModel.getEntities()).isEmpty();
            assertThat(sirenModel.getLinks()).isEmpty();
            assertThat(sirenModel.getProperties()).isNull();
            assertThat(sirenModel.getTitle()).isNull();
        }

        @Nested
        class WithClasses {

            @Test
            void should_throw_exception_if_single_string_was_null() {
                assertThatThrownBy(() -> SirenModelBuilder.sirenModel().classes((String) null))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_throw_exception_if_iterable_of_string_was_null() {
                assertThatThrownBy(() -> SirenModelBuilder.sirenModel().classes((Iterable<String>) null))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_return_siren_model_if_single_string_was_used() {
                String clazz = "class1";
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().classes(clazz).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getClasses, list(String.class)) //
                    .containsExactly(clazz);
            }

            @Test
            void should_return_siren_model_if_iterable_of_strings_was_used() {
                List<String> classes = newArrayList("class1", "class2");
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().classes(classes).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getClasses, list(String.class)) //
                    .containsExactlyElementsOf(classes);
            }
        }

        @Nested
        class WithEntities {

            @Test
            void should_throw_exception_if_single_representation_model_was_null() {
                assertThatThrownBy(() -> SirenModelBuilder.sirenModel().entities((RepresentationModel<?>) null))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_throw_exception_if_iterable_of_representation_models_was_null() {
                assertThatThrownBy(() -> SirenModelBuilder.sirenModel().entities((Iterable<?>) null))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_throw_exception_if_rel_and_iterable_of_representation_models_was_null() {
                assertThatThrownBy(() -> SirenModelBuilder.sirenModel().entities((String) null, (Iterable<?>) null))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_return_siren_model_if_single_pojo_was_used() {
                Person entity = new Person("Peter", 33);
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().entities(entity).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(EmbeddedRepresentation.class)) //
                    .containsExactly(new EmbeddedRepresentation(EntityModel.of(entity)));
            }

            @Test
            void should_return_siren_model_if_single_reprensentation_model_was_used() {
                RepresentationModel<?> entity = new PersonModel("Peter", 46);
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().entities(entity).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(EmbeddedRepresentation.class)) //
                    .containsExactly(new EmbeddedRepresentation(entity));
            }

            @Test
            void should_return_siren_model_if_single_entity_model_was_used() {
                State state = new State("Pennsylvania", new Capital("Philadelphia"));
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().entities(state).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(EmbeddedRepresentation.class)) //
                    .containsExactly(new EmbeddedRepresentation(state));
            }

            @Test
            void should_return_siren_model_if_iterable_of_representation_models_was_used() {
                Iterable<RepresentationModel<?>> entities =
                    newArrayList(new PersonModel("Peter", 22), new PersonModel("Paul", 35));
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().entities(entities).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(EmbeddedRepresentation.class)) //
                    .containsExactlyElementsOf(stream(entities).map(e -> new EmbeddedRepresentation(e)).collect(toList()));
            }

            @Test
            void should_return_siren_model_if_iterable_of_pojos_was_used() {
                Iterable<Person> entities = newArrayList(new Person("Peter", 33), new Person("Paul", 44));
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().entities(entities).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(EmbeddedRepresentation.class)) //
                    .containsExactlyElementsOf(
                        stream(entities).map(e -> new EmbeddedRepresentation(EntityModel.of(e))).collect(toList()));
            }

            @Test
            void should_return_siren_model_if_rel_and_single_representation_model_was_used() {
                String rel = "rel";
                RepresentationModel<?> entity = new PersonModel("Peter", 31);
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().entities(rel, entity).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(EmbeddedRepresentation.class)) //
                    .containsExactly(new EmbeddedRepresentation(entity, rel));
            }

            @Test
            void should_return_siren_model_if_rel_and_iterable_of_representation_models_was_used() {
                String rel = "rel";
                Iterable<RepresentationModel<?>> entities =
                    newArrayList(new PersonModel("Peter", 27), new PersonModel("Paul", 55));
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().entities(rel, entities).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(EmbeddedRepresentation.class)) //
                    .containsExactlyElementsOf(stream(entities).map(e -> new EmbeddedRepresentation(e, rel)).collect(toList()));
            }

            @Test
            void should_return_siren_model_if_different_rels_and_their_iterables_of_representation_models_was_used() {
                List<EmbeddedRepresentation> entities = newArrayList( //
                    new EmbeddedRepresentation(new PersonModel("Pete", 22), "rel1"), //
                    new EmbeddedRepresentation(new PersonModel("Paul", 33), "rel1"), //
                    new EmbeddedRepresentation(new PersonModel("Mary", 34), "rel2") //
                );

                SirenModelBuilder builder = SirenModelBuilder.sirenModel();
                entities.stream().forEach(e -> builder.entities(e.getRels().iterator().next(), e.getModel()));
                RepresentationModel<?> model = builder.build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(EmbeddedRepresentation.class)) //
                    .containsExactlyElementsOf(entities);
            }
        }

        @Nested
        class WithLinksAndActions {

            @Test
            void should_throw_exception_if_single_link_was_null() {
                assertThatThrownBy(() -> SirenModelBuilder.sirenModel().linksAndActions((Link) null))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_throw_exception_if_iterable_of_link_was_null() {
                assertThatThrownBy(() -> SirenModelBuilder.sirenModel().linksAndActions((Collection<Link>) null))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_return_siren_model_if_single_link_was_used() {
                Link link = Link.of("/about");
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().linksAndActions(link).build();

                assertThat(model).asInstanceOf(type(SirenModel.class)).extracting(SirenModel::getLinks, iterable(Link.class))
                    .containsExactly(link);
            }

            @Test
            void should_return_siren_model_if_iterable_of_links_was_used() {
                Iterable<Link> links = newArrayList(Link.of("/about"), Link.of("/help"));
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().linksAndActions(links).build();

                assertThat(model).asInstanceOf(type(SirenModel.class)).extracting(SirenModel::getLinks, iterable(Link.class))
                    .containsExactlyElementsOf(links);
            }

            @Test
            void should_return_siren_model_if_different_links_with_same_rel_was_used() {
                Link link = Link.of("/about", SELF);
                Link override = Link.of("/help", SELF);
                RepresentationModel<?> model =
                    SirenModelBuilder.sirenModel().linksAndActions(link).linksAndActions(override).build();

                assertThat(model).asInstanceOf(type(SirenModel.class)).extracting(SirenModel::getLinks, iterable(Link.class))
                    .containsExactly(override);
            }
        }

        @Nested
        class WithProperties {

            @Test
            void should_return_siren_model_if_simple_pojo_was_used() {
                Object properties = new Person("Peter", 22);
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().properties(properties).build();

                assertThat(model).asInstanceOf(type(SirenModel.class)).extracting(SirenModel::getProperties)
                    .isEqualTo(properties);
            }
        }

        @Nested
        class WithTitle {

            @Test
            void should_return_siren_model_if_string_was_used() {
                String title = "title";
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().title(title).build();

                assertThat(model).asInstanceOf(type(SirenModel.class)).hasFieldOrPropertyWithValue(title, title);
            }
        }
    }
}
