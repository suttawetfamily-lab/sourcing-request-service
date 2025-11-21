package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierElasticsearchRepository extends ElasticsearchRepository<SupplierDocument, String> {

    SupplierDocument findByOrgId(String orgId);

    SupplierDocument findByOrgIdAndInvitationCode(String orgId, String invitationCode);
}
