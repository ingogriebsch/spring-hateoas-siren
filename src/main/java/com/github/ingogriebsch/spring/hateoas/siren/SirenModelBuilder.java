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

import static com.github.ingogriebsch.spring.hateoas.siren.RepresentationModelUtils.wrap;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.Validate.noNullElements;
import static org.springframework.hateoas.Links.of;
import static org.springframework.hateoas.Links.MergeMode.REPLACE_BY_REL;

import java.util.List;

import com.github.ingogriebsch.spring.hateoas.siren.SirenModel.EmbeddedRepresentation;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

import lombok.NonNull;

/**
 * Builder which allows to build complex Siren entity structures.
 * 
 * @author Ingo Griebsch
 */
public final class SirenModelBuilder {

    private final List<EmbeddedRepresentation> entities = newArrayList();
    private final List<String> classes = newArrayList();
    private Links linksAndActions = Links.of();
    private Object properties;
    private String title;

    private SirenModelBuilder() {
    }

    public static SirenModelBuilder sirenModel() {
        return new SirenModelBuilder();
    }

    public SirenModelBuilder classes(@NonNull String... classes) {
        return classes(newArrayList(classes));
    }

    public SirenModelBuilder classes(@NonNull Iterable<String> classes) {
        noNullElements(classes);
        classes.forEach(this.classes::add);
        return this;
    }

    public SirenModelBuilder title(@NonNull String title) {
        this.title = title;
        return this;
    }

    public SirenModelBuilder properties(@NonNull Object properties) {
        // FIXME may not be a representation model subclass
        this.properties = properties;
        return this;
    }

    public SirenModelBuilder entities(@NonNull Object entity) {
        return entities(newArrayList(entity));
    }

    public SirenModelBuilder entities(@NonNull Object... entities) {
        return entities(newArrayList(entities));
    }

    public SirenModelBuilder entities(@NonNull Iterable<?> entities) {
        noNullElements(entities);
        entities.forEach(e -> this.entities.add(new EmbeddedRepresentation(wrap(e))));
        return this;
    }

    public SirenModelBuilder entities(@NonNull String rel, @NonNull Object entity) {
        return entities(LinkRelation.of(rel), entity);
    }

    public SirenModelBuilder entities(@NonNull String rel, @NonNull Object... entities) {
        return entities(LinkRelation.of(rel), entities);
    }

    public SirenModelBuilder entities(@NonNull String rel, @NonNull Iterable<?> entities) {
        return entities(LinkRelation.of(rel), entities);
    }

    public SirenModelBuilder entities(@NonNull LinkRelation rel, @NonNull Object entity) {
        return entities(rel, newArrayList(entity));
    }

    public SirenModelBuilder entities(@NonNull LinkRelation rel, @NonNull Object... entities) {
        return entities(rel, newArrayList(entities));
    }

    public SirenModelBuilder entities(@NonNull LinkRelation rel, @NonNull Iterable<?> entities) {
        noNullElements(entities);
        entities.forEach(e -> this.entities.add(new EmbeddedRepresentation(wrap(e), rel)));
        return this;
    }

    public SirenModelBuilder linksAndActions(@NonNull Link link) {
        return linksAndActions(of(link));
    }

    public SirenModelBuilder linksAndActions(@NonNull Link... links) {
        return linksAndActions(of(links));
    }

    public SirenModelBuilder linksAndActions(@NonNull Iterable<Link> links) {
        return linksAndActions(of(links));
    }

    public SirenModelBuilder linksAndActions(@NonNull Links links) {
        noNullElements(links);

        linksAndActions = linksAndActions.merge(REPLACE_BY_REL, links);
        return this;
    }

    public RepresentationModel<?> build() {
        SirenModel model = new SirenModel(properties, entities, classes, title);
        model.add(linksAndActions);
        return model;
    }
}
