package sang.ik.controller.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sang.ik.service.member.MemberService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    @InjectMocks
    MemberController memberController;
    @Mock
    MemberService memberService;
    MockMvc mockMvc;  // 컨트롤러로 요청을 보내기 위해 사용

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }


    @Test
    void readTest() throws Exception {
        //given
        Long id = 1L;

        //when, then
        mockMvc.perform(get("/api/members/{id}",id))
                .andExpect(status().isOk());

        verify(memberService).read(id);
    }

    @Test
    void deleteTest() throws Exception{
        //given
        Long id = 1L;

        //when, then
        mockMvc.perform(delete("/api/members/{id}",id))
                .andExpect(status().isOk());

        verify(memberService).delete(id);
    }
}
