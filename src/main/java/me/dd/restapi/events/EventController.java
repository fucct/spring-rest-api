package me.dd.restapi.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Optional;

import javax.validation.Valid;

import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.dd.restapi.commons.ErrorsResource;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper,
        EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return createBadRequest(bindingResult);
        }
        eventValidator.validate(eventDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return createBadRequest(bindingResult);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = eventRepository.save(event);
        WebMvcLinkBuilder linkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        newEvent.add(linkTo(EventController.class).withRel("query-events"));
        newEvent.add(linkBuilder.withSelfRel());
        newEvent.add(linkBuilder.withRel("update-event"));
        return ResponseEntity.created(linkBuilder.toUri()).body(newEvent);
    }

    private ResponseEntity<ErrorsResource> createBadRequest(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(new ErrorsResource(bindingResult));
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> events = this.eventRepository.findAll(pageable);
        var pagedResources = assembler.toModel(events, e-> new EventResource(e));
        pagedResources.add(new Link("/docs/index.html#resources-query-list").withRel("profile"));
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        EventResource eventResource = new EventResource(event.get());
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }


}
