package com.navi.assignment2.contract.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String email;
    private String selectedCategory;
    private String selectedCountry;
    private List<String> preferredSources;
}
