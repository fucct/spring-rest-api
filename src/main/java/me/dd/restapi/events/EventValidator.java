package me.dd.restapi.events;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
            errors.rejectValue("basePrice", "wrongValue","basePrice is wrong");
            errors.rejectValue("maxPrice", "wrongValue","maxPrice is wrong");
        }
        LocalDateTime endEventTime = eventDto.getEndEventDateTime();
        if (endEventTime.isBefore(eventDto.getBeginEventDateTime()) ||
            endEventTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
                endEventTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
        }
    }
}
