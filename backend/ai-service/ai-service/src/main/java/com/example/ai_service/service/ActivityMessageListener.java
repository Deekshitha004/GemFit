package com.example.ai_service.service;

import com.example.ai_service.model.Activity;
import com.example.ai_service.model.Recommendation;
import com.example.ai_service.repo.RecommendationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityMessageListener {
    @Autowired
    private final RecommendationRepo repo;

    private final ActivityAiService aiService;

    @RabbitListener(queues="activity.queue")
    public void processActivity(Activity activity){
        log.info("Recieved Activity for processing:{}",activity.getId());
        //log.info("Generated Recommendation:{}",aiService.generateRecommendation(activity));
        Recommendation recommendation=aiService.generateRecommendation(activity);
        repo.save(recommendation);
    }
}
