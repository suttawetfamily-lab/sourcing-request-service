package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

@RequiredArgsConstructor
@Service
public class ValidatorServiceImpl implements ValidatorService {

    private final ValidatorRepository validatorRepository;

    @Override
    public List<ValidatorDto> getAllValidator() {
        try {
            List<Validator> validatorList = validatorRepository.findAll();
            if (!validatorList.isEmpty()) {
                return validatorList.stream()
                        .map(validator -> {
                            ValidatorDto validatorDto = new ValidatorDto();

                            String label = validator.getName();
                            label = label.substring(0, 1).toUpperCase() + label.substring(1);

                            validatorDto.setRecId(validator.getRecId());
                            validatorDto.setName(validator.getName());
                            validatorDto.setValue(validator.getRecId().toString());
                            validatorDto.setLabel(label);
                            return validatorDto;
                        })
                        .collect(Collectors.toList());
            } else {
                throw new DataNotFoundException(String.format(ApiMessage.E7096.description(), "Validator"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataNotFoundException(String.format(ApiMessage.E7096.description(), "Validator"));
        }
    }
}
