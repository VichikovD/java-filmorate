package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Event {
    Integer eventId;

    @NotNull(message = "timestamp should not be null")
    Long timestamp;

    @NotNull(message = "user_id should not be null or empty")
    Integer userId;

    @NotNull(message = "event_type should be one of the following: LIKE, REVIEW, FRIEND")
    EventType eventType;

    @NotNull(message = "event_operation should be one of the following: REMOVE, ADD, UPDATE")
    EventOperation operation;

    @NotNull(message = "entity_id should not be null")
    Integer entityId;
}