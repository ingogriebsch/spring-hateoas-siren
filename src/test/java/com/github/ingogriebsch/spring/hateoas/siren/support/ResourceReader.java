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
package com.github.ingogriebsch.spring.hateoas.siren.support;

import java.io.IOException;
import java.util.Scanner;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResourceReader {

    public static String read(@NonNull String sourceFilename, @NonNull Class<?> clazz) throws IOException {
        return read(new ClassPathResource(sourceFilename, clazz));
    }

    public static String read(Resource resource) throws IOException {
        try (Scanner scanner = new Scanner(resource.getInputStream())) {
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
                if (scanner.hasNextLine()) {
                    sb.append(System.lineSeparator());
                }
            }
            return sb.toString();
        }
    }

}
