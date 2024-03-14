package com.intouch.aligooligo.Service;

import com.intouch.aligooligo.dto.TargetlistDTO;
import com.intouch.aligooligo.entity.Routine;
import com.intouch.aligooligo.repository.RoutineRepository;
import com.intouch.aligooligo.entity.Subgoal;
import com.intouch.aligooligo.repository.SubgoalRepository;
import com.intouch.aligooligo.entity.Target;
import com.intouch.aligooligo.dto.TargetDTO;
import com.intouch.aligooligo.repository.TargetRepository;
import com.intouch.aligooligo.req.TargetUpdateReq;
import com.intouch.aligooligo.entity.User;
import com.intouch.aligooligo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    public List<TargetlistDTO> getTargetList(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("can't get targetList : can't find userEmail"));
        List<Target> list = targetRepository.findByUserIdOrderByIdDesc(user.getId());
        List<TargetlistDTO> DTOlist = new ArrayList<>();
        for(Target target : list){
            int count=target.getSubGoal().size();
            for(Subgoal subgoal : target.getSubGoal())
                if(subgoal.getCompletedDate()==null)
                    count--;
            double achievePer = (double)count/target.getSubGoal().size() * 100;
            DTOlist.add(getTargetListDTO(target, (int)achievePer));
        }
        return DTOlist;
    }

    public TargetDTO getTargetDTO(Integer targetId,Map<String, Integer> resMap){
        Target target = targetRepository.findById(targetId).get();
        return new TargetDTO(target.getId(),target.getUser().getId(),target.getStartDate().toString(),
                target.getEndDate().toString(), target.getGoal(), target.getUrl(), target.getSubGoal(),
                target.getRoutine(), target.getPenalty(), target.getFailureVote(), target.getSuccessVote(),
                target.getVoteTotal(), resMap);
    }

    public TargetlistDTO getTargetListDTO(Target target, Integer achievementPer){
        return new TargetlistDTO(target.getId(),target.getUser().getId(),target.getGoal(),
                target.getSuccessVote(), target.getVoteTotal(), achievementPer);
    }

    @Transactional
    public boolean createTarget(String email, TargetDTO req){
        try {
            Date date = new Date(req.getEndDate());
            LocalDate endDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            User user = userRepository.findByEmail(email).get();
            Target saved = targetRepository.save(Target.builder().startDate(LocalDate.now()).endDate(endDate).goal(req.getGoal())
                    .failureVote(0).successVote(0).voteTotal(0).penalty(req.getPenalty()).user(user).subGoal(req.getSubGoal()).routine(req.getRoutine()).build());
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
    public SortedMap<String, Integer> getChartDate(Integer targetId) {
        Target target = targetRepository.findById(targetId).get();
        LocalDate startDate = target.getStartDate();
        LocalDate calDay = LocalDate.now().plusDays(1);

        int den = Long.valueOf(subgoalRepository.countByTargetId(target.getId())).intValue();//denominator
        int num = 0;//numerator
        int subGoalDateIdx = 0;
        double tmpNum;
        SortedMap<String, Integer> map = new TreeMap<>();
        List<Subgoal> subgoalList = subgoalRepository.findByTargetIdAndCompletedDateNotNullOrderByCompletedDateAsc(target.getId());
        while(!calDay.equals(startDate)){
            while (subGoalDateIdx<subgoalList.size() && startDate.equals(subgoalList.get(subGoalDateIdx).getCompletedDate())){
                num++;
                subGoalDateIdx++;
            }
            tmpNum = ((double)num/den)*100;
            map.put(startDate.toString(), (int) tmpNum);
            startDate = startDate.plusDays(1);
        }
        return map;
    }

    public TargetDTO getDetailTarget(Integer targetId) {
        if(targetRepository.findById(targetId).isPresent()) {//if exist

            SortedMap<String, Integer> resMap = getChartDate(targetId);

            return getTargetDTO(targetId,resMap);
        }
        return null;//if not exist
    }

    public TargetDTO updateTarget(TargetUpdateReq req){
        try{
            Target target = targetRepository.findById(req.id()).orElseThrow(()->new IllegalArgumentException("사용자가 없습니다."));
            for(Subgoal subgoal:target.getSubGoal()){
                if(Objects.equals(subgoal.getValue(), req.value())){
                    if(req.completeDate()==null) {//subGoal 체크 해제했을 경우
                        subgoal.updateDate(null);
                        subgoalRepository.save(subgoal);
                    }
                    else{//subGoal 체크했을 경우
                        subgoal.updateDate(LocalDate.now());
                        subgoalRepository.save(subgoal);
                    }
                    return getTargetDTO(req.id(),getChartDate(req.id()));
                }
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
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
                return getTargetDTO(id,getChartDate(id));
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}