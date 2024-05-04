//package com.intouch.aligooligo.Target;
//
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.intouch.aligooligo.User.Controller.Dto.UserLoginRequestDto;
//import com.intouch.aligooligo.User.Controller.UserController;
//import com.intouch.aligooligo.User.Service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.filter.CharacterEncodingFilter;
//
//@WebMvcTest(UserController.class)
//@ExtendWith(MockitoExtension.class)
//public class TargetControllerTest {
//
//    @Autowired
//    private MockMvc mockmvc;
//
//    @MockBean
//    private UserService userService;
//
//    private ObjectMapper om = new ObjectMapper();
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @BeforeEach
//    public void setup() {
//        this.mockmvc = MockMvcBuilders.webAppContextSetup(context)
//                .addFilter(new CharacterEncodingFilter("UTF-8", true))
//                .apply(SecurityMockMvcConfigurers.springSecurity())
//                .defaultRequest(post("/**").with(csrf()))
//                .defaultRequest(patch("/**").with(csrf()))
//                .defaultRequest(delete("/**").with(csrf()))
//                .defaultRequest(get("/**").with(csrf()))
//                .alwaysDo(print())
//                .build();
//    }
//
//    @Test
//    @WithMockUser
//    public void 유저_일반_로그인() throws Exception {
//        UserLoginRequestDto userDto = new UserLoginRequestDto();
//        userDto.setEmail("test@test.com");
//        userDto.setPassword("test123456");
//        //userDto.setNickName("wjdtmdwn");
//
//        this.mockmvc
//                .perform(post("/users/signin")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(om.writeValueAsString(userDto))
//                )
//                .andExpect(status().is4xxClientError())
//                .andDo(print());
//    }
//
//}