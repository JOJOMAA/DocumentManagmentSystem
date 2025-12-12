package org.example.batch.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "access_logs")
public class AccessLogsXml {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "entry")
    private List<Entry> entries;

    @Data
    public static class Entry {
        @JacksonXmlProperty(localName = "document_id")
        private Long documentId;

        @JacksonXmlProperty(localName = "user_id")
        private String userId;

        @JacksonXmlProperty(localName = "timestamp")
        private String timestamp;
    }
}