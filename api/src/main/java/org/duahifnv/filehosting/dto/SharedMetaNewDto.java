package org.duahifnv.filehosting.dto;

import java.util.List;

public record SharedMetaNewDto(List<String> sharedUsersEmails, String sharingLifetime) {
}
