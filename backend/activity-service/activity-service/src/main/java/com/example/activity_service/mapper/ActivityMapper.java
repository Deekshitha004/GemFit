package com.example.activity_service.mapper;

import com.example.activity_service.dto.requests.ActivityRequest;
import com.example.activity_service.dto.response.ActivityResponse;
import com.example.activity_service.model.Activity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
public class ActivityMapper {


        public Activity toEntity(ActivityRequest req) {
            Activity activity = new Activity();
            activity.setUserId(req.getUserId());
            activity.setType(req.getType());
            activity.setDuration(req.getDuration());
            activity.setCaloriesBurned(req.getCaloriesBurned());
            activity.setStartTime(req.getStartTime());
            activity.setAdditionalMetrics(req.getAdditionalMetrics());
            return activity;
        }

        public static ActivityResponse toDto(Activity activity) {
            ActivityResponse dto = new ActivityResponse();
            dto.setId(activity.getId());
            dto.setUserId(activity.getUserId());                // ✅ now copied
            dto.setType(activity.getType());
            dto.setDuration(activity.getDuration());
            dto.setCaloriesBurned(activity.getCaloriesBurned());
            dto.setStartTime(activity.getStartTime());          // ✅ fixed source
            dto.setAdditionalMetrics(activity.getAdditionalMetrics());
            dto.setCreatedAt(activity.getCreatedAt());
            dto.setUpdateAt(activity.getUpdateAt());
            return dto;
        }


}
