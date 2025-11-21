package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.ReviewerDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Reviewer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ReviewerMapper {
    ReviewerMapper INSTANCE = Mappers.getMapper(ReviewerMapper.class);

    ReviewerDto toRequestReviewerDto(Reviewer requestReviewer);

    List<ReviewerDto> toRequestReviewerDtoList(List<Reviewer> requestReviewers);
}
