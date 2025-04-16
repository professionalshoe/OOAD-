package com.example.socialmedia.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageUploadService {

    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";
    private static final String CLIENT_ID = "aeb10ecbf403ab7";

    private final RestTemplate restTemplate;

    public ImageUploadService() {
        // Use Spring's default RestTemplate which doesn't require HttpClient5
        this.restTemplate = new RestTemplate();
    }

    public String uploadImage(String base64Image) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Client-ID " + CLIENT_ID);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image", base64Image);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(IMGUR_UPLOAD_URL, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return (String) ((Map) response.getBody().get("data")).get("link");
        } else {
            throw new RuntimeException("Failed to upload image to Imgur");
        }
    }
}