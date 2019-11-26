/*-
 * #%L
 * Spring HATEOAS Siren
 * %%
 * Copyright (C) 2018 - 2019 Ingo Griebsch
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
package org.springframework.hateoas.support;

import static java.util.Collections.singletonMap;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class StaticMessageResolver implements MessageResolver {

    @NonNull
    private final Map<String, String> messages;
    private final String fallback;

    public static StaticMessageResolver of(String fallback) {
        return new StaticMessageResolver(newHashMap(), fallback);
    }

    public static StaticMessageResolver of(@NonNull String key, @NonNull String value) {
        return new StaticMessageResolver(singletonMap(key, value), null);
    }

    public static StaticMessageResolver of(@NonNull String key, @NonNull String value, String fallback) {
        return new StaticMessageResolver(singletonMap(key, value), fallback);
    }

    public static StaticMessageResolver of(@NonNull Map<String, String> messages) {
        return new StaticMessageResolver(messages, null);
    }

    @Override
    public String resolve(MessageSourceResolvable resolvable) {
        String[] codes = resolvable.getCodes();
        for (String code : codes) {
            if (messages.containsKey(code)) {
                return messages.get(code);
            }
        }
        return fallback;
    }
}
