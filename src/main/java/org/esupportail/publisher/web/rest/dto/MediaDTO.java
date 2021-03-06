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

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.ToString;
import org.esupportail.publisher.domain.enums.ItemStatus;
import java.time.Instant;
import java.time.LocalDate;

@ToString(callSuper=true)
public class MediaDTO extends ItemDTO implements Serializable {

    /**
     * Constructor to use to create the object from the JPA model.
     * @param modelId
     * @param title
     * @param enclosure
     * @param startDate
     * @param endDate
     * @param validatedDate
     * @param validatedBy
     * @param status
     * @param summary
     * @param rssAllowed
     * @param highlight
     * @param organization
     * @param redactor
     * @param creationDate
     * @param lastUpdateDate
     * @param createdBy
     * @param lastUpdateBy
     */
    public MediaDTO(@NotNull final Long modelId, @NotNull final String title, @NotNull final String enclosure,
            @NotNull final LocalDate startDate, final LocalDate endDate,
            final Instant validatedDate, final SubjectDTO validatedBy, @NotNull final ItemStatus status,
            @NotNull final String summary, final boolean rssAllowed, final boolean highlight, @NotNull final OrganizationDTO organization,
            @NotNull final RedactorDTO redactor, @NotNull final Instant creationDate, final Instant lastUpdateDate,
            @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy) {
        super(modelId, title, enclosure, startDate, endDate, validatedDate,
                validatedBy, status, summary, rssAllowed, highlight, organization, redactor, creationDate,
                lastUpdateDate, createdBy, lastUpdateBy);
    }

    /**
     * Constructor to use when creating a new Object.
     * @param createdBy
     * @param organization
     * @param redactor
     */
    public MediaDTO(@NotNull final SubjectDTO createdBy, @NotNull final OrganizationDTO organization, @NotNull final RedactorDTO redactor) {
        super(createdBy, organization, redactor);
    }


}
