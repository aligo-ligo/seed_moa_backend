package com.intouch.aligooligo.Target.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intouch.aligooligo.Target.Repository.TargetRepository;
import com.intouch.aligooligo.User.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class InitService {
    private final UserRepository userRepository;
    private final TargetRepository targetRepository;
    public Map<String, Integer> getUserAndTargetNumber(){
        Map<String, Integer> res = new HashMap<>();
        Integer userCount = Long.valueOf(userRepository.count()).intValue();
        Integer targetCount = Long.valueOf(targetRepository.count()).intValue();

        res.put("userCount",userCount);
        res.put("targetCount",targetCount);

        return res;
    }
}
