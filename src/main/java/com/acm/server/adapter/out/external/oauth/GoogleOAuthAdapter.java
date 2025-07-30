// package com.acm.server.adapter.out.external.oauth;

// import com.acm.server.domain.auth.model.GoogleUserInfo;
// import com.acm.server.domain.auth.port.out.GoogleOAuthPort;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.*;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestTemplate;

// @Component
// @RequiredArgsConstructor
// public class GoogleOAuthAdapter implements GoogleOAuthPort {

//     private final RestTemplate restTemplate;
//     private final GoogleOAuthProperties googleOAuthProperties;

//     @Override
//     public GoogleUserInfo getUserInfo(String accessToken) {
//         String url = googleOAuthProperties.getUserInfoUrl();

//         HttpHeaders headers = new HttpHeaders();
//         headers.set("Authorization", "Bearer " + accessToken);
//         HttpEntity<Void> entity = new HttpEntity<>(headers);

//         ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
//                 url,
//                 HttpMethod.GET,
//                 entity,
//                 GoogleUserInfo.class
//         );

//         return response.getBody();
//     }
// }
