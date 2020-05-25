package com.github.ingogriebsch.spring.hateoas.siren;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.support.Employee;

class RepresentationModelUtilsTest {

    @Nested
    class IsRepresentationModel {

        @ParameterizedTest
        @ValueSource(classes = { RepresentationModel.class, EntityModel.class, CollectionModel.class, PagedModel.class,
            EntityModelExtension.class, CollectionModelExtension.class })
        void should_return_true_if_type_matches(Class<?> clazz) {
            assertThat(RepresentationModelUtils.isRepresentationModel(clazz)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(classes = { String.class, Object.class, Integer.class, Employee.class })
        void should_return_false_if_type_does_not_match(Class<?> clazz) {
            assertThat(RepresentationModelUtils.isRepresentationModel(clazz)).isFalse();
        }
    }

    @Nested
    class IsRepresentationModelSubclass {

        @ParameterizedTest
        @ValueSource(classes = { EntityModelExtension.class, CollectionModelExtension.class })
        void should_return_true_if_type_matches(Class<?> clazz) {
            assertThat(RepresentationModelUtils.isRepresentationModelSubclass(clazz)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(classes = { RepresentationModel.class, EntityModel.class, CollectionModel.class, PagedModel.class,
            String.class, Object.class, Integer.class, Employee.class })
        void should_return_false_if_type_does_not_match(Class<?> clazz) {
            assertThat(RepresentationModelUtils.isRepresentationModelSubclass(clazz)).isFalse();
        }
    }

    private static class EntityModelExtension extends EntityModel<Object> {
    }

    private static class CollectionModelExtension extends CollectionModel<Object> {
    }
}
