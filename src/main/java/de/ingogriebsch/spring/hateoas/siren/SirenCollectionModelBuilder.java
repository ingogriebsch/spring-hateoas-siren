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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static de.ingogriebsch.spring.hateoas.siren.SirenNavigables.navigables;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.lang.Nullable;

/**
 * Builder that helps the {@link SirenCollectionModelDeserializer} to deserialize a Siren entity into a {@link CollectionModel}.
 * 
 * @author Ingo Griebsch
 */
@RequiredArgsConstructor(access = PRIVATE)
class SirenCollectionModelBuilder {

    private final JavaType type;
    private final CollectionModelFactory modelFactory;
    private final SirenLinkConverter linkConverter;

    private Map<String, Object> properties = newHashMap();
    private List<SirenAction> actions = newArrayList();
    private List<SirenLink> links = newArrayList();
    private List<Object> content = newArrayList();

    static SirenCollectionModelBuilder builder(JavaType type, CollectionModelFactory modelFactory,
        SirenLinkConverter linkConverter) {
        return new SirenCollectionModelBuilder(type, modelFactory, linkConverter);
    }

    SirenCollectionModelBuilder properties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    SirenCollectionModelBuilder content(List<Object> content) {
        this.content = content;
        return this;
    }

    SirenCollectionModelBuilder links(@Nullable List<SirenLink> links) {
        this.links = links != null ? links : newArrayList();
        return this;
    }

    SirenCollectionModelBuilder actions(@Nullable List<SirenAction> actions) {
        this.actions = actions != null ? actions : newArrayList();
        return this;
    }

    CollectionModel<?> build() {
        return modelFactory.create(type, links(), content, properties);
    }

    private Iterable<Link> links() {
        return linkConverter.from(navigables(links, actions));
    }

}
