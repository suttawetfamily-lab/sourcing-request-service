package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.ContactDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactElasticsearchRepository extends ElasticsearchRepository<ContactDocument, String> {

    List<ContactDocument> findByOrgId(String orgId);
}
