package org.duahifnv.filehosting.mapper;

import org.duahifnv.filehosting.dto.SharedMetaDto;
import org.duahifnv.filehosting.model.SharedMeta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SharedMetaMapper {
    @Mapping(source = "id", target = "sharedId")
    SharedMetaDto toDto(SharedMeta sharedMeta);
    List<SharedMetaDto> toDtos(List<SharedMeta> sharedMetas);
}
