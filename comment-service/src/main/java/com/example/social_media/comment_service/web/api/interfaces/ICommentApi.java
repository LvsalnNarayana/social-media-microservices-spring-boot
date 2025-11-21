package com.example.social_media.comment_service.web.api.interfaces;

import com.example.social_media.comment_service.models.CommentRequestModel;
import com.example.social_media.comment_service.models.CommentResponseModel;
import com.example.social_media.comment_service.models.FlagRequest;
import com.example.social_media.shared_libs.models.MasterResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICommentApi {

    // Create a comment
    ResponseEntity<MasterResponse<CommentResponseModel>> createComment(
            CommentRequestModel requestModel
    );

    // Update a comment
    ResponseEntity<MasterResponse<CommentResponseModel>> updateComment(
            String commentId,
            String updatedContent
    );

    // Soft delete a comment
    ResponseEntity<MasterResponse<Void>> deleteComment(
            String commentId
    );

    // Get a single comment
    ResponseEntity<MasterResponse<CommentResponseModel>> getCommentById(
            String commentId
    );

    // Get all comments under a post
    ResponseEntity<MasterResponse<List<CommentResponseModel>>> getCommentsForPost(
            String postId
    );

    // Add replyId to a parent comment
    ResponseEntity<MasterResponse<CommentResponseModel>> addReply(
            String postId,
            String parentCommentId,
            String replyId
    );

    // Remove replyId from a parent comment
    ResponseEntity<MasterResponse<Void>> removeReply(
            String postId,
            String parentCommentId,
            String replyId
    );

    // Get replies for a specific comment
    ResponseEntity<MasterResponse<List<CommentResponseModel>>> getReplies(
            String parentCommentId
    );

    // Get all comments authored by a user
    ResponseEntity<MasterResponse<List<CommentResponseModel>>> getCommentsByAuthor(
            String authorId
    );

    // Count comments under a post
    ResponseEntity<MasterResponse<Integer>> countCommentsForPost(
            String postId
    );


    // ---------------------------------------------------------
    // Moderation
    // ---------------------------------------------------------

    // Pin comment
    ResponseEntity<MasterResponse<CommentResponseModel>> pinComment(
            String commentId
    );

    // Unpin comment
    ResponseEntity<MasterResponse<CommentResponseModel>> unpinComment(
            String commentId
    );

    // Flag comment
    ResponseEntity<MasterResponse<CommentResponseModel>> flagComment(
            String commentId,
            FlagRequest flagRequest
    );

    // Remove flag from comment
    ResponseEntity<MasterResponse<CommentResponseModel>> removeFlag(
            String commentId
    );

    // Like comment
    ResponseEntity<MasterResponse<CommentResponseModel>> likeComment(
            String commentId
    );

    // Unlike comment
    ResponseEntity<MasterResponse<CommentResponseModel>> unlikeComment(
            String commentId
    );
}
