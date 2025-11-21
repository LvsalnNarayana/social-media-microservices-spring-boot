package com.example.social_media.user_service.web.api.interfaces;

import com.example.social_media.shared_libs.constants.BaseUrlConstants;
import com.example.social_media.shared_libs.models.MasterResponse;
import com.example.social_media.user_service.models.PagedUserResponseModel;
import com.example.social_media.user_service.models.UserModel;
import com.example.social_media.user_service.models.UserRequestModel;
import com.example.social_media.user_service.models.UserResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(BaseUrlConstants.BASE_URL)
public interface IUserApi {

    @GetMapping(BaseUrlConstants.ADMIN_BASE_URL + "/users")
    ResponseEntity<MasterResponse<PagedUserResponseModel>> listAllUsers(
            @RequestParam(defaultValue = "0")
            String page,
            @RequestParam(defaultValue = "10")
            String size
    );

    @GetMapping("/users/search")
    ResponseEntity<MasterResponse<PagedUserResponseModel>> searchUsers(
            @RequestParam("username")
            String username
    );

    @GetMapping("/users/{userId}")
    ResponseEntity<MasterResponse<UserResponseModel>> getUserById(
            @PathVariable
            String userId
    );

    @PostMapping("/users")
    ResponseEntity<MasterResponse<UserResponseModel>> createUser(
            @RequestBody
            UserRequestModel userModel
    );

    @PutMapping("/users/{userId}")
    ResponseEntity<MasterResponse<UserResponseModel>> updateUser(
            @PathVariable
            String userId,
            @RequestBody
            UserModel userModel
    );

    @DeleteMapping("/users/{userId}")
    ResponseEntity<MasterResponse<Void>> deleteUser(@PathVariable String userId);

}
