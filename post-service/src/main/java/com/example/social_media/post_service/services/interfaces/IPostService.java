package com.example.social_media.post_service.services.interfaces;

import com.example.social_media.post_service.models.PostRequestModel;
import com.example.social_media.post_service.models.PostResponseModel;

import java.util.List;

public interface IPostService {

    // Create a post
    PostResponseModel createPost(PostRequestModel request);

    // Update an existing post
    PostResponseModel updatePost(String postId, PostRequestModel request);

    // Get post by ID
    PostResponseModel getPostById(String postId);

    // Delete post
    void deletePost(String postId);

    // Get posts created by a specific author
    List<PostResponseModel> getPostsByAuthor(String authorId);

    // Get all posts (with pagination)
    List<PostResponseModel> getAllPosts(int page, int size);

    // Like a post
    void likePost(String postId);

    // Unlike a post
    void unlikePost(String postId);

    // Share a post
    void sharePost(String postId);

    // Pin a post
    void pinPost(String postId);

    // Unpin a post
    void unpinPost(String postId);

    // Flag a post
    void flagPost(String postId, String reason);

    // Remove flag from a post
    void unflagPost(String postId);

    // Add a comment ID (comment service will send ID)
    void addCommentId(String postId, String commentId);

    // Remove a comment ID (when comment is deleted)
    void removeCommentId(String postId, String commentId);
}
