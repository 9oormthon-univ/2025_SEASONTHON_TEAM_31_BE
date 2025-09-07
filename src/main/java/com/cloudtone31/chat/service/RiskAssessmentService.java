package com.cloudtone31.chat.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RiskAssessmentService {

    private static final List<String> DANGER_KEYWORDS = Arrays.asList(
            "죽고싶다", "자살", "자해", "목숨을 끊고싶다",
            "사라지고싶다", "끝내고싶다", "의미없다"
    );

    private static final List<String> WARNING_KEYWORDS = Arrays.asList(
            "우울하다", "힘들다", "포기하고싶다",
            "절망적이다", "외롭다", "버텨낼 수 없다"
    );

    public enum RiskLevel {
        DANGER, WARNING, NORMAL
    }

    public RiskLevel assessRisk(String message){
        for(String keyword : DANGER_KEYWORDS){
            if(message.contains(keyword)){
                return RiskLevel.DANGER;
            }
        }

        for(String keyword : WARNING_KEYWORDS){
            if(message.contains(keyword)){
                return RiskLevel.WARNING;
            }
        }

        return RiskLevel.NORMAL;
    }




}
