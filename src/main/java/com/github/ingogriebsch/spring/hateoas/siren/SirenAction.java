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

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.http.HttpMethod.GET;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpMethod;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

/**
 * Representation of a Siren action.
 *
 * @author Ingo Griebsch
 */
@Builder
@JsonPropertyOrder({"name", "class", "method", "href", "title", "type", "fields"})
@Value
class SirenAction {

    @NonNull
    String name;

    @JsonInclude(NON_EMPTY)
    @JsonProperty("class")
    List<String> classes;

    @Default
    @JsonInclude(NON_NULL)
    @NonNull
    HttpMethod method = GET;

    @NonNull
    String href;

    @JsonInclude(NON_NULL)
    String title;

    // FIXME Default needs to be enabled and type needs to be handled in general as soon as
    // https://github.com/spring-projects/spring-hateoas/issues/1087 is part of Spring HATEOAS
    // @Default
    @JsonInclude(NON_NULL)
    // @NonNull
            String type /* = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE */;

    @JsonInclude(NON_EMPTY)
    @Singular
    List<Field> fields;

    @Builder
    @JsonPropertyOrder({"name", "class", "type", "value", "title"})
    @Value
    static class Field {

        @NonNull
        String name;

        @JsonInclude(NON_EMPTY)
        @JsonProperty("class")
        List<String> classes;

        @Default
        @JsonInclude(NON_NULL)
        Type type = Type.text;

        @JsonInclude(NON_NULL)
        Object value;

        @JsonInclude(NON_NULL)
        String title;

        enum Type {
            checkbox, color, date, datetime, datetime_local, email, file, hidden, month, number, password, radio, range,
            search, tel, text, time, url, week
        }

        @Value(staticConstructor = "of")
        static class TitleResolvable implements MessageSourceResolvable {

            String name;

            @Override
            public String[] getCodes() {
                return Stream.of(name, "default").map(it -> String.format("_field.%s.title", it)).toArray(String[]::new);
            }
        }
    }

    @Value(staticConstructor = "of")
    static class TitleResolvable implements MessageSourceResolvable {

        String name;

        @Override
        public String[] getCodes() {
            return Stream.of(name, "default").map(it -> String.format("_action.%s.title", it)).toArray(String[]::new);
        }
    }
}
