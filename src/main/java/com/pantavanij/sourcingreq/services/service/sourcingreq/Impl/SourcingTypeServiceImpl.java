package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.repository.sourcingreq.SourcingTypeRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SourcingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourcingTypeServiceImpl implements SourcingTypeService {

    @Autowired
    private SourcingTypeRepository sourcingTypeRepository;
}
