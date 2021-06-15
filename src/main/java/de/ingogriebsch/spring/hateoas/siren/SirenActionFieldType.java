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
import org.springframework.hateoas.mediatype.html.HtmlInputType;

/**
 * A representation of a Siren action field type.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see <a href="https://github.com/kevinswiber/siren#type-3" target="_blank">Siren Action Field Type</a>
 * @see HtmlInputType
 * @deprecated use {@link HtmlInputType} instead.
 */
@Deprecated
public enum SirenActionFieldType {

        CHECKBOX(HtmlInputType.CHECKBOX), //
        COLOR(HtmlInputType.COLOR), //
        DATE(HtmlInputType.DATE), //
        DATETIME(HtmlInputType.TIME), //
        DATETIME_LOCAL(HtmlInputType.DATETIME_LOCAL), //
        EMAIL(HtmlInputType.EMAIL), //
        FILE(HtmlInputType.FILE), //
        HIDDEN(HtmlInputType.HIDDEN), //
        MONTH(HtmlInputType.MONTH), //
        NUMBER(HtmlInputType.NUMBER), //
        PASSWORD(HtmlInputType.PASSWORD), //
        RADIO(HtmlInputType.RADIO), //
        RANGE(HtmlInputType.RANGE), //
        SEARCH(HtmlInputType.SEARCH), //
        TEL(HtmlInputType.TEL), //
        TEXT(HtmlInputType.TEXT), //
        TIME(HtmlInputType.TIME), //
        URL(HtmlInputType.URL), //
        WEEK(HtmlInputType.WEEK);

    @Getter
    private final HtmlInputType type;

    SirenActionFieldType(@NonNull HtmlInputType type) {
        this.type = type;
    }
}
