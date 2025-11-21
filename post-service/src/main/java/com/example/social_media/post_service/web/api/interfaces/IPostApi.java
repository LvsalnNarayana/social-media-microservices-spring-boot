package com.example.social_media.post_service.web.api.interfaces;

import com.example.social_media.post_service.models.FlagRequest;
import com.example.social_media.post_service.models.PostRequestModel;
import com.example.social_media.post_service.models.PostResponseModel;
import com.example.social_media.shared_libs.constants.BaseUrlConstants;
import com.example.social_media.shared_libs.models.MasterResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/*
 *  POST   /api/v1/posts
 *  PUT    /api/v1/posts/{postId}
 *  GET    /api/v1/posts/{postId}
 *  DELETE /api/v1/posts/{postId}
 *
 *  Additional operations based on authors, likes, flags, comments, etc.
 */
@RequestMapping(BaseUrlConstants.BASE_URL + "/posts")
public interface IPostApi {

    ResponseEntity<MasterResponse<PostResponseModel>> createPost(
            PostRequestModel request
    );

    ResponseEntity<MasterResponse<PostResponseModel>> updatePost(
            String postId,
            PostRequestModel request
    );

    ResponseEntity<MasterResponse<PostResponseModel>> getPostById(
            String postId
    );

    ResponseEntity<MasterResponse<Void>> deletePost(
            String postId
    );

    ResponseEntity<MasterResponse<List<PostResponseModel>>> getPostsByAuthor(
            String authorId
    );

    ResponseEntity<MasterResponse<List<PostResponseModel>>> getAllPosts(
            int page,
            int size
    );

    ResponseEntity<MasterResponse<Void>> likePost(
            String postId
    );

    ResponseEntity<MasterResponse<Void>> unlikePost(
            String postId
    );

    ResponseEntity<MasterResponse<Void>> sharePost(
            String postId
    );

    ResponseEntity<MasterResponse<Void>> pinPost(
            String postId
    );

    ResponseEntity<MasterResponse<Void>> unpinPost(
            String postId
    );

    ResponseEntity<MasterResponse<Void>> flagPost(
            String postId,
            FlagRequest flagRequest
    );

    ResponseEntity<MasterResponse<Void>> unflagPost(
            String postId
    );

    ResponseEntity<MasterResponse<Void>> addCommentId(
            String postId,
            String commentId
    );

    ResponseEntity<MasterResponse<Void>> removeCommentId(
            String postId,
            String commentId
    );
}
