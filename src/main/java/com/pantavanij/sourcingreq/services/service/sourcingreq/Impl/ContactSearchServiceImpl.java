package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.mapper.ContactDocumentMapper;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.ContactDocument;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierContactResponse;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ContactElasticsearchRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.ContactSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContactSearchServiceImpl implements ContactSearchService {

    private final ContactElasticsearchRepository repository;

    @Override
    public SupplierContactResponse getContact(String TPShortName) {
        List<ContactDocument> contactList = new ArrayList<>();

        contactList = repository.findByOrgId(TPShortName);

        SupplierContactResponse response = new SupplierContactResponse();

        if (contactList == null) {
            response.setErrors(List.of(Map.of(
                    "code", "4467",
                    "message", String.format("Branch TPShortName %s is not found", TPShortName)
            )));
            return response;
        }

        response.setData(ContactDocumentMapper.INSTANCE.toSupplierContactDtoList(contactList));
        response.setErrors(Collections.emptyList());
        return response;
    }

}
