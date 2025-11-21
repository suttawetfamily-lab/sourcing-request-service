package com.pantavanij.sourcingreq.services.repository.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CategoryPurchaserRepository extends JpaRepository<CategoryPurchaser, Integer> {

    List<CategoryPurchaser> findByCategory_RecIdIn(List<Integer> categoryIds);

    void deleteByCategory_RecIdIn(List<Integer> categoriesId);

    void deleteAllByCategoryRecId(Integer categoryId);
}
