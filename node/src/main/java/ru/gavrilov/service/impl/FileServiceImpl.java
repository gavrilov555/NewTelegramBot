package ru.gavrilov.service.impl;

import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.gavrilov.dao.AppDocumentDAO;
import ru.gavrilov.dao.AppPhotoDAO;
import ru.gavrilov.dao.BinaryContentDAO;
import ru.gavrilov.entity.AppDocument;
import ru.gavrilov.entity.AppPhoto;
import ru.gavrilov.entity.BinaryContent;
import ru.gavrilov.exception.UploadFileException;
import ru.gavrilov.service.FileService;
import ru.gavrilov.service.enums.LinkType;
import ru.gavrilov.utils.CryptoTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.address}")
    private String linkAddress;
    private AppDocumentDAO appDocumentDAO;
    private BinaryContentDAO binaryContentDAO;
    private AppPhotoDAO appPhotoDAO;
    private CryptoTool cryptoTool;


    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document telegramDoc = telegramMessage.getDocument();
        String fileId = telegramDoc.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent
                .builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        var photoSizeCount = telegramMessage.getPhoto().size();
        var photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
            }else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }


    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

        private AppDocument buildTransientAppDoc (Document telegramDoc, BinaryContent persistentBinaryContent){
            return AppDocument.builder()
                    .telegramFileId(telegramDoc.getFileId())
                    .docName(telegramDoc.getFileName())
                    .binaryContent(persistentBinaryContent)
                    .mimeType(telegramDoc.getMimeType())
                    .fileSize(telegramDoc.getFileSize())
                    .build();
        }

        private ResponseEntity<String> getFilePath (String fileId){
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> request = new HttpEntity<>(headers);

            return restTemplate.exchange(fileInfoUri, HttpMethod.GET, request, String.class, token, fileId);
        }

        private byte[] downloadFile (String filePath){
            String fullUri = fileStorageUri.replace("{token}", token)
                    .replace("{filePath}", filePath);
            URL urlObj = null;
            try {
                urlObj = new URL(fullUri);
            } catch (MalformedURLException e) {
                throw new UploadFileException(e);
            }
            //TODO подумать над оптимизацией
            try (InputStream is = urlObj.openStream()) {
                return is.readAllBytes();
            } catch (IOException e) {
                throw new UploadFileException(urlObj.toExternalForm(), e);
            }
        }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }

}

