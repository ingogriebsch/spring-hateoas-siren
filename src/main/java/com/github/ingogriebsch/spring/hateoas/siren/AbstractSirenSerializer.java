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

import java.util.List;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;

/**
 * Base class supporting the implementation of serializers which are able to serialize a representation model related object into
 * a Siren entity.
 * 
 * @author Ingo Griebsch
 */
abstract class AbstractSirenSerializer<T> extends ContainerSerializer<T> implements ContextualSerializer {

    private static final long serialVersionUID = -8665900081601124431L;
    protected static final String ATTR_KEY_PARENT = "__SIREN_ENTITY_PARENT__";
    protected static final String ATTR_KEY_REL = "__SIREN_ENTITY_REL__";

    protected final SirenConfiguration configuration;
    protected final SirenSerializerFacilities serializerFacilities;
    protected final BeanProperty property;

    protected AbstractSirenSerializer(Class<?> type, SirenConfiguration configuration,
        SirenSerializerFacilities serializerFacilities, BeanProperty property) {
        super(type, false);
        this.configuration = configuration;
        this.serializerFacilities = serializerFacilities;
        this.property = property;
    }

    @Override
    public JavaType getContentType() {
        return null;
    }

    @Override
    public JsonSerializer<?> getContentSerializer() {
        return null;
    }

    @Override
    public boolean hasSingleElement(T value) {
        return false;
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        return null;
    }

    protected SirenLinkConverter getLinkConverter() {
        return serializerFacilities.getLinkConverter();
    }

    protected List<String> classes(RepresentationModel<?> model) {
        return serializerFacilities.getEntityClassProvider().get(model);
    }

    protected List<LinkRelation> rels(RepresentationModel<?> model, SerializerProvider provider) {
        List<LinkRelation> rels = removeAttribute(ATTR_KEY_REL, provider);
        if (rels != null) {
            return rels;
        }

        RepresentationModel<?> parent = getAttribute(ATTR_KEY_PARENT, provider);
        return serializerFacilities.getEntityRelProvider().get(model, parent);
    }

    protected String title(Class<?> type) {
        return serializerFacilities.getMessageResolver().resolve(SirenEntity.TitleResolvable.of(type));
    }

    protected Object setAttribute(String key, Object value, SerializerProvider provider) {
        Object current = provider.getAttribute(key);
        provider.setAttribute(key, value);
        return current;
    }

    @SuppressWarnings("unchecked")
    protected <A> A getAttribute(String key, SerializerProvider provider) {
        return (A) provider.getAttribute(key);
    }

    protected <A> A removeAttribute(String key, SerializerProvider provider) {
        A attribute = getAttribute(key, provider);
        provider.setAttribute(key, null);
        return attribute;
    }
}
