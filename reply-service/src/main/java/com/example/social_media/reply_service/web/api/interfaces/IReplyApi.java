package com.example.social_media.reply_service.web.api.interfaces;

import com.example.social_media.reply_service.models.FlagRequest;
import com.example.social_media.reply_service.models.ReplyRequestModel;
import com.example.social_media.reply_service.models.ReplyResponseModel;
import com.example.social_media.shared_libs.models.MasterResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IReplyApi {

    // ---------------------------------------------------------
    // Core Reply CRUD
    // ---------------------------------------------------------

    ResponseEntity<MasterResponse<ReplyResponseModel>> createReply(
            ReplyRequestModel requestModel
    );

    ResponseEntity<MasterResponse<ReplyResponseModel>> updateReply(
            String replyId,
            String updatedContent
    );

    ResponseEntity<MasterResponse<Void>> deleteReply(
            String replyId
    );

    ResponseEntity<MasterResponse<ReplyResponseModel>> getReplyById(
            String replyId
    );

    // ---------------------------------------------------------
    // Fetch Replies
    // ---------------------------------------------------------

    ResponseEntity<MasterResponse<List<ReplyResponseModel>>> getRepliesForComment(
            String parentCommentId
    );

    ResponseEntity<MasterResponse<List<ReplyResponseModel>>> getRepliesForPost(
            String postId
    );

    ResponseEntity<MasterResponse<List<ReplyResponseModel>>> getRepliesByAuthor(
            String authorId
    );

    // ---------------------------------------------------------
    // Moderation Endpoints
    // ---------------------------------------------------------

    ResponseEntity<MasterResponse<ReplyResponseModel>> flagReply(
            String replyId,
            FlagRequest flagRequest
    );

    ResponseEntity<MasterResponse<ReplyResponseModel>> removeFlag(
            String replyId
    );

    ResponseEntity<MasterResponse<ReplyResponseModel>> likeReply(
            String replyId
    );

    ResponseEntity<MasterResponse<ReplyResponseModel>> unlikeReply(
            String replyId
    );

    // ---------------------------------------------------------
    // Analytics / Count APIs
    // ---------------------------------------------------------

    ResponseEntity<MasterResponse<Integer>> countRepliesForComment(
            String parentCommentId
    );

    ResponseEntity<MasterResponse<Integer>> countRepliesForPost(
            String postId
    );

    ResponseEntity<MasterResponse<Integer>> countRepliesByAuthor(
            String authorId
    );
}
