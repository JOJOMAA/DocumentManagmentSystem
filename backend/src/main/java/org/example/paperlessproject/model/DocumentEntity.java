package org.example.paperlessproject.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob //can handle large documents
    @Column(name = "content", nullable = false)
    private byte[] content;
}
