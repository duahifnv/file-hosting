package org.duahifnv.filehosting.dto;

import org.duahifnv.filehosting.model.FileMeta;

public record FileData(FileMeta metaData, byte[] bytes) {
}
