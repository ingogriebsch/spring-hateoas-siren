/*-
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ingogriebsch.spring.hateoas.siren;

import static de.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;

import de.ingogriebsch.spring.hateoas.siren.support.SimpleObjectProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

class SirenMediaTypeConfigurationTest {

    private static SirenMediaTypeConfiguration sirenMediaTypeConfiguration;

    @BeforeAll
    static void beforeAll() {
        ObjectProvider<SirenConfiguration> configuration = new SimpleObjectProvider<>(new SirenConfiguration());

        ObjectProvider<RepresentationModelFactories> representationModelFactories =
            new SimpleObjectProvider<>(new RepresentationModelFactories() {
            });

        ObjectProvider<SirenEntityClassProvider> entityClassProvider = new SimpleObjectProvider<>(new SirenEntityClassProvider() {
        });

        ObjectProvider<SirenEntityRelProvider> entityRelProvider = new SimpleObjectProvider<>(new SirenEntityRelProvider() {
        });

        sirenMediaTypeConfiguration = new SirenMediaTypeConfiguration(configuration, representationModelFactories,
            entityClassProvider, entityRelProvider, DEFAULTS_ONLY);
    }

    @Nested
    class GetMediaTypes {

        @Test
        void should_return_matching_media_type() {
            assertThat(sirenMediaTypeConfiguration.getMediaTypes()).containsExactly(SIREN_JSON);
        }
    }

    @Nested
    class GetJacksonModule {

        @Test
        void should_return_matching_jackson_module() {
            assertThat(sirenMediaTypeConfiguration.getJacksonModule()).isNotNull().isInstanceOf(Jackson2SirenModule.class);
        }
    }
}
