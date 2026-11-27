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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;

import org.esupportail.publisher.domain.ReadingIndicator;
import org.esupportail.publisher.repository.ReadingIndicatorRepository;
import org.esupportail.publisher.repository.predicates.ReadingIndicatorPredicates;
import org.esupportail.publisher.web.rest.dto.ReadingStatisticsDTO;
import org.springframework.stereotype.Service;

@Service
public class ReadingStatisticsService {

    @Inject
    private ReadingIndicatorRepository readingIndicatorRepository;

    public ReadingStatisticsDTO getStatistics(final long itemId) {
        return getStatistics(java.util.Collections.singleton(itemId)).getOrDefault(itemId, new ReadingStatisticsDTO());
    }

    public Map<Long, ReadingStatisticsDTO> getStatistics(final Collection<Long> itemIds) {
        final Map<Long, ReadingStatisticsDTO> statistics = new HashMap<>();
        if (itemIds.isEmpty()) {
            return statistics;
        }

        this.readingIndicatorRepository.findAll(ReadingIndicatorPredicates.readingIndicationsOfItems(itemIds))
            .forEach(indicator -> addIndicator(statistics, indicator));
        return statistics;
    }

    private void addIndicator(final Map<Long, ReadingStatisticsDTO> statistics, final ReadingIndicator indicator) {
        final ReadingStatisticsDTO itemStatistics = statistics.computeIfAbsent(indicator.getItem().getId(), key -> new ReadingStatisticsDTO());
        itemStatistics.setReadingCount(itemStatistics.getReadingCount() + indicator.getReadingCounter());
        if (indicator.isRead()) {
            itemStatistics.setReaderCount(itemStatistics.getReaderCount() + 1);
        }
    }
}
