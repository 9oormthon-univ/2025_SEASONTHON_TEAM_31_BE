package com.cloudtone31.community.controller;

import com.cloudtone31.auth.LoginUser;
import com.cloudtone31.community.domain.Community;
import com.cloudtone31.community.dto.*;
import com.cloudtone31.community.service.CommentService;
import com.cloudtone31.community.service.CommunityLikeService;
import com.cloudtone31.community.service.CommunityService;
import com.cloudtone31.global.api.ApiResponse;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import com.cloudtone31.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/posts")
public class CommunityController {

    private final CommunityService communityService;
    private final UserLoginRepository userLoginRepository;
    private final CommentService commentService;
    private final CommunityLikeService communityLikeService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CommunityRequestDTO requestDTO, @LoginUser String kakaoId) {

        User user = userLoginRepository.findByKakaoId(kakaoId).orElse(null);
        Long userId = user.getId();
        Community community = communityService.create(requestDTO, userId);
        CommunityResponseDTO communityResponseDTO = CommunityResponseDTO.from(community);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(communityResponseDTO, "게시물이 작성되었습니다."));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<CommunityListResponseDTO>> getPostsList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ){
        CommunityListResponseDTO data = communityService.getCommunityList(page, limit, sort);
        return ResponseEntity.ok(ApiResponse.ok(data, "게시물 목록이 성공적으로 조회되었습니다."));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDTO requestDTO,
            @LoginUser String kakaoId){
        User user = userLoginRepository.findByKakaoId(kakaoId).orElse(null);
        Long userId = user.getId();

        var createdComment = commentService.createComment(postId, userId, requestDTO);
        var responseDTO = CommentResponseDTO.from(createdComment);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(responseDTO, "댓글이 작성되었습니다."));

    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommunityDetailDTO>> getCommunityDetail(@PathVariable Long postId) {
        CommunityDetailDTO data = communityService.getCommunityDetail(postId);
        return ResponseEntity.ok(ApiResponse.ok(data, "게시물이 성공적으로 조회되었습니다."));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<?>> addLike(
                                                   @PathVariable Long postId,
                                                   @LoginUser String kakaoId) {

        User user = userLoginRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        communityLikeService.addLike(postId, user.getId()); // 반환값이 없도록 수정

        return ResponseEntity.ok(ApiResponse.ok("게시물에 좋아요를 눌렀습니다."));
    }



}
