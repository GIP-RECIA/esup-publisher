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
