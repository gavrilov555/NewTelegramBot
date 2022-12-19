package ru.gavrilov.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gavrilov.service.UpdateProducer;

@Service
@Log4j
public class UpdateProducerImpl implements UpdateProducer {

    private final RabbitTemplate rabbitTeamplate;

    public UpdateProducerImpl(RabbitTemplate rabbitTeamplate) {
        this.rabbitTeamplate = rabbitTeamplate;
    }


    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());
        rabbitTeamplate.convertAndSend(rabbitQueue,update);
    }
}
