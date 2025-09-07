package com.lakshmi.interview_tracking_system.controller;

import com.lakshmi.interview_tracking_system.dto.Rating;
import com.lakshmi.interview_tracking_system.service.PanelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class PanelController {
    @Autowired
    private PanelService panelService;

    @GetMapping("api/panel/interviews")
       public ResponseEntity<?> getInterviews (Principal principal){
        return panelService.getInterviews(principal);
    }
    @GetMapping("api/panel/candidate/{id}")
    public ResponseEntity <?>  getCandidate (@PathVariable Long id){
        return panelService.getCandidate(id);
    }
    @PostMapping("api/panel/interview/{id}")
    public ResponseEntity <?> submitRating (@RequestBody Rating rating,@PathVariable Long id,Principal p){
        return panelService.submitRating(rating,id,p);


    }

}
