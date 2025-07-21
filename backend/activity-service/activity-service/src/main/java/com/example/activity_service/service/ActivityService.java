package com.example.activity_service.service;

import com.example.activity_service.dto.requests.ActivityRequest;
import com.example.activity_service.dto.response.ActivityResponse;
import com.example.activity_service.mapper.ActivityMapper;
import com.example.activity_service.model.Activity;
import com.example.activity_service.repository.ActivityRepo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    @Autowired
    private ActivityRepo activityRepo;
    @Autowired
    private ActivityMapper mapper;
    @Autowired
    private UserValidationService userValidationService;

    @Autowired
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;




    public ActivityResponse trackActivity(ActivityRequest request) {

        boolean isValidUser = userValidationService.validateUser(request.getUserId());
        if (!isValidUser) {
            throw new RuntimeException("Invalid User: " + request.getUserId());
        }

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepo.save(activity);

        // Publish to RabbitMQ for AI Processing
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        } catch(Exception e) {
            log.error("Failed to publish activity to RabbitMQ : ", e);
        }

        ActivityResponse activityRequest=ActivityMapper.toDto(savedActivity);
        return activityRequest;
    }

    public List<ActivityResponse> getUserActivity(String userId) {
        List<Activity> activities=activityRepo.findByUserId(userId);
        return activities.stream()
                .map(ActivityMapper::toDto)
                .collect(Collectors.toList());

    }


    public ActivityResponse getActivityById(String activityId) {
        return activityRepo.findById(activityId)
                .map(ActivityMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Activity not found with id:"+activityId));
    }

    // ActivityService
    public void deleteActivityById(String activityId) {
        activityRepo.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found: " + activityId));
        activityRepo.deleteById(activityId);
    }

    public void updateActivity(String activityId,
                               String userId,
                               ActivityRequest req) {

        Activity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found: " + activityId));

        // ensure the caller owns this activity
        if (!activity.getUserId().equals(userId)) {
            throw new RuntimeException("Forbidden: activity does not belong to user " + userId);
        }

        // ---- update mutable fields only ----
        if (req.getType() != null) activity.setType(req.getType());
        if (req.getDuration() != null) activity.setDuration(req.getDuration());
        if (req.getCaloriesBurned() != null) activity.setCaloriesBurned(req.getCaloriesBurned());
        if (req.getAdditionalMetrics() != null) activity.setAdditionalMetrics(req.getAdditionalMetrics());

        activityRepo.save(activity);
    }
}
