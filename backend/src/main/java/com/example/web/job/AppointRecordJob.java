package com.example.web.job;

import com.example.web.service.AppointRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AppointRecordJob {

    @Autowired
    private AppointRecordService _AppointRecordService;

    /**
     * 每隔10s检测座位预约是否结束
     */
    @Scheduled(cron = "*/10 * * * * ?")
    private void AutoCompletedAppoint() {
        long nowDateTime = System.currentTimeMillis();
        _AppointRecordService.AutoCompletedAppoint();
    }
    /**
     * 每隔10s检测自动逾期
     */
    @Scheduled(cron = "*/10 * * * * ?")
    private void AutoOverdueTimes() {
        long nowDateTime = System.currentTimeMillis();
        _AppointRecordService.AutoOverdueTimes();
    }

}
