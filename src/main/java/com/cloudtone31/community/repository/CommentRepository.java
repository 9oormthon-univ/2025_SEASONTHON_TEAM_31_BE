package com.cloudtone31.community.repository;

import com.cloudtone31.community.domain.Comments;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comments, Long> {
}
