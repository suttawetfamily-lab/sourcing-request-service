package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.RequesterDto;
import com.pantavanij.sourcingreq.services.domain.dto.RequesterSearchDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Requester;
import com.pantavanij.sourcingreq.services.domain.request.RequesterRequest;
import com.pantavanij.sourcingreq.services.domain.request.RequesterSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.SequenceRequest;
import com.pantavanij.sourcingreq.services.domain.response.EPAuthRequesterResponse;
import com.pantavanij.sourcingreq.services.domain.response.RequesterResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequesterService {
    RequesterResponse getRequestRequester(RequesterSearchRequest request, Pageable pageable);

    Integer saveRequester(RequesterDto requesterDto);

    EPAuthRequesterResponse getRequesterListByConditions(RequesterSearchRequest request);

    RequesterSearchDto searchRequesterListByConditions(RequesterSearchRequest request, Pageable pageable);

    RequesterDto findRequesterByRecId(Integer requesterId);

    List<Requester> findByRequesterName(String requesterName);

    RequesterDto createRequester(RequesterRequest requesterRequest);

    RequesterDto updateRequester(RequesterRequest requesterRequest);

    boolean deleteRequester(Integer requesterId);

    RequesterDto updateRequesterSequence(SequenceRequest request);
}
