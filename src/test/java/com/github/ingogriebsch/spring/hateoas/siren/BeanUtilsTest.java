package com.github.ingogriebsch.spring.hateoas.siren;

import static java.util.Collections.singletonMap;

import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.applyProperties;
import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Data;

class BeanUtilsTest {

    @Nested
    class Instantiate {

        @Test
        void should_throw_exception_if_input_is_null() {
            assertThatThrownBy(() -> instantiate(null, null, null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_create_string_object() {
            String expected = "String";
            assertThat(instantiate(String.class, new Class[] { String.class }, new Object[] { expected })).isEqualTo(expected);
        }

        @Test
        void should_create_pojo() {
            Pojo pojo = new Pojo("Peter");
            assertThat(instantiate(Pojo.class, new Class[] { String.class }, new Object[] { pojo.getName() })).isEqualTo(pojo);
        }
    }

    @Nested
    class ApplyProperties {

        @Test
        void should_throw_exception_if_input_is_null() {
            assertThatThrownBy(() -> applyProperties(null, null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_set_simple_property_on_pojo() {
            Pojo pojo = new Pojo("Paul");
            assertThat(applyProperties(new Pojo("Peter"), singletonMap("name", pojo.getName()))).isEqualTo(pojo);
        }
    }

    @AllArgsConstructor
    @Data
    private static class Pojo {

        private String name;
    }
}
