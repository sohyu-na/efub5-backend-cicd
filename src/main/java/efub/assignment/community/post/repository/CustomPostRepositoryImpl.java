package efub.assignment.community.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import efub.assignment.community.member.domain.QMember;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.domain.QPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static efub.assignment.community.member.domain.QMember.member;
import static efub.assignment.community.post.domain.QPost.post;

@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> search(String keyword, String writerNickname){
        QPost qPost = post;
        QMember qMember = member;

        BooleanBuilder builder = new BooleanBuilder();
        if(writerNickname!=null && !writerNickname.isBlank()){
            builder.and(post.author.nickname.eq(writerNickname));
        }
        if(keyword!=null && !keyword.isBlank()){
            builder.and(post.content.containsIgnoreCase(keyword));
        }
        return jpaQueryFactory.selectFrom(post)
                .join(post.author,member).fetchJoin()
                .where(builder)
                .orderBy(post.createdAt.desc())
                .distinct().fetch();
    }
}
