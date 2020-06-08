package me.dd.restapi.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class EventResource extends EntityModel<Event> {

    public EventResource(Event content, Link... links) {
        super(content, links);
        add(linkTo(EventController.class).withSelfRel());
    }
}
