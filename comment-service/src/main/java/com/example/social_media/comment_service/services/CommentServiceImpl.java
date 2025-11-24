package com.example.social_media.comment_service.services;

import com.example.social_media.comment_service.entity.CommentEntity;
import com.example.social_media.comment_service.mappers.CommentModelMapper;
import com.example.social_media.comment_service.models.CommentRequestModel;
import com.example.social_media.comment_service.models.CommentResponseModel;
import com.example.social_media.comment_service.repository.CommentRepository;
import com.example.social_media.comment_service.services.interfaces.ICommentService;
import com.example.social_media.shared_libs.builders.LogEventBuilder;
import com.example.social_media.shared_libs.exceptions.BadRequest;
import com.example.social_media.shared_libs.exceptions.NotFound;
import com.example.social_media.shared_libs.kafka.KafkaProducer;
import com.example.social_media.shared_libs.models.LogEvent;
import com.example.social_media.shared_libs.services.BaseService;
import com.example.social_media.shared_libs.utils.RestClientUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final KafkaProducer kafkaProducer;

    private final String commentKafkaStringTopic;
    private final String commentKafkaJsonTopic;
    private final String commentKafkaErrorStringTopic;
    private final String commentKafkaErrorJsonTopic;

    public CommentServiceImpl(
            KafkaProducer kafkaProducer,
            CommentRepository commentRepository,
            CommentModelMapper mapper,
            RestClientUtility restClient,
            @Value("${logging.topic-strings}") String commentKafkaStringTopic,
            @Value("${logging.topic-json}") String commentKafkaJsonTopic,
            @Value("${logging.error.topic-strings}") String commentKafkaErrorStringTopic,
            @Value("${logging.error.topic-json}") String commentKafkaErrorJsonTopic
    ) {
        super(restClient);
        this.kafkaProducer = kafkaProducer;
        this.commentRepository = commentRepository;
        this.mapper = mapper;
        this.restClient = restClient;
        this.commentKafkaStringTopic = commentKafkaStringTopic;
        this.commentKafkaJsonTopic = commentKafkaJsonTopic;
        this.commentKafkaErrorStringTopic = commentKafkaErrorStringTopic;
        this.commentKafkaErrorJsonTopic = commentKafkaErrorJsonTopic;
    }

    // ---------------------------------------------------------------------
    // LOGGING HELPERS (similar to PostServiceImpl)
    // ---------------------------------------------------------------------
    private void logInfo(String message) {
        LogEvent event = new LogEventBuilder("INFO", message)
                .service("post-service")
                .logger(CommentServiceImpl.class)
                .tags(List.of("post-service"))
                .build();

        kafkaProducer.publishEvent(commentKafkaJsonTopic, event);
        kafkaProducer.infoRaw(commentKafkaStringTopic, message);
    }

    private void logError(String message, Exception ex) {
        LogEvent event = new LogEventBuilder("ERROR", message)
                .service("post-service")
                .logger(CommentServiceImpl.class)
                .tags(List.of("post-service", "error"))
                .exception(ex)
                .build();

        kafkaProducer.errorEvent(commentKafkaErrorJsonTopic, event);
        kafkaProducer.errorRaw(commentKafkaErrorStringTopic, message + " | ex=" + ex.getMessage());
    }

    // ---------------------------------------------------------------------
    // UUID VALIDATION (renamed to parseToUUID)
    // ---------------------------------------------------------------------
    private UUID parseToUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (Exception ex) {
            logError("Invalid UUID: " + id, ex);
            throw new BadRequest("Invalid UUID format: " + id);
        }
    }

    // ---------------------------------------------------------------------
    // Create Comment
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel createComment(CommentRequestModel requestModel) {
        try {
            logInfo("Creating comment for post=" + requestModel.getPostId());

            CommentEntity entity = mapper.toEntity(requestModel);
            CommentEntity saved = commentRepository.save(entity);

            restClient.post(
                    POST_SERVICE_BASE_URL + "/" + saved.getPostId() + "/comments/" + saved.getId(),
                    null, null, Void.class
            );

            return mapper.toResponseModel(saved);

        } catch (Exception ex) {
            logError("Failed to create comment", ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Update Comment
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel updateComment(String commentId, String updatedContent) {
        try {
            logInfo("Updating comment=" + commentId);

            UUID uuid = parseToUUID(commentId);

            CommentEntity entity = commentRepository.findById(uuid)
                    .orElseThrow(() -> new NotFound("Comment not found"));

            mapper.updateEntityFromRequest(entity, updatedContent);
            commentRepository.save(entity);

            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to update comment=" + commentId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Delete Comment
    // ---------------------------------------------------------------------
    @Override
    public void deleteComment(String commentId) {
        try {
            logInfo("Deleting comment=" + commentId);

            UUID uuid = parseToUUID(commentId);

            CommentEntity entity = commentRepository.findById(uuid)
                    .orElseThrow(() -> new NotFound("Comment not found"));

            entity.setDeleted(true);
            commentRepository.save(entity);

            restClient.delete(
                    POST_SERVICE_BASE_URL + "/" + entity.getPostId() + "/comments/" + uuid,
                    null, Void.class
            );

        } catch (Exception ex) {
            logError("Failed to delete comment=" + commentId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Get Comment by ID
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel getCommentById(String commentId) {
        try {
            logInfo("Fetching comment=" + commentId);

            UUID uuid = parseToUUID(commentId);

            CommentEntity entity = commentRepository.findById(uuid)
                    .orElseThrow(() -> new NotFound("Comment not found"));

            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to fetch comment=" + commentId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Get Comments For Post
    // ---------------------------------------------------------------------
    @Override
    public List<CommentResponseModel> getCommentsForPost(String postId) {
        try {
            logInfo("Fetching comments for post=" + postId);

            UUID postUUID = parseToUUID(postId);

            return commentRepository.findByPostIdOrderByCreatedAtAsc(postUUID)
                    .stream()
                    .map(mapper::toResponseModel)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            logError("Failed to fetch comments for post=" + postId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Add Reply ID
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel addReply(String postId, String parentCommentId, String replyId) {
        try {
            logInfo("Adding reply=" + replyId + " to parent=" + parentCommentId);

            UUID postUUID = parseToUUID(postId);
            UUID parentUUID = parseToUUID(parentCommentId);
            UUID replyUUID = parseToUUID(replyId);

            CommentEntity parent = commentRepository.findById(parentUUID)
                    .orElseThrow(() -> new NotFound("Parent comment not found"));

            if (!parent.getPostId().equals(postUUID)) {
                throw new BadRequest("PostId mismatch");
            }

            List<UUID> replyIds = parent.getReplyIds();
            if (replyIds == null) {
                replyIds = new ArrayList<>();
            }

            if (!replyIds.contains(replyUUID)) {
                replyIds.add(replyUUID);
            }

            parent.setReplyIds(replyIds);
            commentRepository.save(parent);

            return mapper.toResponseModel(parent);

        } catch (Exception ex) {
            logError("Failed to add reply=" + replyId + " to comment=" + parentCommentId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Remove Reply ID
    // ---------------------------------------------------------------------
    @Override
    public void removeReply(String postId, String parentCommentId, String replyId) {
        try {
            logInfo("Removing reply=" + replyId + " from parent=" + parentCommentId);

            UUID postUUID = parseToUUID(postId);
            UUID parentUUID = parseToUUID(parentCommentId);
            UUID replyUUID = parseToUUID(replyId);

            CommentEntity parent = commentRepository.findById(parentUUID)
                    .orElseThrow(() -> new NotFound("Parent comment not found"));

            if (!parent.getPostId().equals(postUUID)) {
                throw new BadRequest("PostId mismatch");
            }

            List<UUID> replyIds = parent.getReplyIds();
            if (replyIds == null || !replyIds.contains(replyUUID)) {
                throw new NotFound("Reply not found");
            }

            replyIds.remove(replyUUID);
            parent.setReplyIds(replyIds.isEmpty() ? null : replyIds);

            commentRepository.save(parent);

        } catch (Exception ex) {
            logError("Failed to remove reply=" + replyId + " from comment=" + parentCommentId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Get Replies
    // ---------------------------------------------------------------------
    @Override
    public List<CommentResponseModel> getReplies(String parentCommentId) {
        try {
            logInfo("Fetching replies for parent=" + parentCommentId);

            UUID uuid = parseToUUID(parentCommentId);

            return commentRepository.findReplies(uuid)
                    .stream()
                    .map(mapper::toResponseModel)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            logError("Failed fetching replies for comment=" + parentCommentId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Get Comments by Author
    // ---------------------------------------------------------------------
    @Override
    public List<CommentResponseModel> getCommentsByAuthor(String authorId) {
        try {
            logInfo("Fetching comments for author=" + authorId);

            return commentRepository.findByAuthorId(authorId)
                    .stream()
                    .map(mapper::toResponseModel)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            logError("Failed fetching comments for author=" + authorId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Count Comments For Post
    // ---------------------------------------------------------------------
    @Override
    public int countCommentsForPost(String postId) {
        try {
            logInfo("Counting comments for post=" + postId);

            UUID uuid = parseToUUID(postId);
            return commentRepository.countByPostId(uuid);

        } catch (Exception ex) {
            logError("Failed counting comments for post=" + postId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Moderation: Pin / Unpin
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel pinComment(String commentId) {
        try {
            logInfo("Pinning comment=" + commentId);

            UUID uuid = parseToUUID(commentId);

            CommentEntity entity = commentRepository.findById(uuid)
                    .orElseThrow(() -> new NotFound("Comment not found"));

            entity.setPinned(true);
            commentRepository.save(entity);
            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to pin comment=" + commentId, ex);
            throw ex;
        }
    }

    @Override
    public CommentResponseModel unpinComment(String commentId) {
        try {
            logInfo("Unpinning comment=" + commentId);

            UUID uuid = parseToUUID(commentId);

            CommentEntity entity = commentRepository.findById(uuid)
                    .orElseThrow(() -> new NotFound("Comment not found"));

            entity.setPinned(false);
            commentRepository.save(entity);
            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to unpin comment=" + commentId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Moderation: Flag / Remove Flag
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel flagComment(String commentId, String reason) {
        try {
            logInfo("Flagging comment=" + commentId + " reason=" + reason);

            UUID uuid = parseToUUID(commentId);

            CommentEntity entity = commentRepository.findById(uuid)
                    .orElseThrow(() -> new NotFound("Comment not found"));

            entity.setFlagged(true);
            entity.setFlaggedReason(reason);
            commentRepository.save(entity);

            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to flag comment=" + commentId, ex);
            throw ex;
        }
    }

    @Override
    public CommentResponseModel removeFlag(String commentId) {
        try {
            logInfo("Removing flag from comment=" + commentId);

            UUID uuid = parseToUUID(commentId);

            CommentEntity entity = commentRepository.findById(uuid)
                    .orElseThrow(() -> new NotFound("Comment not found"));

            entity.setFlagged(false);
            entity.setFlaggedReason(null);
            commentRepository.save(entity);

            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to remove flag on comment=" + commentId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------------------
    // Likes / Unlikes
    // ---------------------------------------------------------------------
    @Override
    public CommentResponseModel likeComment(String commentId) {
        try {
            logInfo("Liking comment=" + commentId);

            UUID uuid = parseToUUID(commentId);

            CommentEntity entity = commentRepository.findById(uuid)
                    .orElseThrow(() -> new NotFound("Comment not found"));

            entity.setLikesCount(entity.getLikesCount() + 1);
            commentRepository.save(entity);

            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to like comment=" + commentId, ex);
            throw ex;
        }
    }

    @Override
    public CommentResponseModel unlikeComment(String commentId) {
        try {
            logInfo("Unliking comment=" + commentId);

            UUID uuid = parseToUUID(commentId);

            CommentEntity entity = commentRepository.findById(uuid)
                    .orElseThrow(() -> new NotFound("Comment not found"));

            if (entity.getLikesCount() > 0) {
                entity.setLikesCount(entity.getLikesCount() - 1);
            }

            commentRepository.save(entity);
            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to unlike comment=" + commentId, ex);
            throw ex;
        }
    }
}
