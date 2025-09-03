package com.smartsensesolutions.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsensesolutions.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public abstract class BaseService implements ServiceUtil {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }
}
