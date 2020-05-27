package com.github.ingogriebsch.spring.hateoas.siren;

import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

class RepresentationModelFactoryTest {

    @Test
    void create_should_throw_exception_if_input_is_null() {
        RepresentationModelFactory factory = new RepresentationModelFactory() {
        };

        assertThatThrownBy(() -> factory.create(null, null, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_should_throw_exception_if_type_is_not_matching() {
        RepresentationModelFactory factory = new RepresentationModelFactory() {
        };

        JavaType type = defaultInstance().constructSimpleType(String.class, null);
        assertThatThrownBy(() -> factory.create(type, newArrayList(), null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_should_return_representation_model_containing_given_links() {
        RepresentationModelFactory factory = new RepresentationModelFactory() {
        };

        JavaType type = defaultInstance().constructParametricType(RepresentationModel.class, String.class);
        Iterable<Link> links = newArrayList(new Link("href1"), new Link("href2"));
        RepresentationModel<?> model = factory.create(type, links, null);

        assertThat(model).isNotNull();
        assertThat(model.getLinks()).isNotNull();
        assertThat(model.getLinks().toList()).isEqualTo(links);
    }

    @Test
    void create_should_return_representation_model_having_given_properties() {
        RepresentationModelFactory factory = new RepresentationModelFactory() {
        };

        JavaType type = defaultInstance().constructSimpleType(Capital.class, null);
        Map<String, Object> properties = Collections.singletonMap("name", "Sacramento");
        RepresentationModel<?> model = factory.create(type, newArrayList(), properties);

        assertThat(model).isNotNull().isInstanceOf(Capital.class).hasFieldOrPropertyWithValue("name", "Sacramento");
    }

    @Test
    void create_should_return_representation_model_not_having_properties_if_properties_not_given() {
        RepresentationModelFactory factory = new RepresentationModelFactory() {
        };

        JavaType type = defaultInstance().constructSimpleType(Capital.class, null);
        RepresentationModel<?> model = factory.create(type, newArrayList(), null);

        assertThat(model).isNotNull().isInstanceOf(Capital.class).hasAllNullFieldsOrPropertiesExcept("links");
    }
}
