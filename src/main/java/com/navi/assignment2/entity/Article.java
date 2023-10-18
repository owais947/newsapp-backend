package com.navi.assignment2.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.navi.assignment2.entity.Source;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @JsonProperty("publishedAt")
    private String publishedAt;

    @JsonProperty("author")
    private String author;

    @JsonProperty("urlToImage")
    private String urlToImage;

    @JsonProperty("description")
    private String description;

    @JsonProperty("title")
    private String title;

    @JsonProperty("url")
    private String url;

    @JsonProperty("content")
    private String content;

    @JsonProperty("source")
    private Source source;
}

