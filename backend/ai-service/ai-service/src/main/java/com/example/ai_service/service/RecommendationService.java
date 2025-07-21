package com.example.ai_service.service;

import com.example.ai_service.model.Recommendation;
import com.example.ai_service.repo.RecommendationRepo;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    @Autowired
    private RecommendationRepo repo;

    public List<Recommendation> getUserRecommendation(String userId) {
        return repo.findByUserId(userId);

    }

    public Recommendation getActivityRecommendation(String activityId) {
        return repo.findByActivityId(activityId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No recommendation yet for activity " + activityId));
    }

}
