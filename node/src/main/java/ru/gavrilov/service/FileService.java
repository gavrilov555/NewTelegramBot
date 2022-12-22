package ru.gavrilov.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.gavrilov.entity.AppDocument;
import ru.gavrilov.entity.AppPhoto;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
