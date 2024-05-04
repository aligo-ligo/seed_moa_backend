package com.intouch.aligooligo.seed.controller.Dto;


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
        private Integer id;
        private String startDate;
        private String endDate;
        private String seed;
        private Integer routineCount;
        private
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

    public void updateSeedList() {

    }

    public void updatePages (Page<Seed> page) {
        this.pages = new Pages(page.getTotalPages(), page.getTotalElements(),  page.getNumber(),
                page.hasPrevious(), page.hasNext());
    }

}
