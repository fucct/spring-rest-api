package me.dd.restapi.events;

import static org.springframework.hateoas.MediaTypes.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("정상적인 입력을 받는 경우")
    void createEvent() throws Exception {
        /*
          만약 Event에 계산되어야 하는 값이 임의로 들어오는 경우를 막으려면 어떻게 해야하는가?
          Ans : Dto를 활용한다.
          다른 프로퍼티에 대해 무시하지 않고 에러를 리턴하려면..?
         */
        EventDto event = EventDto.builder()
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

        mockMvc.perform(post("/api/events")
            .contentType(APPLICATION_JSON_VALUE)
            .accept(HAL_JSON)
            .content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string("Content-Type", "application/hal+json"))
            .andExpect(jsonPath("id").value(Matchers.not(100)))
            .andExpect(jsonPath("free").value(Matchers.not(true)))
            .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }

    @Test
    @DisplayName("입력받을 수 없는 값을 입력받는 경우")
    void createEvent_Bad_Request() throws Exception {
        /*
          만약 Event에 계산되어야 하는 값이 임의로 들어오는 경우를 막으려면 어떻게 해야하는가?
          Ans : Dto를 활용한다.
          다른 프로퍼티에 대해 무시하지 않고 에러를 리턴하려면..?
          Ans : jackson.deserialization -> fail on 설정
         */
        Event event = Event.builder()
            .id(100)
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
            .free(true)
            .offline(true)
            .eventStatus(EventStatus.PUBLISHED)
            .build();

        mockMvc.perform(post("/api/events")
            .contentType(APPLICATION_JSON_VALUE)
            .accept(HAL_JSON)
            .content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("빈 입력이 들어오는 경우")
    void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
            .contentType(APPLICATION_JSON_VALUE)
            .accept(HAL_JSON_VALUE)
            .content(objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적이지 않은 값이 입력되는 경우")
    void createEvent_Bad_Request_Wrong_Input() throws Exception {
        /*
          만약 Event에 계산되어야 하는 값이 임의로 들어오는 경우를 막으려면 어떻게 해야하는가?
          Ans : Dto를 활용한다.
          다른 프로퍼티에 대해 무시하지 않고 에러를 리턴하려면..?
          Ans : jackson.deserialization -> fail on 설정
         */
        EventDto event = EventDto.builder()
            .name("Spring")
            .description("REST API Development with Spring")
            .beginEnrollmentDateTime(LocalDateTime.of(2020, 6, 5, 12, 0))
            .closeEnrollmentDateTime(LocalDateTime.of(2020, 6, 6, 12, 0))
            .beginEventDateTime(LocalDateTime.of(2020, 6, 9, 12, 0))
            .endEventDateTime(LocalDateTime.of(2020, 6, 8, 12, 0))
            .basePrice(10000)
            .maxPrice(100)
            .limitOfEnrollment(100)
            .location("강남역 D2 스타트업 팩토리")
            .build();

        mockMvc.perform(post("/api/events")
            .contentType(APPLICATION_JSON_VALUE)
            .accept(HAL_JSON)
            .content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }
}
