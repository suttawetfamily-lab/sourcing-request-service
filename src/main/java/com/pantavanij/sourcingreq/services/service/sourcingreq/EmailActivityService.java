package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.*;

import javax.validation.*;
import java.util.*;

public interface EmailActivityService {

    List<EmailActivityDto> getAllEmailActivity();

    EmailActivityDto createEmailActivity(EmailActivityRequest request);

    EmailActivityDto updateEmailActivity(EmailActivityRequest request);

    EmailActivitySearchDto searchEmailActivityByConditions(EmailActivitySearchRequest request, Pageable pageable);

    EmailActivityDto deleteById(Long id);
}
