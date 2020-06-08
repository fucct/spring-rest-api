package me.dd.restapi.events;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource(value = {"0,0,true", "100,0,false", "0,100,false"}, delimiter = ',')
    void testFree(int basePrice, int maxPrice, boolean free) {
        // Given
        Event event = Event.builder()
            .basePrice(basePrice)
            .maxPrice(maxPrice)
            .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(free);
    }

    @ParameterizedTest
    @CsvSource(value={"강남역 네이버 D2 스타트업 팩토리,true", ",false"}, delimiter = ',')
    void testOffline(String location, boolean offline) {
        // Given
        Event event = Event.builder()
            .location(location)
            .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isEqualTo(offline);
    }
}