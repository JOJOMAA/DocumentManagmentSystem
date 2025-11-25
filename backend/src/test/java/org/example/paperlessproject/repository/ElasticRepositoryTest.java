package org.example.paperlessproject.repository;

import org.example.paperlessproject.model.elastic.ElasticSearch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataElasticsearchTest
@TestPropertySource(properties = "spring.elasticsearch.uris=http://localhost:9200")
class ElasticRepositoryTest {

    @Autowired
    private ElasticSearchRepository repository;

    @Test
    void testSaveAndFind_RealDocker() {

        String specialWord = "Apfelkuchen";
        ElasticSearch doc = new ElasticSearch(777L, "rezept.pdf", "Ein Text über leckeren " + specialWord);

        System.out.println("Speichere in Elastic...");
        repository.save(doc);

        System.out.println("Suche nach '" + specialWord + "'...");
        List<ElasticSearch> results = repository.findByOcrTextContaining(specialWord);

        assertFalse(results.isEmpty(), "Sollte das Dokument gefunden haben");
        assertEquals("rezept.pdf", results.get(0).getName());

        System.out.println("Gefunden: " + results.get(0).getName());

        repository.delete(doc);
    }
}