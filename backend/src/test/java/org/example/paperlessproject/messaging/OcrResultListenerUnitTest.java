package org.example.paperlessproject.messaging;

import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.repository.DocumentRepository;
import org.example.paperlessproject.repository.ElasticSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OcrResultListenerUnitTest {

    @Mock DocumentRepository documentRepository;
    @Mock ElasticSearchRepository elasticSearchRepository;
    @Mock AmqpTemplate amqpTemplate;

    @InjectMocks OcrResultListener listener;

    @BeforeEach
    void setup() {
        // damit queue nicht null ist
        ReflectionTestUtils.setField(listener, "genAiRequestQueue", "genai.queue");
    }

    @Test
    void onOcrResult_savesTextToElastic_whenDocumentExists() {
        // given
        Long docId = 10L;

        DocumentEntity doc = new DocumentEntity();
        doc.setId(docId);
        doc.setName("dummy.pdf");

        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        DocumentOcrResult msg = new DocumentOcrResult(docId, "OCR TEXT");

        // when
        listener.onOcrResult(msg);

        // then
        verify(documentRepository).findById(docId);
        verify(elasticSearchRepository).save(argThat(es ->
                docId.equals(es.getId()) &&
                        "OCR TEXT".equals(es.getOcrText())
        ));
        verify(amqpTemplate).convertAndSend(eq("genai.queue"), anyMap());

    }

    @Test
    void onOcrResult_doesNotIndex_whenDocumentNotFound_butStillSendsGenAiIfTextPresent() {
        // given
        Long docId = 99L;
        when(documentRepository.findById(docId)).thenReturn(Optional.empty());

        DocumentOcrResult msg = new DocumentOcrResult(docId, "TEXT");

        // when
        listener.onOcrResult(msg);

        // then
        verify(documentRepository).findById(docId);
        verifyNoInteractions(elasticSearchRepository);

        // wichtig: dein Code sendet trotzdem GenAI wenn text != blank
        verify(amqpTemplate).convertAndSend(eq("genai.queue"), anyMap());

    }

    @Test
    void onOcrResult_whenTextIsNull_doesNotSendGenAi_andStillIndexesCurrentImplementation() {
        // given
        Long docId = 1L;

        DocumentEntity doc = new DocumentEntity();
        doc.setId(docId);
        doc.setName("dummy.pdf");
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        DocumentOcrResult msg = new DocumentOcrResult(docId, null);

        // when
        listener.onOcrResult(msg);

        verify(elasticSearchRepository).save(any());
        verifyNoInteractions(amqpTemplate);

    }
}
