package com.localgaji.taxi.auth.kakao_oauth.fetch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class DTOtoMultiMap {
    /** 자바 객체를 x-www-form-urlencoded;charset=utf-8 타입으로 전송하기 위해 MultiValueMap 으로 변환 필요 */
    public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object dto) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Map<String, String> map = objectMapper.convertValue(dto, new TypeReference<>() {});
        params.setAll(map);
        return params;
    }
}