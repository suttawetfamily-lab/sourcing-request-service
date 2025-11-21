package com.pantavanij.sourcingreq.services.interceptor;

import com.pantavanij.sourcingreq.services.exception.DataNotFoundException;
import com.pantavanij.sourcingreq.services.exception.ExternalServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    private ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String details;
        try {
            details = IOUtils.toString(response.body().asInputStream(), "UTF-8");
        } catch (IOException e) {
//            throw new RuntimeException(e);
            details = null;
        }
        switch (response.status()) {
            case 404:
            {
                return new DataNotFoundException(details);
            }
            case 400:
            case 401:
            case 500:
            {
                return new ExternalServiceException(details);
            }
            default:
                return errorDecoder.decode(methodKey, response);
        }
    }
}
