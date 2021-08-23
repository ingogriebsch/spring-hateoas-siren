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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.Test;

class PropertiesFacilityTest {

    @Test
    void should_extract_properties() {
        Pojo pojo = new Pojo();
        pojo.setStringValue("test");
        pojo.setIntegerValue(1);
        pojo.setLongValue(1L);
        pojo.setDoubleValue(1.0);
        pojo.setFloatValue(1.0F);
        pojo.setBooleanValue(true);

        PropertiesFacility facility = new PropertiesFacility(objectMapper());
        Map<String, Object> properties = facility.extract(pojo);

        assertThat(properties).containsOnly( //
            entry("stringValue", pojo.getStringValue()), //
            entry("integerValue", pojo.getIntegerValue()), //
            entry("longValue", pojo.getLongValue()), //
            entry("doubleValue", pojo.getDoubleValue()), //
            entry("floatValue", pojo.getFloatValue()), //
            entry("booleanValue", pojo.getBooleanValue()) //
        );
    }

    private static ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Data
    private static class Pojo {

        private String stringValue;
        private Integer integerValue;
        private Long longValue;
        private Double doubleValue;
        private Float floatValue;
        private Boolean booleanValue;
    }
}
