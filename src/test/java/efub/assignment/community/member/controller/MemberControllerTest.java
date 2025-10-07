package efub.assignment.community.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.member.dto.request.MemberRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private MemberRequestDto memberRequestDto;

    @Test
    @DisplayName("회원 생성 성공 테스트")
    void Member_create_success() throws Exception {
        // given
        memberRequestDto = MemberRequestDto.builder()
                .studentId("2071000")
                .university("ewha")
                .nickname("nickname")
                .email("email@example.com")
                .password("Password1111111111!")
                .build();
        String requestBody = objectMapper.writeValueAsString(memberRequestDto);

        // when & then
        mockMvc.perform(post("/members")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value("2071000"))
                .andReturn();
    }

    @Test
    @DisplayName("회원 생성 실패 테스트 _ 16자 미만 password")
    void Member_create_fail_short_password() throws Exception {
        // given
        memberRequestDto = MemberRequestDto.builder()
                .studentId("2071000")
                .university("ewha")
                .nickname("nickname")
                .email("email@example.com")
                .password("password1!")
                .build();
        String requestBody = objectMapper.writeValueAsString(memberRequestDto);

        // when & then
        mockMvc.perform(post("/members")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

}