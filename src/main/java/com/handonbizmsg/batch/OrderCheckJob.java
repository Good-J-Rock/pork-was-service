package com.handonbizmsg.batch;

import com.handonbizmsg.domain.Order;
import com.handonbizmsg.service.NaverApiService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
public class OrderCheckJob implements Job {

    public final NaverApiService naverApiService;

    public OrderCheckJob(NaverApiService naverApiService) {
        this.naverApiService = naverApiService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 여기에 실제 실행할 작업 로직을 작성합니다.
//        System.out.println(">>> Quartz Job이 실행되었습니다! 현재 시간: " + new java.util.Date());

        // JobDataMap에 담긴 데이터를 사용할 수도 있습니다.
//        String data = context.getJobDetail().getJobDataMap().getString("key");
//        log.info("data: {}", data);

        // naver commerce api token
//        Unirest.setTimeouts(0, 0);
//        HttpResponse<String> response = Unirest.post("https://api.commerce.naver.com/external/v1/oauth2/token")
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .header("Accept", "application/json")
//                .asString();

        // 주문 데이터 조회
//        List<Order> orderList = naverApiService.getNewOrders();
//        log.info("Order check job executed : " + orderList);
    }
}
