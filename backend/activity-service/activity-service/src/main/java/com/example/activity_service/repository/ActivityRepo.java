package com.example.activity_service.repository;

import com.example.activity_service.model.Activity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepo extends MongoRepository<Activity,String> {

    List<Activity> findByUserId(String userId);
}
