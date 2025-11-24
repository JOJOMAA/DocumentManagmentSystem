package org.example.paperlessproject.repository;

import org.example.paperlessproject.model.elastic.ElasticSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface ElasticSearchRepository extends ElasticsearchRepository<ElasticSearch, Long> {
    // Spring Data generiert die Suche automatisch
    List<ElasticSearch> findByOcrTextContaining(String query);
}