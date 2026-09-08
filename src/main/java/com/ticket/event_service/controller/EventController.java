package com.ticket.event_service.controller;

import com.ticket.event_service.entity.Event;
import com.ticket.event_service.repository.EventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public List<Event> getAvailableEvents() {
        return eventRepository.findByAvailableSeatsGreaterThan(0);
    }

    @GetMapping("/search")
    public List<Event> searchEvents(@RequestParam String name) {
        return eventRepository.findByNameContainingIgnoreCase(name);
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event updatedEvent) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setName(updatedEvent.getName());
                    event.setDate(updatedEvent.getDate());
                    event.setVenue(updatedEvent.getVenue());
                    event.setTotalSeats(updatedEvent.getTotalSeats());
                    event.setAvailableSeats(updatedEvent.getAvailableSeats());
                    return ResponseEntity.ok(eventRepository.save(event));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/seats")
    public ResponseEntity<Event> decrementSeat(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(event -> {
                    if (event.getAvailableSeats() > 0) {
                        event.setAvailableSeats(event.getAvailableSeats() - 1);
                        eventRepository.save(event);
                        return ResponseEntity.ok(event);
                    }
                    return ResponseEntity.status(409).<Event>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/increment-seat")
    public ResponseEntity<Event> incrementSeat(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(event -> {
                    if (event.getAvailableSeats() < event.getTotalSeats()) {
                        event.setAvailableSeats(event.getAvailableSeats() + 1);
                        eventRepository.save(event);
                    }
                    return ResponseEntity.ok(event);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}