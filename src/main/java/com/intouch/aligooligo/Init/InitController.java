package com.intouch.aligooligo.Init;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping
public class InitController {
    private final InitService initService;
    @GetMapping
    public ResponseEntity<String> mainPage(){
        String res = initService.getUserAndTargetNumber();
        if(res==null)
            return ResponseEntity.internalServerError().build();
        return ResponseEntity.ok(res);
    }
}
