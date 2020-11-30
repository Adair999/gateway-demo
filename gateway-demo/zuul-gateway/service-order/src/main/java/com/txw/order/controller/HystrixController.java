package com.txw.order.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping
public class HystrixController {
    /**
     * 使用线程池限流
     */
    @RequestMapping(value = "/testQPS1", method = RequestMethod.GET)
    @ResponseBody
    @HystrixCommand(fallbackMethod = "flowError",
            commandProperties = {
              // 线程池隔离
              @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "THREAD"),
              // 线程超时时间
              @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "1000"),
              // 设置超时的时候不中断线程,默认为true
              @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_INTERRUPT_ON_TIMEOUT, value = "true")
            })
    public String testQPS1() {
        try {
            System.out.println(Thread.currentThread().getName() + ":testQPS1 before sleep 5s....");
            Thread.sleep(1000 * 5);
            System.out.println(Thread.currentThread().getName() + ":testQPS1 after sleep 5s....");
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
        return "ok";
    }
    /**
     * 使用信号量限流
     */
    @RequestMapping(value = "/testQPS2", method = RequestMethod.GET)
    @ResponseBody
    @HystrixCommand(fallbackMethod = "flowError",
            commandProperties = {
                    // 信号量隔离
                    @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "SEMAPHORE"),
                    // 最大并发数
                    @HystrixProperty(name=HystrixPropertiesManager.EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS,value="3")
            })
    public String testQPS2() {
        try {
            System.out.println(Thread.currentThread().getName() + ":testQPS2 before sleep 5s....");
            Thread.sleep(1000 * 5);
            System.out.println(Thread.currentThread().getName() + ":testQPS2 after sleep 5s....");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }
//    /**
//     * 信号量测试
//     *
//     * @return
//     */
//    @RequestMapping(value = "/testQPS2", method = RequestMethod.GET)
//    @ResponseBody
//    @HystrixCommand(fallbackMethod = "flowError",
//            commandProperties = {
//                    @HystrixProperty(
//                            name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY,
//                            value = "SEMAPHORE"), // 信号量隔离
//                    @HystrixProperty(
//                            name = HystrixPropertiesManager.EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS,
//                            value = "1"), // 信*/
//                   /* @HystrixProperty(
//                            name = HystrixPropertiesManager.FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS,
//                            value = "1"), // 信号量最大并发数,*/
//                    @HystrixProperty(
//                            name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS,
//                            value = "1000")
//            })
//    public String testQPS2() {
//        System.out.println("testQPS2 success");
//        return "testQPS2 success";
//    }

    public String flowError() {
        System.out.println("当前访问人数过多，请稍后再试！");
        return "当前访问人数过多，请稍后再试！";
    }

}