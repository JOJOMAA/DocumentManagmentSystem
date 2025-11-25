package org.example.paperlessproject.messaging;

import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.repository.DocumentRepository;
import org.example.paperlessproject.repository.ElasticSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.test.context.TestPropertySource;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataElasticsearchTest
@TestPropertySource(properties = "spring.elasticsearch.uris=http://localhost:9200")
class OcrResultListenerTest {

    @Autowired
    private ElasticSearchRepository realElasticRepo;

    @Test
    void testWorkerSavesToElastic() {
        DocumentRepository fakePostgres = new DummyDocumentRepo();

        OcrResultListener listener = new OcrResultListener(null, fakePostgres, realElasticRepo);

        Long docId = 999L;
        DocumentOcrResult message = new DocumentOcrResult(docId, "Worker Test Text");

        listener.onOcrResult(message);

        var savedDoc = realElasticRepo.findById(docId);
        assertTrue(savedDoc.isPresent());
        assertEquals("Worker Test Text", savedDoc.get().getOcrText());

        realElasticRepo.deleteById(docId);
    }

    static class DummyDocumentRepo implements DocumentRepository {
        @Override
        public Optional<DocumentEntity> findById(Long id) {
            DocumentEntity doc = new DocumentEntity();
            doc.setId(id);
            doc.setName("dummy.pdf");
            return Optional.of(doc);
        }
        @Override public <S extends DocumentEntity> S save(S entity) { return entity; }

        // Für alle anderen Methoden: Einfach "return null" (IntelliJ hilft beim Generieren)
        @Override public java.util.List<DocumentEntity> findAll() { return null; }
        @Override public java.util.List<DocumentEntity> findAll(org.springframework.data.domain.Sort sort) { return null; }
        @Override public java.util.List<DocumentEntity> findAllById(Iterable<Long> longs) { return null; }
        @Override public <S extends DocumentEntity> java.util.List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public void flush() {}
        @Override public <S extends DocumentEntity> S saveAndFlush(S entity) { return null; }
        @Override public <S extends DocumentEntity> java.util.List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<DocumentEntity> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override public void deleteAllInBatch() {}
        @Override public DocumentEntity getOne(Long aLong) { return null; }
        @Override public DocumentEntity getById(Long aLong) { return null; }
        @Override public DocumentEntity getReferenceById(Long aLong) { return null; }
        @Override public <S extends DocumentEntity> java.util.List<S> findAll(org.springframework.data.domain.Example<S> example) { return null; }
        @Override public <S extends DocumentEntity> java.util.List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort) { return null; }
        @Override public org.springframework.data.domain.Page<DocumentEntity> findAll(org.springframework.data.domain.Pageable pageable) { return null; }
        @Override public boolean existsById(Long aLong) { return false; }
        @Override public long count() { return 0; }
        @Override public void deleteById(Long aLong) {}
        @Override public void delete(DocumentEntity entity) {}
        @Override public void deleteAllById(Iterable<? extends Long> longs) {}
        @Override public void deleteAll(Iterable<? extends DocumentEntity> entities) {}
        @Override public void deleteAll() {}
        @Override public <S extends DocumentEntity> Optional<S> findOne(org.springframework.data.domain.Example<S> example) { return Optional.empty(); }
        @Override public <S extends DocumentEntity> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) { return null; }
        @Override public <S extends DocumentEntity> long count(org.springframework.data.domain.Example<S> example) { return 0; }
        @Override public <S extends DocumentEntity> boolean exists(org.springframework.data.domain.Example<S> example) { return false; }
        @Override public <S extends DocumentEntity, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override
        public java.util.List<DocumentEntity> findByOcrTextContainingIgnoreCase(String query) {
            return null;
        }
    }
}