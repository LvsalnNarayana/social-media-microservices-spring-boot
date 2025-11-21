package com.example.social_media.post_service.web.api;

import com.example.social_media.post_service.models.FlagRequest;
import com.example.social_media.post_service.models.PostRequestModel;
import com.example.social_media.post_service.models.PostResponseModel;
import com.example.social_media.post_service.services.interfaces.IPostService;
import com.example.social_media.post_service.web.api.interfaces.IPostApi;
import com.example.social_media.shared_libs.models.MasterResponse;
import com.example.social_media.shared_libs.models.Status;
import com.example.social_media.shared_libs.web.api.BaseApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostControllerImpl extends BaseApi implements IPostApi {

    private final IPostService postService;

    private <T> ResponseEntity<MasterResponse<T>> buildResponse(T data, HttpStatus status) {
        Status s = Status.builder()
                .statusCode(status.value())
                .message(status.getReasonPhrase())
                .build();

        MasterResponse<T> response = MasterResponse.<T>builder()
                .status(s)
                .data(data)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    // ---------------------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<MasterResponse<PostResponseModel>> createPost(
            @RequestBody PostRequestModel request
    ) {
        PostResponseModel result = postService.createPost(request);
        return buildResponse(result, HttpStatus.CREATED);
    }

    // ---------------------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------------------
    @PutMapping("/{postId}")
    public ResponseEntity<MasterResponse<PostResponseModel>> updatePost(
            @PathVariable String postId,
            @RequestBody PostRequestModel request
    ) {
        PostResponseModel result = postService.updatePost(postId, request);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------------------
    // GET
    // ---------------------------------------------------------------------
    @GetMapping("/{postId}")
    public ResponseEntity<MasterResponse<PostResponseModel>> getPostById(
            @PathVariable String postId
    ) {
        PostResponseModel result = postService.getPostById(postId);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------------
    @DeleteMapping("/{postId}")
    public ResponseEntity<MasterResponse<Void>> deletePost(
            @PathVariable String postId
    ) {
        postService.deletePost(postId);
        return buildResponse(null, HttpStatus.NO_CONTENT);
    }

    // ---------------------------------------------------------------------
    // AUTHOR POSTS
    // ---------------------------------------------------------------------
    @GetMapping("/author/{authorId}")
    public ResponseEntity<MasterResponse<List<PostResponseModel>>> getPostsByAuthor(
            @PathVariable String authorId
    ) {
        List<PostResponseModel> result = postService.getPostsByAuthor(authorId);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------------------
    // ALL POSTS
    // ---------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<MasterResponse<List<PostResponseModel>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<PostResponseModel> result = postService.getAllPosts(page, size);
        return buildResponse(result, HttpStatus.OK);
    }

    // =====================================================================
    // INTERACTIONS: LIKE / UNLIKE
    // =====================================================================

    @PostMapping("/{postId}/like")
    public ResponseEntity<MasterResponse<Void>> likePost(
            @PathVariable String postId
    ) {
        postService.likePost(postId);
        return buildResponse(null, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<MasterResponse<Void>> unlikePost(
            @PathVariable String postId
    ) {
        postService.unlikePost(postId);
        return buildResponse(null, HttpStatus.OK);
    }

    // =====================================================================
    // SHARE (POST only — no "unshare")
    // =====================================================================

    @PostMapping("/{postId}/share")
    public ResponseEntity<MasterResponse<Void>> sharePost(
            @PathVariable String postId
    ) {
        postService.sharePost(postId);
        return buildResponse(null, HttpStatus.OK);
    }

    // =====================================================================
    // MODERATION: PIN / UNPIN
    // =====================================================================

    @PostMapping("/{postId}/pin")
    public ResponseEntity<MasterResponse<Void>> pinPost(
            @PathVariable String postId
    ) {
        postService.pinPost(postId);
        return buildResponse(null, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}/pin")
    public ResponseEntity<MasterResponse<Void>> unpinPost(
            @PathVariable String postId
    ) {
        postService.unpinPost(postId);
        return buildResponse(null, HttpStatus.OK);
    }

    // =====================================================================
    // MODERATION: FLAG / UNFLAG
    // =====================================================================

    @PostMapping("/{postId}/flag")
    public ResponseEntity<MasterResponse<Void>> flagPost(
            @PathVariable String postId,
            @RequestBody FlagRequest flagRequest
    ) {
        postService.flagPost(postId, flagRequest.getReason());
        return buildResponse(null, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}/flag")
    public ResponseEntity<MasterResponse<Void>> unflagPost(
            @PathVariable String postId
    ) {
        postService.unflagPost(postId);
        return buildResponse(null, HttpStatus.OK);
    }

    // =====================================================================
    // COMMENT REFERENCES (used by Comment service)
    // =====================================================================

    @PostMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<MasterResponse<Void>> addCommentId(
            @PathVariable String postId,
            @PathVariable String commentId
    ) {
        postService.addCommentId(postId, commentId);
        return buildResponse(null, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<MasterResponse<Void>> removeCommentId(
            @PathVariable String postId,
            @PathVariable String commentId
    ) {
        postService.removeCommentId(postId, commentId);
        return buildResponse(null, HttpStatus.OK);
    }
}
