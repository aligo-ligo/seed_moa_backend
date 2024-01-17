package com.intouch.aligooligo.Target.Controller.Dto;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TargetListResponse {
    /* 유저당 총 타겟 개수와 해당 타겟 정보 리스트 */

    private Integer totalCount;
    private List<TargetInfo> targetInfo;

    //@Getter
    @AllArgsConstructor
    public static class TargetInfo {
        private Integer id;
        private Integer userId;
        private String goal;
        private Integer successRate;
        private Integer achievementPer;
    }
}
