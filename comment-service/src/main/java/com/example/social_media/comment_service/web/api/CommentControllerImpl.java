package com.example.social_media.comment_service.web.api;

import com.example.social_media.comment_service.models.CommentRequestModel;
import com.example.social_media.comment_service.models.CommentResponseModel;
import com.example.social_media.comment_service.models.FlagRequest;
import com.example.social_media.comment_service.services.interfaces.ICommentService;
import com.example.social_media.comment_service.web.api.interfaces.ICommentApi;
import com.example.social_media.shared_libs.constants.BaseUrlConstants;
import com.example.social_media.shared_libs.models.MasterResponse;
import com.example.social_media.shared_libs.models.Status;
import com.example.social_media.shared_libs.web.api.BaseApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(BaseUrlConstants.BASE_URL + "/comments")
public class CommentControllerImpl extends BaseApi implements ICommentApi {

    private final ICommentService commentService;

    // ----------------------------
    // RESPONSE WRAPPER
    // ----------------------------
    private <T> ResponseEntity<MasterResponse<T>> buildResponse(T data, HttpStatus status) {
        Status st = Status.builder()
                .statusCode(status.value())
                .message(status.getReasonPhrase())
                .build();

        MasterResponse<T> response = MasterResponse.<T>builder()
                .status(st)
                .data(data)
                .build();

        return ResponseEntity.status(status).body(response);
    }


    // ---------------------------------------------------------
    // CREATE COMMENT
    // ---------------------------------------------------------
    @Override
    @PostMapping
    public ResponseEntity<MasterResponse<CommentResponseModel>> createComment(
            @RequestBody CommentRequestModel requestModel
    ) {
        CommentResponseModel result = commentService.createComment(requestModel);
        return buildResponse(result, HttpStatus.CREATED);
    }


    // ---------------------------------------------------------
    // UPDATE COMMENT
    // ---------------------------------------------------------
    @Override
    @PutMapping("/{commentId}")
    public ResponseEntity<MasterResponse<CommentResponseModel>> updateComment(
            @PathVariable("commentId") String commentId,
            @RequestBody String updatedContent
    ) {
        CommentResponseModel result = commentService.updateComment(commentId, updatedContent);
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // DELETE COMMENT
    // ---------------------------------------------------------
    @Override
    @DeleteMapping("/{commentId}")
    public ResponseEntity<MasterResponse<Void>> deleteComment(
            @PathVariable("commentId") String commentId
    ) {
        commentService.deleteComment(commentId);
        return buildResponse(null, HttpStatus.NO_CONTENT);
    }


    // ---------------------------------------------------------
    // GET COMMENT BY ID
    // ---------------------------------------------------------
    @Override
    @GetMapping("/{commentId}")
    public ResponseEntity<MasterResponse<CommentResponseModel>> getCommentById(
            @PathVariable("commentId") String commentId
    ) {
        CommentResponseModel result = commentService.getCommentById(commentId);
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // GET COMMENTS FOR POST
    // ---------------------------------------------------------
    @Override
    @GetMapping("/post/{postId}")
    public ResponseEntity<MasterResponse<List<CommentResponseModel>>> getCommentsForPost(
            @PathVariable("postId") String postId
    ) {
        List<CommentResponseModel> result = commentService.getCommentsForPost(postId);
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // GET COMMENTS BY AUTHOR
    // ---------------------------------------------------------
    @Override
    @GetMapping("/author/{authorId}")
    public ResponseEntity<MasterResponse<List<CommentResponseModel>>> getCommentsByAuthor(
            @PathVariable("authorId") String authorId
    ) {
        List<CommentResponseModel> result = commentService.getCommentsByAuthor(authorId);
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // COUNT COMMENTS FOR POST
    // ---------------------------------------------------------
    @Override
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<MasterResponse<Integer>> countCommentsForPost(
            @PathVariable("postId") String postId
    ) {
        int result = commentService.countCommentsForPost(postId);
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // ADD REPLY ID
    // ---------------------------------------------------------
    @Override
    @PostMapping("/{postId}/comments/{commentId}/replies/{replyId}")
    public ResponseEntity<MasterResponse<CommentResponseModel>> addReply(
            @PathVariable("postId") String postId,
            @PathVariable("commentId") String commentId,
            @PathVariable("replyId") String replyId
    ) {
        CommentResponseModel result = commentService.addReply(postId, commentId, replyId);
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // REMOVE REPLY ID
    // ---------------------------------------------------------
    @Override
    @DeleteMapping("/{postId}/comments/{commentId}/replies/{replyId}")
    public ResponseEntity<MasterResponse<Void>> removeReply(
            @PathVariable("postId") String postId,
            @PathVariable("commentId") String commentId,
            @PathVariable("replyId") String replyId
    ) {
        commentService.removeReply(postId, commentId, replyId);
        return buildResponse(null, HttpStatus.NO_CONTENT);
    }


    // ---------------------------------------------------------
    // GET REPLIES
    // ---------------------------------------------------------
    @Override
    @GetMapping("/{parentCommentId}/replies")
    public ResponseEntity<MasterResponse<List<CommentResponseModel>>> getReplies(
            @PathVariable("parentCommentId") String parentCommentId
    ) {
        List<CommentResponseModel> result = commentService.getReplies(parentCommentId);
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // PIN COMMENT
    // ---------------------------------------------------------
    @Override
    @PostMapping("/{commentId}/pin")
    public ResponseEntity<MasterResponse<CommentResponseModel>> pinComment(
            @PathVariable("commentId") String commentId
    ) {
        CommentResponseModel result = commentService.pinComment(commentId);
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // UNPIN COMMENT
    // ---------------------------------------------------------
    @Override
    @DeleteMapping("/{commentId}/pin")
    public ResponseEntity<MasterResponse<CommentResponseModel>> unpinComment(
            @PathVariable("commentId") String commentId
    ) {
        CommentResponseModel result = commentService.unpinComment(commentId);
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // FLAG COMMENT
    // ---------------------------------------------------------
    @Override
    @PostMapping("/{commentId}/flag")
    public ResponseEntity<MasterResponse<CommentResponseModel>> flagComment(
            @PathVariable("commentId") String commentId,
            @RequestBody FlagRequest flagRequest
    ) {
        CommentResponseModel result = commentService.flagComment(commentId, flagRequest.getReason());
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // REMOVE FLAG
    // ---------------------------------------------------------
    @Override
    @DeleteMapping("/{commentId}/flag")
    public ResponseEntity<MasterResponse<CommentResponseModel>> removeFlag(
            @PathVariable("commentId") String commentId
    ) {
        CommentResponseModel result = commentService.removeFlag(commentId);
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // LIKE COMMENT
    // ---------------------------------------------------------
    @Override
    @PostMapping("/{commentId}/like")
    public ResponseEntity<MasterResponse<CommentResponseModel>> likeComment(
            @PathVariable("commentId") String commentId
    ) {
        CommentResponseModel result = commentService.likeComment(commentId);
        return buildResponse(result, HttpStatus.OK);
    }


    // ---------------------------------------------------------
    // UNLIKE COMMENT
    // ---------------------------------------------------------
    @Override
    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<MasterResponse<CommentResponseModel>> unlikeComment(
            @PathVariable("commentId") String commentId
    ) {
        CommentResponseModel result = commentService.unlikeComment(commentId);
        return buildResponse(result, HttpStatus.OK);
    }
}
