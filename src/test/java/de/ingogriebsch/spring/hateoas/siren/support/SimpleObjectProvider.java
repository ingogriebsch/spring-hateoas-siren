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
package de.ingogriebsch.spring.hateoas.siren.support;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;

@RequiredArgsConstructor
public class SimpleObjectProvider<T> implements ObjectProvider<T> {

    private final T object;

    @Override
    public T getObject() throws BeansException {
        return object;
    }

    @Override
    public T getObject(Object... args) throws BeansException {
        return getObject();
    }

    @Override
    public T getIfAvailable() throws BeansException {
        return getObject();
    }

    @Override
    public T getIfUnique() throws BeansException {
        return getObject();
    }
}
