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
package org.esupportail.publisher.service.factories.impl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Attachment;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.Media;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Resource;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.service.factories.ItemVOForReadFactory;
import org.esupportail.publisher.web.rest.util.ISO8601LocalDateTimeXmlAdapter;
import org.esupportail.publisher.web.rest.vo.ItemVOForRead;
import org.esupportail.publisher.web.rest.vo.LinkedFileVO;
import org.esupportail.publisher.web.rest.vo.ns.ArticleVO;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ItemVOForReadFactoryImpl implements ItemVOForReadFactory {

    @Inject
    private ServiceUrlHelper urlHelper;

    private XmlAdapter<String, Instant> ISO8601Adapter = new ISO8601LocalDateTimeXmlAdapter();

    @SneakyThrows
    public ItemVOForRead from(final AbstractItem item, final List<AbstractClassification> classifications, final List<Subscriber> subscribers,
                       final List<LinkedFileItem> linkedFiles, final HttpServletRequest request) {

        if (item != null) {
            ItemVOForRead vo = new ItemVOForRead();
            vo.setRubriques(new ArrayList<>());
            vo.setType(item.getClass().getSimpleName());
            ArticleVO article = new ArticleVO();
            article.setTitle(item.getTitle());
            article.setLink(urlHelper.getContextPath() + urlHelper.getMyNewsUri() + item.getId());
            if (item.getEnclosure() != null) {
                if (item.getEnclosure().matches("^https?://.*$")) {
                    article.setEnclosure(item.getEnclosure());
                } else {
                    article.setEnclosure(urlHelper.getContextPath() + "/" + item.getEnclosure());
                }
            }
            article.setDescription(item.getSummary());
            article.setPubDate(item.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            article.setGuid(item.getId().hashCode());
            article.setCategories(new ArrayList<>());
            for (AbstractClassification classif: classifications) {
                article.getCategories().add(classif.getDisplayName());
                vo.getRubriques().add(classif.getId());
            }

            article.setCreator(item.getCreatedBy().getDisplayName());
            article.setDate(item.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            article.setFiles(new ArrayList<>());
            for (LinkedFileItem linkedFile: linkedFiles) {
                if (linkedFile.getUri() != null && !linkedFile.getUri().isEmpty()) {
                    LinkedFileVO fileVO = new LinkedFileVO();
                    fileVO.setUri(urlHelper.getContextPath() + "/" + linkedFile.getUri());
                    fileVO.setFileName(linkedFile.getFilename());
                    fileVO.setContentType(linkedFile.getContentType());
                    article.getFiles().add(fileVO);
                }
            }

            vo.setCreatedBy(item.getCreatedBy().getDisplayName());
            vo.setLastModifiedBy(item.getLastModifiedBy().getDisplayName());
            vo.setValidatedBy(item.getValidatedBy().getDisplayName());
            vo.setPubBy(item.getOrganization().getName());
            if (item instanceof News) {
                vo.setBody(urlHelper.replaceBodyUrl(((News) item).getBody(), request));
            } else if (item instanceof Resource) {
                vo.setRessourceUrl(urlHelper.replaceRelativeUrl(((Resource) item).getRessourceUrl(), request));
            } else if (!(item instanceof Media) && !(item instanceof Attachment)) {
                log.error("Warning a new type of Item wasn't managed at this place, the item is : {}", item);
                throw new IllegalStateException("Warning missing type management of :" + item);
            }
            vo.setArticle(article);
            vo.setPubDate(ISO8601Adapter.marshal(item.getStartDate().atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().toInstant()));
            vo.setCreatedDate(ISO8601Adapter.marshal(item.getCreatedDate()));
            if (item.getLastModifiedDate() != null)
                vo.setModifiedDate(ISO8601Adapter.marshal(item.getLastModifiedDate().truncatedTo(ChronoUnit.MILLIS)));
            vo.setValidatedDate(ISO8601Adapter.marshal(item.getValidatedDate()));

            vo.setInternalViewLink(urlHelper.getContextPath() + urlHelper.getItemUri() + item.getId());

            return vo;
        }
        return null;
    }

}
