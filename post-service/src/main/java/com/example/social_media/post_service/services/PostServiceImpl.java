package com.example.social_media.post_service.services;

import com.example.social_media.post_service.entity.PostEntity;
import com.example.social_media.post_service.mappers.PostModelMapper;
import com.example.social_media.post_service.models.PostRequestModel;
import com.example.social_media.post_service.models.PostResponseModel;
import com.example.social_media.post_service.repository.PostRepository;
import com.example.social_media.post_service.services.interfaces.IPostService;
import com.example.social_media.shared_libs.builders.LogEventBuilder;
import com.example.social_media.shared_libs.exceptions.BadRequest;
import com.example.social_media.shared_libs.kafka.KafkaProducer;
import com.example.social_media.shared_libs.models.LogEvent;
import com.example.social_media.shared_libs.services.BaseService;
import com.example.social_media.shared_libs.utils.RestClientUtility;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PostServiceImpl extends BaseService implements IPostService {

    private final PostRepository postRepository;
    private final PostModelMapper mapper;
    private final KafkaProducer kafkaProducer;

    private final String postKafkaStringTopic;
    private final String postKafkaJsonTopic;
    private final String postKafkaErrorStringTopic;
    private final String postKafkaErrorJsonTopic;

    public PostServiceImpl(
            KafkaProducer kafkaProducer,
            RestClientUtility restClientUtility,
            PostRepository postRepository,
            PostModelMapper mapper,
            @Value("${logging.topic-strings}") String postKafkaStringTopic,
            @Value("${logging.topic-json}") String postKafkaJsonTopic,
            @Value("${logging.error.topic-strings}") String postKafkaErrorStringTopic,
            @Value("${logging.error.topic-json}") String postKafkaErrorJsonTopic
    ) {
        super(restClientUtility);
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.kafkaProducer = kafkaProducer;
        this.postKafkaStringTopic = postKafkaStringTopic;
        this.postKafkaJsonTopic = postKafkaJsonTopic;
        this.postKafkaErrorStringTopic = postKafkaErrorStringTopic;
        this.postKafkaErrorJsonTopic = postKafkaErrorJsonTopic;
    }

    private void logInfo(String message) {
        LogEvent event = new LogEventBuilder("INFO", message)
                .service("post-service")
                .logger(PostServiceImpl.class)
                .tags(List.of("post-service"))
                .build();

        kafkaProducer.publishEvent(postKafkaJsonTopic, event);
        kafkaProducer.infoRaw(postKafkaStringTopic, message);
    }

    private void logError(String message, Exception ex) {
        LogEvent event = new LogEventBuilder("ERROR", message)
                .service("post-service")
                .logger(PostServiceImpl.class)
                .tags(List.of("post-service", "error"))
                .exception(ex)
                .build();

        kafkaProducer.errorEvent(postKafkaErrorJsonTopic, event);
        kafkaProducer.errorRaw(postKafkaErrorStringTopic, message + " | ex=" + ex.getMessage());
    }

    private PostEntity getPostOrThrow(String postId) {
        try {
            UUID uuid = UUID.fromString(postId);
            return postRepository.findById(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));
        } catch (IllegalArgumentException e) {
            logError("Invalid UUID requested: " + postId, e);
            throw new BadRequest("Invalid UUID format: " + postId);
        }
    }

    @Override
    public PostResponseModel createPost(PostRequestModel request) {
        try {
            logInfo("Creating post for author=" + request.getAuthorId());
            PostEntity entity = mapper.toEntity(request);
            PostEntity saved = postRepository.save(entity);
            return mapper.toResponse(saved);
        } catch (Exception ex) {
            logError("Failed to create post for author=" + request.getAuthorId(), ex);
            throw ex;
        }
    }

    @Override
    public PostResponseModel updatePost(String postId, PostRequestModel request) {
        try {
            logInfo("Updating post=" + postId);
            PostEntity entity = getPostOrThrow(postId);
            mapper.updateEntity(request, entity);
            PostEntity saved = postRepository.save(entity);
            return mapper.toResponse(saved);
        } catch (Exception ex) {
            logError("Failed to update post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public PostResponseModel getPostById(String postId) {
        try {
            PostEntity entity = getPostOrThrow(postId);
            PostResponseModel response = mapper.toResponse(entity);
            logInfo("Fetched post successfully: " + postId);
            return response;
        } catch (Exception ex) {
            logError("Failed to fetch post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public void deletePost(String postId) {
        try {
            logInfo("Deleting post=" + postId);
            PostEntity entity = getPostOrThrow(postId);
            postRepository.delete(entity);
        } catch (Exception ex) {
            logError("Failed to delete post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public List<PostResponseModel> getPostsByAuthor(String authorId) {
        try {
            logInfo("Listing posts for author=" + authorId);
            return postRepository.findAllByAuthorId(authorId)
                    .stream()
                    .map(mapper::toResponse)
                    .toList();
        } catch (Exception ex) {
            logError("Failed listing posts for author=" + authorId, ex);
            throw ex;
        }
    }

    @Override
    public List<PostResponseModel> getAllPosts(int page, int size) {
        try {
            logInfo("Fetching all posts page=" + page + ", size=" + size);
            return postRepository.findAll(PageRequest.of(page, size))
                    .getContent()
                    .stream()
                    .map(mapper::toResponse)
                    .toList();
        } catch (Exception ex) {
            logError("Failed fetching posts page=" + page + ", size=" + size, ex);
            throw ex;
        }
    }

    @Override
    public void likePost(String postId) {
        try {
            logInfo("Like post=" + postId);
            PostEntity entity = getPostOrThrow(postId);
            entity.setLikesCount(entity.getLikesCount() + 1);
            postRepository.save(entity);
        } catch (Exception ex) {
            logError("Failed to like post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public void unlikePost(String postId) {
        try {
            logInfo("Unlike post=" + postId);
            PostEntity entity = getPostOrThrow(postId);
            if (entity.getLikesCount() > 0) {
                entity.setLikesCount(entity.getLikesCount() - 1);
            }
            postRepository.save(entity);
        } catch (Exception ex) {
            logError("Failed to unlike post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public void sharePost(String postId) {
        try {
            logInfo("Share post=" + postId);
            PostEntity entity = getPostOrThrow(postId);
            entity.setSharesCount(entity.getSharesCount() + 1);
            postRepository.save(entity);
        } catch (Exception ex) {
            logError("Failed to share post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public void pinPost(String postId) {
        try {
            logInfo("Pin post=" + postId);
            PostEntity entity = getPostOrThrow(postId);
            entity.setPinned(true);
            postRepository.save(entity);
        } catch (Exception ex) {
            logError("Failed to pin post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public void unpinPost(String postId) {
        try {
            logInfo("Unpin post=" + postId);
            PostEntity entity = getPostOrThrow(postId);
            entity.setPinned(false);
            postRepository.save(entity);
        } catch (Exception ex) {
            logError("Failed to unpin post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public void flagPost(String postId, String reason) {
        try {
            logInfo("Flag post=" + postId + " | reason=" + reason);
            PostEntity entity = getPostOrThrow(postId);
            entity.setFlagged(true);
            entity.setFlaggedReason(reason);
            postRepository.save(entity);
        } catch (Exception ex) {
            logError("Failed to flag post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public void unflagPost(String postId) {
        try {
            logInfo("Unflag post=" + postId);
            PostEntity entity = getPostOrThrow(postId);
            entity.setFlagged(false);
            entity.setFlaggedReason(null);
            postRepository.save(entity);
        } catch (Exception ex) {
            logError("Failed to unflag post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public void addCommentId(String postId, String commentId) {
        try {
            logInfo("Add comment=" + commentId + " to post=" + postId);
            PostEntity entity = getPostOrThrow(postId);
            UUID commentUUID = UUID.fromString(commentId);
            if (entity.getCommentIds() == null) {
                entity.setCommentIds(new ArrayList<>());
            }
            entity.getCommentIds().add(commentUUID);
            entity.setCommentsCount(entity.getCommentsCount() + 1);
            postRepository.save(entity);
        } catch (Exception ex) {
            logError("Failed adding comment=" + commentId + " to post=" + postId, ex);
            throw ex;
        }
    }

    @Override
    public void removeCommentId(String postId, String commentId) {
        try {
            logInfo("Remove comment=" + commentId + " from post=" + postId);
            PostEntity entity = getPostOrThrow(postId);
            UUID commentUUID = UUID.fromString(commentId);
            if (entity.getCommentIds() != null) {
                entity.getCommentIds().remove(commentUUID);
                if (entity.getCommentsCount() > 0) {
                    entity.setCommentsCount(entity.getCommentsCount() - 1);
                }
            }
            postRepository.save(entity);
        } catch (Exception ex) {
            logError("Failed removing comment=" + commentId + " from post=" + postId, ex);
            throw ex;
        }
    }
}
