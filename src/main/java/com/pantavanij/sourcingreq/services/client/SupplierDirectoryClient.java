package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.FeignClientConfig;
import com.pantavanij.sourcingreq.services.config.SupplierDirectoryConfig;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@FeignClient(value = "supplier-directory-client", url = "${base.url.api.supplier.directory}", configuration = SupplierDirectoryConfig.class)
public interface SupplierDirectoryClient {

    String AUTH_TOKEN = "Authorization";

    @MethodExecuteTime
    @RequestMapping(method = RequestMethod.GET, value = "/search/supplier/contacts", consumes = MediaType.APPLICATION_JSON_VALUE)
    SupplierContactResponse searchSupplierContact(@RequestParam("pageNo") Integer pageNo,
                                              @RequestParam("pageSize") Integer pageSize,
                                              @RequestParam("catLevel1Id") Integer catLevel1Id,
                                              @RequestParam("catLevel2Id") Integer catLevel2Id,
                                              @RequestParam("catLevel3Id") Integer catLevel3Id,
                                              @RequestParam("ruleName") String ruleName);


}
