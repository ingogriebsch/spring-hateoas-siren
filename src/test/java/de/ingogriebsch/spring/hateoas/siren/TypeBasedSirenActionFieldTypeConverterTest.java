package de.ingogriebsch.spring.hateoas.siren;

import static com.google.common.collect.Lists.newArrayList;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.CHECKBOX;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.DATE;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.DATETIME_LOCAL;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.FILE;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.MONTH;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.NUMBER;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.TEXT;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.URL;
import static de.ingogriebsch.spring.hateoas.siren.TypeMapping.typeMapping;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.core.ResolvableType.forClass;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.ResolvableType;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

public class TypeBasedSirenActionFieldTypeConverterTest {

    @Test
    void convert_should_throw_exception_if_no_input_given() {
        assertThatThrownBy(() -> new TypeBasedSirenActionFieldTypeConverter().convert(null, null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_treat_added_mappings_with_priority() {
        SirenActionFieldTypeConverter converter = new TypeBasedSirenActionFieldTypeConverter();

        assertThat(converter.convert(propertyMetadata(String.class), APPLICATION_FORM_URLENCODED)).isEqualTo(TEXT);

        List<TypeMapping> mappings = newArrayList(typeMapping(String.class, CHECKBOX));
        converter = new TypeBasedSirenActionFieldTypeConverter(mappings);

        assertThat(converter.convert(propertyMetadata(String.class), APPLICATION_FORM_URLENCODED)).isEqualTo(CHECKBOX);
    }

    @ParameterizedTest(name = "should convert {0} to {1}")
    @MethodSource("convertShouldReturnMatchingTypeInput")
    void convert_should_return_matching_type(Class<?> sourceType, SirenActionFieldType targetType) {
        TypeBasedSirenActionFieldTypeConverter converter = new TypeBasedSirenActionFieldTypeConverter();
        assertThat(converter.convert(propertyMetadata(sourceType), APPLICATION_FORM_URLENCODED)).isEqualTo(targetType);
    }

    private static Stream<Arguments> convertShouldReturnMatchingTypeInput() {
        return Stream.of( //
            arguments(Date.class, DATE), //
            arguments(LocalDateTime.class, DATETIME_LOCAL), //
            arguments(File.class, FILE), //
            arguments(Path.class, FILE), //
            arguments(Month.class, MONTH), //
            arguments(Number.class, NUMBER), //
            arguments(Byte.class, NUMBER), //
            arguments(Short.class, NUMBER), //
            arguments(Integer.class, NUMBER), //
            arguments(Long.class, NUMBER), //
            arguments(Float.class, NUMBER), //
            arguments(Double.class, NUMBER), //
            arguments(URL.class, URL), //
            arguments(URI.class, URL), //
            arguments(String.class, TEXT), //
            arguments(Object.class, TEXT) //
        );
    }

    private static PropertyMetadata propertyMetadata(Class<?> type) {
        return SimplePropertyMetadata.of(forClass(type), false, false, null, null);
    }

    @Value(staticConstructor = "of")
    static class SimplePropertyMetadata implements PropertyMetadata {

        ResolvableType type;
        boolean required;
        boolean readOnly;
        String name;
        Optional<String> pattern;
    }
}
