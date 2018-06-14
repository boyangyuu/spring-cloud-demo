package com.mobileenerlytics.controller;

import com.mobileenerlytics.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/{userId}/bookmarks")
public class ProjectController {

    private final ProjectRepository customerRepository;

    @Autowired
    ProjectController(ProjectRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private RestTemplate restTemplate;

//    @PostMapping
//    ResponseEntity<?> add(@PathVariable String id, @RequestBody Demo input) {
//        this.validateUser(id);
//
//        return this.customerRepository
//                .findById(id)
//                .map(customer -> {
//                    Bookmark result = bookmarkRepository.save(new Bookmark(customer,
//                            input.getUri(), input.getDescription()));
//
//                    URI location = ServletUriComponentsBuilder
//                            .fromCurrentRequest().path("/{id}")
//                            .buildAndExpand(result.getId()).toUri();
//
//                    return ResponseEntity.created(location).build();
//                })
//                .orElse(ResponseEntity.noContent().build());
//
//    }

    private void validateUser(String name) {
        this.customerRepository.findByFirstName(name);
    }
}
