package com.txw.order.controller;

import com.txw.order.feign.FeignStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
public class OrderController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FeignStorageService feignStorageService;

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public String order() {
        String result = restTemplate.getForEntity("http://service-storage/storage", String.class).getBody();
        System.out.println("扣减库存结果：" + result);
        return "order success";
    }

    @RequestMapping(value = "/orderByFeign", method = RequestMethod.GET)
    public String orderByFeign(String params) {
        String result = feignStorageService.storage();
        return result;

    }

}
