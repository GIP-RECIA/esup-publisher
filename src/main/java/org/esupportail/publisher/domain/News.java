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
package org.esupportail.publisher.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ItemStatus;

/**
 * @author GIP RECIA - Julien Gribonvald 18 juin 2014
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonTypeName("NEWS")
@Entity
public class News extends AbstractItem implements Serializable {

    /** */
    private static final long serialVersionUID = 8024488811030037006L;

    /** This field corresponds to the database column body. */
    @NotNull
    @NonNull
    @Size(min = 5)
    @Lob
    @Basic
    // @Column(nullable=false) can't apply as single table strategy
    private String body;
    
    /**
     * Empty constructor of News.
     */
    public News() {
        super();
    }

    /**
     * Constructor of News.
     *
     * @param title
     * @param enclosure
     * @param body
     * @param startDate
     * @param endDate
     * @param validatedDate
     * @param validatedBy
     * @param status
     * @param summary
     * @param rssAllowed
     * @param organization
     * @param redactor
     */
    public News(final String title, final String enclosure, final String body,
                final LocalDate startDate, final LocalDate endDate,
                final Instant validatedDate, final User validatedBy,
                final ItemStatus status, final String summary, final boolean rssAllowed,
                final boolean highlight, final Organization organization, final Redactor redactor) {
        super(title, enclosure, startDate, endDate, validatedDate, validatedBy,
            status, summary, rssAllowed, highlight, organization, redactor);
        this.body = body;
    }

}
