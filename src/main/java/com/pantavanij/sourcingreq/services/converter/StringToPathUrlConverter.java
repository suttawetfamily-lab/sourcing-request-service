package com.pantavanij.sourcingreq.services.converter;

import com.pantavanij.sourcingreq.services.enums.PathUrl;
import org.springframework.core.convert.converter.Converter;

public class StringToPathUrlConverter implements Converter<String, PathUrl> {

    @Override
    public PathUrl convert(String source) {
        return PathUrl.findByStrPath(source);
    }
}
