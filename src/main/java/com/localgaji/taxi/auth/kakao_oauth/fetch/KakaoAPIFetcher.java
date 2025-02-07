package com.localgaji.taxi.auth.kakao_oauth.fetch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static com.localgaji.taxi.auth.kakao_oauth.fetch.RequestKakaoAPI.*;
import static com.localgaji.taxi.auth.kakao_oauth.fetch.ResponseKakaoAPI.*;


@Component @RequiredArgsConstructor
public class KakaoAPIFetcher {
    @Value("${kakaoAuth.client_id}")
    private String client_id;
    @Value("${kakaoAuth.redirect_uri}")
    private String redirect_uri;
    @Value("${kakaoAuth.token_url}")
    private String token_url;
    @Value("${kakaoAuth.user_info_url}")
    private String user_info_url;

    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.create();

    public Long codeToKakaoId(String code) {
        String token = codeToKakaoToken(code);
        String kakaoId = tokenToKakaoId(token);
        return Long.parseLong(kakaoId);
    }

    // 1. 인가 코드로 카카오 토큰 호출
    private String codeToKakaoToken(String code) {

        // Request Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        GetTokenRequest requestDTO = GetTokenRequest.builder()
                .client_id(client_id)
                .redirect_uri(redirect_uri)
                .code(code)
                .build();

        String url = token_url;

        return sendRequest(url,
                HttpMethod.POST,
                headers,
                DTOtoMultiMap.convert(objectMapper, requestDTO),
                GetTokenResponse.class
        ).access_token();
    }


    // 2. 토큰으로 카카오 id 호출
    private String tokenToKakaoId(String accessToken) {
        // Request Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 보내기
        String url = user_info_url;

        return sendRequest(url,
                HttpMethod.GET,
                headers,
                null,
                GetKakaoIdResponse.class
        ).id();
    }



    private <ResponseDTO> ResponseDTO sendRequest(String requestUrl,
                                                   HttpMethod method,
                                                   HttpHeaders requestHeaders,
                                                   Object requestBody,
                                                   Class<ResponseDTO> responseDTO) {

        RestClient.RequestBodySpec requestSpec = restClient
                .method(method)
                .uri(requestUrl)
                .headers(httpHeaders -> httpHeaders.addAll(requestHeaders));

        if (requestBody != null) {
            requestSpec.body(requestBody);
        }

        return requestSpec
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new CustomException(ErrorType.OAUTH_FAIL);
                })
                .body(responseDTO);
    }

}
