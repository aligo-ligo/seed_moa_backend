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
    private Integer totalCount;
    private List<TargetInfo> targetInfo;

    @AllArgsConstructor
    public static class TargetInfo {
        private Integer id;
        private Integer userId;
        private String goal;
        private Integer successRate;
        private Integer achievementPer;
    }
}
