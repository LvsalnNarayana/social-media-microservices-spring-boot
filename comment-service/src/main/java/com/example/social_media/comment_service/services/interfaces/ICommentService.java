package com.example.social_media.comment_service.services.interfaces;

import com.example.social_media.comment_service.models.CommentRequestModel;
import com.example.social_media.comment_service.models.CommentResponseModel;

import java.util.List;

public interface ICommentService {

    // Create a new comment
    CommentResponseModel createComment(CommentRequestModel requestModel);

    // Update an existing comment
    CommentResponseModel updateComment(String commentId, String updatedContent);

    // Soft delete a comment
    void deleteComment(String commentId);

    // Fetch a comment by ID
    CommentResponseModel getCommentById(String commentId);

    // Fetch all comments belonging to a post
    List<CommentResponseModel> getCommentsForPost(String postId);

    // Add a reply reference (replyId) to a comment
    CommentResponseModel addReply(String postId,
                                  String parentCommentId,
                                  String replyId);

    // Remove a reply reference
    void removeReply(String postId,
                     String parentCommentId,
                     String replyId);

    // Fetch all replies of a parent comment
    List<CommentResponseModel> getReplies(String parentCommentId);

    // Fetch all comments posted by an author
    List<CommentResponseModel> getCommentsByAuthor(String authorId);

    // Count comments for a post
    int countCommentsForPost(String postId);


    /* ===========================
       Moderation & Interaction
       =========================== */

    // Pin a comment
    CommentResponseModel pinComment(String commentId);

    // Unpin a comment
    CommentResponseModel unpinComment(String commentId);

    // Flag a comment with a reason
    CommentResponseModel flagComment(String commentId, String reason);

    // Remove flagged status
    CommentResponseModel removeFlag(String commentId);

    // Increase like count
    CommentResponseModel likeComment(String commentId);

    // Decrease like count (ensure non-negative)
    CommentResponseModel unlikeComment(String commentId);
}
