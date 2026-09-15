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
package org.esupportail.publisher.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Collections;

import org.esupportail.publisher.web.rest.dto.PaginatedResultDTO;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;
import org.junit.jupiter.api.Test;

public class PagingServiceTest {

    private final PagingService pagingService = new PagingService();

    @Test
    public void paginateActualite_SourceAbsent_PaginatesAllItemsAndKeepsRubriques() {
        Actualite actualite = actualite(Arrays.asList(item("one", "first", 1L), item("two", "second", 2L),
            item("three", "first", 3L)), Arrays.asList(rubrique(1L), rubrique(2L), rubrique(3L)));

        PaginatedResultDTO result = pagingService.paginateActualite(actualite, 1, 2, null, Collections.singletonList(1L));

        assertThat(result.getActualite().getItems().stream().map(ItemVO::getUuid).collect(java.util.stream.Collectors.toList()),
            contains("three"));
        assertThat(result.getActualite().getRubriques().stream().map(RubriqueVO::getUuid).collect(java.util.stream.Collectors.toList()),
            contains("1", "2", "3"));
        assertThat(result.getTotalItems(), is(3));
        assertThat(result.getTotalPages(), is(2));
    }

    @Test
    public void paginateActualite_SourcePresent_FiltersItemsAndPrunesRubriques() {
        Actualite actualite = actualite(Arrays.asList(item("one", "first", 1L), item("two", "second", 2L),
            item("three", "first", 3L)), Arrays.asList(rubrique(1L), rubrique(2L), rubrique(3L), rubrique(4L)));

        PaginatedResultDTO result = pagingService.paginateActualite(actualite, 0, 10, "first", Collections.singletonList(3L));

        assertThat(result.getActualite().getItems().stream().map(ItemVO::getUuid).collect(java.util.stream.Collectors.toList()),
            contains("three"));
        assertThat(result.getActualite().getRubriques().stream().map(RubriqueVO::getUuid).collect(java.util.stream.Collectors.toList()),
            contains("1", "3"));
        assertThat(result.getTotalItems(), is(1));
        assertThat(result.getTotalPages(), is(1));
    }

    @Test
    public void paginateActualite_PageBeyondLastPage_ReturnsEmptyPageWithTotals() {
        Actualite actualite = actualite(Arrays.asList(item("one", "first", 1L), item("two", "second", 2L),
            item("three", "first", 3L)), Collections.emptyList());

        PaginatedResultDTO result = pagingService.paginateActualite(actualite, 2, 2, null, null);

        assertThat(result.getActualite().getItems(), empty());
        assertThat(result.getTotalItems(), is(3));
        assertThat(result.getTotalPages(), is(2));
    }

    private Actualite actualite(java.util.List<ItemVO> items, java.util.List<RubriqueVO> rubriques) {
        Actualite actualite = new Actualite();
        actualite.setItems(items);
        actualite.setRubriques(rubriques);
        return actualite;
    }

    private ItemVO item(String uuid, String source, Long rubrique) {
        ItemVO item = new ItemVO();
        item.setUuid(uuid);
        item.setSource(source);
        item.setRubriques(Collections.singletonList(rubrique));
        return item;
    }

    private RubriqueVO rubrique(Long uuid) {
        RubriqueVO rubrique = new RubriqueVO();
        rubrique.setUuid(uuid.toString());
        rubrique.setName(uuid.toString());
        return rubrique;
    }
}
