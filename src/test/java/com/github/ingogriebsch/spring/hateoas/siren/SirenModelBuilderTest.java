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

import java.util.List;

import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import lombok.EqualsAndHashCode;
import lombok.Value;

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
                assertThatThrownBy(() -> SirenModelBuilder.sirenModel().entities((Iterable<RepresentationModel<?>>) null))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_throw_exception_if_rel_and_iterable_of_representation_models_was_null() {
                assertThatThrownBy(
                    () -> SirenModelBuilder.sirenModel().entities((String) null, (Iterable<RepresentationModel<?>>) null))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_return_siren_model_if_single_reprensentation_model_was_used() {
                RepresentationModel<?> entity = new RepresentationModelExtension("Peter");
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().entities(entity).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(SirenEmbeddedRepresentation.class)) //
                    .containsExactly(new SirenEmbeddedRepresentation(entity));
            }

            @Test
            void should_return_siren_model_if_iterable_of_representation_models_was_used() {
                Iterable<RepresentationModel<?>> entities =
                    newArrayList(new RepresentationModelExtension("Peter"), new RepresentationModelExtension("Paul"));
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().entities(entities).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(SirenEmbeddedRepresentation.class)) //
                    .containsExactlyElementsOf(stream(entities).map(e -> new SirenEmbeddedRepresentation(e)).collect(toList()));
            }

            @Test
            void should_return_siren_model_if_rel_and_single_representation_model_was_used() {
                String rel = "rel";
                RepresentationModel<?> entity = new RepresentationModelExtension("Peter");
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().entities(rel, entity).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(SirenEmbeddedRepresentation.class)) //
                    .containsExactly(new SirenEmbeddedRepresentation(entity, rel));
            }

            @Test
            void should_return_siren_model_if_rel_and_iterable_of_representation_models_was_used() {
                String rel = "rel";
                Iterable<RepresentationModel<?>> entities =
                    newArrayList(new RepresentationModelExtension("Peter"), new RepresentationModelExtension("Paul"));
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().entities(rel, entities).build();

                assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(SirenEmbeddedRepresentation.class)) //
                    .containsExactlyElementsOf(
                        stream(entities).map(e -> new SirenEmbeddedRepresentation(e, rel)).collect(toList()));
            }

            @Test
            void should_return_siren_model_if_different_rels_and_their_iterables_of_representation_models_was_used() {
                List<SirenEmbeddedRepresentation> entities = newArrayList( //
                    new SirenEmbeddedRepresentation(new RepresentationModelExtension("Pete"), "rel1"), //
                    new SirenEmbeddedRepresentation(new RepresentationModelExtension("Paul"), "rel1"), //
                    new SirenEmbeddedRepresentation(new RepresentationModelExtension("Mary"), "rel2") //
                );

                SirenModelBuilder builder = SirenModelBuilder.sirenModel();
                entities.stream().forEach(e -> builder.entities(e.getRels().iterator().next(), e.getModel()));
                RepresentationModel<?> model = builder.build();

                ListAssert<SirenEmbeddedRepresentation> extracting = assertThat(model) //
                    .asInstanceOf(type(SirenModel.class)) //
                    .extracting(SirenModel::getEntities, list(SirenEmbeddedRepresentation.class));
                extracting.containsExactlyElementsOf(entities);
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
                assertThatThrownBy(() -> SirenModelBuilder.sirenModel().linksAndActions((Iterable<Link>) null))
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
            void should_throw_exception_if_input_was_null() {
                assertThatThrownBy(() -> SirenModelBuilder.sirenModel().properties(null))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_return_siren_model_if_simple_pojo_was_used() {
                Object properties = new Pojo("Peter");
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().properties(properties).build();

                assertThat(model).asInstanceOf(type(SirenModel.class)).extracting(SirenModel::getProperties)
                    .isEqualTo(properties);
            }
        }

        @Nested
        class WithTitle {

            @Test
            void should_throw_exception_if_input_was_null() {
                assertThatThrownBy(() -> SirenModelBuilder.sirenModel().title(null)).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_return_siren_model_if_string_was_used() {
                String title = "title";
                RepresentationModel<?> model = SirenModelBuilder.sirenModel().title(title).build();

                assertThat(model).asInstanceOf(type(SirenModel.class)).hasFieldOrPropertyWithValue(title, title);
            }
        }
    }

    @Value
    private static class Pojo {

        String name;
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    private static class RepresentationModelExtension extends RepresentationModel<RepresentationModelExtension> {

        String name;
    }
}
