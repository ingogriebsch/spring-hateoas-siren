package de.ingogriebsch.spring.hateoas.siren;

import static com.google.common.collect.Lists.newArrayList;
import static de.ingogriebsch.spring.hateoas.siren.TypeMapping.typeMapping;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.core.ResolvableType.forClass;
import static org.springframework.hateoas.mediatype.html.HtmlInputType.CHECKBOX;
import static org.springframework.hateoas.mediatype.html.HtmlInputType.DATE;
import static org.springframework.hateoas.mediatype.html.HtmlInputType.DATETIME_LOCAL;
import static org.springframework.hateoas.mediatype.html.HtmlInputType.FILE;
import static org.springframework.hateoas.mediatype.html.HtmlInputType.MONTH;
import static org.springframework.hateoas.mediatype.html.HtmlInputType.NUMBER;
import static org.springframework.hateoas.mediatype.html.HtmlInputType.TEXT;
import static org.springframework.hateoas.mediatype.html.HtmlInputType.TIME;
import static org.springframework.hateoas.mediatype.html.HtmlInputType.URL;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.springframework.hateoas.mediatype.html.HtmlInputType;

class TypeBasedSirenActionFieldTypeConverterTest {

    @Test
    void should_throw_exception_if_no_input_given() {
        TypeBasedSirenActionFieldTypeConverter converter = new TypeBasedSirenActionFieldTypeConverter();
        assertThatThrownBy(() -> converter.execute(null, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_treat_added_mappings_with_priority() {
        SirenActionFieldTypeConverter converter = new TypeBasedSirenActionFieldTypeConverter();

        assertThat(converter.execute(propertyMetadata(String.class), APPLICATION_FORM_URLENCODED)).contains(TEXT);

        List<TypeMapping> mappings = newArrayList(typeMapping(String.class, CHECKBOX));
        converter = new TypeBasedSirenActionFieldTypeConverter(mappings);

        assertThat(converter.execute(propertyMetadata(String.class), APPLICATION_FORM_URLENCODED)).contains(CHECKBOX);
    }

    @ParameterizedTest(name = "should convert {0} to {1}")
    @MethodSource("convertShouldReturnMatchingTypeInput")
    void should_return_matching_type(Class<?> sourceType, HtmlInputType targetType) {
        TypeBasedSirenActionFieldTypeConverter converter = new TypeBasedSirenActionFieldTypeConverter();
        assertThat(converter.execute(propertyMetadata(sourceType), APPLICATION_FORM_URLENCODED)).contains(targetType);
    }

    private static Stream<Arguments> convertShouldReturnMatchingTypeInput() {
        return Stream.of( //
            arguments(Date.class, DATE), //
            arguments(LocalTime.class, TIME), //
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
            arguments(byte.class, NUMBER), //
            arguments(short.class, NUMBER), //
            arguments(int.class, NUMBER), //
            arguments(long.class, NUMBER), //
            arguments(float.class, NUMBER), //
            arguments(double.class, NUMBER), //
            arguments(BigDecimal.class, NUMBER), //
            arguments(BigInteger.class, NUMBER), //
            arguments(URL.class, URL), //
            arguments(URI.class, URL), //
            arguments(String.class, TEXT), //
            arguments(Object.class, TEXT) //
        );
    }

    private static PropertyMetadata propertyMetadata(Class<?> type) {
        return SimplePropertyMetadata.of(forClass(type), false, false, null, null, null);
    }

    @Value(staticConstructor = "of")
    static class SimplePropertyMetadata implements PropertyMetadata {

        ResolvableType type;
        boolean required;
        boolean readOnly;
        String name;
        Optional<String> pattern;
        String inputType;
    }
}
