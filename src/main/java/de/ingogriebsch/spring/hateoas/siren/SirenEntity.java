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

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.hateoas.LinkRelation;

/**
 * Representation of a Siren entity.
 *
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see <a href="https://github.com/kevinswiber/siren#entity" target="_blank">Siren Entity</a>
 */
@Builder
@JsonPropertyOrder({ "class", "rel", "properties", "entities", "links", "actions", "title" })
@Value
class SirenEntity {

    @JsonInclude(NON_EMPTY)
    @JsonProperty("class")
    List<String> classes;

    @JsonInclude(NON_EMPTY)
    @JsonProperty("rel")
    @Singular
    List<LinkRelation> rels;

    @JsonInclude(NON_NULL)
    Object properties;

    @JsonInclude(NON_EMPTY)
    @Singular
    List<Object> entities;

    @JsonInclude(NON_EMPTY)
    @Singular
    List<SirenLink> links;

    @JsonInclude(NON_EMPTY)
    @Singular
    List<SirenAction> actions;

    @JsonInclude(NON_NULL)
    String title;

    @Value(staticConstructor = "of")
    static class TitleResolvable implements MessageSourceResolvable {

        @NonNull
        Class<?> type;

        @Override
        public String[] getCodes() {
            return Stream.of(type.getName(), type.getSimpleName(), "default").map(it -> String.format("_entity.%s.title", it))
                .toArray(String[]::new);
        }
    }
}
