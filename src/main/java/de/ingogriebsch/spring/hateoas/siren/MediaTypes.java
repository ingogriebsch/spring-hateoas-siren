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

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.parseMediaType;

import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

/**
 * Constants for the Siren hypermedia type.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 */
@NoArgsConstructor(access = PRIVATE)
public final class MediaTypes {

    /**
     * A String equivalent of {@link MediaTypes#SIREN_JSON}.
     */
    public static final String SIREN_JSON_VALUE = "application/vnd.siren+json";

    /**
     * Public constant media type for {@code application/vnd.siren+json}.
     */
    public static final MediaType SIREN_JSON = parseMediaType(SIREN_JSON_VALUE);

}
