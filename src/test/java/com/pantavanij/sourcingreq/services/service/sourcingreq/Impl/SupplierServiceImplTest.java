package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierWebworksDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Supplier;
import com.pantavanij.sourcingreq.services.domain.request.SupplierWebWorkSearchByTPShortNameRequest;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SupplierRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SupplierServiceImplTest {
    @Mock
    private SupplierServiceImpl supplierService;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private TenantRepository tenantRepository;

    @Before
    public void setup() {

    }

//    @Test
//    public void getSupplierBySearchTerm_success() {
//        Optional<Supplier> supplierOptional = Optional.empty();
//        List<SupplierDto> supplierDtos = new ArrayList<>();
//        String searchTerm = "";
//        Tenant tenant = tenantRepository.findTenantByCode(Mockito.anyString());
//        if (supplierOptional.isPresent()) {
//            SupplierDto supplierDto = new SupplierDto();
//            supplierDtos.add(supplierDto);
//        }
//        Assert.assertEquals(supplierDtos, supplierService.getSupplierBySearchTerm(Mockito.anyString()));
//    }
    @Test
    public void getSupplierByShortName_success() {
        Supplier supplier = null;
        Assert.assertEquals(supplier, supplierService.getSupplierByShortName(Mockito.anyString(), Mockito.anyInt()));
    }

    @Test
    public  void  createSupplier_success(){
        Supplier supplier = new Supplier();
        supplier.setFullCompanyNameLocal("บริษัท เทส e จำกัด");
        supplierRepository.save(supplier);
    }
    @Test
    public void getSupplierWebWorkByTPShortName_success() {
        SupplierWebworksDto webworksDtos = null;
        SupplierWebWorkSearchByTPShortNameRequest searchRequest = new SupplierWebWorkSearchByTPShortNameRequest();
        Assert.assertEquals(webworksDtos, supplierService.getSupplierWebWorkByTPShortName(searchRequest));
    }

}

