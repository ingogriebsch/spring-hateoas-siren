package com.github.ingogriebsch.spring.hateoas.siren;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = PRIVATE)
class PropertyUtils {

    static Map<String, Object> extractPropertyValues(@NonNull Object object, @NonNull String... excludes) {
        Map<String, Object> properties = new ObjectMapper().convertValue(object, new TypeReference<Map<String, Object>>() {
        });

        for (String exclude : excludes) {
            properties.remove(exclude);
        }

        return properties;
    }
}
