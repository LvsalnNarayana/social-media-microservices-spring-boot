package com.example.social_media.reply_service.services.interfaces;

import com.example.social_media.reply_service.models.ReplyRequestModel;
import com.example.social_media.reply_service.models.ReplyResponseModel;

import java.util.List;

public interface IReplyService {

    // ---------------------------------------------------------
    // Core CRUD
    // ---------------------------------------------------------

    // Create a reply
    ReplyResponseModel createReply(ReplyRequestModel requestModel);

    // Update reply content
    ReplyResponseModel updateReply(String replyId, String updatedContent);

    // Soft delete reply
    void deleteReply(String replyId);

    // Get a single reply
    ReplyResponseModel getReplyById(String replyId);

    // ---------------------------------------------------------
    // Fetch Replies
    // ---------------------------------------------------------

    // All replies under a comment
    List<ReplyResponseModel> getRepliesForComment(String parentCommentId);

    // All replies under a post
    List<ReplyResponseModel> getRepliesForPost(String postId);

    // All replies by an author
    List<ReplyResponseModel> getRepliesByAuthor(String authorId);

    // ---------------------------------------------------------
    // Moderation
    // ---------------------------------------------------------

    // Flag a reply
    ReplyResponseModel flagReply(String replyId, String reason);

    // Remove flag
    ReplyResponseModel removeFlag(String replyId);

    // Like a reply
    ReplyResponseModel likeReply(String replyId);

    // Unlike a reply
    ReplyResponseModel unlikeReply(String replyId);

    // ---------------------------------------------------------
    // Analytics / Counts
    // ---------------------------------------------------------

    int countRepliesForComment(String parentCommentId);

    int countRepliesForPost(String postId);

    int countRepliesByAuthor(String authorId);
}
