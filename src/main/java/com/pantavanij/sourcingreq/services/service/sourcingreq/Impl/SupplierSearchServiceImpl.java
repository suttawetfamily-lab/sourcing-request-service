package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.mapper.SupplierDocumentMapper;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierDocument;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SupplierElasticsearchRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SupplierSearchService;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierWebworksDto;
import com.pantavanij.sourcingreq.services.domain.request.SupplierWebWorkSearchByTPShortNameRequest;
import com.pantavanij.sourcingreq.services.domain.request.SupplierWebWorksSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierWebWorkResponse;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierWebWorksResponse;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierSearchServiceImpl implements SupplierSearchService {

    private final SupplierElasticsearchRepository repository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public SupplierWebWorksResponse searchCompany(SupplierWebWorksSearchRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (request.getSearching() != null && !request.getSearching().isBlank()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(
                    request.getSearching(),
                    "companyNameLocal",
                    "companyNameInter",
                    "invNameLocal",
                    "invNameInter"
            ).operator(Operator.AND));
        }

        if (request.getInvitationCode() != null && !request.getInvitationCode().isBlank()) {
            boolQuery.filter(QueryBuilders.termQuery("invitationCode.keyword", request.getInvitationCode()));
        }

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, request.getLimit()))
                .build();

        SearchHits<SupplierDocument> hits = elasticsearchRestTemplate.search(searchQuery, SupplierDocument.class);

        List<SupplierWebworksDto> dtoList = hits.getSearchHits().stream()
                .map(hit -> SupplierDocumentMapper.INSTANCE.toSupplierWebworksDto(hit.getContent()))
                .collect(Collectors.toList());

        SupplierWebWorksResponse response = new SupplierWebWorksResponse();
        response.setData(dtoList);
        response.setErrors(Collections.emptyList());
        return response;
    }


    @Override
    public SupplierWebWorkResponse getSupplier(SupplierWebWorkSearchByTPShortNameRequest request) {
        SupplierDocument doc;

        if (request.getInvitationCode() != null && !request.getInvitationCode().isBlank()) {
            doc = repository.findByOrgIdAndInvitationCode(request.getTPShortName(), request.getInvitationCode());
        } else {
            doc = repository.findByOrgId(request.getTPShortName());
        }

        SupplierWebWorkResponse response = new SupplierWebWorkResponse();

        if (doc == null) {
            response.setErrors(List.of(Map.of(
                    "code", "4467",
                    "message", String.format("Branch TPShortName %s is not found", request.getTPShortName())
            )));
            return response;
        }

        response.setData(SupplierDocumentMapper.INSTANCE.toSupplierWebworksDto(doc));
        response.setErrors(Collections.emptyList());
        return response;
    }


//    private SupplierWebworksDto toDto(SupplierDocument doc) {
//        return SupplierWebworksDto.builder()
//                .fullCompanyNameLocal(doc.getInvNameLocal())
//                .fullCompanyNameEN(doc.getInvNameInter())
//                .companyNameLocal(doc.getCompanyNameLocal())
//                .companyNameEN(doc.getCompanyNameInter())
//                .branchNumber(doc.getBranch())
//                .TPShortName(doc.getOrgId())
//                .taxId(doc.getTaxId())
//                .build();
//    }

    private SupplierWebWorkResponse toResponse(SupplierDocument doc) {
        SupplierWebWorkResponse response = new SupplierWebWorkResponse();
        response.setData(SupplierDocumentMapper.INSTANCE.toSupplierWebworksDto(doc));
        return response;
    }
}
