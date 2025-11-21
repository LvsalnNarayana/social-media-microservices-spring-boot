package com.example.social_media.reply_service.web.api;

import com.example.social_media.reply_service.models.FlagRequest;
import com.example.social_media.reply_service.models.ReplyRequestModel;
import com.example.social_media.reply_service.models.ReplyResponseModel;
import com.example.social_media.reply_service.services.interfaces.IReplyService;
import com.example.social_media.reply_service.web.api.interfaces.IReplyApi;
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
@RequestMapping(BaseUrlConstants.BASE_URL + "/reply")
public class ReplyApiImpl extends BaseApi implements IReplyApi {

    private final IReplyService replyService;

    // ---------------------------------------------------------
    // RESPONSE WRAPPER
    // ---------------------------------------------------------
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
    // CREATE REPLY
    // ---------------------------------------------------------
    @Override
    @PostMapping
    public ResponseEntity<MasterResponse<ReplyResponseModel>> createReply(
            @RequestBody ReplyRequestModel requestModel
    ) {
        ReplyResponseModel result = replyService.createReply(requestModel);
        return buildResponse(result, HttpStatus.CREATED);
    }

    // ---------------------------------------------------------
    // UPDATE REPLY
    // ---------------------------------------------------------
    @Override
    @PutMapping("/{replyId}")
    public ResponseEntity<MasterResponse<ReplyResponseModel>> updateReply(
            @PathVariable("replyId") String replyId,
            @RequestBody String updatedContent
    ) {
        ReplyResponseModel result = replyService.updateReply(replyId, updatedContent);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------
    // DELETE REPLY (Soft Delete)
    // ---------------------------------------------------------
    @Override
    @DeleteMapping("/{replyId}")
    public ResponseEntity<MasterResponse<Void>> deleteReply(
            @PathVariable("replyId") String replyId
    ) {
        replyService.deleteReply(replyId);
        return buildResponse(null, HttpStatus.NO_CONTENT);
    }

    // ---------------------------------------------------------
    // GET REPLY BY ID
    // ---------------------------------------------------------
    @Override
    @GetMapping("/{replyId}")
    public ResponseEntity<MasterResponse<ReplyResponseModel>> getReplyById(
            @PathVariable("replyId") String replyId
    ) {
        ReplyResponseModel result = replyService.getReplyById(replyId);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------
    // GET REPLIES FOR COMMENT
    // ---------------------------------------------------------
    @Override
    @GetMapping("/comment/{parentCommentId}")
    public ResponseEntity<MasterResponse<List<ReplyResponseModel>>> getRepliesForComment(
            @PathVariable("parentCommentId") String parentCommentId
    ) {
        List<ReplyResponseModel> result = replyService.getRepliesForComment(parentCommentId);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------
    // GET REPLIES FOR POST
    // ---------------------------------------------------------
    @Override
    @GetMapping("/post/{postId}")
    public ResponseEntity<MasterResponse<List<ReplyResponseModel>>> getRepliesForPost(
            @PathVariable("postId") String postId
    ) {
        List<ReplyResponseModel> result = replyService.getRepliesForPost(postId);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------
    // GET REPLIES BY AUTHOR
    // ---------------------------------------------------------
    @Override
    @GetMapping("/author/{authorId}")
    public ResponseEntity<MasterResponse<List<ReplyResponseModel>>> getRepliesByAuthor(
            @PathVariable("authorId") String authorId
    ) {
        List<ReplyResponseModel> result = replyService.getRepliesByAuthor(authorId);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------
    // FLAG REPLY
    // ---------------------------------------------------------
    @Override
    @PostMapping("/{replyId}/flag")
    public ResponseEntity<MasterResponse<ReplyResponseModel>> flagReply(
            @PathVariable("replyId") String replyId,
            @RequestBody FlagRequest flagRequest
    ) {
        ReplyResponseModel result = replyService.flagReply(replyId, flagRequest.getReason());
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------
    // REMOVE FLAG
    // ---------------------------------------------------------
    @Override
    @DeleteMapping("/{replyId}/flag")
    public ResponseEntity<MasterResponse<ReplyResponseModel>> removeFlag(
            @PathVariable("replyId") String replyId
    ) {
        ReplyResponseModel result = replyService.removeFlag(replyId);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------
    // LIKE REPLY
    // ---------------------------------------------------------
    @Override
    @PostMapping("/{replyId}/like")
    public ResponseEntity<MasterResponse<ReplyResponseModel>> likeReply(
            @PathVariable("replyId") String replyId
    ) {
        ReplyResponseModel result = replyService.likeReply(replyId);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------
    // UNLIKE REPLY
    // ---------------------------------------------------------
    @Override
    @DeleteMapping("/{replyId}/like")
    public ResponseEntity<MasterResponse<ReplyResponseModel>> unlikeReply(
            @PathVariable("replyId") String replyId
    ) {
        ReplyResponseModel result = replyService.unlikeReply(replyId);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------
    // COUNT REPLIES FOR COMMENT
    // ---------------------------------------------------------
    @Override
    @GetMapping("/comment/{parentCommentId}/count")
    public ResponseEntity<MasterResponse<Integer>> countRepliesForComment(
            @PathVariable("parentCommentId") String parentCommentId
    ) {
        int result = replyService.countRepliesForComment(parentCommentId);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------
    // COUNT REPLIES FOR POST
    // ---------------------------------------------------------
    @Override
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<MasterResponse<Integer>> countRepliesForPost(
            @PathVariable("postId") String postId
    ) {
        int result = replyService.countRepliesForPost(postId);
        return buildResponse(result, HttpStatus.OK);
    }

    // ---------------------------------------------------------
    // COUNT REPLIES BY AUTHOR
    // ---------------------------------------------------------
    @Override
    @GetMapping("/author/{authorId}/count")
    public ResponseEntity<MasterResponse<Integer>> countRepliesByAuthor(
            @PathVariable("authorId") String authorId
    ) {
        int result = replyService.countRepliesByAuthor(authorId);
        return buildResponse(result, HttpStatus.OK);
    }
}
