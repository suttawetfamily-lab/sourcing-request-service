package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.BudgetRefNo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BudgetRefNoMapper {
    BudgetRefNoMapper INSTANCE = Mappers.getMapper(BudgetRefNoMapper.class);

    @Mapping(target = "value", source = "budgetRefNo.recId")
    @Mapping(target = "name", source = "budgetRefNo.code")
    @Mapping(target = "label", source = "budgetRefNo.name")
    OptionDto toBudgetRefNoOptionDto(BudgetRefNo budgetRefNo);

    List<OptionDto> toBudgetRefNoOptionDto(List<BudgetRefNo> budgetRefNos);
}
