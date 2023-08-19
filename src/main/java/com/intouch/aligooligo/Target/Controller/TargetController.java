package com.intouch.aligooligo.Target.Controller;


import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.Target.Controller.Dto.TargetDTO;
import com.intouch.aligooligo.Target.Service.TargetService;
import com.intouch.aligooligo.Target.Controller.Dto.TargetlistDTO;
import com.intouch.aligooligo.Target.Controller.Dto.TargetUpdateReq;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final TargetService targetService;

    @GetMapping("/list")
    public ResponseEntity<List<TargetlistDTO>> getTargetList(HttpServletRequest request){
        try{
            List<TargetlistDTO> list = targetService.getTargetList(ExtractEmail(request));
            return ResponseEntity.ok().body(list);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createTarget(HttpServletRequest request, @RequestBody TargetDTO req) {
        try {
            if(targetService.createTarget(ExtractEmail(request), req))
                return ResponseEntity.ok().build();//ok
            return ResponseEntity.internalServerError().build();//server error
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();//server error
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> deleteTarget(@RequestParam Integer id){
        targetService.deleteTarget(id);
        return ResponseEntity.ok().build();
    }

    public String ExtractEmail(HttpServletRequest request){
        return jwtTokenProvider.getAuthentication(
                request.getHeader("Authorization").
                        substring(7)).getName();
    }

    @GetMapping("/detail")
    public ResponseEntity<TargetDTO> detailTarget(@RequestParam Integer id){
        try{
            TargetDTO targetDTO = targetService.getDetailTarget(id);
            if(targetDTO==null)
                return ResponseEntity.internalServerError().build();
            return ResponseEntity.ok().body(targetDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<TargetDTO> updateTarget(@RequestBody TargetUpdateReq req){
        try{
            if(req==null){return ResponseEntity.badRequest().build();}
            TargetDTO targetDTO = targetService.updateTarget(req);
            if(targetDTO==null){
                return ResponseEntity.internalServerError().build();
            }
            return ResponseEntity.ok().body(targetDTO);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/vote")
    public ResponseEntity<HttpStatus> voteTarget(@RequestParam(value = "id") Integer id, @RequestParam(value = "success") boolean success){
        try{
            if(targetService.voteTarget(id, success))
                return ResponseEntity.ok().build();//ok
            return ResponseEntity.internalServerError().build();//server error
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();//server error
        }
    }
}
