package com.cloudtone31.community.repository;

import com.cloudtone31.community.domain.Comments;
import com.cloudtone31.community.domain.Community;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comments, Long> {

    // Community 객체로 댓글 목록을 찾아 생성 시간 오름차순으로 정렬하는 메서드 추가
    List<Comments> findByCommunityOrderByCreatedAtAsc(Community community);
}
