package com.board.controller;

import com.board.dto.ScheduleRequest;
import com.board.dto.ScheduleResponse;
import com.board.entity.User;
import com.board.repository.UserRepository;
import com.board.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserRepository userRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getMySchedules(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(scheduleService.getMySchedules(user));
    }

    @GetMapping("/month")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByMonth(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(scheduleService.getSchedulesByMonth(user, year, month));
    }

    @GetMapping("/date")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByDate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String date) {
        User user = getCurrentUser(userDetails);
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(scheduleService.getSchedulesByDate(user, localDate));
    }

    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ScheduleRequest request) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(scheduleService.createSchedule(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ScheduleRequest request) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(scheduleService.updateSchedule(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        scheduleService.deleteSchedule(id, user);
        return ResponseEntity.noContent().build();
    }
}
