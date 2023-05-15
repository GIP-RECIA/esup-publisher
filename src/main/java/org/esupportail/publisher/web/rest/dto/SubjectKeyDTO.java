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
package org.esupportail.publisher.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 15 oct. 2014
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class SubjectKeyDTO implements ICompositeKey<String, SubjectType>, Serializable {

    @NotNull
    @NonNull
    private String keyId;
    @NotNull
    @NonNull
    @JsonSerialize(using = CustomEnumSerializer.class)
    private SubjectType keyType;
    /**
     * @param keyId
     * @param keyType
     */
    public SubjectKeyDTO(@NonNull final String keyId, @NonNull final SubjectType keyType) {
        this.keyId = keyId;
        this.keyType = keyType;
    }

}
