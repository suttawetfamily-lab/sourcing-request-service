package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface SubCategoryPurchaserRepository extends JpaRepository<SubCategoryPurchaser, Integer> {
    void deleteAllBySubCategoryRecId(Integer subCategoryId);

    void deleteAllBySubCategoryRecIdIn(List<Integer> subCategoryDeleteList);

    List<SubCategoryPurchaser> findBySubCategory_RecIdIn(List<Integer> subCategoryIds);
}
