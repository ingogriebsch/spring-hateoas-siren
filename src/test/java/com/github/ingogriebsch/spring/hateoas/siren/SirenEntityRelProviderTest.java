package com.github.ingogriebsch.spring.hateoas.siren;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.hateoas.IanaLinkRelations.ITEM;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.RepresentationModel;

class SirenEntityRelProviderTest {

    private static final SirenEntityRelProvider provider = new SirenEntityRelProvider() {
    };

    @Test
    void get_should_throw_exception_if_input_is_null() {
        assertThrows(IllegalArgumentException.class, () -> provider.get(null, null));
    }

    @Test
    void get_should_return_no_rel_if_parent_is_null() {
        assertThat(provider.get(new RepresentationModel<>(), null)).isEmpty();
    }

    @Test
    void get_should_return_single_rel_if_parent_is_not_null() {
        assertThat(provider.get(new RepresentationModel<>(), new RepresentationModel<>())).containsExactly(ITEM);
    }
}
