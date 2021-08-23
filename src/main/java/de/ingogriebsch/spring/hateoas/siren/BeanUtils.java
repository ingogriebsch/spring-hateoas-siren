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

import static java.util.Optional.ofNullable;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static org.springframework.beans.BeanUtils.instantiateClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import lombok.NoArgsConstructor;
import org.springframework.util.ReflectionUtils;

/**
 * Utility methods for instantiating beans, extracting bean properties, applying bean properties, etc.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 */
@NoArgsConstructor(access = PRIVATE)
class BeanUtils {

    static <T> T instantiate(Class<T> clazz, Class<?>[] types, Object[] args) {
        try {
            return instantiateClass(clazz.getDeclaredConstructor(types), args);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    static <T> T applyProperties(T obj, Map<String, Object> properties) {
        properties.forEach((key, value) -> ofNullable(getPropertyDescriptor(obj.getClass(), key)).ifPresent(property -> {
            try {
                Method writeMethod = property.getWriteMethod();
                ReflectionUtils.makeAccessible(writeMethod);
                writeMethod.invoke(obj, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }));

        return obj;
    }

}
