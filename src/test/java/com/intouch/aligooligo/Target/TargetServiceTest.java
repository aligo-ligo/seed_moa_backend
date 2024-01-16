//package com.intouch.aligooligo.Target;
//
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.intouch.aligooligo.Target.Controller.Dto.TargetListResponse;
//import com.intouch.aligooligo.Target.Repository.TargetRepository;
//import com.intouch.aligooligo.Target.Service.TargetService;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//
//public class TargetServiceTest {
//
//    private ObjectMapper om = new ObjectMapper();
//
//    @Test
//    void 타겟_리스트_페이지네이션_테스트(){
//        String email = "test@test.com";
//        int size = 2;
//        int page = 1;
//
//
//        TargetService targetService = new TargetService();
//
//        List<TargetListResponse> responses = targetService.getTargetList(email,page,size);
//
//        assertThat(responses.get(0).getGoal()).isEqualTo("테스트입니다");
//    }
//
//}
