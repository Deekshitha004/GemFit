package com.example.activity_service.controller;

import com.example.activity_service.dto.requests.ActivityRequest;
import com.example.activity_service.dto.response.ActivityResponse;
import com.example.activity_service.service.ActivityService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request, @RequestHeader("X-User-ID") String userId){
        if (userId != null) {
            request.setUserId(userId);
        }
        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivities(@RequestHeader("X-User-ID") String userId){
        return ResponseEntity.ok(activityService.getUserActivity(userId));
    }


    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable String activityId){
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }

    // ActivityController
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivity(@PathVariable String activityId,
                                               @RequestHeader("X-User-ID") String userId) {
        // (optional) ownership check
        activityService.deleteActivityById(activityId);
        return ResponseEntity.noContent().build();   // HTTP 204
    }

    @PutMapping("/{activityId}")
    public ResponseEntity<Void> updateActivity(@PathVariable String activityId,
                                               @RequestHeader("X-User-ID") String userId,
                                               @RequestBody ActivityRequest update) {

        activityService.updateActivity(activityId, userId, update);
        return ResponseEntity.noContent().build();   // 204
    }




}
