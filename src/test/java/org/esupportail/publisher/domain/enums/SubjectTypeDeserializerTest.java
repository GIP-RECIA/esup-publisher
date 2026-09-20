/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.domain.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

class SubjectTypeDeserializerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void deserializeAcceptsSubjectTypeNamesCaseInsensitively() throws JacksonException {
        assertEquals(SubjectType.GROUP, objectMapper.readValue("\"group\"", SubjectType.class));
        assertEquals(SubjectType.PERSON_ATTR_REGEX,
            objectMapper.readValue("\"person_attr_regex\"", SubjectType.class));
    }

    @Test
    void deserializeRejectsUnknownSubjectTypeNames() {
        assertThrows(JacksonException.class,
            () -> objectMapper.readValue("\"UNKNOWN\"", SubjectType.class));
        assertThrows(JacksonException.class,
            () -> objectMapper.readValue("\"GROUPS\"", SubjectType.class));
        assertThrows(JacksonException.class,
            () -> objectMapper.readValue("\"\"", SubjectType.class));
    }
}
