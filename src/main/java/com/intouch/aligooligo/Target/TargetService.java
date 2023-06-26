package com.intouch.aligooligo.Target;


import com.intouch.aligooligo.User.User;
import com.intouch.aligooligo.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TargetService {
    private TargetRepository targetRepository;

    private UserRepository userRepository;

    public List<Target> getTargetList(Integer id){
        return targetRepository.findAllById(id);
    }

    public void writeTargetList(Target req){
        User user = userRepository.findById(Long.valueOf(25)).get();
        targetRepository.save(Target.builder().startDate(LocalDate.now()).endDate(LocalDate.of(2023,7,20)).goal("test").subGoalTotal(0.0)
                .successCount(0).failureVote(0).successVote(0).voteTotal(0).penalty("테스트입니다").user(user).build());
    }
}
