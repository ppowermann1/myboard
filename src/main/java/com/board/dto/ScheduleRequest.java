package com.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequest {
    private LocalDate scheduleDate;
    private LocalTime filmingStartTime;
    private LocalTime filmingEndTime;
    private String productionName;
    private String pdName;
    private String pdContact;
    private String memo;
}
