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

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.hateoas.IanaLinkRelations.ITEM;

import java.util.List;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;

import lombok.NonNull;

/**
 * API to provide information about the relationship of a Siren entity to it's parent.
 *
 * @href https://github.com/kevinswiber/siren#rel
 * @author Ingo Griebsch
 */
public interface SirenEntityRelProvider {

    /**
     * Returns the {@link IanaLinkRelations relations} explaining the relationship between the model and it's parent.
     * <p>
     * The default implementation returns the {@link IanaLinkRelations#ITEM item} relation if the model is a child object.
     * 
     * @param model the {@link RepresentationModel} that is transfered into a Siren entity. Is never {@literal null}.
     * @param parent the parent {@link RepresentationModel} of the {@literal model}. Can be {@literal null}
     * @return the {@link IanaLinkRelations relations} explaining the relationship between the model and it's parent
     */
    default List<LinkRelation> get(@NonNull RepresentationModel<?> model, RepresentationModel<?> parent) {
        return parent != null ? newArrayList(ITEM) : newArrayList();
    }
}
