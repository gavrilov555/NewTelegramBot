package ru.gavrilov.service;

import org.springframework.core.io.FileSystemResource;
import ru.gavrilov.dao.AppDocumentDAO;
import ru.gavrilov.entity.AppDocument;
import ru.gavrilov.entity.AppPhoto;
import ru.gavrilov.entity.BinaryContent;

public interface FileService {

    AppDocument getDocument(String id);

    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
