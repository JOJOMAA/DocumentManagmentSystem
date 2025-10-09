package org.example.paperlessproject.mapper;

import org.example.paperlessproject.dto.DocumentDto;
import org.example.paperlessproject.model.DocumentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentDto toDto(DocumentEntity entity);
}
