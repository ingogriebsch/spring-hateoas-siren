package de.ingogriebsch.spring.hateoas.siren;

import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.NUMBER;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.TEXT;
import static de.ingogriebsch.spring.hateoas.siren.TypeBasedSirenActionFieldTypeConverter.TypeMapping.typeMapping;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SirenConfigurationTest {

    @Test
    void ctor_should_define_default_for_entityAndCollectionModelSubclassingEnabled() {
        assertThat(new SirenConfiguration().isEntityAndCollectionModelSubclassingEnabled()).isFalse();
    }

    @Test
    void ctor_should_define_default_for_actionFieldTypeMappings() {
        assertThat(new SirenConfiguration().getActionFieldTypeMappings()).isEmpty();
    }

    @Test
    void withEntityAndCollectionModelSubclassingEnabled_should_return_set_value() {
        SirenConfiguration configuration = new SirenConfiguration();
        assertThat(configuration.isEntityAndCollectionModelSubclassingEnabled()).isFalse();

        configuration.withEntityAndCollectionModelSubclassingEnabled(true);
        assertThat(configuration.isEntityAndCollectionModelSubclassingEnabled()).isTrue();
    }

    @Test
    void configuration_should_be_kept_if_wither_are_used() {
        SirenConfiguration configuration = new SirenConfiguration();
        assertThat(configuration.isEntityAndCollectionModelSubclassingEnabled()).isFalse();

        configuration.withEntityAndCollectionModelSubclassingEnabled(true);
        assertThat(configuration.isEntityAndCollectionModelSubclassingEnabled()).isTrue();

        configuration.withActionFieldTypeMappging(String.class, TEXT);
        assertThat(configuration.getActionFieldTypeMappings()).containsExactly( //
            typeMapping(String.class, TEXT) //
        );

        assertThat(configuration.isEntityAndCollectionModelSubclassingEnabled()).isTrue();

        configuration.withEntityAndCollectionModelSubclassingEnabled(false);
        assertThat(configuration.isEntityAndCollectionModelSubclassingEnabled()).isFalse();

        configuration.withActionFieldTypeMappging(Integer.class, NUMBER);
        assertThat(configuration.getActionFieldTypeMappings()).containsExactly( //
            typeMapping(String.class, TEXT), //
            typeMapping(Integer.class, NUMBER) //
        );
    }

    @Test
    void withActionFieldTypeMappging_should_return_set_value() {
        SirenConfiguration configuration = new SirenConfiguration();
        assertThat(configuration.getActionFieldTypeMappings()).isEmpty();

        configuration.withActionFieldTypeMappging(String.class, TEXT);
        configuration.withActionFieldTypeMappging(Integer.class, NUMBER);
        assertThat(configuration.getActionFieldTypeMappings()).containsExactly( //
            typeMapping(String.class, TEXT), //
            typeMapping(Integer.class, NUMBER) //
        );
    }
}
