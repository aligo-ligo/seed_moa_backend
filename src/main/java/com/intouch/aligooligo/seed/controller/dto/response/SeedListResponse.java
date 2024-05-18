package com.intouch.aligooligo.seed.controller.dto.response;


import com.intouch.aligooligo.seed.controller.dto.RoutineInfo;
import com.intouch.aligooligo.seed.domain.Routine;
import com.intouch.aligooligo.seed.domain.Seed;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class SeedListResponse {
    /* 유저당 총 타겟 개수와 해당 타겟 정보 리스트 */

    private Pages pages;
    private List<SeedInfo> seedInfo;

    public SeedListResponse() {
        pages = null;
        seedInfo = new ArrayList<>();
    }

    @Getter
    @AllArgsConstructor
    private static class SeedInfo {
        private Long id;
        private String startDate;
        private String endDate;
        private String seedName;
        private List<RoutineInfo> routineInfos;
        private Integer completedRoutineCount;
        private Long cheeringCount;
        private String seedState;
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

    private List<RoutineInfo> convertToRoutineInfo(List<Routine> routines) {
        List<RoutineInfo> routineInfos = new ArrayList<>();

        for (Routine routine : routines) {
            routineInfos.add(new RoutineInfo(routine.getTitle()));
        }

        return routineInfos;
    }

    public void updateSeedList(Page<Seed> seedList, List<List<Routine>> routinesList, Long cheeringCount, List<Integer> completedRoutineCountList) {
        List<Seed> seeds = seedList.getContent();
        for (int i = 0; i < seeds.size(); i++) {
            String seedStartDate = seeds.get(i).getStartDate().toString();
            String seedEndDate = seeds.get(i).getEndDate().toString();
            Integer completedRoutineCount = completedRoutineCountList.get(i);

            List<Routine> routines = routinesList.get(i);

            SeedInfo newSeedInfo = new SeedInfo(
                    seeds.get(i).getId(), seedStartDate, seedEndDate,
                    seeds.get(i).getSeed(), convertToRoutineInfo(routines),
                    completedRoutineCount, cheeringCount, seeds.get(i).getState());

            this.seedInfo.add(newSeedInfo);
        }
    }

    public void updatePages (Page<Seed> page) {
        this.pages = new Pages(page.getTotalPages(), page.getTotalElements(),  page.getNumber(),
                page.hasPrevious(), page.hasNext());
    }

}
