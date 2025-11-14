package com.handonbizmsg.config;

import com.handonbizmsg.batch.OrderCheckJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    // 1. JobDetail 설정
    @Bean
    public JobDetail orderCheckJobDetail() {
        return JobBuilder.newJob(OrderCheckJob.class) // 구현한 Job 클래스 지정
                .withIdentity("orderCheckJob", "groupOrder") // Job의 이름과 그룹 지정
                .storeDurably() // Scheduler가 실행 중이 아니어도 Job을 유지하도록 설정
                .build();
    }

    // 2. Trigger 설정 (CronTrigger 예시 - 매 10초마다 실행)
    @Bean
    public Trigger orderCheckJobTrigger(JobDetail myJobDetail) {

        // Cron 스케줄 설정
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0/10 * * * * ?"); // 매 10초마다

        return TriggerBuilder.newTrigger()
                .forJob(myJobDetail) // 연결할 JobDetail 지정
                .withIdentity("orderCheckTrigger", "groupOrder") // Trigger의 이름과 그룹 지정
                .withSchedule(scheduleBuilder)
                .build();
    }
}
