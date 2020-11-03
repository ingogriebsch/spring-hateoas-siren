package de.ingogriebsch.spring.hateoas.siren;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SimpleObjectProviderTest {

    @MethodSource("source")
    @ParameterizedTest
    void getObject_should_return_matching_instance(Object object) {
        SimpleObjectProvider<Object> objectProvider = new SimpleObjectProvider<>(object);
        assertThat(objectProvider.getObject()).isEqualTo(object);
    }

    @MethodSource("source")
    @ParameterizedTest
    void getIfUnique_should_return_matching_instance(Object object) {
        SimpleObjectProvider<Object> objectProvider = new SimpleObjectProvider<>(object);
        assertThat(objectProvider.getIfUnique()).isEqualTo(object);
    }

    @MethodSource("source")
    @ParameterizedTest
    void getIfAvailable_should_return_matching_instance(Object object) {
        SimpleObjectProvider<Object> objectProvider = new SimpleObjectProvider<>(object);
        assertThat(objectProvider.getIfAvailable()).isEqualTo(object);
    }

    static Stream<Arguments> source() {
        return newArrayList(arguments("Test"), arguments((Object) null)).stream();
    }
}
