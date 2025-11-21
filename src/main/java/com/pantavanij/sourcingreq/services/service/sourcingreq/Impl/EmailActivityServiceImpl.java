package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.exception.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.stereotype.*;

import javax.persistence.criteria.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.SearchReportLine.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailActivityServiceImpl implements EmailActivityService {

    private final EmailActivityRepository emailActivityRepository;
    private final UaaService uaaService;

    @Override
    public List<EmailActivityDto> getAllEmailActivity() {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            List<EmailActivity> emailActivitiList = emailActivityRepository.findAll();
            if (!emailActivitiList.isEmpty()) {
                return EmailActivityMapper.INSTANCE.toEmailActivityDtoList(emailActivitiList, timeZone);
            }
            return null;
        } catch (Exception e) {
            log.error("Error while get all EmailActivity: {} ", e.getMessage());
            System.out.println(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public EmailActivityDto createEmailActivity(EmailActivityRequest request) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<EmailActivity> emailActivityOtp = emailActivityRepository.findLastRecord();
            if (emailActivityOtp.isPresent() && request.getSequence() == 0) {
                request.setSequence(emailActivityOtp.get().getSequence() + 1);
            } else if (emailActivityOtp.isEmpty() && request.getSequence() == 0) {
                request.setSequence(1);
            } else {
                emailActivityOtp.ifPresent(emailActivity -> emailActivityRepository.reOrderOtherEmailActivitySequence(emailActivity.getSequence() + 1, request.getSequence()));
            }

            EmailActivity emailActivity = EmailActivity.builder()
                    .activityName(request.getActivityName())
                    .sequence(request.getSequence())
                    .isDefault(request.isDefault())
                    .active(request.isActive())
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .updatedBy(AppUtil.getUserName())
                    .updatedDate(DateTimeUtil.getTimestampUTC())
                    .build();

            EmailActivity saved = emailActivityRepository.save(emailActivity);
            return EmailActivityMapper.INSTANCE.toEmailActivityDto(saved, timeZone);
        } catch (Exception e) {
            log.error("Error while create EmailActivity: {} ", e.getMessage());
            System.out.println(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public EmailActivityDto updateEmailActivity(EmailActivityRequest request) {
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        try {
            Optional<EmailActivity> emailActivityOpt = emailActivityRepository.findByRecId(request.getRecId());
            if (emailActivityOpt.isPresent()) {
                EmailActivity existedEmailActivity = emailActivityOpt.get();
                existedEmailActivity.setActivityName(request.getActivityName());
                existedEmailActivity.setSequence(request.getSequence());
                existedEmailActivity.setDefault(request.isDefault());
                existedEmailActivity.setActive(request.isActive());
                existedEmailActivity.setUpdatedBy(AppUtil.getUserName());
                existedEmailActivity.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                EmailActivity updated = emailActivityRepository.save(existedEmailActivity);
                return EmailActivityMapper.INSTANCE.toEmailActivityDto(updated, timeZone);
            }
            return null;
        } catch (Exception e) {
            log.error("Error while update EmailActivity: {} ", e.getMessage());
            System.out.println(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public EmailActivitySearchDto searchEmailActivityByConditions(EmailActivitySearchRequest request, Pageable pageable) {
        Page<EmailActivity> emailActivityPage = emailActivityRepository.findAll(Specification.where(searchEmailActivitySpecificationByConditions(request)), pageable);
        int totalPage = emailActivityPage.getTotalPages();
        long total = emailActivityPage.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<EmailActivityDto> emailActivityDtoList = EmailActivityMapper.INSTANCE.toEmailActivityDtoList(emailActivityPage.getContent(), timeZone);

        EmailActivitySearchDto emailActivitySearchDto = new EmailActivitySearchDto();
        emailActivitySearchDto.setEmailActivityDtoList(emailActivityDtoList);
        emailActivitySearchDto.setTotal(total);
        emailActivitySearchDto.setTotalPage(totalPage);
        emailActivitySearchDto.setPageSize(pageable.getPageSize());

        return emailActivitySearchDto;
    }

    @Override
    public EmailActivityDto deleteById(Long id) {
        try {
            Optional<EmailActivity> emailActivityOpt = emailActivityRepository.findById(id);
            if (emailActivityOpt.isPresent()) {
                EmailActivity emailActivity = emailActivityOpt.get();
                emailActivityRepository.delete(emailActivity);
                return EmailActivityMapper.INSTANCE.toEmailActivityDto(emailActivity);
            }
            return null;
        } catch (Exception e) {
            log.error("Error while delete EmailActivity: {} ", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    private Specification<EmailActivity> searchEmailActivitySpecificationByConditions(EmailActivitySearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            List<ConditionSearchRequest> conditionSearchRequestList = request.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();

                    Predicate predicate = null;
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (Constant.ACTIVITY_NAME.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (Constant.CREATED_BY.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else {
                            if (CommonUtils.isNumeric(searchValue)) {
                                predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                            } else {
                                predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%");
                            }
                        }
                        if (predicate != null) {
                            orPredicates.add(predicate);
                        }
                    }
                }
                if (!orPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
