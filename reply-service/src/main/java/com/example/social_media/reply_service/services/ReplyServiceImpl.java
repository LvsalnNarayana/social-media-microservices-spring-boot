package com.example.social_media.reply_service.services;

import com.example.social_media.reply_service.entity.ReplyEntity;
import com.example.social_media.reply_service.mappers.ReplyModelMapper;
import com.example.social_media.reply_service.models.ReplyRequestModel;
import com.example.social_media.reply_service.models.ReplyResponseModel;
import com.example.social_media.reply_service.repository.ReplyRepository;
import com.example.social_media.reply_service.services.interfaces.IReplyService;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ReplyServiceImpl extends BaseService implements IReplyService {

    private static final String COMMENT_SERVICE_BASE_URL =
            "http://localhost:4300/api/v1/comments/";
    private final ReplyRepository replyRepository;
    private final ReplyModelMapper mapper;
    private final KafkaProducer kafkaProducer;
    private final RestClientUtility restClient;
    private final String replyKafkaStringTopic;
    private final String replyKafkaJsonTopic;
    private final String replyKafkaErrorStringTopic;
    private final String replyKafkaErrorJsonTopic;

    public ReplyServiceImpl(
            ReplyRepository replyRepository,
            ReplyModelMapper mapper,
            RestClientUtility restClient,
            KafkaProducer kafkaProducer,
            @Value("${logging.topic-strings}") String replyKafkaStringTopic,
            @Value("${logging.topic-json}") String replyKafkaJsonTopic,
            @Value("${logging.error.topic-strings}") String replyKafkaErrorStringTopic,
            @Value("${logging.error.topic-json}") String replyKafkaErrorJsonTopic

    ) {
        super(restClient);
        this.mapper = mapper;
        this.restClient = restClient;
        this.kafkaProducer = kafkaProducer;
        this.replyRepository = replyRepository;
        this.replyKafkaStringTopic = replyKafkaStringTopic;
        this.replyKafkaJsonTopic = replyKafkaJsonTopic;
        this.replyKafkaErrorStringTopic = replyKafkaErrorStringTopic;
        this.replyKafkaErrorJsonTopic = replyKafkaErrorJsonTopic;
    }

    // ---------------------------------------------------------
    // LOG HELPERS (same style as Post & Comment Services)
    // ---------------------------------------------------------
    private void logInfo(String message) {
        LogEvent event = new LogEventBuilder("INFO", message)
                .service("post-service")
                .logger(ReplyServiceImpl.class)
                .tags(List.of("post-service"))
                .build();

        kafkaProducer.publishEvent(replyKafkaJsonTopic, event);
        kafkaProducer.infoRaw(replyKafkaStringTopic, message);
    }

    private void logError(String message, Exception ex) {
        LogEvent event = new LogEventBuilder("ERROR", message)
                .service("post-service")
                .logger(ReplyServiceImpl.class)
                .tags(List.of("post-service", "error"))
                .exception(ex)
                .build();

        kafkaProducer.errorEvent(replyKafkaErrorJsonTopic, event);
        kafkaProducer.errorRaw(replyKafkaErrorStringTopic, message + " | ex=" + ex.getMessage());
    }

    // ---------------------------------------------------------
    // Utility: Convert String → UUID (renamed to parseToUUID)
    // ---------------------------------------------------------
    private UUID parseToUUID(String value) {
        try {
            return UUID.fromString(value);
        } catch (Exception e) {
            logError("Invalid UUID format: " + value, e);
            throw new BadRequest("Invalid UUID format: " + value);
        }
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel createReply(ReplyRequestModel requestModel) {
        try {
            logInfo("Creating reply for post=" + requestModel.getPostId()
                    + " under comment=" + requestModel.getParentCommentId());

            ReplyEntity entity = mapper.toEntity(requestModel);
            ReplyEntity saved = replyRepository.save(entity);

            // Notify CommentService to attach replyId
            String url = COMMENT_SERVICE_BASE_URL
                    + requestModel.getPostId() + "/comments/"
                    + requestModel.getParentCommentId() + "/replies/"
                    + saved.getId();

            logInfo("Notifying CommentService → Add ReplyId: " + url);
            restClient.post(url, null, null, Void.class);

            return mapper.toResponseModel(saved);

        } catch (Exception ex) {
            logError("Failed to create reply for comment=" + requestModel.getParentCommentId(), ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel updateReply(String replyId, String updatedContent) {
        try {
            logInfo("Updating reply=" + replyId);

            UUID replyUUID = parseToUUID(replyId);

            ReplyEntity entity = replyRepository.findById(replyUUID)
                    .orElseThrow(() -> new NotFound("Reply not found"));

            mapper.updateEntityFromContent(entity, updatedContent);
            replyRepository.save(entity);

            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to update reply=" + replyId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @Override
    public void deleteReply(String replyId) {
        try {
            logInfo("Deleting reply=" + replyId);

            UUID replyUUID = parseToUUID(replyId);

            ReplyEntity entity = replyRepository.findById(replyUUID)
                    .orElseThrow(() -> new NotFound("Reply not found"));

            entity.setDeleted(true);
            replyRepository.save(entity);

            // Notify CommentService to remove replyId
            String url = COMMENT_SERVICE_BASE_URL
                    + entity.getPostId() + "/comments/"
                    + entity.getParentCommentId() + "/replies/"
                    + entity.getId();

            logInfo("Notifying CommentService → REMOVE ReplyId: " + url);
            restClient.delete(url, null, Void.class);

        } catch (Exception ex) {
            logError("Failed to delete reply=" + replyId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel getReplyById(String replyId) {
        try {
            logInfo("Fetching reply=" + replyId);

            UUID uuid = parseToUUID(replyId);

            ReplyEntity entity = replyRepository.findById(uuid)
                    .orElseThrow(() -> new NotFound("Reply not found"));

            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to fetch reply=" + replyId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------
    // GET Replies for Comment
    // ---------------------------------------------------------
    @Override
    public List<ReplyResponseModel> getRepliesForComment(String parentCommentId) {
        try {
            logInfo("Fetching replies for comment=" + parentCommentId);

            UUID parentUUID = parseToUUID(parentCommentId);

            return replyRepository.findByParentCommentId(parentUUID)
                    .stream()
                    .map(mapper::toResponseModel)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            logError("Failed fetching replies for comment=" + parentCommentId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------
    // GET Replies for Post
    // ---------------------------------------------------------
    @Override
    public List<ReplyResponseModel> getRepliesForPost(String postId) {
        try {
            logInfo("Fetching replies for post=" + postId);

            UUID postUUID = parseToUUID(postId);

            return replyRepository.findByPostId(postUUID)
                    .stream()
                    .map(mapper::toResponseModel)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            logError("Failed fetching replies for post=" + postId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------
    // GET Replies by Author
    // ---------------------------------------------------------
    @Override
    public List<ReplyResponseModel> getRepliesByAuthor(String authorId) {
        try {
            logInfo("Fetching replies by author=" + authorId);

            return replyRepository.findByAuthorId(authorId)
                    .stream()
                    .map(mapper::toResponseModel)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            logError("Failed fetching replies for author=" + authorId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------
    // FLAG
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel flagReply(String replyId, String reason) {
        try {
            logInfo("Flagging reply=" + replyId + " | reason=" + reason);

            UUID replyUUID = parseToUUID(replyId);

            ReplyEntity entity = replyRepository.findById(replyUUID)
                    .orElseThrow(() -> new NotFound("Reply not found"));

            entity.setFlagged(true);
            entity.setFlaggedReason(reason);

            replyRepository.save(entity);

            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to flag reply=" + replyId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------
    // REMOVE FLAG
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel removeFlag(String replyId) {
        try {
            logInfo("Removing flag from reply=" + replyId);

            UUID replyUUID = parseToUUID(replyId);

            ReplyEntity entity = replyRepository.findById(replyUUID)
                    .orElseThrow(() -> new NotFound("Reply not found"));

            entity.setFlagged(false);
            entity.setFlaggedReason(null);

            replyRepository.save(entity);

            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to remove flag from reply=" + replyId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------
    // LIKE
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel likeReply(String replyId) {
        try {
            logInfo("Liking reply=" + replyId);

            UUID replyUUID = parseToUUID(replyId);

            ReplyEntity entity = replyRepository.findById(replyUUID)
                    .orElseThrow(() -> new NotFound("Reply not found"));

            entity.setLikesCount(entity.getLikesCount() + 1);
            replyRepository.save(entity);

            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to like reply=" + replyId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------
    // UNLIKE
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel unlikeReply(String replyId) {
        try {
            logInfo("Unliking reply=" + replyId);

            UUID replyUUID = parseToUUID(replyId);

            ReplyEntity entity = replyRepository.findById(replyUUID)
                    .orElseThrow(() -> new NotFound("Reply not found"));

            if (entity.getLikesCount() > 0) {
                entity.setLikesCount(entity.getLikesCount() - 1);
            }

            replyRepository.save(entity);

            return mapper.toResponseModel(entity);

        } catch (Exception ex) {
            logError("Failed to unlike reply=" + replyId, ex);
            throw ex;
        }
    }

    // ---------------------------------------------------------
    // COUNTS
    // ---------------------------------------------------------
    @Override
    public int countRepliesForComment(String parentCommentId) {
        try {
            logInfo("Counting replies for comment=" + parentCommentId);

            UUID uuid = parseToUUID(parentCommentId);
            return replyRepository.countByParentCommentId(uuid);

        } catch (Exception ex) {
            logError("Failed counting replies for comment=" + parentCommentId, ex);
            throw ex;
        }
    }

    @Override
    public int countRepliesForPost(String postId) {
        try {
            logInfo("Counting replies for post=" + postId);

            UUID uuid = parseToUUID(postId);
            return replyRepository.countByPostId(uuid);

        } catch (Exception ex) {
            logError("Failed counting replies for post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public int countRepliesByAuthor(String authorId) {
        try {
            logInfo("Counting replies for author=" + authorId);

            return replyRepository.countByAuthorId(authorId);

        } catch (Exception ex) {
            logError("Failed counting replies for author=" + authorId, ex);
            throw ex;
        }
    }
}
