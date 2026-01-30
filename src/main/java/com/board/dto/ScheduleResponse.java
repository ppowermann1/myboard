package com.board.dto;

import com.board.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponse {
    private Long id;
    private LocalDate scheduleDate;
    private LocalTime filmingStartTime;
    private LocalTime filmingEndTime;
    private String productionName;
    private String pdName;
    private String pdContact;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .scheduleDate(schedule.getScheduleDate())
                .filmingStartTime(schedule.getFilmingStartTime())
                .filmingEndTime(schedule.getFilmingEndTime())
                .productionName(schedule.getProductionName())
                .pdName(schedule.getPdName())
                .pdContact(schedule.getPdContact())
                .memo(schedule.getMemo())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}
