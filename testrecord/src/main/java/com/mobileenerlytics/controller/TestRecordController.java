package com.mobileenerlytics.controller;

import com.mobileenerlytics.repository.ProjectRepository;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/testrecords")
public class TestRecordController {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MongoDatabase mongoDatabase;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRecordController.class);

    @Autowired
    private RestTemplate restTemplate; // consume other api.




    @GetMapping(value="/{id}/**")
    public ResponseEntity getTestRecord(@PathVariable String id, HttpServletRequest request) {
        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String ) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        AntPathMatcher apm = new AntPathMatcher();
        String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);
        LOGGER.info("test id: " + id + ", path: " + finalPath);
        return null;
    }

}
