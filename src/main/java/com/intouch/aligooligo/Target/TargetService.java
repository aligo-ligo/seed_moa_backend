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
        dto.setId(target.getId());
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
    public boolean createTarget(String email, Target req){
        try {
            User user = userRepository.findByEmail(email).get();
            Target saved = targetRepository.save(Target.builder().startDate(LocalDate.now()).endDate(req.getEndDate()).goal(req.getGoal()).subGoalTotal(0.0)
                    .successCount(0).failureVote(0).successVote(0).voteTotal(0).penalty(req.getPenalty()).user(user).subGoal(req.getSubGoal()).routine(req.getRoutine()).build());

            for (Subgoal subgoal : req.getSubGoal()) {
                subgoalRepository.save(Subgoal.builder().target(saved).value(subgoal.getValue()).success(false).build());
            }
            for (Routine routine : req.getRoutine()) {
                routineRepository.save(Routine.builder().target(saved).value(routine.getValue()).build());
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

//    public String updateTarget(TargetUpdateReq req){
//
//    }

    public boolean voteTarget(Long id, boolean success){
        try{
            Target target = targetRepository.findById(id).orElseThrow(()->new IllegalArgumentException("타겟을 찾을 수 없습니다."));
            if(success){
                targetRepository.save(Target.builder().startDate(target.getStartDate()).endDate(target.getEndDate()).goal(target.getGoal()).subGoalTotal(target.getSubGoalTotal())
                        .successCount(target.getSuccessCount()).failureVote(target.getFailureVote()).successVote(target.getSuccessVote()+1).voteTotal(target.getVoteTotal()+1)
                        .penalty(target.getPenalty()).user(target.getUser()).subGoal(target.getSubGoal()).routine(target.getRoutine()).build());
            }
            else {
                targetRepository.save(Target.builder().startDate(target.getStartDate()).endDate(target.getEndDate()).goal(target.getGoal()).subGoalTotal(target.getSubGoalTotal())
                        .successCount(target.getSuccessCount()).failureVote(target.getFailureVote() + 1).successVote(target.getSuccessVote()).voteTotal(target.getVoteTotal() + 1)
                        .penalty(target.getPenalty()).user(target.getUser()).subGoal(target.getSubGoal()).routine(target.getRoutine()).build());
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
