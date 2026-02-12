package com.databass.coreservice.dto;

import lombok.Data;

@Data
public class GenerateRequest {

    private String prompt;

    private int duration;

    private boolean useAdvancedParams;

    private Double temperature;

    private Integer topK;

    private Double guidanceScale;
}