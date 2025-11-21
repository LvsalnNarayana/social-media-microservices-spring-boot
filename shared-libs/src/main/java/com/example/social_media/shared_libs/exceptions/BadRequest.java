package com.example.social_media.shared_libs.exceptions;

public class BadRequest extends RuntimeException {

  public BadRequest(String message) {
    super(message);
  }
}
