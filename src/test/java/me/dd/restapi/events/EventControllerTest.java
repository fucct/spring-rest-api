package me.dd.restapi.events;

import static org.mockito.Mockito.*;
import static org.springframework.hateoas.MediaTypes.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;

    @Test
    void createEvent() throws Exception {
        Event event = Event.builder()
            .name("Spring")
            .description("REST API Development with Spring")
            .beginEnrollmentDateTime(LocalDateTime.of(2020, 6, 5, 12, 0))
            .closeEnrollmentDateTime(LocalDateTime.of(2020, 6, 6, 12, 0))
            .beginEventDateTime(LocalDateTime.of(2020, 6, 7, 12, 0))
            .endEventDateTime(LocalDateTime.of(2020, 6, 8, 12, 0))
            .basePrice(100)
            .maxPrice(100)
            .limitOfEnrollment(100)
            .location("강남역 D2 스타트업 팩토리")
            .build();
        event.setId(10);

        when(eventRepository.save(any())).thenReturn(event);

        mockMvc.perform(post("/api/events")
            .contentType(APPLICATION_JSON_VALUE)
            .accept(HAL_JSON)
            .content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists());
    }
}
