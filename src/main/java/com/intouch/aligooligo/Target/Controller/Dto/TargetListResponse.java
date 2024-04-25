package com.intouch.aligooligo.Target.Controller.Dto;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.intouch.aligooligo.Target.Entity.Subgoal;
import com.intouch.aligooligo.Target.Entity.Target;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class TargetListResponse {
    /* 유저당 총 타겟 개수와 해당 타겟 정보 리스트 */

    private Pages pages;
    private List<TargetInfo> targetInfo;

    public TargetListResponse() {
        pages = null;
        targetInfo = new ArrayList<>();
    }

    @Getter
    @AllArgsConstructor
    private static class TargetInfo {
        private Integer id;
        private Integer userId;
        private String goal;
        private Integer successRate;
        private Integer achievementPer;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Pages{
        private Integer totalPages;
        private Long totalElements;
        private Integer currentPage;
        private Boolean hasPrevious;
        private Boolean hasNext;
    }
//    public void updateInfo (Page<Target> targetList) {
//        for(Target target : targetList){
//            int count=target.getSubGoal().size();
//            for(Subgoal subgoal : target.getSubGoal())
//                if(subgoal.getCompletedDate()==null)
//                    count--;
//            double successRate = (double)target.getSuccessVote()/target.getVoteTotal() * 100;
//            double achievePer = (double)count/target.getSubGoal().size() * 100;
//            TargetInfo targetinfo = new TargetInfo(target.getId(), target.getUser().getId(), target.getGoal(),
//                    (int) successRate, (int) achievePer);
//            this.targetInfo.add(targetinfo);
//        }
//    }

    public void updateInfo (List<Target> targetList) {
        for(Target target : targetList){
            int count=target.getSubGoal().size();
            for(Subgoal subgoal : target.getSubGoal())
                if(subgoal.getCompletedDate()==null)
                    count--;
            double successRate = (double)target.getSuccessVote()/target.getVoteTotal() * 100;
            double achievePer = (double)count/target.getSubGoal().size() * 100;
            TargetInfo targetinfo = new TargetInfo(target.getId(), target.getUser().getId(), target.getGoal(),
                    (int) successRate, (int) achievePer);
            this.targetInfo.add(targetinfo);
        }
    }

    public void updatePages (Page<Target> page) {
        this.pages = new Pages(page.getTotalPages(), page.getTotalElements(),  page.getNumber(),
                page.hasPrevious(), page.hasNext());
    }

}
