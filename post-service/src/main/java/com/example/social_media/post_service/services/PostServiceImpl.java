package com.example.social_media.post_service.services;

import com.example.social_media.post_service.entity.PostEntity;
import com.example.social_media.post_service.mappers.PostModelMapper;
import com.example.social_media.post_service.models.PostRequestModel;
import com.example.social_media.post_service.models.PostResponseModel;
import com.example.social_media.post_service.repository.PostRepository;
import com.example.social_media.post_service.services.interfaces.IPostService;
import com.example.social_media.shared_libs.builders.LogEventBuilder;
import com.example.social_media.shared_libs.exceptions.BadRequest;
import com.example.social_media.shared_libs.kafka.KafkaLogProducer;
import com.example.social_media.shared_libs.models.LogEvent;
import com.example.social_media.shared_libs.services.BaseService;
import com.example.social_media.shared_libs.utils.RestClientUtility;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PostServiceImpl extends BaseService implements IPostService {

    private final PostRepository postRepository;
    private final PostModelMapper mapper;
    private final KafkaLogProducer KafkaLogProducer;

    public PostServiceImpl(
            KafkaLogProducer KafkaLogProducer,
            RestClientUtility restClientUtility,
            PostRepository postRepository,
            PostModelMapper mapper, KafkaTemplate<String, LogEvent> kafkaTemplate) {
        super(restClientUtility);
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.KafkaLogProducer = KafkaLogProducer;
    }


    private PostEntity getPostOrThrow(String postId) {
        UUID uuid;
        try {
            uuid = UUID.fromString(postId);
        } catch (IllegalArgumentException e) {
            log.info("hello world!!");
            throw new BadRequest("Invalid UUID format: " + postId);
        }
        return postRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));
    }

    @Override
    public PostResponseModel createPost(PostRequestModel request) {
        PostEntity entity = mapper.toEntity(request);
        PostEntity saved = postRepository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public PostResponseModel updatePost(String postId, PostRequestModel request) {

        PostEntity entity = getPostOrThrow(postId);
        mapper.updateEntity(request, entity);
        PostEntity saved = postRepository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public PostResponseModel getPostById(String postId) {

        PostEntity entity = getPostOrThrow(postId);
        LogEvent event = new LogEventBuilder("INFO", "My first test log")
                .service("test-service")
                .logger(PostServiceImpl.class)
                .tags(List.of("test"))
                .build();
        KafkaLogProducer.info("Id Requested", event.getContext());
        return mapper.toResponse(entity);
    }

    @Override
    public void deletePost(String postId) {

        PostEntity entity = getPostOrThrow(postId);
        postRepository.delete(entity);
    }

    @Override
    public List<PostResponseModel> getPostsByAuthor(String authorId) {
        List<PostEntity> posts = postRepository.findAllByAuthorId(authorId);
        return posts.stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<PostResponseModel> getAllPosts(int page, int size) {
        List<PostEntity> posts =
                postRepository.findAll(PageRequest.of(page, size)).getContent();
        return posts.stream().map(mapper::toResponse).toList();
    }

    @Override
    public void likePost(String postId) {

        PostEntity entity = getPostOrThrow(postId);
        entity.setLikesCount(entity.getLikesCount() + 1);
        postRepository.save(entity);
    }

    @Override
    public void unlikePost(String postId) {

        PostEntity entity = getPostOrThrow(postId);
        if (entity.getLikesCount() > 0) {
            entity.setLikesCount(entity.getLikesCount() - 1);
        }
        postRepository.save(entity);
    }

    @Override
    public void sharePost(String postId) {

        PostEntity entity = getPostOrThrow(postId);
        entity.setSharesCount(entity.getSharesCount() + 1);
        postRepository.save(entity);
    }

    @Override
    public void pinPost(String postId) {

        PostEntity entity = getPostOrThrow(postId);
        entity.setPinned(true);
        postRepository.save(entity);
    }

    @Override
    public void unpinPost(String postId) {

        PostEntity entity = getPostOrThrow(postId);
        entity.setPinned(false);
        postRepository.save(entity);
    }

    @Override
    public void flagPost(String postId, String reason) {

        PostEntity entity = getPostOrThrow(postId);
        entity.setFlagged(true);
        entity.setFlaggedReason(reason);
        postRepository.save(entity);
    }

    @Override
    public void unflagPost(String postId) {

        PostEntity entity = getPostOrThrow(postId);
        entity.setFlagged(false);
        entity.setFlaggedReason(null);
        postRepository.save(entity);
    }

    @Override
    public void addCommentId(String postId, String commentId) {
        PostEntity entity = getPostOrThrow(postId);
        UUID commentUUID;
        try {
            commentUUID = UUID.fromString(commentId);
        } catch (IllegalArgumentException e) {
            throw new BadRequest("Invalid UUID format: " + commentId);
        }
        if (entity.getCommentIds() == null) {
            entity.setCommentIds(new ArrayList<>());
        }

        entity.getCommentIds().add(commentUUID);
        entity.setCommentsCount(entity.getCommentsCount() + 1);

        postRepository.save(entity);
    }

    @Override
    public void removeCommentId(String postId, String commentId) {

        PostEntity entity = getPostOrThrow(postId);
        UUID commentUUID;
        try {
            commentUUID = UUID.fromString(commentId);
        } catch (IllegalArgumentException e) {
            throw new BadRequest("Invalid UUID format: " + commentId);
        }
        if (entity.getCommentIds() != null) {
            entity.getCommentIds().remove(commentUUID);

            if (entity.getCommentsCount() > 0) {
                entity.setCommentsCount(entity.getCommentsCount() - 1);
            }
        }

        postRepository.save(entity);
    }
}
