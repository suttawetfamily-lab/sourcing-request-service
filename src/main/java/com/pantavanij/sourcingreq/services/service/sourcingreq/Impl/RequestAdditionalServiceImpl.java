package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestAdditional;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.RequestRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestAdditionalRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestAdditionalService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestAdditionalServiceImpl implements RequestAdditionalService {

    private final RequestAdditionalRepository requestAdditionalRepository;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Request request, Tenant tenant, RequestRequest reqRequest) {
        if(reqRequest == null ) return;
        Optional<RequestAdditional> requestAdditionalOptional = requestAdditionalRepository.findTop1ByRequestId(request.getRecId());
        RequestAdditional requestAdditional = requestAdditionalOptional.orElseGet(RequestAdditional::new);
        requestAdditional.setRequest(request);
        requestAdditional.setTenant(tenant);
        requestAdditional.setWhtAbsorbedBy(null != reqRequest.getWhtAbsorbedBy() ? reqRequest.getWhtAbsorbedBy().getValue() : null);
        requestAdditional.setEvaluationDate(reqRequest.getEvaluationDate());
        requestAdditional.setPerformanceEvaluation(null != reqRequest.getPerformanceEvaluation() ? reqRequest.getPerformanceEvaluation().getValue() : null);
        requestAdditional.setPdpaQ01(null != reqRequest.getPdpaQ01() ? reqRequest.getPdpaQ01().getValue() : null);
        requestAdditional.setPdpaQ02(null != reqRequest.getPdpaQ02() ? reqRequest.getPdpaQ02().getValue() : null);
        requestAdditional.setPdpaQ03(null != reqRequest.getPdpaQ03() ? reqRequest.getPdpaQ03().getValue() : null);
        requestAdditional.setPdpaQ04(null != reqRequest.getPdpaQ04() ? reqRequest.getPdpaQ04().getValue() : null);
        requestAdditional.setPdpaQ05(null != reqRequest.getPdpaQ05() ? reqRequest.getPdpaQ05().getValue() : null);
        requestAdditional.setPdpaQ06(null != reqRequest.getPdpaQ06() ? reqRequest.getPdpaQ06().getValue() : null);
        requestAdditional.setRelatePdpa(reqRequest.getRelatePdpa());
        requestAdditional.setThirdPartyRole(reqRequest.getThirdPartyRole());
        requestAdditional.setDpaType(reqRequest.getDpaType());
        requestAdditional.setOutsourceService(null != reqRequest.getOutsourceService() ? reqRequest.getOutsourceService().getValue() : null);
        requestAdditional.setBackground(reqRequest.getBackground());
        requestAdditional.setRequestStatus(reqRequest.getRequestStatus());
        requestAdditional.setDepartment(reqRequest.getDepartment());
        requestAdditional.setCostcenter(reqRequest.getCostcenter());
        requestAdditional.setApproveNo(reqRequest.getApproveNo());
        requestAdditional.setMakingContract(null != reqRequest.getMakingContract() ? reqRequest.getMakingContract().getValue() : null);
        requestAdditional.setMakingRptContract(null != reqRequest.getMakingRptContract() ? reqRequest.getMakingRptContract().getValue() : null);
        requestAdditional.setNeedWhtCert(null != reqRequest.getNeedWhtCert() ? reqRequest.getNeedWhtCert().getValue() : null);
        requestAdditional.setVatAbsorbedBy(null != reqRequest.getVatAbsorbedBy() ? reqRequest.getVatAbsorbedBy().getValue() : null);
        requestAdditional.setStampDuty(null != reqRequest.getStampDuty() ? reqRequest.getStampDuty().getValue() : null);
        requestAdditional.setMakingContractReason(reqRequest.getMakingContractReason());
        requestAdditional.setMakingRptContractReason(reqRequest.getMakingRptContractReason());

        if (requestAdditional.getRecId() == null) {
            requestAdditional.setCreatedBy(AppUtil.getUserName());
            requestAdditional.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }
        requestAdditional.setUpdatedBy(AppUtil.getUserName());
        requestAdditional.setUpdatedDate(DateTimeUtil.getTimestampUTC());

        requestAdditionalRepository.save(requestAdditional);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Request request) {
        requestAdditionalRepository.deleteByRequest(request);
    }


}
