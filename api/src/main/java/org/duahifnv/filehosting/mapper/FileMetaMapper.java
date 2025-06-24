package org.duahifnv.filehosting.mapper;

import org.duahifnv.filehosting.dto.FileMetaDto;
import org.duahifnv.filehosting.model.FileMeta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMetaMapper {
    @Mapping(target = "username", source = "user.username")
    FileMetaDto toDto(FileMeta meta);

    List<FileMetaDto> toDtos(List<FileMeta> metas);
}
