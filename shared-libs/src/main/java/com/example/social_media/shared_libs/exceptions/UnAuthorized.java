package com.example.social_media.shared_libs.exceptions;

public class UnAuthorized extends RuntimeException {

  public UnAuthorized(String message) {
    super(message);
  }
}
