package com.example.social_media.shared_libs.schema;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponseSchema<T> {
  public T content;

  public int page;

  public int size;

  public long totalElements;

  public int totalPages;

  public boolean last;

  public boolean first;
}
