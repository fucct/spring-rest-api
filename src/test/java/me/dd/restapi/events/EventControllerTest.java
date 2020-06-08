package me.dd.restapi.events;

import static org.springframework.hateoas.MediaTypes.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import me.dd.restapi.BaseControllerTest;

public class EventControllerTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

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
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string("Content-Type", HAL_JSON_VALUE))
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("free").value(false))
            .andExpect(jsonPath("offline").value(true))
            .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.query-events").exists())
            .andExpect(jsonPath("_links.update-event").exists())
            .andDo(document("create-event",
                links(
                    linkWithRel("self").description("link to self"),
                    linkWithRel("query-events").description("link to self"),
                    linkWithRel("update-event").description("link to update an event")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("name").description("Name of new event"),
                    fieldWithPath("description").description("description of new event"),
                    fieldWithPath("beginEnrollmentDateTime").description(
                        "date time of begin enrollment"),
                    fieldWithPath("closeEnrollmentDateTime").description(
                        "date time of end enrollment"),
                    fieldWithPath("beginEventDateTime").description("date time of begin event"),
                    fieldWithPath("endEventDateTime").description("date time of end event"),
                    fieldWithPath("location").description("location of new event"),
                    fieldWithPath("basePrice").description("base price of new event"),
                    fieldWithPath("maxPrice").description("max price of new event"),
                    fieldWithPath("limitOfEnrollment").description("limit of enrollment number")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("Location header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type header")
                ),
                relaxedResponseFields(
                    fieldWithPath("id").description("Id of new event"),
                    fieldWithPath("name").description("Name of new event"),
                    fieldWithPath("description").description("description of new event"),
                    fieldWithPath("beginEnrollmentDateTime").description(
                        "date time of begin enrollment"),
                    fieldWithPath("closeEnrollmentDateTime").description(
                        "date time of end enrollment"),
                    fieldWithPath("beginEventDateTime").description("date time of begin event"),
                    fieldWithPath("endEventDateTime").description("date time of end event"),
                    fieldWithPath("location").description("location of new event"),
                    fieldWithPath("basePrice").description("base price of new event"),
                    fieldWithPath("maxPrice").description("max price of new event"),
                    fieldWithPath("limitOfEnrollment").description("limit of enrollment number"),
                    fieldWithPath("free").description("is Free event"),
                    fieldWithPath("offline").description("is Offline evnet"),
                    fieldWithPath("eventStatus").description("event status")
                )
            ));
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
        // .andExpect(jsonPath("$[0].objectName").exists())
        // .andExpect(jsonPath("$[0].defaultMessage").exists())
        // .andExpect(jsonPath("$[0].code").exists());
        // .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    void queryEvents() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });

        //when
        this.mockMvc.perform(get("/api/events")
            .param("page", "1")
            .param("size", "10")
            .param("sort", "name,DESC"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("page").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("query-events"));
    }

    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    void getEvent() throws Exception {
        //Given
        var event = this.generateEvent(100);

        //When&Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("name").exists())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("get-an-event"));
    }

    @Test
    @DisplayName("없는 이벤트를 조회하는 경우 NotFound")
    void getNotExistEvent() throws Exception {
        //Given
        var event = this.generateEvent(100);

        //When&Then
        this.mockMvc.perform(get("/api/events/{id}", 214908))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이벤트 수정")
    public void updateEvent() throws Exception {
        // Given
        Event event = this.generateEvent(100);
        EventDto eventDto = EventDto.builder()
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

        // When && Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
            .contentType(APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(eventDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("name").value("Spring"))
            .andExpect(jsonPath("description").value("REST API Development with Spring"))
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("event-update"));
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 수정")
    public void updateNotExistEvent() throws Exception {
        // Given
        Event event = this.generateEvent(100);
        EventDto eventDto = EventDto.builder()
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

        // When && Then
        this.mockMvc.perform(put("/api/events/{id}", 53982750)
            .contentType(APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(eventDto)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적이지 않은 값으로 이벤트 수정")
    public void updateInvalidValueEvent() throws Exception {
        // Given
        Event event = this.generateEvent(100);
        EventDto updateEvent = EventDto.builder()
            .name("")
            .description(null)
            .build();

        // When && Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
            .contentType(APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updateEvent)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("불가능한 값으로 이벤트 수정")
    public void updateNotPossibleValueEvent() throws Exception {
        // Given
        Event event = this.generateEvent(100);
        EventDto updateEvent = EventDto.builder()
            .name("Spring")
            .description("REST API Development with Spring")
            .basePrice(50000)
            .maxPrice(1000)
            .build();

        // When && Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
            .contentType(APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updateEvent)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
            .name("event " + index)
            .description("test Event")
            .build();

        return this.eventRepository.save(event);
    }
}
