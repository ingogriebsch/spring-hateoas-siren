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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.RepresentationModelMixin;

/**
 * Jackson {@link Module} to serialize and deserialize all the parts of Siren entities.
 *
 * @author Ingo Griebsch
 */
class Jackson2SirenModule extends SimpleModule {

    private static final long serialVersionUID = 7377778164657569053L;

    Jackson2SirenModule() {
        super("siren-module", new Version(1, 0, 0, null, "com.github.ingogriebsch", "spring-hateoas-siren"));

        setMixInAnnotation(RepresentationModel.class, RepresentationModelMixIn.class);
        setMixInAnnotation(EntityModel.class, EntityModelMixIn.class);
        setMixInAnnotation(CollectionModel.class, CollectionModelMixIn.class);
        setMixInAnnotation(PagedModel.class, PagedModelMixIn.class);
        setMixInAnnotation(SirenModel.class, SirenModelMixIn.class);
        setMixInAnnotation(SirenEmbeddedRepresentation.class, SirenEmbeddedRepresentationMixIn.class);
    }

    @JsonSerialize(using = SirenRepresentationModelSerializer.class)
    @JsonDeserialize(using = SirenRepresentationModelDeserializer.class)
    abstract static class RepresentationModelMixIn extends RepresentationModel<RepresentationModelMixin> {
    }

    @JsonSerialize(using = SirenEntityModelSerializer.class)
    @JsonDeserialize(using = SirenEntityModelDeserializer.class)
    abstract static class EntityModelMixIn<T> extends EntityModel<T> {
    }

    @JsonSerialize(using = SirenCollectionModelSerializer.class)
    @JsonDeserialize(using = SirenCollectionModelDeserializer.class)
    abstract static class CollectionModelMixIn<T> extends CollectionModel<T> {
    }

    @JsonSerialize(using = SirenPagedModelSerializer.class)
    @JsonDeserialize(using = SirenPagedModelDeserializer.class)
    abstract static class PagedModelMixIn<T> extends PagedModel<T> {
    }

    @JsonSerialize(using = SirenModelSerializer.class)
    abstract static class SirenModelMixIn extends SirenModel {
    }

    @JsonSerialize(using = SirenEmbeddedRepresentationSerializer.class)
    abstract static class SirenEmbeddedRepresentationMixIn extends SirenEmbeddedRepresentation {
    }

}
