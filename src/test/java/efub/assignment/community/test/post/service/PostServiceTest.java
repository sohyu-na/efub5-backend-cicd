package efub.assignment.community.test.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostCreateRequestDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.repository.PostRepository;
import efub.assignment.community.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class PostServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Member member;
    private Board board;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = Member.builder()
                .email("email@gmail.com")
                .nickname("nickname")
                .password("password")
                .studentId("2071020")
                .university("ewha")
                .build();
        board = Board.builder()
                .owner(member)
                .name("게시판 이름")
                .description("게시판 설명")
                .notice("게시판 공지")
                .build();
        post = Post.builder()
                .postId(1L)
                .board(board)
                .author(member)
                .anonymous(true)
                .content("게시글 내용")
                .build();
    }
    @Test
    void 게시글_생성_성공(){
        // given
        PostCreateRequestDto dto = new PostCreateRequestDto(
                board.getBoardId(),
                false,
                member.getMemberId(),
                "게시글 내용"
        );
        given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
        given(boardRepository.findByBoardId(board.getBoardId())).willReturn(Optional.of(board));
        given(postRepository.save(any(Post.class))).willReturn(post);

        // when
        PostResponseDto response = postService.createPost(dto);

        // then
        assertThat(response.getContent()).isEqualTo("게시글 내용");
        assertThat(response.getAuthorId()).isEqualTo(member.getMemberId());
        assertThat(response.getBoardId()).isEqualTo(board.getBoardId());

        verify(boardRepository).findByBoardId(board.getBoardId());
        verify(memberRepository).findByMemberId(member.getMemberId());
        verify(postRepository).save(any(Post.class));
    }

}
