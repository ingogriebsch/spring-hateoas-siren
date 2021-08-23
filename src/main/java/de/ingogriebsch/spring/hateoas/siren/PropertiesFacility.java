/*-
 * Copyright 2019-2021 the original author or authors.
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

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class PropertiesFacility {

    private static final Set<String> PROPERTIES_TO_IGNORE = new HashSet<>(asList("class", "links"));
    private final ObjectMapper objectMapper;

    Map<String, Object> extract(Object object, String... excludes) {
        Map<String, Object> properties = objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {
        });

        for (String exclude : PROPERTIES_TO_IGNORE) {
            properties.remove(exclude);
        }

        for (String exclude : excludes) {
            properties.remove(exclude);
        }

        return properties;
    }
}
