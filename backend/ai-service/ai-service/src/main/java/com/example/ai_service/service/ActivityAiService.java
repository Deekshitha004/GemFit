package com.example.ai_service.service;

import com.example.ai_service.model.Recommendation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.ai_service.model.Activity;
@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiWebService geminiWebService;

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;


    public Recommendation generateRecommendation(Activity activity){
        String prompt=createPromptForActivity(activity);
        String aiResponse=geminiWebService.getAnswer(prompt);
        //log.info("Response from Ai:{}",aiResponse);
        return processAiResponse(activity,aiResponse);


    }
    // to convert into proper text in json
    private Recommendation processAiResponse(Activity activity, String aiResponse){
       try {
           ObjectMapper objectMapper=new ObjectMapper();
           JsonNode rootNode=objectMapper.readTree(aiResponse);
           JsonNode textNode=rootNode.path("candidates")
                   .get(0)
                   .path("content")
                   .path("parts")
                   .get(0)
                   .path("text");
           String jsonContent=textNode.asText()
                   .replaceAll("```json\\n","")
                   .replaceAll("\\n```","")
                   .trim();

           // to extract analysis from generated ai-ans to store in db
           JsonNode analysisJson =objectMapper.readTree(jsonContent);
           JsonNode analysisNode=analysisJson.path("analysis");
           StringBuilder fullAnalysis=new StringBuilder();
           addAnalysis(fullAnalysis,analysisNode,"overall","overall:");
           addAnalysis(fullAnalysis,analysisNode,"pace","Pace:");
           addAnalysis(fullAnalysis,analysisNode,"heartRate","Heart Rate:");
           addAnalysis(fullAnalysis,analysisNode,"calories","Calories:");

           List<String>  improvements=extractImprovements(analysisJson.path("improvements"));
           List<String> suggestions=extractSuggestions(analysisJson.path("suggestions"));
           List<String> safety=extractSafety(analysisJson.path("safety"));
           log.info("Parsed Response from Ai:{}",jsonContent);

           return Recommendation.builder()
                           .activityId(activity.getId())
                                   .userId(activity.getUserId())
                                           .activityType(String.valueOf(activity.getType()))
                                                   .recommendation(fullAnalysis.toString().trim())
                                                           .improvements(improvements)
                                                                   .suggestions(suggestions)
                                                                           .safety(safety)
                                                                                   .createdAt(LocalDateTime.now())
                                                                                           .build();


       }
       catch (Exception e){
           e.getStackTrace();
           return  createDefaultRecommendation(activity);
       }

    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        String genericAnalysis = """
            You completed a %s session for %d minutes and burned %d calories.
            Keep a steady pace, stay hydrated, and stretch before/after activity.
            """.formatted(activity.getType(), activity.getDuration(), activity.getCaloriesBurned());

        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType().toString())
                .recommendation(genericAnalysis.trim())
                .improvements(List.of(
                        "Maintain consistent pace throughout the workout",
                        "Schedule one rest day per week to aid recovery"
                ))
                .suggestions(List.of(
                        "Incorporate light strength training twice a week",
                        "Add a 5-minute cool-down walk after intense sessions"
                ))
                .safety(List.of(
                        "Hydrate before, during, and after exercise",
                        "Stop immediately if you feel dizzy or light-headed"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }


    private List<String> extractSafety(JsonNode safetyNode) {
        List<String> safety=new ArrayList<>();
        if(safetyNode.isArray()) {
            safetyNode.forEach(
                    item -> safety.add(item.asText()));

        }
        return safety.isEmpty() ?
                Collections.singletonList("No specific safety instructions Provided"):
                safety;
    }



    private List<String> extractSuggestions(JsonNode suggestionNode) {
        List<String> suggestions =new ArrayList<>();
        if(suggestionNode.isArray()) {
            suggestionNode.forEach(
                    suggestion -> {
                        String workout = suggestion.path("workout").asText();
                        String description = suggestion.path("description").asText();
                        suggestions.add(String.format("%s: %s", workout, description));
                    }
            );
        }
        return suggestions.isEmpty() ?
                Collections.singletonList("No specific suggestions Provided"):
                suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements=new ArrayList<>();
        if(improvementsNode.isArray()) {
            improvementsNode.forEach(
                    improvement -> {
                        String area = improvement.path("area").asText();
                        String detail = improvement.path("recommendation").asText();
                        improvements.add(String.format("%s: %s", area, detail));
                    }
            );
        }
        return improvements.isEmpty() ?
                    Collections.singletonList("No specific Improvements Provided"):
                improvements;

    }

    private void addAnalysis(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");

        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }

}
