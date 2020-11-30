package com.txw.order.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class SentinelController {

    //限流规则名称，与作用的接口名称一致
    private static final String LIMIT_KEY_1 = "testQPS1";

    private static final String LIMIT_KEY_2 = "testQPS2";

    //@PostConstruct该注解被用来修饰一个非静态的void（）方法。被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器执行一次。PostConstruct在构造函数之后执行，init（）方法之前执行。
    @PostConstruct
    public void initFlowQpsRule() {
        //设置testQPS1接口限流规则
        List<FlowRule> flowRules = new ArrayList<FlowRule>();
        FlowRule rule1 = new FlowRule();
        //设置限流规则名称
        rule1.setResource(LIMIT_KEY_1);
        //设置QPS限流
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //设置QPS为1
        rule1.setCount(1);
        rule1.setLimitApp("default");
        flowRules.add(rule1);

        //设置testQPS2限流规则
        FlowRule rule2 = new FlowRule();
        //设置限流规则名称
        rule2.setResource(LIMIT_KEY_2);
        //设置QPS限流
        rule2.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //设置QPS为2
        rule2.setCount(2);
        rule2.setLimitApp("default");
        flowRules.add(rule2);

        FlowRuleManager.loadRules(flowRules);
        System.out.println("-------------------配置限流规则成功-----------------------");
    }

    @GetMapping("/testQPS1")
    public String testQPS1() {
        Entry entry = null;
        try {
            entry = SphU.entry(LIMIT_KEY_1);
        } catch (BlockException e) { // 限流后进入此异常
            //e.printStackTrace();
            System.out.println("当前访问人数过多，请稍后再试！");
            return "当前访问人数过多，请稍后再试！";
        } finally {
            // SphU.entry与entry.exit()成对出现，否则会导致调用链记录异常
            if (entry != null) {
                entry.exit();
            }
        }
        System.out.println("testQPS1 success");
        return "testQPS1 success";
    }

    // value的值即为限流配置的key
    @SentinelResource(value = LIMIT_KEY_2, blockHandler = "testBlockHandler")
    @GetMapping("/testQPS2")
    public String testQPS2() {
        System.out.println("testQPS2 success");
        return "testQPS2 success";
    }

    public String testBlockHandler(BlockException e) {
        System.out.println("当前访问人数过多，请稍后再试！");
        return "当前访问人数过多，请稍后再试！";
    }
}
