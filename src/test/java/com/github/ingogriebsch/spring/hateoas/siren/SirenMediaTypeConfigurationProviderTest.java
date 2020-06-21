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
package com.github.ingogriebsch.spring.hateoas.siren;

import static com.github.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.MediaTypes.HAL_FORMS_JSON;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SirenMediaTypeConfigurationProviderTest {

    @Nested
    class GetConfiguration {

        @Test
        void should_return_matching_configuration_class() {
            assertThat(new SirenMediaTypeConfigurationProvider().getConfiguration()).isEqualTo(SirenMediaTypeConfiguration.class);
        }
    }

    @Nested
    class SupportsAny {

        @Test
        void should_return_true_if_supported_media_type_is_contained() {
            assertThat(new SirenMediaTypeConfigurationProvider().supportsAny(newArrayList(SIREN_JSON))).isTrue();
        }

        @Test
        void should_return_false_if_supported_media_type_is_not_contained() {
            // TODO Check the corresponding provider method to understand why the test is implemented this way!
            assertThat(new SirenMediaTypeConfigurationProvider().supportsAny(newArrayList(HAL_FORMS_JSON))).isTrue();
        }
    }

}
