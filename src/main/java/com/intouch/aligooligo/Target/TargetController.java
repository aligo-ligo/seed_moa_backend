package com.intouch.aligooligo.Target;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RequestMapping(value="/target")
@RestController
@CrossOrigin
public class TargetController {

    private final TargetService targetService;
    @GetMapping("/list")
    public ResponseEntity<List<Target>> getTargetList(@RequestParam Long id){
        try{
            return ResponseEntity.ok().body(targetService.getTargetList(id));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createTarget(@RequestBody Target req){
        try{
            String createRes = targetService.createTarget(req);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }



}
