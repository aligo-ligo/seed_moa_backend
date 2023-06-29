package com.intouch.aligooligo.Target;


import com.intouch.aligooligo.Routine.Routine;
import com.intouch.aligooligo.Routine.RoutineRepository;
import com.intouch.aligooligo.Subgoal.Subgoal;
import com.intouch.aligooligo.Subgoal.SubgoalRepository;
import com.intouch.aligooligo.User.User;
import com.intouch.aligooligo.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TargetService {
    private final TargetRepository targetRepository;
    private final UserRepository userRepository;
    private final SubgoalRepository subgoalRepository;
    private final RoutineRepository routineRepository;

    public List<TargetDTO> getTargetList(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        List<Target> list = targetRepository.findAllByUserId(user.getId());
        List<TargetDTO> DTOlist = new ArrayList<>();
        for(Target target : list){
            DTOlist.add(getTargetDTO(target));
        }
        return DTOlist;
    }

    public TargetDTO getTargetDTO(Target target){
        TargetDTO dto = new TargetDTO();
        dto.setUserId(target.getUser().getId());
        dto.setGoal(target.getGoal());
        dto.setPenalty(target.getPenalty());
        dto.setStartDate(target.getStartDate());
        dto.setEndDate(target.getEndDate());
        dto.setSubGoal(target.getSubGoal());
        dto.setRoutine(target.getRoutine());
        dto.setSubGoalTotal(target.getSubGoalTotal());
        dto.setSuccessCount(target.getSuccessCount());
        dto.setSuccessVote(target.getSuccessVote());
        dto.setFailureVote(target.getFailureVote());
        dto.setVoteTotal(target.getVoteTotal());
        return dto;
    }

    @Transactional
    public String createTarget(String email, Target req){

        User user = userRepository.findByEmail(email).get();
        Target saved = targetRepository.save(Target.builder().startDate(LocalDate.now()).endDate(req.getEndDate()).goal(req.getGoal()).subGoalTotal(0.0)
                .successCount(0).failureVote(0).successVote(0).voteTotal(0).penalty(req.getPenalty()).user(user).subGoal(req.getSubGoal()).routine(req.getRoutine()).build());

        for(Subgoal subgoal : req.getSubGoal()){
            subgoalRepository.save(Subgoal.builder().target(saved).value(subgoal.getValue()).success(false).build());
        }
        for(Routine routine : req.getRoutine()){
            routineRepository.save(Routine.builder().target(saved).value(routine.getValue()).build());
        }
        return null;
    }
}
