package com.example.social_media.comment_service.services;

import com.example.social_media.comment_service.entity.CommentEntity;
import com.example.social_media.comment_service.mappers.CommentModelMapper;
import com.example.social_media.comment_service.models.CommentRequestModel;
import com.example.social_media.comment_service.models.CommentResponseModel;
import com.example.social_media.comment_service.repository.CommentRepository;
import com.example.social_media.comment_service.services.interfaces.ICommentService;
import com.example.social_media.shared_libs.exceptions.BadRequest;
import com.example.social_media.shared_libs.exceptions.NotFound;
import com.example.social_media.shared_libs.services.BaseService;
import com.example.social_media.shared_libs.utils.RestClientUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CommentServiceImpl extends BaseService implements ICommentService {

    private static final String POST_SERVICE_BASE_URL = "http://localhost:4200/api/v1/posts";

    private final CommentRepository commentRepository;
    private final CommentModelMapper mapper;
    private final RestClientUtility restClient;

    public CommentServiceImpl(CommentRepository commentRepository,
                              CommentModelMapper mapper,
                              RestClientUtility restClient) {
        super(restClient);
        this.commentRepository = commentRepository;
        this.mapper = mapper;
        this.restClient = restClient;
    }

    // ---------------------------------------------------------------------
    // UUID VALIDATION (Reusable helper)
    // ---------------------------------------------------------------------
    private UUID toUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (Exception ex) {
            throw new BadRequest("Invalid UUID format: " + id);
        }
    }

    // ---------------------------------------------------------------------
    // Create Comment
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel createComment(CommentRequestModel requestModel) {
        CommentEntity entity = mapper.toEntity(requestModel);
        CommentEntity saved = commentRepository.save(entity);

        restClient.post(
                POST_SERVICE_BASE_URL + "/" + saved.getPostId() + "/comments/" + saved.getId(),
                null, null, Void.class
        );

        return mapper.toResponseModel(saved);
    }

    // ---------------------------------------------------------------------
    // Update Comment
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel updateComment(String commentId, String updatedContent) {
        UUID uuid = toUUID(commentId);

        CommentEntity entity = commentRepository.findById(uuid)
                .orElseThrow(() -> new NotFound("Comment not found"));

        mapper.updateEntityFromRequest(entity, updatedContent);
        commentRepository.save(entity);

        return mapper.toResponseModel(entity);
    }

    // ---------------------------------------------------------------------
    // Delete Comment
    // ---------------------------------------------------------------------
    @Override
    public void deleteComment(String commentId) {
        UUID uuid = toUUID(commentId);

        CommentEntity entity = commentRepository.findById(uuid)
                .orElseThrow(() -> new NotFound("Comment not found"));

        entity.setDeleted(true);
        commentRepository.save(entity);

        restClient.delete(
                POST_SERVICE_BASE_URL + "/" + entity.getPostId() + "/comments/" + uuid,
                null, Void.class
        );
    }

    // ---------------------------------------------------------------------
    // Get Comment
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel getCommentById(String commentId) {
        UUID uuid = toUUID(commentId);

        CommentEntity entity = commentRepository.findById(uuid)
                .orElseThrow(() -> new NotFound("Comment not found"));

        return mapper.toResponseModel(entity);
    }

    // ---------------------------------------------------------------------
    // Get Comments For Post
    // ---------------------------------------------------------------------
    @Override
    public List<CommentResponseModel> getCommentsForPost(String postId) {
        UUID postUUID = toUUID(postId);

        return commentRepository.findByPostIdOrderByCreatedAtAsc(postUUID)
                .stream()
                .map(mapper::toResponseModel)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // Add Reply ID to Parent Comment
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel addReply(String postId, String parentCommentId, String replyId) {
        UUID postUUID = toUUID(postId);
        UUID parentUUID = toUUID(parentCommentId);
        UUID replyUUID = toUUID(replyId);

        CommentEntity parent = commentRepository.findById(parentUUID)
                .orElseThrow(() -> new NotFound("Parent comment not found"));

        if (!parent.getPostId().equals(postUUID)) {
            throw new BadRequest("PostId does not match parent comment's postId");
        }

        List<UUID> replyIds = parent.getReplyIds();
        if (replyIds == null) replyIds = new ArrayList<>();

        if (!replyIds.contains(replyUUID)) {
            replyIds.add(replyUUID);
        }

        parent.setReplyIds(replyIds);
        commentRepository.save(parent);

        return mapper.toResponseModel(parent);
    }

    // ---------------------------------------------------------------------
    // Remove Reply ID
    // ---------------------------------------------------------------------
    @Override
    public void removeReply(String postId, String parentCommentId, String replyId) {
        UUID postUUID = toUUID(postId);
        UUID parentUUID = toUUID(parentCommentId);
        UUID replyUUID = toUUID(replyId);

        CommentEntity parent = commentRepository.findById(parentUUID)
                .orElseThrow(() -> new NotFound("Parent comment not found"));

        if (!parent.getPostId().equals(postUUID)) {
            throw new BadRequest("PostId does not match parent comment's postId");
        }

        List<UUID> replyIds = parent.getReplyIds();
        if (replyIds == null || !replyIds.contains(replyUUID)) {
            throw new NotFound("Reply ID not found in parent comment");
        }

        replyIds.remove(replyUUID);
        parent.setReplyIds(replyIds.isEmpty() ? null : replyIds);

        commentRepository.save(parent);
    }

    // ---------------------------------------------------------------------
    // Get Replies
    // ---------------------------------------------------------------------
    @Override
    public List<CommentResponseModel> getReplies(String parentCommentId) {
        UUID uuid = toUUID(parentCommentId);

        return commentRepository.findReplies(uuid)
                .stream()
                .map(mapper::toResponseModel)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // Get Comments by Author
    // ---------------------------------------------------------------------
    @Override
    public List<CommentResponseModel> getCommentsByAuthor(String authorId) {
        return commentRepository.findByAuthorId(authorId)
                .stream()
                .map(mapper::toResponseModel)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // Count Comments For Post
    // ---------------------------------------------------------------------
    @Override
    public int countCommentsForPost(String postId) {
        UUID uuid = toUUID(postId);
        return commentRepository.countByPostId(uuid);
    }

    // ---------------------------------------------------------------------
    // Moderation: Pin / Unpin
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel pinComment(String commentId) {
        UUID uuid = toUUID(commentId);

        CommentEntity entity = commentRepository.findById(uuid)
                .orElseThrow(() -> new NotFound("Comment not found"));

        entity.setPinned(true);
        commentRepository.save(entity);
        return mapper.toResponseModel(entity);
    }

    @Override
    public CommentResponseModel unpinComment(String commentId) {
        UUID uuid = toUUID(commentId);

        CommentEntity entity = commentRepository.findById(uuid)
                .orElseThrow(() -> new NotFound("Comment not found"));

        entity.setPinned(false);
        commentRepository.save(entity);
        return mapper.toResponseModel(entity);
    }

    // ---------------------------------------------------------------------
    // Moderation: Flag / Remove Flag
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel flagComment(String commentId, String reason) {
        UUID uuid = toUUID(commentId);

        CommentEntity entity = commentRepository.findById(uuid)
                .orElseThrow(() -> new NotFound("Comment not found"));

        entity.setFlagged(true);
        entity.setFlaggedReason(reason);
        commentRepository.save(entity);

        return mapper.toResponseModel(entity);
    }

    @Override
    public CommentResponseModel removeFlag(String commentId) {
        UUID uuid = toUUID(commentId);

        CommentEntity entity = commentRepository.findById(uuid)
                .orElseThrow(() -> new NotFound("Comment not found"));

        entity.setFlagged(false);
        entity.setFlaggedReason(null);
        commentRepository.save(entity);

        return mapper.toResponseModel(entity);
    }

    // ---------------------------------------------------------------------
    // Likes / Unlikes
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel likeComment(String commentId) {
        UUID uuid = toUUID(commentId);

        CommentEntity entity = commentRepository.findById(uuid)
                .orElseThrow(() -> new NotFound("Comment not found"));

        entity.setLikesCount(entity.getLikesCount() + 1);
        commentRepository.save(entity);

        return mapper.toResponseModel(entity);
    }

    @Override
    public CommentResponseModel unlikeComment(String commentId) {
        UUID uuid = toUUID(commentId);

        CommentEntity entity = commentRepository.findById(uuid)
                .orElseThrow(() -> new NotFound("Comment not found"));

        if (entity.getLikesCount() > 0) {
            entity.setLikesCount(entity.getLikesCount() - 1);
        }

        commentRepository.save(entity);
        return mapper.toResponseModel(entity);
    }
}
