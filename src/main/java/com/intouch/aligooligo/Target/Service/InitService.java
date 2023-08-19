package com.intouch.aligooligo.Target.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intouch.aligooligo.Target.Repository.TargetRepository;
import com.intouch.aligooligo.User.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InitService {
    private final UserRepository userRepository;
    private final TargetRepository targetRepository;
    public String getUserAndTargetNumber(){
        try{
            Long userCount = userRepository.count();
            Long targetCount = targetRepository.count();

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode mainNode = objectMapper.createObjectNode();

            mainNode.put("userCount",userCount);
            mainNode.put("targetCount",targetCount);
            return mainNode.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
