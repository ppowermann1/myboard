package com.board.service;

import com.board.dto.ScheduleRequest;
import com.board.dto.ScheduleResponse;
import com.board.entity.Schedule;
import com.board.entity.User;
import com.board.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request, User user) {
        Schedule schedule = Schedule.builder()
                .user(user)
                .scheduleDate(request.getScheduleDate())
                .filmingStartTime(request.getFilmingStartTime())
                .filmingEndTime(request.getFilmingEndTime())
                .productionName(request.getProductionName())
                .pdName(request.getPdName())
                .pdContact(request.getPdContact())
                .memo(request.getMemo())
                .build();

        Schedule saved = scheduleRepository.save(schedule);
        return ScheduleResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getMySchedules(User user) {
        return scheduleRepository.findByUserOrderByScheduleDateDesc(user)
                .stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByMonth(User user, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return scheduleRepository.findByUserAndScheduleDateBetween(user, start, end)
                .stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByDate(User user, LocalDate date) {
        return scheduleRepository.findByUserAndScheduleDate(user, date)
                .stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long id, ScheduleRequest request, User user) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다"));

        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        schedule.setScheduleDate(request.getScheduleDate());
        schedule.setFilmingStartTime(request.getFilmingStartTime());
        schedule.setFilmingEndTime(request.getFilmingEndTime());
        schedule.setProductionName(request.getProductionName());
        schedule.setPdName(request.getPdName());
        schedule.setPdContact(request.getPdContact());
        schedule.setMemo(request.getMemo());

        return ScheduleResponse.from(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id, User user) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다"));

        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다");
        }

        scheduleRepository.delete(schedule);
    }
}
