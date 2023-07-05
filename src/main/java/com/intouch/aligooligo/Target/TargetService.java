package com.intouch.aligooligo.Target;


import com.intouch.aligooligo.Base62.Base62Util;
import com.intouch.aligooligo.Routine.Routine;
import com.intouch.aligooligo.Routine.RoutineRepository;
import com.intouch.aligooligo.ShortUrl.ShortUrl;
import com.intouch.aligooligo.ShortUrl.ShortUrlRepository;
import com.intouch.aligooligo.Subgoal.Subgoal;
import com.intouch.aligooligo.Subgoal.SubgoalRepository;
import com.intouch.aligooligo.User.User;
import com.intouch.aligooligo.User.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class TargetService {
    private final TargetRepository targetRepository;
    private final UserRepository userRepository;
    private final SubgoalRepository subgoalRepository;
    private final RoutineRepository routineRepository;
    private final ShortUrlRepository shortUrlRepository;
    private final Base62Util base62Util;

    public TargetService(ShortUrlRepository shortUrlRepository, TargetRepository targetRepository, Base62Util base62Util,
                         UserRepository userRepository, SubgoalRepository subgoalRepository, RoutineRepository routineRepository){
        this.shortUrlRepository = shortUrlRepository;
        this.targetRepository = targetRepository;
        this.base62Util = base62Util;
        this.userRepository = userRepository;
        this.routineRepository = routineRepository;
        this.subgoalRepository = subgoalRepository;
    }

    private String originPrefix = "http://localhost:8081/target/result/id=";
    private String shortPrefix = "http://aligo.it/";

    public List<TargetListDTO> getTargetList(String email){
        System.out.println(email);
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("can't get targetList : can't find userEmail"));
        List<Target> list = targetRepository.findAllByUserId(user.getId());
        List<TargetListDTO> DTOlist = new ArrayList<>();
        for(Target target : list){
            DTOlist.add(getTargetListDTO(target));
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

    public TargetListDTO getTargetListDTO(Target target){
        TargetListDTO dto = new TargetListDTO();
        dto.setId(target.getId());
        dto.setUserId(target.getUser().getId());
        dto.setGoal(target.getGoal());
        dto.setSubGoalTotal(target.getSubGoalTotal());
        dto.setSuccessCount(target.getSuccessCount());
        dto.setSuccessVote(target.getSuccessVote());
        dto.setVoteTotal(target.getVoteTotal());
        return dto;
    }

    @Transactional
    public boolean createTarget(String email, Target req){
        try {
            User user = userRepository.findByEmail(email).get();
            Target saved = targetRepository.save(Target.builder().startDate(LocalDate.now().toString()).endDate(req.getEndDate()).goal(req.getGoal()).subGoalTotal(req.getSubGoal().size())
                    .successCount(0).failureVote(0).successVote(0).voteTotal(0).penalty(req.getPenalty()).user(user).subGoal(req.getSubGoal()).routine(req.getRoutine()).build());
            for (Subgoal subgoal : req.getSubGoal()) {
                subgoalRepository.save(Subgoal.builder().target(saved).value(subgoal.getValue()).success(false).build());
            }
            for (Routine routine : req.getRoutine()) {
                routineRepository.save(Routine.builder().target(saved).value(routine.getValue()).build());
            }
            ShortUrl shortUrl = createShareUrl(saved.getId());
            //targetRepository.save(Target.builder().id(saved.getId()).shortUrl(shortUrl).build());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public TargetDTO getDetailTarget(Long targetId) {
        if(targetRepository.findById(targetId).isPresent()) {//if exist
            Target target = targetRepository.findById(targetId).get();
            return getTargetDTO(target);
        }
        return null;//if not exist
    }

    public boolean updateTarget(TargetUpdateReq req){
        try{
            Target target = targetRepository.findById(req.targetId()).orElseThrow(()->new IllegalArgumentException("사용자가 없습니다."));
            for(Subgoal subgoal:target.getSubGoal()){
                if(Objects.equals(subgoal.getValue(), req.subGoal())){
                    if(req.completeDate()==null)
                        subgoalRepository.save(Subgoal.builder().id(subgoal.getId()).target(target).success(false).completedDate(null).value(subgoal.getValue()).build());
                    else
                        subgoalRepository.save(Subgoal.builder().id(subgoal.getId()).target(target).success(true).completedDate(req.completeDate()).value(subgoal.getValue()).build());
                    return true;
                }
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean voteTarget(Long id, boolean success){
        try{
            Target target = targetRepository.findById(id).orElseThrow(()->new IllegalArgumentException("타겟을 찾을 수 없습니다."));
            if(success){
                targetRepository.save(Target.builder().successVote(target.getSuccessVote()+1)
                        .voteTotal(target.getVoteTotal()+1).build());
            }
            targetRepository.save(Target.builder().failureVote(target.getFailureVote()+1)
                    .voteTotal(target.getVoteTotal()+1).build());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public ShortUrl createShareUrl(Long targetId){
        String encodedUrl = null;
        try{
            Target target = targetRepository.findById(targetId).orElseThrow(()->new IllegalArgumentException("can't make shortUrl: can't find targetId"));
            //originUrl
            String originUrl = originPrefix+targetId.toString();

            //random create
            do {
                BigInteger number = new BigInteger(RandomStringUtils.randomNumeric(1, 15), 10);

                //UUID uuid = UUID.randomUUID();//uuid : 16 radix
                //BigInteger number = new BigInteger(uuid.toString().replace("-",""),16);

                //encoding(shortedUrl)
                encodedUrl = shortPrefix + base62Util.UrlEncoding(number);
                System.out.println(encodedUrl);
            }while(shortUrlRepository.existsByShortUrl(encodedUrl));
            return shortUrlRepository.save(ShortUrl.builder().originUrl(originUrl).shortUrl(encodedUrl).target(target).build());
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public boolean existId(Long id){
        return shortUrlRepository.existsByTargetId(id);
    }
    public boolean existShortUrl(String shortUrl){
        return shortUrlRepository.existsByShortUrl(shortUrl);
    }

    public String shareUrl(Long targetId){
        try {
            if (existId(targetId))
                return shortUrlRepository.findByTargetId(targetId).getShortUrl();

            else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public TargetDTO resultTargetPage(String shortUrl){
        try{
            if(!existShortUrl(shortUrl))
                return null;
            ShortUrl url = shortUrlRepository.findByShortUrl(shortUrl);
            System.out.println(url.getTarget());
            TargetDTO targetDTO = getTargetDTO(url.getTarget());
            System.out.println(targetDTO);
            return targetDTO;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
