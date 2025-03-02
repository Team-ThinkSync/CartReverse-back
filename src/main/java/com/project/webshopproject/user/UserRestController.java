package com.project.webshopproject.user;

import static com.project.webshopproject.security.JwtProvider.AUTHORIZATION_HEADER;
import static com.project.webshopproject.security.JwtProvider.REFRESHTOKEN_HEADER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.webshopproject.user.dto.KakaoUserInfoDto;
import com.project.webshopproject.user.dto.UserChangePasswordRequestDto;
import com.project.webshopproject.user.dto.UserKakaoProfileUpdateRequestDto;
import com.project.webshopproject.user.dto.UserResignRequestDto;
import com.project.webshopproject.user.dto.UserSignupRequestDto;
import com.project.webshopproject.common.RestApiResponseDto;
import com.project.webshopproject.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final KakaoService kakaoService;

    //TODO 클라이언트 주소 넣기
//    @Value("${uri.client.address}")
    private String clientBaseUrl = "http://localhost:8080";

    /**
     * 회원가입 기능
     *
     * @param requestDto: email, username, nickname, phone_number, password, address
     */
    @PostMapping("/users/signup")
    public ResponseEntity<RestApiResponseDto<String>> signup(
            @Valid @RequestBody final UserSignupRequestDto requestDto
    ) {
        try {
            userService.signup(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(RestApiResponseDto.of("회원가입에 성공하였습니다!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RestApiResponseDto.of(e.getMessage()));
        }
    }

    /**
     * 비밀번호 변경 기능
     * @param requestDto: password, newPassword, confirmNewPassword
     * @param userDetails
     */
    @PatchMapping("/users/password")
    public ResponseEntity<RestApiResponseDto<String>> changePassword(
            @Valid @RequestBody final UserChangePasswordRequestDto requestDto,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        try {
            userService.changePassword(requestDto,userDetails.getEmail());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(RestApiResponseDto.of("비밀번호가 변경되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RestApiResponseDto.of(e.getMessage()));
        }
    }

    /**
     * 회원탈퇴
     * @param requestDto: password
     * @param userDetails
     */
    @PatchMapping("/users/resign")
    public ResponseEntity<RestApiResponseDto<String>> resign(
            @Valid @RequestBody final UserResignRequestDto requestDto,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        try {
            userService.resign(requestDto, userDetails.getEmail());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(RestApiResponseDto.of("회원탈퇴 되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RestApiResponseDto.of(e.getMessage()));
        }
    }

    /**
     * 카카오 회원가입 콜백
     */
    @GetMapping("/users/kakao/callback")
    public ResponseEntity<RestApiResponseDto<String>> kakaoLogin(@RequestParam final String code,
            HttpServletResponse response)
            throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드 Service 전달 후 인증 처리 및 JWT 반환
        KakaoUserInfoDto kakaoUserInfo = kakaoService.kakaoLogin(code, response);

        // 클라이언트로 리다이렉트 (쿼리 파라미터로 토큰 전달)
        String accessToken = response.getHeader(AUTHORIZATION_HEADER);
        String refreshToken = response.getHeader(REFRESHTOKEN_HEADER);

        String redirectUrl;

        if (kakaoUserInfo.isNewUser()) {
            // 신규 사용자일 경우 추가 정보 입력 페이지로 리다이렉트
            redirectUrl =
                    clientBaseUrl + "/kakao-signup?accessToken=" + accessToken
                            + "&refreshToken=" + refreshToken;
        } else {
            // 기존 사용자일 경우 메인 페이지로 리다이렉트
            redirectUrl =
                    clientBaseUrl + "/kakao-callback?accessToken=" + accessToken
                            + "&refreshToken=" + refreshToken;
        }

        // RestApiResponseDto를 통해 메시지와 리다이렉션 URL을 본문에 포함
        RestApiResponseDto<String> responseBody = RestApiResponseDto.of("리다이렉션을 수행합니다.",
                redirectUrl);

        // 리다이렉션 URL을 Location 헤더에 설정하고, 본문에 추가 정보를 포함한 응답 반환
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .body(responseBody);
    }

    @PatchMapping("/users/kakao/signup")
    public ResponseEntity<RestApiResponseDto<String>> completeSignup(
            @Valid @RequestBody final UserKakaoProfileUpdateRequestDto requestDto
    ) {

        kakaoService.profileUpdate(requestDto.accessToken(), requestDto.nickname(),
                requestDto.phoneNumber(), requestDto.address());

        // RestApiResponseDto를 이용해 반환
        return ResponseEntity.ok(RestApiResponseDto.of("카카오 회원가입이 완료되었습니다."));
    }
}
