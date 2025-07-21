package com.example.ai_service.controller;

import com.example.ai_service.model.Recommendation;
import com.example.ai_service.service.RecommendationService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/user/{userId}")
    public List<Recommendation> getUserRecommendation(@PathVariable String userId) {
        return recommendationService.getUserRecommendation(userId);
    }

    @GetMapping("/activity/{activityId}")
    public Recommendation getActivityRecommendation(@PathVariable String activityId) {
        return recommendationService.getActivityRecommendation(activityId);
    }
}