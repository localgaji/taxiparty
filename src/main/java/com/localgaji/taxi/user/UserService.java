package com.localgaji.taxi.user;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.localgaji.taxi.auth.dto.RequestAuth.*;

@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /** 유저 추가 */
    public User makeNewUser(SignUpRequest signUpRequest) {
        User newUser = signUpRequest.toEntity();
        userRepository.save(newUser);
        return newUser;
    }

    /** util: id로 entity 찾기 (없으면 404) */
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()->
                        new CustomException(ErrorType.MEMBER_NOT_FOUND)
                );
    }
}
