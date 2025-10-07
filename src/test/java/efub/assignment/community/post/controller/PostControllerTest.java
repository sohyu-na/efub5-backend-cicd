package efub.assignment.community.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.dto.request.PostCreateRequestDto;
import org.junit.jupiter.api.BeforeEach;
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
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = new Member("2071000","ewha","nickname","email@example.com","Password1111111111!");
        memberRepository.save(member);

        Board board = new Board(member,"description","notice","name");
        boardRepository.save(board);
    }

    @Test
    @DisplayName("게시물 생성 성공 테스트")
    void Post_create_success() throws Exception {
        // given
        PostCreateRequestDto postCreateRequestDto =new PostCreateRequestDto(1L, false, 1L, "content");
        String requestBody = objectMapper.writeValueAsString(postCreateRequestDto);

        // when & then
        mockMvc.perform(post("/posts")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.authorId").value(postCreateRequestDto.authorId()));
    }

    @Test
    @DisplayName("게시물 생성 실패 테스트 _ blank content")
    void Post_create_fail_blankContent() throws Exception {
        // given
        PostCreateRequestDto postCreateRequestDto =new PostCreateRequestDto(1L, false, 1L, "");
        String requestBody = objectMapper.writeValueAsString(postCreateRequestDto);

        // when & then
        mockMvc.perform(post("/posts")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}