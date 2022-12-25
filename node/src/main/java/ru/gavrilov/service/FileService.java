package ru.gavrilov.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.gavrilov.entity.AppDocument;
import ru.gavrilov.entity.AppPhoto;
import ru.gavrilov.service.enums.LinkType;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink (Long docId, LinkType linkType);
}
