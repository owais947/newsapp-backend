package com.navi.assignment2.contract.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navi.assignment2.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    @JsonProperty("articles")
    private ArrayList<Article> articles;
}
