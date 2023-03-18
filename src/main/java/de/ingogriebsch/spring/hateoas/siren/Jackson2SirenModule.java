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
import org.springframework.http.HttpMethod;

/**
 * Jackson {@link Module} to serialize and deserialize all the parts of Siren entities.
 *
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see Module
 */
class Jackson2SirenModule extends SimpleModule {

    @JsonSerialize(using = SirenCollectionModelSerializer.class)
    @JsonDeserialize(using = SirenCollectionModelDeserializer.class)
    abstract static class CollectionModelMixIn<T> extends CollectionModel<T> {
    }

    @JsonSerialize(using = SirenEntityModelSerializer.class)
    @JsonDeserialize(using = SirenEntityModelDeserializer.class)
    abstract static class EntityModelMixIn<T> extends EntityModel<T> {
    }

    @JsonSerialize(using = SirenPagedModelSerializer.class)
    @JsonDeserialize(using = SirenPagedModelDeserializer.class)
    abstract static class PagedModelMixIn<T> extends PagedModel<T> {
    }

    @JsonSerialize(using = SirenRepresentationModelSerializer.class)
    @JsonDeserialize(using = SirenRepresentationModelDeserializer.class)
    abstract static class RepresentationModelMixIn extends RepresentationModel<RepresentationModelMixin> {
    }

    @JsonSerialize(using = SirenModelSerializer.class)
    abstract static class SirenModelMixIn extends SirenModel {

        @JsonSerialize(using = SirenModelSerializer.EmbeddedRepresentationSerializer.class)
        abstract static class EmbeddedRepresentationMixIn extends SirenModel.EmbeddedRepresentation {
        }

    }

    private static final Version MODULE_VERSION = new Version(1, 1, 0, null, "de.ingogriebsch.hateoas", "spring-hateoas-siren");

    private static final long serialVersionUID = 7377778164657569053L;

    Jackson2SirenModule() {
        super("siren-module", MODULE_VERSION);

        this.setMixInAnnotation(RepresentationModel.class, RepresentationModelMixIn.class);
        this.setMixInAnnotation(EntityModel.class, EntityModelMixIn.class);
        this.setMixInAnnotation(CollectionModel.class, CollectionModelMixIn.class);
        this.setMixInAnnotation(PagedModel.class, PagedModelMixIn.class);
        this.setMixInAnnotation(SirenModel.class, SirenModelMixIn.class);
        this.setMixInAnnotation(SirenModel.EmbeddedRepresentation.class, SirenModelMixIn.EmbeddedRepresentationMixIn.class);
        this.addSerializer(HttpMethod.class, new HttpMethodSerializer());
        this.addDeserializer(HttpMethod.class, new HttpMethodDeserializer());
    }

}
