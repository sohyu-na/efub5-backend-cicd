package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostCreateRequestDto;
import efub.assignment.community.post.dto.response.PostListResponseDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.dto.request.UpdateContentDto;
import efub.assignment.community.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // 게시글 생성
    @Transactional
    public PostResponseDto createPost(PostCreateRequestDto postCreateRequestDto) {
        Board board = findBoardOrThrow(postCreateRequestDto.boardId());
        Member author = findMemberOrThrow(postCreateRequestDto.authorId());

        Post post = postCreateRequestDto.toEntity(board, author);
        Post savedPost = postRepository.save(post);
        return PostResponseDto.from(savedPost);
    }

    // 게시글 수정
    @Transactional
    public PostResponseDto updateContent(Long postId, UpdateContentDto updateContentDto) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        String newContent = updateContentDto.getContent();
        post.updateContent(newContent);
        Post savedPost = postRepository.save(post);
        return PostResponseDto.from(savedPost);
    }

    // 게시글 상세 내용 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return PostResponseDto.from(post);
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public PostListResponseDto getPostList(Long boardId) {
        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
        List<Post> postList = postRepository.findAllByBoard(board);
        return PostListResponseDto.from(boardId, postList);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteByPostId(postId);
    }

    private Board findBoardOrThrow(Long boardId) {
        return boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));
    }

    private Member findMemberOrThrow(Long memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));
    }
}
