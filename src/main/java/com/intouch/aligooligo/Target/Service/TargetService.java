package com.intouch.aligooligo.Target.Service;

import com.intouch.aligooligo.Target.Controller.Dto.TargetListResponse;
import com.intouch.aligooligo.Target.Entity.Routine;
import com.intouch.aligooligo.Target.Entity.Subgoal;
import com.intouch.aligooligo.Target.Repository.SubgoalRepository;
import com.intouch.aligooligo.Target.Entity.Target;
import com.intouch.aligooligo.Target.Controller.Dto.TargetDTO;
import com.intouch.aligooligo.Target.Repository.TargetRepository;
import com.intouch.aligooligo.Target.Controller.Dto.TargetUpdateReq;
import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.User.Repository.UserRepository;
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

    @Value("${urlPrefix}")
    private String urlPrefix;

    public TargetService(TargetRepository targetRepository, UserRepository userRepository,
                         SubgoalRepository subgoalRepository){
        this.targetRepository = targetRepository;
        this.userRepository = userRepository;
        this.subgoalRepository = subgoalRepository;
    }

    public List<TargetListResponse> getTargetList(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("can't get targetList : can't find userEmail"));
        List<Target> list = targetRepository.findByUserIdOrderByIdDesc(user.getId());
        List<TargetListResponse> DTOlist = new ArrayList<>();
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

    public TargetListResponse getTargetListDTO(Target target, Integer achievementPer){
        return new TargetListResponse(target.getId(),target.getUser().getId(),target.getGoal(),
                target.getSuccessVote(), target.getVoteTotal(), achievementPer);
    }

    public TargetDTO getTargetDTO(Integer targetId,Map<String, Integer> resMap){
        Target target = targetRepository.findById(targetId).get();
        return new TargetDTO(target.getId(),target.getUser().getId(),target.getStartDate().toString(),
                target.getEndDate().toString(), target.getGoal(), target.getUrl(), target.getSubGoal(),
                target.getRoutine(), target.getFailureVote(), target.getSuccessVote(),
                target.getVoteTotal(), resMap);
    }

    @Transactional
    public boolean createTarget(String email, TargetDTO req){
        try {
            Date date = new Date(req.getEndDate());
            LocalDate endDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("can't create target : can't find userEmail"));
            Target saved = targetRepository.save(Target.builder().startDate(LocalDate.now()).endDate(endDate).goal(req.getGoal())
                    .failureVote(0).successVote(0).voteTotal(0).user(user).subGoal(req.getSubGoal()).routine(req.getRoutine()).build());
            String url = urlPrefix + saved.getId().toString();
            List<Subgoal> subgoals = req.getSubGoal().stream()
                    .map(SubgoalDTO -> new Subgoal(SubgoalDTO.getValue(), SubgoalDTO.getCompletedDate(),saved)).toList();
            List<Routine> routines = req.getRoutine().stream()
                    .map(RoutineDTO -> new Routine(RoutineDTO.getValue(), saved)).toList();
            saved.setSubGoalRoutine(subgoals, routines);
            saved.updateUrl(url);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void deleteTarget(Integer targetId){
        if(targetRepository.existsById(targetId))
            targetRepository.deleteById(targetId);
    }

    public Target findByIdTarget(Integer targetId){
        return targetRepository.findById(targetId).orElseThrow(()->new IllegalArgumentException("can't find target"));
    }

    public SortedMap<String, Integer> getChartDate(Integer targetId) {
        Target target;
        try{
            target = findByIdTarget(targetId);
        }catch (IllegalArgumentException e){
            return null;
        }

        System.out.println(target.getSubGoal().size());
        LocalDate startDate = target.getStartDate();
        int den = Long.valueOf(subgoalRepository.countByTargetId(target.getId())).intValue();//denominator
        int num = 0;//numerator
        int subGoalDateIdx = 0;

        SortedMap<String, Integer> map = new TreeMap<>();
        List<Subgoal> subgoalList = subgoalRepository.findByTargetIdAndCompletedDateNotNullOrderByCompletedDateAsc(target.getId());

        while(!LocalDate.now().plusDays(1).equals(startDate)){
            while (subGoalDateIdx<subgoalList.size() && startDate.equals(subgoalList.get(subGoalDateIdx).getCompletedDate())){
                num++;
                subGoalDateIdx++;
            }
            double tmpNum = ((double)num/den)*100;
            map.put(startDate.toString(), (int) tmpNum);
            startDate = startDate.plusDays(1);
        }
        return map;
    }

    public TargetDTO getDetailTarget(Integer targetId) {
        if(targetRepository.existsById(targetId)) {//if exist
            SortedMap<String, Integer> resMap = getChartDate(targetId);
            return getTargetDTO(targetId,resMap);
        }
        return null;//if not exist
    }

    public TargetDTO updateTarget(TargetUpdateReq req){
        Target target;
        try{
            target = findByIdTarget(req.id());
        }catch (IllegalArgumentException e){
            throw e;
        }
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
    }

    public boolean voteTarget(Integer id, boolean success){
        try{
            Target target = targetRepository.findById(id).orElseThrow(()->new IllegalArgumentException("can't find target"));
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
        if(targetRepository.existsById(id)) {//if exist
            return getTargetDTO(id, getChartDate(id));
        }
        return null;
    }
}