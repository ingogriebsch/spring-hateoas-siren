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

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.hateoas.LinkRelation;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

/**
 * Representation of a Siren link.
 *
 * @author Ingo Griebsch
 */
@Builder
@JsonPropertyOrder({ "rel", "class", "href", "title", "type" })
@Value
class SirenLink {

    @JsonProperty("rel")
    @Singular
    List<String> rels;

    @JsonInclude(NON_EMPTY)
    @JsonProperty("class")
    List<String> classes;

    @NonNull
    String href;

    @JsonInclude(NON_NULL)
    String title;

    @JsonInclude(NON_NULL)
    String type;

    @Value(staticConstructor = "of")
    static class TitleResolvable implements MessageSourceResolvable {

        LinkRelation rel;

        @Override
        public String[] getCodes() {
            return Stream.of(rel.value(), "default").map(it -> String.format("_link.%s.title", it)).toArray(String[]::new);
        }

    }
}
