package com.github.ingogriebsch.spring.hateoas.siren;

import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JavaType;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

class EntityModelFactoryTest {

    @Test
    void create_should_throw_exception_if_input_is_null() {
        EntityModelFactory factory = new EntityModelFactory() {
        };

        assertThatThrownBy(() -> factory.create(null, null, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_should_throw_exception_if_type_is_not_matching() {
        EntityModelFactory factory = new EntityModelFactory() {
        };

        JavaType type = defaultInstance().constructSimpleType(String.class, null);
        assertThatThrownBy(() -> factory.create(type, newArrayList(), new Object()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_should_return_entity_model_containing_given_links() {
        EntityModelFactory factory = new EntityModelFactory() {
        };

        JavaType type = defaultInstance().constructParametricType(EntityModel.class, String.class);
        Iterable<Link> links = newArrayList(new Link("href1"), new Link("href2"));
        EntityModel<?> model = factory.create(type, links, "content");

        assertThat(model).isNotNull();
        assertThat(model.getLinks()).isNotNull();
        assertThat(model.getLinks().toList()).isEqualTo(links);
    }

}
