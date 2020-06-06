package me.dd.restapi.events;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void builder() {
        Event event = Event.builder()
            .name("Inflearn Spring REST API")
            .description("REST API development with Spring")
            .build();
        assertThat(event).isNotNull();
    }

    @Test
    void javaBean() {
        //given
        String name = "Event";
        String description = "Spring";

        //when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}