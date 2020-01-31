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
package org.springframework.hateoas.mediatype.siren;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase;

import org.springframework.hateoas.RepresentationModel;

abstract class AbstractSirenDeserializer<T extends RepresentationModel<?>> extends ContainerDeserializerBase<T>
    implements ContextualDeserializer {

    private static final long serialVersionUID = 3796755247545654672L;

    protected final SirenConfiguration sirenConfiguration;
    protected final SirenLinkConverter linkConverter;
    protected final JavaType contentType;

    protected AbstractSirenDeserializer(SirenConfiguration sirenConfiguration, SirenLinkConverter linkConverter,
        JavaType contentType) {
        super(contentType);
        this.sirenConfiguration = sirenConfiguration;
        this.linkConverter = linkConverter;
        this.contentType = contentType;
    }

    @Override
    public JavaType getContentType() {
        return contentType;
    }

    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return null;
    }

}
