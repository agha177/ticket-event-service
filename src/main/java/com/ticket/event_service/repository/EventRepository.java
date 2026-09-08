package com.ticket.event_service.repository;

import com.ticket.event_service.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByAvailableSeatsGreaterThan(Integer seats);

    List<Event> findByNameContainingIgnoreCase(String name);

}