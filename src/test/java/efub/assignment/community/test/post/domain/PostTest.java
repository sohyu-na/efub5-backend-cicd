package efub.assignment.community.test.post.domain;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PostTest {

    private Post post;
    private Board board;
    private Member member;

    @BeforeEach
    void setUp() {
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
    void 게시글_생성_정상작동(){
        assertNotNull(member);
        assertEquals(1L, post.getPostId());
        assertEquals(board, post.getBoard());
        assertEquals(member, post.getAuthor());
        assertTrue(post.isAnonymous());
        assertEquals("게시글 내용", post.getContent());
    }
    @Test
    void 게시글_내용_수정_정상작동() {
        post.updateContent("수정된 내용");
        assertEquals("수정된 내용", post.getContent());
    }

}
