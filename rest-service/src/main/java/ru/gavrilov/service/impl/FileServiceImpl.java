package ru.gavrilov.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import ru.gavrilov.dao.AppDocumentDAO;
import ru.gavrilov.dao.AppPhotoDAO;
import ru.gavrilov.dao.BinaryContentDAO;
import ru.gavrilov.entity.AppDocument;
import ru.gavrilov.entity.AppPhoto;
import ru.gavrilov.entity.BinaryContent;
import ru.gavrilov.service.FileService;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    private final BinaryContentDAO binaryContentDAO;
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO,
                           BinaryContentDAO binaryContentDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;

        this.binaryContentDAO = binaryContentDAO;
    }

    @Override
    public AppDocument getDocument(String docId) {
        //TODO добавить дешифрование хеш-строки
        var id = Long.parseLong(docId);
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        //TODO добваить дешифрование хеш-строки
        var id = Long.parseLong((photoId));
        return  appPhotoDAO.findById(id).orElse(null);

    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try{
            //TODO добавить генерацию имени временного файла
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);

        } catch (IOException e) {
            log.error(e);
            return  null;
        }
    }
}
