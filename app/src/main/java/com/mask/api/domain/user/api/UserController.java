package com.mask.api.domain.user.api;


import com.mask.api.domain.user.dto.favorite.FavoriteRequestDto;
import com.mask.api.domain.user.dto.login.LoginRequestDto;
import com.mask.api.domain.user.dto.login.LogoutRequestDto;
import com.mask.api.domain.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor //DI
@Api( tags = "User")
public class UserController {
    private final UserService userService;

    @Operation(summary = "로그인 API", description = "로그인 후 토큰 발급.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청 성공."),
            @ApiResponse(code = 500, message = "서버 내부 에러.")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto){
        return userService.login(loginRequestDto);
    }

    @Operation(summary = "메인 화면 요청 API", description = "메인 화면에서 필요한 정보 요청.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청 성공."),
            @ApiResponse(code = 401, message = "인증 에러."),
            @ApiResponse(code = 403, message = "토큰 권한 에러."),
            @ApiResponse(code = 404, message = "유저 정보 에러."),
            @ApiResponse(code = 500, message = "서버 내부 에러.")
    })
    @GetMapping("/user/{email}")
    public ResponseEntity<?> mainRequest(@PathVariable(name="email")String email){
        return userService.mainRequest(email);
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃 후 토큰 삭제.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청 성공."),
            @ApiResponse(code = 400, message = "리소스 에러."),
            @ApiResponse(code = 401, message = "인증 에러."),
            @ApiResponse(code = 403, message = "토큰 권한 에러."),
            @ApiResponse(code = 404, message = "유저 정보 에러."),
            @ApiResponse(code = 500, message = "서버 내부 에러.")
    })
    @PostMapping("/user/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDto logoutRequestDto){
        return userService.logout(logoutRequestDto);
    }

    @Operation(summary = "문제 요청 API", description = "요청된 난이도에 해당하는 문제 제공.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청 성공."),
            @ApiResponse(code = 400, message = "리소스 에러."),
            @ApiResponse(code = 401, message = "인증 에러."),
            @ApiResponse(code = 403, message = "토큰 권한 에러."),
            @ApiResponse(code = 404, message = "유저 정보 에러."),
            @ApiResponse(code = 500, message = "서버 내부 에러.")
    })
    @GetMapping("/user/{email}/test/{level}")
    public ResponseEntity<?> requestTest(@PathVariable(name="email")String email,
                                         @PathVariable(name="level")Integer level){
        return userService.requestTest(email, level);
    }

    @Operation(summary = "즐겨찾기 추가 API", description = "즐겨찾기 추가 및 성취율 갱신.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청 성공."),
            @ApiResponse(code = 401, message = "인증 에러."),
            @ApiResponse(code = 403, message = "토큰 권한 에러."),
            @ApiResponse(code = 404, message = "유저 정보 에러."),
            @ApiResponse(code = 500, message = "서버 내부 에러.")
    })
    @PostMapping("/user/{email}/fav")
    public ResponseEntity<?> favoriteAdd(@PathVariable(name="email")String email,
                                         @RequestBody FavoriteRequestDto favoriteRequestDto){
        return userService.favoriteAdd(email,favoriteRequestDto);
    }

    @Operation(summary = "즐겨찾기 요청 API", description = "유저에 해당하는 즐겨찾기 문제 제공.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청 성공."),
            @ApiResponse(code = 401, message = "인증 에러."),
            @ApiResponse(code = 403, message = "토큰 권한 에러."),
            @ApiResponse(code = 404, message = "유저 정보 에러."),
            @ApiResponse(code = 500, message = "서버 내부 에러.")
    })
    @GetMapping("/user/{email}/fav")
    public ResponseEntity<?> favoriteRequest(@PathVariable(name="email")String email){
        return userService.favoriteRequest(email);
    }

    @Operation(summary = "즐겨찾기 삭제 API", description = "요청된 인덱스에 해당하는 즐겨찾기 목록 삭제.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청 성공."),
            @ApiResponse(code = 400, message = "리소스 에러."),
            @ApiResponse(code = 401, message = "인증 에러."),
            @ApiResponse(code = 403, message = "토큰 권한 에러."),
            @ApiResponse(code = 404, message = "유저 정보 에러."),
            @ApiResponse(code = 500, message = "서버 내부 에러.")
    })
    @DeleteMapping("/user/{email}/fav/{index}")
    public ResponseEntity<?> favoriteRemove(@PathVariable(name="email")String email,
                                            @PathVariable(name="index")Integer index){
        return userService.favoriteRemove(email,index);
    }
}
