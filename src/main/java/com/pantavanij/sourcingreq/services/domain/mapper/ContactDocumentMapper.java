package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierContactsDto;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.ContactDocument;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ContactDocumentMapper {
    ContactDocumentMapper INSTANCE = Mappers.getMapper(ContactDocumentMapper.class);

    SupplierContactsDto toSupplierContactDto(ContactDocument contactDocument);
    List<SupplierContactsDto> toSupplierContactDtoList(List<ContactDocument> contactDocuments);
}
