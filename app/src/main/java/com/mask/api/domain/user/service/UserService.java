package com.mask.api.domain.user.service;

import com.mask.api.domain.problem.dao.ProblemRepository;
import com.mask.api.domain.problem.domain.Problem;
import com.mask.api.domain.user.dao.UserRepository;
import com.mask.api.domain.user.domain.Progress;
import com.mask.api.domain.user.domain.User;
import com.mask.api.domain.user.dto.favorite.FavoriteRequestDto;
import com.mask.api.domain.user.dto.login.LoginRequestDto;
import com.mask.api.domain.user.dto.login.LoginResponseDto;
import com.mask.api.domain.user.dto.login.LogoutRequestDto;
import com.mask.api.domain.problem.dto.ProblemResponseDto;
import com.mask.api.global.common.Response;
import com.mask.api.global.exception.CustomException;
import com.mask.api.global.exception.ErrorCode;
import com.mask.api.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final Response response;
    private final RedisTemplate<String, String> redisTemplate;
    private Integer TEST_NUM = 5;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) throw new UsernameNotFoundException(email);

        return optionalUser.get();
    }

    public ResponseEntity<?> login(LoginRequestDto loginRequestDto ){
        if(!userRepository.existsByEmail(loginRequestDto.getEmail())){
            var authority = new HashSet<GrantedAuthority>();
            authority.add(new SimpleGrantedAuthority("ROLE_USER"));
            var progress = Progress.builder()
                    .advanced(new HashSet<>())
                    .intermediate(new HashSet<>())
                    .beginner(new HashSet<>())
                    .build();
            var newUser = User.builder()
                    .email(loginRequestDto.getEmail())
                    .calendar(null)
                    .progress(progress)
                    .library(new HashSet<Problem>())
                    .authorities(authority)
                    .build();
            userRepository.save(newUser);
            log.info("NEW USER CREATED {} ",loginRequestDto.getEmail());
        }
        var user = userRepository.findByEmail(loginRequestDto.getEmail()).get();
        var token = JwtTokenProvider.generateToken(user);
        var responseDto = LoginResponseDto.builder()
                .token(token)
                .build();

        // Redis
        redisTemplate.opsForValue()
                .set(user.getEmail(), token,JwtTokenProvider.ACCESS_TIME
                        , TimeUnit.SECONDS);
        log.info("NEW TOKEN CREATED {}",token);

        return response.success(responseDto,HttpStatus.OK);
    }

    public ResponseEntity<?> logout(LogoutRequestDto logoutRequestDto) {
        var email = logoutRequestDto.getEmail();
        String isLogout = redisTemplate.opsForValue().get(logoutRequestDto.getToken());

        if(!ObjectUtils.isEmpty(isLogout)) throw new CustomException(ErrorCode.INVALID_ACCESS);

        // redis에서 refreshToken 지우기
        if(redisTemplate.opsForValue().get(email)!=null){
            redisTemplate.delete(email);
        }

        // redis black list에 추가
        redisTemplate.opsForValue()
                .set(logoutRequestDto.getToken(), "logout", JwtTokenProvider.ACCESS_TIME, TimeUnit.SECONDS);

        log.info("LOGOUT {}",email);

        return response.success(null,HttpStatus.OK);
    }

    public ResponseEntity<?> requestTest(String email, Integer level){
        var userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        var user = userOptional.get();
        List<String> urls = new ArrayList<>();
        List<String> sentences = new ArrayList<>();
        List<String> prons = new ArrayList<>();
        List<String> engs = new ArrayList<>();
        List<Integer> idxs = new ArrayList<>();
        HashSet<Integer> progress;
        String level_s = null;
        switch(level){
            case 1: level_s = "beginner"; progress = user.getProgress().getBeginner();break;
            case 2: level_s = "intermediate"; progress = user.getProgress().getIntermediate();break;
            case 3: level_s = "advanced"; progress = user.getProgress().getAdvanced();break;
            default: throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
        // 요청한 전체 problem.
        var problems = problemRepository.findProblemsByLevel(level_s);
        var temp = new ArrayList<Integer>();

        // 랜덤하게 TEST_NUM 만큼 중복없이 선택.
        Random random = new Random(); //랜덤 객체 생성
        random.setSeed(System.currentTimeMillis()); //시드값 설정
        for(int i=0;i<TEST_NUM;i++){
            var idx = random.nextInt(problems.size());
            temp.add(idx);
            if(temp.size() != temp.stream().distinct().count()){
                // 중복 제거.
                temp.remove(i);
                i--;
            }
            else{
                urls.add(problems.get(idx).getUrl());
                sentences.add(problems.get(idx).getAnswer());
                prons.add(problems.get(idx).getPron());
                engs.add(problems.get(idx).getEnglish());
                idxs.add(problems.get(idx).getIdx());
                /* Main에서는 (Hashset size) / (해당 level problem size) 로 진행율 전송.*/
                progress.add(problems.get(idx).getIdx()); // user 해당 난이도 진행도 기록.
            }
        }
        // user 해당 난이도 진행도 기록.
        if(level == 1){
            user.getProgress().setBeginner(progress);
        }
        else if(level == 2){
            user.getProgress().setIntermediate(progress);
        }
        else{
            user.getProgress().setAdvanced(progress);
        }
        userRepository.save(user);

        // response dto 작성.
        var responseDto = ProblemResponseDto.builder()
                .index(idxs)
                .sentence(sentences)
                .voiceUrl(urls)
                .pron(prons)
                .english(engs)
                .build();

        log.info("Test Request Success Idx:{} level:{}",idxs,level_s);
        return response.success(responseDto,HttpStatus.OK);
    }

    public ResponseEntity<?> favoriteAdd(String email,FavoriteRequestDto favoriteRequestDto){
        var userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        var user = userOptional.get();
        var idxs = favoriteRequestDto.getProblem();
        var pbs = user.getLibrary();

        for(int i=0;i<idxs.size();i++){
            var idx = idxs.get(i);
            var pb = problemRepository.findProblemByIdx(idx);
            pbs.add(pb);
        }
        user.setLibrary(pbs);
        userRepository.save(user);
        log.info("Add Favorite Success Idx:{}",idxs);
        return response.success(null,HttpStatus.OK);
    }
}
