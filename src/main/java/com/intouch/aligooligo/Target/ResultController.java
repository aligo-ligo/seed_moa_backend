package com.intouch.aligooligo.Target;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping(value="/result")
@RestController
@CrossOrigin
public class ResultController {

    private final TargetService targetService;
    @GetMapping
    public ResponseEntity<TargetDTO> resultTargetPage(@RequestParam Integer id){
        try{
            TargetDTO targetDTO = targetService.resultTargetPage(id);
            if(targetDTO ==null)
                return ResponseEntity.internalServerError().build();
            return ResponseEntity.ok().body(targetDTO);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
