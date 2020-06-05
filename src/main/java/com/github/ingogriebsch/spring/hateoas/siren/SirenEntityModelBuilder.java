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

import static com.github.ingogriebsch.spring.hateoas.siren.SirenNavigables.navigables;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builder that helps the {@link SirenEntityModelDeserializer} to deserialize a Siren entity into a {@link EntityModel}.
 * 
 * @author Ingo Griebsch
 */
@RequiredArgsConstructor(access = PRIVATE)
class SirenEntityModelBuilder {

    @NonNull
    private final JavaType type;
    @NonNull
    private final EntityModelFactory modelFactory;
    @NonNull
    private final SirenLinkConverter linkConverter;

    private Map<String, Object> properties = newHashMap();
    private List<SirenAction> actions = newArrayList();
    private List<SirenLink> links = newArrayList();
    private Object content;

    static SirenEntityModelBuilder builder(@NonNull JavaType type, @NonNull EntityModelFactory modelFactory,
        @NonNull SirenLinkConverter linkConverter) {
        return new SirenEntityModelBuilder(type, modelFactory, linkConverter);
    }

    SirenEntityModelBuilder properties(@NonNull Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    SirenEntityModelBuilder content(@NonNull Object content) {
        this.content = content;
        return this;
    }

    SirenEntityModelBuilder links(List<SirenLink> links) {
        this.links = links != null ? links : newArrayList();
        return this;
    }

    SirenEntityModelBuilder actions(List<SirenAction> actions) {
        this.actions = actions != null ? actions : newArrayList();
        return this;
    }

    EntityModel<?> build() {
        return modelFactory.create(type, links(), content, properties);
    }

    private Iterable<Link> links() {
        return linkConverter.from(navigables(links, actions));
    }

}
