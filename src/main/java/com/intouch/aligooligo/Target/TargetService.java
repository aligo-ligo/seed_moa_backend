package com.intouch.aligooligo.Target;


import com.intouch.aligooligo.Routine.Routine;
import com.intouch.aligooligo.Routine.RoutineRepository;
import com.intouch.aligooligo.Subgoal.Subgoal;
import com.intouch.aligooligo.Subgoal.SubgoalRepository;
import com.intouch.aligooligo.User.User;
import com.intouch.aligooligo.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TargetService {
    private final TargetRepository targetRepository;
    private final UserRepository userRepository;
    private final SubgoalRepository subgoalRepository;
    private final RoutineRepository routineRepository;

    public List<Target> getTargetList(Long id){
        return targetRepository.findAllById(id);
    }

    @Transactional
    public String createTarget(String email, Target req){
        System.out.println("req : " +req.getGoal());
        System.out.println("req : " +req.getEndDate());
        System.out.println("req : " +req.getPenalty());
        System.out.println("req : " +req.getSubGoal());
        System.out.println("req : " +req.getRoutine());

        User user = userRepository.findByEmail(email).get();
        System.out.println(user);
        Target saved = targetRepository.save(Target.builder().startDate(LocalDate.now()).endDate(req.getEndDate()).goal(req.getGoal()).subGoalTotal(0.0)
                .successCount(0).failureVote(0).successVote(0).voteTotal(0).penalty("테스트입니다").user(user).subGoal(req.getSubGoal()).routine(req.getRoutine()).build());

        System.out.println("hihihih");
        for(Subgoal subgoal : req.getSubGoal()){
            System.out.println("hihihih");
            subgoalRepository.save(Subgoal.builder().target(saved).value(subgoal.getValue()).success(false).build());
        }
        for(Routine routine : req.getRoutine()){
            System.out.println("hihihihssfsdfd");
            routineRepository.save(Routine.builder().target(saved).value(routine.getValue()).build());
            System.out.println("hihihihssfsdsfdsfsdfsdfsfd");
        }

        return null;
    }
}
