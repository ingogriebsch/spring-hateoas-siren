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
package org.springframework.hateoas.mediatype.siren;

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
import lombok.Singular;
import lombok.Value;

@Builder
@JsonPropertyOrder({ "class", "rel", "properties", "entities", "links", "actions", "title" })
@Value
class SirenEntity {

    @JsonInclude(NON_EMPTY)
    @JsonProperty("class")
    private List<String> classes;

    @JsonInclude(NON_EMPTY)
    @JsonProperty("rel")
    @Singular
    private List<LinkRelation> rels;

    @JsonInclude(NON_NULL)
    private Object properties;

    @JsonInclude(NON_EMPTY)
    @Singular
    private List<Object> entities;

    @JsonInclude(NON_EMPTY)
    @Singular
    private List<SirenLink> links;

    @JsonInclude(NON_EMPTY)
    @Singular
    private List<SirenAction> actions;

    @JsonInclude(NON_NULL)
    private String title;

    @Value(staticConstructor = "of")
    public static final class TitleResolvable implements MessageSourceResolvable {

        private Class<?> type;

        @Override
        public String[] getCodes() {
            return Stream.of(type.getName(), type.getSimpleName(), "default").map(it -> String.format("_entity.%s.title", it))
                .toArray(String[]::new);
        }
    }
}
