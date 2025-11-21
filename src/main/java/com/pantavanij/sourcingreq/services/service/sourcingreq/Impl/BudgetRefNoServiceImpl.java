package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.BudgetRefNo;
import com.pantavanij.sourcingreq.services.domain.mapper.BudgetRefNoMapper;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.BudgetRefNoRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.BudgetRefNoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BudgetRefNoServiceImpl implements BudgetRefNoService {

    private final BudgetRefNoRepository budgetRefNoRepository;

    @Override
    public List<OptionDto> getBudgetRefNoByTenantIdAndSearchTerm(Integer tenantId, String searchTerm) {
        List<BudgetRefNo> budgetRefNos = budgetRefNoRepository.findByTenantIdAndCodeOrName(tenantId, searchTerm.trim());
        return BudgetRefNoMapper.INSTANCE.toBudgetRefNoOptionDto(budgetRefNos);
    }
}
