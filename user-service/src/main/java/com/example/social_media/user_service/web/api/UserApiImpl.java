package com.example.social_media.user_service.web.api;

import com.example.social_media.shared_libs.models.MasterResponse;
import com.example.social_media.shared_libs.models.Status;
import com.example.social_media.shared_libs.web.api.BaseApi;
import com.example.social_media.user_service.models.PagedUserResponseModel;
import com.example.social_media.user_service.models.UserModel;
import com.example.social_media.user_service.models.UserRequestModel;
import com.example.social_media.user_service.models.UserResponseModel;
import com.example.social_media.user_service.services.UserServiceImpl;
import com.example.social_media.user_service.web.api.interfaces.IUserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserApiImpl extends BaseApi implements IUserApi {

    @Autowired
    UserServiceImpl userService;

    /**
     * @return
     */
    @Override
    public ResponseEntity<MasterResponse<PagedUserResponseModel>> listAllUsers(
            String page,
            String size
    ) {
        PagedUserResponseModel serviceResponse = userService.listUsers(
                page,
                size
        );
        Status
                status =
                Status.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(HttpStatus.OK.getReasonPhrase())
                        .build();
        MasterResponse<PagedUserResponseModel>
                finalResponse =
                new MasterResponse<PagedUserResponseModel>();
        finalResponse.setData(serviceResponse);
        finalResponse.setStatus(status);
        return ResponseEntity.status(HttpStatus.OK)
                .body(finalResponse);
    }

    /**
     * @param username
     * @return
     */
    @Override
    public ResponseEntity<MasterResponse<PagedUserResponseModel>> searchUsers(String username) {
        return null;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public ResponseEntity<MasterResponse<UserResponseModel>> getUserById(String userId) {
        UserResponseModel serviceResponse = userService.getUserById(userId);
        Status
                status =
                Status.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(HttpStatus.OK.getReasonPhrase())
                        .build();
        MasterResponse<UserResponseModel> finalResponse = new MasterResponse<UserResponseModel>();
        finalResponse.setData(serviceResponse);
        finalResponse.setStatus(status);
        return ResponseEntity.status(HttpStatus.OK)
                .body(finalResponse);
    }

    /**
     * @param userModel
     * @return
     */
    @Override
    public ResponseEntity<MasterResponse<UserResponseModel>> createUser(UserRequestModel userModel) {
        log.info("Email in request: " + userModel.toString());
        UserResponseModel serviceResponse = userService.createUser(userModel);
        Status
                status =
                Status.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message(HttpStatus.CREATED.getReasonPhrase())
                        .build();
        MasterResponse<UserResponseModel> finalResponse = new MasterResponse<UserResponseModel>();
        finalResponse.setData(serviceResponse);
        finalResponse.setStatus(status);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(finalResponse);
    }

    /**
     * @param userId
     * @param userModel
     * @return
     */
    @Override
    public ResponseEntity<MasterResponse<UserResponseModel>> updateUser(
            String userId,
            UserModel userModel
    ) {
        UserResponseModel
                serviceResponse =
                userService.updateUser(
                        userId,
                        userModel
                );
        Status
                status =
                Status.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(HttpStatus.OK.getReasonPhrase())
                        .build();
        MasterResponse<UserResponseModel> finalResponse = new MasterResponse<UserResponseModel>();
        finalResponse.setData(serviceResponse);
        finalResponse.setStatus(status);
        return ResponseEntity.status(HttpStatus.OK)
                .body(finalResponse);
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public ResponseEntity<MasterResponse<Void>> deleteUser(String userId) {

        userService.deleteUser(userId);

        Status status = Status.builder()
                .statusCode(HttpStatus.OK.value())
                .message("User deleted successfully")
                .build();

        MasterResponse<Void> response = new MasterResponse<>();
        response.setStatus(status);
        response.setData(null);

        return ResponseEntity.ok(response);
    }

}
