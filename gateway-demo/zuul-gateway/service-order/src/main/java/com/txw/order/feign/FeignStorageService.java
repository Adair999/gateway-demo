package com.txw.order.feign;

import com.txw.order.feign.fallback.FeignStorageServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
@FeignClient(value = "service-storage", fallback = FeignStorageServiceFallback.class)
public interface FeignStorageService {
    @RequestMapping("/storage")
    String storage();
}