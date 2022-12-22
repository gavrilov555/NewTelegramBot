package ru.gavrilov.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.gavrilov.entity.AppDocument;

public interface FileService {
    AppDocument processDoc(Message externalMessage);
}
