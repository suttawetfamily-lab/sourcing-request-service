package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.stereotype.*;


public interface NotificationService {
    Integer getCountApproverTask();
    Integer getCountReviewerTask();
}
