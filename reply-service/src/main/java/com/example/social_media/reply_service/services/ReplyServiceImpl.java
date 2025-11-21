package com.example.social_media.reply_service.services;

import com.example.social_media.reply_service.entity.ReplyEntity;
import com.example.social_media.reply_service.mappers.ReplyModelMapper;
import com.example.social_media.reply_service.models.ReplyRequestModel;
import com.example.social_media.reply_service.models.ReplyResponseModel;
import com.example.social_media.reply_service.repository.ReplyRepository;
import com.example.social_media.reply_service.services.interfaces.IReplyService;
import com.example.social_media.shared_libs.exceptions.BadRequest;
import com.example.social_media.shared_libs.exceptions.NotFound;
import com.example.social_media.shared_libs.services.BaseService;
import com.example.social_media.shared_libs.utils.RestClientUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ReplyServiceImpl extends BaseService implements IReplyService {

    private final ReplyRepository replyRepository;
    private final ReplyModelMapper mapper;

    public ReplyServiceImpl(
            ReplyRepository replyRepository,
            ReplyModelMapper mapper,
            RestClientUtility restClient
    ) {
        super(restClient);
        this.replyRepository = replyRepository;
        this.mapper = mapper;
    }

    // ---------------------------------------------------------
    // Utility: Convert String → UUID
    // ---------------------------------------------------------
    private UUID toUUID(String value) {
        try {
            return UUID.fromString(value);
        } catch (Exception e) {
            throw new BadRequest("Invalid UUID format: " + value);
        }
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel createReply(ReplyRequestModel requestModel) {

        log.info("Creating reply for post {} under comment {}",
                requestModel.getPostId(), requestModel.getParentCommentId());

        ReplyEntity entity = mapper.toEntity(requestModel);
        ReplyEntity saved = replyRepository.save(entity);

        // -----------------------------------------
        // Notify CommentService to attach replyId
        // -----------------------------------------
        String commentUrl = "http://localhost:4300/api/v1/comments/"
                + requestModel.getPostId() + "/comments/"
                + requestModel.getParentCommentId() + "/replies/"
                + saved.getId();

        log.info("Notifying CommentService → Add ReplyId: {}", commentUrl);

        restClient.post(commentUrl, null, null, Void.class);

        return mapper.toResponseModel(saved);
    }


    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel updateReply(String replyId, String updatedContent) {

        UUID replyUUID = toUUID(replyId);

        ReplyEntity entity = replyRepository.findById(replyUUID)
                .orElseThrow(() -> new NotFound("Reply not found"));

        mapper.updateEntityFromContent(entity, updatedContent);

        replyRepository.save(entity);
        return mapper.toResponseModel(entity);
    }

    // ---------------------------------------------------------
    // DELETE (soft delete)
    // ---------------------------------------------------------
    @Override
    public void deleteReply(String replyId) {

        UUID replyUUID = toUUID(replyId);

        ReplyEntity entity = replyRepository.findById(replyUUID)
                .orElseThrow(() -> new NotFound("Reply not found"));

        entity.setDeleted(true);
        replyRepository.save(entity);

        // -----------------------------------------
        // Notify CommentService to remove replyId
        // -----------------------------------------
        String commentUrl = "http://localhost:4300/api/v1/comments/"
                + entity.getPostId() + "/comments/"
                + entity.getParentCommentId() + "/replies/"
                + entity.getId();

        log.info("Notifying CommentService → REMOVE replyId: {}", commentUrl);

        restClient.delete(commentUrl, null, Void.class);
    }


    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel getReplyById(String replyId) {

        UUID uuid = toUUID(replyId);

        ReplyEntity entity = replyRepository.findById(uuid)
                .orElseThrow(() -> new NotFound("Reply not found"));

        return mapper.toResponseModel(entity);
    }

    // ---------------------------------------------------------
    // GET Replies for Comment
    // ---------------------------------------------------------
    @Override
    public List<ReplyResponseModel> getRepliesForComment(String parentCommentId) {

        UUID parentUUID = toUUID(parentCommentId);

        return replyRepository.findByParentCommentId(parentUUID)
                .stream()
                .map(mapper::toResponseModel)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // GET Replies for Post
    // ---------------------------------------------------------
    @Override
    public List<ReplyResponseModel> getRepliesForPost(String postId) {

        UUID postUUID = toUUID(postId);

        return replyRepository.findByPostId(postUUID)
                .stream()
                .map(mapper::toResponseModel)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // GET Replies by Author
    // ---------------------------------------------------------
    @Override
    public List<ReplyResponseModel> getRepliesByAuthor(String authorId) {

        return replyRepository.findByAuthorId(authorId)
                .stream()
                .map(mapper::toResponseModel)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // FLAG
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel flagReply(String replyId, String reason) {

        UUID replyUUID = toUUID(replyId);

        ReplyEntity entity = replyRepository.findById(replyUUID)
                .orElseThrow(() -> new NotFound("Reply not found"));

        entity.setFlagged(true);
        entity.setFlaggedReason(reason);

        replyRepository.save(entity);
        return mapper.toResponseModel(entity);
    }

    // ---------------------------------------------------------
    // REMOVE FLAG
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel removeFlag(String replyId) {

        UUID replyUUID = toUUID(replyId);

        ReplyEntity entity = replyRepository.findById(replyUUID)
                .orElseThrow(() -> new NotFound("Reply not found"));

        entity.setFlagged(false);
        entity.setFlaggedReason(null);

        replyRepository.save(entity);
        return mapper.toResponseModel(entity);
    }

    // ---------------------------------------------------------
    // LIKE
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel likeReply(String replyId) {

        UUID replyUUID = toUUID(replyId);

        ReplyEntity entity = replyRepository.findById(replyUUID)
                .orElseThrow(() -> new NotFound("Reply not found"));

        entity.setLikesCount(entity.getLikesCount() + 1);
        replyRepository.save(entity);

        return mapper.toResponseModel(entity);
    }

    // ---------------------------------------------------------
    // UNLIKE
    // ---------------------------------------------------------
    @Override
    public ReplyResponseModel unlikeReply(String replyId) {

        UUID replyUUID = toUUID(replyId);

        ReplyEntity entity = replyRepository.findById(replyUUID)
                .orElseThrow(() -> new NotFound("Reply not found"));

        if (entity.getLikesCount() > 0) {
            entity.setLikesCount(entity.getLikesCount() - 1);
        }

        replyRepository.save(entity);
        return mapper.toResponseModel(entity);
    }

    // ---------------------------------------------------------
    // COUNTS
    // ---------------------------------------------------------
    @Override
    public int countRepliesForComment(String parentCommentId) {
        UUID uuid = toUUID(parentCommentId);
        return replyRepository.countByParentCommentId(uuid);
    }

    @Override
    public int countRepliesForPost(String postId) {
        UUID uuid = toUUID(postId);
        return replyRepository.countByPostId(uuid);
    }

    @Override
    public int countRepliesByAuthor(String authorId) {
        return replyRepository.countByAuthorId(authorId);
    }
}
