package com.pantavanij.sourcingreq.services.converter;

import com.pantavanij.sourcingreq.services.enums.MasterGroup;
import com.pantavanij.sourcingreq.services.enums.PathUrl;
import org.springframework.core.convert.converter.Converter;

public class StringToMasterGroupConverter implements Converter<String, MasterGroup> {

    @Override
    public MasterGroup convert(String source) {
        return MasterGroup.findByStrGroup(source);
    }
}
