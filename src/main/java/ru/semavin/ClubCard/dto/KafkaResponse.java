package ru.semavin.ClubCard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaResponse {
    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("text")
    private String text;

    @JsonProperty("timestamp")
    private String timestamp;
}
