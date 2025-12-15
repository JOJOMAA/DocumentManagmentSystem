package org.example.paperlessproject.messaging;

import org.example.paperlessproject.model.DocumentEntity;
import org.example.paperlessproject.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenAiResultListenerTest {

    @Mock
    DocumentRepository documentRepository;

    @InjectMocks
    GenAiResultListener listener;

    @Test
    void onGenAiResult_updatesDocumentAndSaves_whenDocumentExists() {
        // given
        Long docId = 5L;
        DocumentEntity doc = new DocumentEntity();
        doc.setId(docId);

        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        GenAiResultMessage msg = new GenAiResultMessage();
        msg.setDocumentId(docId);
        msg.setSummary("This is a summary");

        // when
        listener.onGenAiResult(msg);

        // then
        verify(documentRepository).findById(docId);

        ArgumentCaptor<DocumentEntity> cap = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(documentRepository).save(cap.capture());

        DocumentEntity saved = cap.getValue();
        assertEquals(docId, saved.getId());
        assertEquals("This is a summary", saved.getSummary());
    }

    @Test
    void onGenAiResult_doesNothing_whenDocumentMissing() {
        // given
        Long docId = 99L;
        when(documentRepository.findById(docId)).thenReturn(Optional.empty());

        GenAiResultMessage msg = new GenAiResultMessage();
        msg.setDocumentId(docId);
        msg.setSummary("summary");

        // when
        listener.onGenAiResult(msg);

        // then
        verify(documentRepository).findById(docId);
        verify(documentRepository, never()).save(any());
    }

    @Test
    void onGenAiResult_ignores_whenDocumentIdNull() {
        // given
        GenAiResultMessage msg = new GenAiResultMessage();
        msg.setDocumentId(null);
        msg.setSummary("summary");

        // when
        listener.onGenAiResult(msg);

        // then
        verifyNoInteractions(documentRepository);
    }
}
