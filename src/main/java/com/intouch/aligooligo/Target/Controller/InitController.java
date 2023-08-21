package com.intouch.aligooligo.Target.Controller;

import com.intouch.aligooligo.Target.Service.InitService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping
public class InitController {
    private final InitService initService;
    @GetMapping
    public ResponseEntity<Map<String, Integer>> mainPage(){
        Map<String, Integer> res = initService.getUserAndTargetNumber();
        if(res==null)
            return ResponseEntity.internalServerError().build();
        return ResponseEntity.ok(res);
    }
}
