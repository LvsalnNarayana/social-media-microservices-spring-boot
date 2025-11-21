package com.example.social_media.user_service.services.interfaces;

import com.example.social_media.user_service.models.PagedUserResponseModel;
import com.example.social_media.user_service.models.UserModel;
import com.example.social_media.user_service.models.UserRequestModel;
import com.example.social_media.user_service.models.UserResponseModel;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface IUserService {

    /**
     *
     * @param userId - user identifier
     *
     */
    UserResponseModel getUserById(String userId) throws BadRequestException;

    /**
     *
     * @param userData - JSON string containing user details
     *
     */
    UserResponseModel createUser(UserRequestModel userData);

    /**
     *
     * @param userId - user identifier
     *
     */
    @Transactional
    UserResponseModel updateUser(String userId, UserModel userData);

    /**
     *
     * @param userId - user identifier
     *
     */

    @Transactional
    void deleteUser(String userId);

    /**
     *
     * @param page - page number for pagination
     * @param size - number of users per page
     *
     */
    PagedUserResponseModel listUsers(String page, String size);

    /**
     *
     * @param query - search query string
     * @param page  - page number for pagination
     * @param size  - number of users per page
     *
     */
    List<UserModel> searchUserByName(String query, int page, int size);
}
