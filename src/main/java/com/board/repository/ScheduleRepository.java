package com.board.repository;

import com.board.entity.Schedule;
import com.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByUserOrderByScheduleDateDesc(User user);

    List<Schedule> findByUserAndScheduleDateBetween(User user, LocalDate start, LocalDate end);

    List<Schedule> findByUserAndScheduleDate(User user, LocalDate date);
}
