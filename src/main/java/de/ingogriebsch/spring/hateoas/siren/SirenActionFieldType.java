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

import lombok.Getter;
import lombok.NonNull;

/**
 * A representation of a Siren action field type.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see <a href="https://github.com/kevinswiber/siren#type-3" target="_blank">Siren Action Field Type</a>
 */
public enum SirenActionFieldType {

        CHECKBOX("checkbox"), //
        COLOR("color"), //
        DATE("date"), //
        DATETIME("datetime"), //
        DATETIME_LOCAL("datetime-local"), //
        EMAIL("email"), //
        FILE("file"), //
        HIDDEN("hidden"), //
        MONTH("month"), //
        NUMBER("number"), //
        PASSWORD("password"), //
        RADIO("radio"), //
        RANGE("range"), //
        SEARCH("search"), //
        TEL("tel"), //
        TEXT("text"), //
        TIME("time"), //
        URL("url"), //
        WEEK("week");

    @Getter
    private final String keyword;

    SirenActionFieldType(@NonNull String keyword) {
        this.keyword = keyword;
    }
}
