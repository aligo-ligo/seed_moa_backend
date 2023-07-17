package com.intouch.aligooligo.Target;


import com.intouch.aligooligo.Routine.Routine;
import com.intouch.aligooligo.Routine.RoutineRepository;
import com.intouch.aligooligo.Subgoal.Subgoal;
import com.intouch.aligooligo.Subgoal.SubgoalRepository;
import com.intouch.aligooligo.User.User;
import com.intouch.aligooligo.User.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class TargetService {
    private final TargetRepository targetRepository;
    private final UserRepository userRepository;
    private final SubgoalRepository subgoalRepository;
    private final RoutineRepository routineRepository;

    public TargetService(TargetRepository targetRepository, UserRepository userRepository,
                         SubgoalRepository subgoalRepository, RoutineRepository routineRepository){
        this.targetRepository = targetRepository;
        this.userRepository = userRepository;
        this.routineRepository = routineRepository;
        this.subgoalRepository = subgoalRepository;
    }
    @Value("${urlPrefix}")
    private String urlPrefix;

    public List<TargetDTO> getTargetList(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("can't get targetList : can't find userEmail"));
        List<Target> list = targetRepository.findByUserIdOrderByIdAsc(user.getId());
        List<TargetDTO> DTOlist = new ArrayList<>();
        for(Target target : list){
            DTOlist.add(getTargetListDTO(target));
        }
        return DTOlist;
    }

    public TargetDTO getTargetDTO(Target target){
        return new TargetDTO(target.getId(),target.getUser().getId(),target.getGoal(), target.getUrl(),target.getPenalty(),
                target.getStartDate(), target.getEndDate(), target.getSubGoal(), target.getRoutine(),
                target.getSubGoalTotal(),target.getSuccessCount(),target.getSuccessVote(), target.getFailureVote(),
                target.getVoteTotal());
    }

    public TargetDTO getTargetListDTO(Target target){
        return new TargetDTO(target.getId(),target.getUser().getId(),target.getGoal(),
                target.getSubGoalTotal(),target.getSuccessCount(),target.getSuccessVote(),
                target.getVoteTotal());
    }

    @Transactional
    public boolean createTarget(String email, TargetDTO req){
        try {
            Date date = new Date(req.getEndDate());
            LocalDate endDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            User user = userRepository.findByEmail(email).get();
            Target saved = targetRepository.save(Target.builder().startDate(LocalDate.now().toString()).endDate(endDate.toString()).goal(req.getGoal()).subGoalTotal(req.getSubGoal().size())
                    .successCount(0).failureVote(0).successVote(0).voteTotal(0).penalty(req.getPenalty()).user(user).subGoal(req.getSubGoal()).routine(req.getRoutine()).build());
            for (Subgoal subgoal : req.getSubGoal()) {
                subgoalRepository.save(Subgoal.builder().target(saved).value(subgoal.getValue()).build());
            }
            for (Routine routine : req.getRoutine()) {
                routineRepository.save(Routine.builder().target(saved).value(routine.getValue()).build());
            }
            String url = urlPrefix + saved.getId().toString();
            saved.updateUrl(url);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public TargetDTO getDetailTarget(Integer targetId) {
        if(targetRepository.findById(targetId).isPresent()) {//if exist
            Target target = targetRepository.findById(targetId).get();
            return getTargetDTO(target);
        }
        return null;//if not exist
    }

    public boolean updateTarget(TargetUpdateReq req){
        try{
            Target target = targetRepository.findById(req.id()).orElseThrow(()->new IllegalArgumentException("사용자가 없습니다."));
            for(Subgoal subgoal:target.getSubGoal()){
                if(Objects.equals(subgoal.getValue(), req.value())){
                    if(req.completeDate()==null) {//subGoal 체크 해제했을 경우
                        subgoal.updateDate(null);
                        subgoalRepository.save(subgoal);
                        if(target.getSuccessCount()==0)//해당 target successCount가 0이면
                            return true;
                        else {
                            target.updateTarget(target.getSuccessCount()-1);
                            targetRepository.save(target);
                        }
                    }
                    else{//subGoal 체크했을 경우
                        Date date = new Date(req.completeDate());
                        LocalDate completeDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        subgoal.updateDate(completeDate.toString());
                        target.updateTarget(target.getSuccessCount()+1);
                        subgoalRepository.save(subgoal);
                        targetRepository.save(target);
                    }
                    return true;
                }
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean voteTarget(Integer id, boolean success){
        try{
            Target target = targetRepository.findById(id).orElseThrow(()->new IllegalArgumentException("타겟을 찾을 수 없습니다."));
            if(success) {
                target.updateVote(target.getSuccessVote() + 1, target.getFailureVote(), target.getVoteTotal()+1);
                targetRepository.save(target);
            }
            else {
                target.updateVote(target.getSuccessVote(), target.getFailureVote() + 1, target.getVoteTotal()+1);
                targetRepository.save(target);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public TargetDTO resultTargetPage(Integer id){
        try{
            if(targetRepository.findById(id).isPresent()) {//if exist
                TargetDTO targetDTO = getTargetDTO(targetRepository.findById(id).get());
                return targetDTO;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
