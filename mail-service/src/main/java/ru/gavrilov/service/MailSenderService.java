package ru.gavrilov.service;

import ru.gavrilov.dto.MailParams;

public interface MailSenderService {
    void send (MailParams mailParams);
}
