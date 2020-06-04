/*-
 * #%L
 * Spring HATEOAS Siren
 * %%
 * Copyright (C) 2018 - 2020 Ingo Griebsch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
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

@Builder
@JsonPropertyOrder({ "rel", "class", "href", "title", "type" })
@Value
class SirenLink {

    @JsonProperty("rel")
    @Singular
    private List<String> rels;

    @JsonInclude(NON_EMPTY)
    @JsonProperty("class")
    private List<String> classes;

    @NonNull
    private String href;

    @JsonInclude(NON_NULL)
    private String title;

    @JsonInclude(NON_NULL)
    private String type;

    @Value(staticConstructor = "of")
    static final class TitleResolvable implements MessageSourceResolvable {

        private LinkRelation rel;

        @Override
        public String[] getCodes() {
            return Stream.of(rel.value(), "default").map(it -> String.format("_link.%s.title", it)).toArray(String[]::new);
        }

    }
}
