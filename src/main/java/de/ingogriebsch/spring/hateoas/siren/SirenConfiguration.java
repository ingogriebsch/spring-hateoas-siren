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

import lombok.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

/**
 * Siren specific configuration.
 *
 * @author Ingo Griebsch
 */
@Value
public class SirenConfiguration {

    boolean entityAndCollectionModelSubclassingEnabled;

    public SirenConfiguration() {
        this(false);
    }

    private SirenConfiguration(boolean entityAndCollectionModelSubclassingEnabled) {
        this.entityAndCollectionModelSubclassingEnabled = entityAndCollectionModelSubclassingEnabled;
    }

    /**
     * Configures whether it is possible to subclass {@link EntityModel}s and {@link CollectionModel}s.
     * Defaults to {@literal false}.
     */
    public SirenConfiguration withEntityAndCollectionModelSubclassingEnabled(boolean enabled) {
        return new SirenConfiguration(enabled);
    }

}
