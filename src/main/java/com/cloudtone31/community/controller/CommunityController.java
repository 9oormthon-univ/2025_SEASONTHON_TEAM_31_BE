package com.cloudtone31.community.controller;

import com.cloudtone31.community.domain.Community;
import com.cloudtone31.community.dto.CommentRequestDTO;
import com.cloudtone31.community.dto.CommentResponseDTO;
import com.cloudtone31.community.dto.CommunityDetailDTO;
import com.cloudtone31.community.dto.CommunityListResponseDTO;
import com.cloudtone31.community.dto.CommunityRequestDTO;
import com.cloudtone31.community.dto.CommunityResponseDTO;
import com.cloudtone31.community.service.CommentService;
import com.cloudtone31.community.service.CommunityLikeService;
import com.cloudtone31.community.service.CommunityService;
import com.cloudtone31.global.api.ApiResponse;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/posts")
public class CommunityController {

    private final CommunityService communityService;
    private final UserLoginRepository userLoginRepository;
    private final CommentService commentService;
    private final CommunityLikeService communityLikeService;

    /**
     * 게시글 작성
     * POST /community/posts
     */
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CommunityRequestDTO requestDTO,
                                        Authentication authentication) {

        // 1) 인증/유저 식별자 확인
        Long userId = resolveUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("인증이 필요합니다."));
        }

        // 2) 유저 조회
        User user = userLoginRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
        }

        // 3) 게시글 생성
        Community community = communityService.create(requestDTO, user.getId());
        CommunityResponseDTO response = CommunityResponseDTO.from(community);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "게시물이 작성되었습니다."));
    }

    /**
     * 게시글 목록 조회 (페이지네이션)
     * GET /community/posts?page=1&limit=20&sort=latest
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CommunityListResponseDTO>> getPostsList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ) {
        CommunityListResponseDTO data = communityService.getCommunityList(page, limit, sort);
        return ResponseEntity.ok(ApiResponse.ok(data, "게시물 목록이 성공적으로 조회되었습니다."));
    }

    /**
     * 댓글 작성
     * POST /community/posts/{postId}/comments
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long postId,
                                           @RequestBody CommentRequestDTO requestDTO,
                                           Authentication authentication) {

        Long userId = resolveUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("인증이 필요합니다."));
        }

        User user = userLoginRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
        }

        var created = commentService.createComment(postId, user.getId(), requestDTO);
        var response = CommentResponseDTO.from(created);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "댓글이 작성되었습니다."));
    }

    /**
     * 게시글 상세 조회
     * GET /community/posts/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommunityDetailDTO>> getCommunityDetail(@PathVariable Long postId) {
        CommunityDetailDTO data = communityService.getCommunityDetail(postId);
        return ResponseEntity.ok(ApiResponse.ok(data, "게시물이 성공적으로 조회되었습니다."));
    }

    /**
     * 게시글 좋아요
     * POST /community/posts/{postId}/like
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<?>> addLike(@PathVariable Long postId,
                                                  Authentication authentication) {

        Long userId = resolveUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("인증이 필요합니다."));
        }

        User user = userLoginRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
        }

        communityLikeService.addLike(postId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("게시물에 좋아요를 눌렀습니다."));
    }

    /**
     * Authentication → userId(Long) 추출 유틸
     * JwtAuthFilter가 principal에 userId(String)을 넣는 전제
     */
    private Long resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof String s) || s.isBlank()) return null;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}