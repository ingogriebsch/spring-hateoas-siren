package com.github.ingogriebsch.spring.hateoas.siren;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

import static com.github.ingogriebsch.spring.hateoas.siren.PropertyUtils.extractPropertyValues;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PropertyUtilsTest {

    @Nested
    class ExtractPropertyValues {

        @Test
        void should_throw_exception_if_input_is_null() {
            assertThatThrownBy(() -> extractPropertyValues(null, (String[]) null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_return_map_containing_properties() {
            Capital object = new Capital("Sacramento");
            Map<String, Object> propertyValues =
                of(new SimpleEntry<>("links", newArrayList()), new SimpleEntry<>("name", object.getName()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            assertThat(extractPropertyValues(object)).isEqualTo(propertyValues);
        }

        @Test
        void should_return_map_containing_properties_filtered_by_excludes() {
            Capital object = new Capital("Sacramento");
            Map<String, Object> propertyValues =
                of(new SimpleEntry<>("name", object.getName())).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            assertThat(extractPropertyValues(object, "links")).isEqualTo(propertyValues);
        }
    }
}
