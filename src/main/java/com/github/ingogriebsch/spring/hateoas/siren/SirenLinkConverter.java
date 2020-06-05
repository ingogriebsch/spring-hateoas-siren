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

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import static com.github.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;
import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.http.HttpMethod.GET;

import java.util.List;
import java.util.stream.Collectors;

import com.github.ingogriebsch.spring.hateoas.siren.SirenAction.Field;
import com.github.ingogriebsch.spring.hateoas.siren.SirenAction.Field.Type;

import org.springframework.core.ResolvableType;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.AffordanceModel.InputPayloadMetadata;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Converter which is able to either convert {@link Link}s (and their {@link Affordance}s) into a {@link SirenNavigables}, or the
 * other way around.
 * 
 * @author Ingo Griebsch
 */
@RequiredArgsConstructor
class SirenLinkConverter {

    @NonNull
    private final MessageResolver messageResolver;

    SirenNavigables to(@NonNull Iterable<Link> links) {
        return SirenNavigables.merge(stream(links.spliterator(), false).map(l -> convert(l)).collect(toList()));
    }

    List<Link> from(@NonNull SirenNavigables navigables) {
        return slice(navigables).stream().map(n -> convert(n)).collect(toList());
    }

    SirenNavigables convert(Link link) {
        return SirenNavigables.of(links(link), actions(link));
    }

    private Link convert(SirenNavigables navigables) {
        SirenLink link = navigables.getLinks().iterator().next();
        String rel = link.getRels().stream().findFirst().orElse(null);

        return Link.of(link.getHref(), rel) //
            .withTitle(link.getTitle()) //
            .withType(link.getType());
    }

    private List<SirenLink> links(Link link) {
        SirenLink sirenLink = SirenLink.builder() //
            .rel(link.getRel().value()) //
            .href(link.getHref()).title(title(link)) //
            .type(link.getType()) //
            .build();

        return newArrayList(sirenLink);
    }

    private List<SirenAction> actions(Link link) {
        List<SirenAction> result = newArrayList();
        for (SirenAffordanceModel model : affordanceModels(link)) {
            if (!GET.equals(model.getHttpMethod())) {
                result.add(convert(model));
            }
        }
        return result;
    }

    private SirenAction convert(SirenAffordanceModel model) {
        List<Field> fields = fields(model);

        return SirenAction.builder() //
            .name(model.getName()) //
            .method(model.getHttpMethod()) //
            .href(model.getLink().getHref()) //
            .title(actionTitle(model.getName())) //
            .fields(fields) //
            .build();
    }

    private List<Field> fields(SirenAffordanceModel model) {
        InputPayloadMetadata input = model.getInput();
        if (input == null) {
            return newArrayList();
        }
        return input.stream().map(pm -> field(pm)).collect(toList());
    }

    private Field field(PropertyMetadata propertyMetadata) {
        return Field.builder() //
            .name(propertyMetadata.getName()) //
            .type(fieldType(propertyMetadata.getType()))//
            .title(fieldTitle(propertyMetadata.getName())) //
            .build();
    }

    private String title(Link link) {
        String title = link.getTitle();
        if (title != null) {
            return title;
        }

        LinkRelation rel = link.getRel();
        if (rel != null) {
            return messageResolver.resolve(SirenLink.TitleResolvable.of(link.getRel()));
        }
        return null;
    }

    private String actionTitle(String name) {
        return name != null ? messageResolver.resolve(SirenAction.TitleResolvable.of(name)) : null;
    }

    private String fieldTitle(String name) {
        return name != null ? messageResolver.resolve(SirenAction.Field.TitleResolvable.of(name)) : null;
    }

    private static Type fieldType(ResolvableType type) {
        return Number.class.isAssignableFrom(type.getRawClass()) ? Type.number : Type.text;
    }

    private static List<SirenNavigables> slice(SirenNavigables navigables) {
        return navigables.getLinks().stream().map(l -> slice(l, navigables.getActions())).collect(Collectors.toList());
    }

    private static SirenNavigables slice(SirenLink link, List<SirenAction> actions) {
        return SirenNavigables.of(newArrayList(link), actions);
    }

    private static List<SirenAffordanceModel> affordanceModels(Link link) {
        return link.getAffordances().stream().map(a -> a.getAffordanceModel(SIREN_JSON)).map(SirenAffordanceModel.class::cast)
            .collect(toList());
    }
}
