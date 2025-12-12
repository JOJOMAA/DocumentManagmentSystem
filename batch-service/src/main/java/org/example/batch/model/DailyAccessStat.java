package org.example.batch.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "daily_access_stats")
@Data
@NoArgsConstructor
public class DailyAccessStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "access_date", nullable = false)
    private LocalDate accessDate;

    @Column(name = "access_count", nullable = false)
    private Long accessCount;

    public DailyAccessStat(Long documentId, LocalDate accessDate, Long accessCount) {
        this.documentId = documentId;
        this.accessDate = accessDate;
        this.accessCount = accessCount;
    }
}