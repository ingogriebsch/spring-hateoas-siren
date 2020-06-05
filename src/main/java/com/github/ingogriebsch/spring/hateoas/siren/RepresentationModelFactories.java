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

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;

/**
 * Holds factories for all known types of {@link RepresentationModel}s to be used during the deserialization process.
 * 
 * @author Ingo Griebsch
 */
public interface RepresentationModelFactories {

    /**
     * @return a factory to create {@link RepresentationModel} instances.
     */
    default RepresentationModelFactory forRepresentationModel() {
        return new RepresentationModelFactory() {
        };
    }

    /**
     * @return a factory to create {@link EntityModel} instances.
     */
    default EntityModelFactory forEntityModel() {
        return new EntityModelFactory() {
        };
    }

    /**
     * @return a factory to create {@link CollectionModel} instances.
     */
    default CollectionModelFactory forCollectioModel() {
        return new CollectionModelFactory() {
        };
    }

    /**
     * @return a factory to create {@link PagedModel} instances.
     */
    default PagedModelFactory forPagedModel() {
        return new PagedModelFactory() {
        };
    }

}
