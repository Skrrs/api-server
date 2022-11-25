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
import com.mask.api.domain.user.dto.login.MainResponseDto;
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
    public ResponseEntity<?> mainRequest(String email){
        var userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        var user = userOptional.get();
        var beginner_size = problemRepository.findProblemsByLevel("beginner").size();
        var intermediate_size = problemRepository.findProblemsByLevel("intermediate").size();
        var advanced_size = problemRepository.findProblemsByLevel("advanced").size();

        var beginner_ptg = ((double)user.getProgress().getBeginner().size() / beginner_size) * 100;
        var intermediate_ptg = ((double)user.getProgress().getIntermediate().size() / intermediate_size) * 100;
        var advanced_ptg = ((double)user.getProgress().getAdvanced().size() / advanced_size) * 100;

        var responseDto = MainResponseDto.builder()
                .beginner((int)beginner_ptg)
                .intermediate((int)intermediate_ptg)
                .advanced((int)advanced_ptg)
                .build();

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
        String level_s = null;
        switch(level){
            case 1: level_s = "beginner"; break;
            case 2: level_s = "intermediate"; break;
            case 3: level_s = "advanced"; break;
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
            }
        }

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
    public ResponseEntity<?> favoriteRequest(String email){
        var userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        var user = userOptional.get();
        var pbs = user.getLibrary();
        List<String> urls = new ArrayList<>();
        List<String> sentences = new ArrayList<>();
        List<String> prons = new ArrayList<>();
        List<String> engs = new ArrayList<>();
        List<Integer> idxs = new ArrayList<>();
        pbs.forEach(
                pb->{
                    idxs.add(pb.getIdx());
                    sentences.add(pb.getAnswer());
                    prons.add(pb.getPron());
                    engs.add(pb.getEnglish());
                    urls.add(pb.getUrl());
                }
        );
        var responseDto = ProblemResponseDto.builder()
                .index(idxs)
                .sentence(sentences)
                .voiceUrl(urls)
                .pron(prons)
                .english(engs)
                .build();
        log.info("Favorite Request Success Idx:{}",idxs);
        return response.success(responseDto,HttpStatus.OK);
    }

    public ResponseEntity<?> favoriteAdd(String email,Integer level,FavoriteRequestDto favoriteRequestDto){
        var userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        var user = userOptional.get();
        var idxs = favoriteRequestDto.getProblem();
        var corrected = favoriteRequestDto.getCorrected();
        var library_pbs = user.getLibrary();
        List<Problem> level_pbs;
        HashSet<Integer> progress;

        // 즐겨찾기 추가.
        idxs.forEach(
                idx->{
                    var pb = problemRepository.findProblemByIdx(idx);
                    library_pbs.add(pb);
                }
        );
        user.setLibrary(library_pbs);

        // user 해당 난이도별 성취율,DB 인덱스들 가져옴.
        switch(level){
            case 1:
                level_pbs = problemRepository.findProblemsByLevel("beginner");
                progress = user.getProgress().getBeginner();break;
            case 2:
                level_pbs = problemRepository.findProblemsByLevel("intermediate");
                progress = user.getProgress().getIntermediate();break;
            case 3:
                level_pbs = problemRepository.findProblemsByLevel("advanced");
                progress = user.getProgress().getAdvanced();break;
            default:
                throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        corrected.forEach(
                idx -> {
                    if (level_pbs.contains(problemRepository.findProblemByIdx(idx))) {
                        //해당 레벨 문제중에 없는 인덱스면 에러.
                        throw new CustomException(ErrorCode.INVALID_ACCESS);
                    }
                    else {
                        progress.add(idx);
                    }
                }
        );
        // user 해당 난이도 성취율 기록.
        if(level == 1){ //beginner
            user.getProgress().setBeginner(progress);
        }
        else if(level == 2){ //intermediate
            user.getProgress().setIntermediate(progress);
        }
        else { //advanced
            user.getProgress().setAdvanced(progress);
        }
        userRepository.save(user);

        log.info("Add Favorite Success Idx:{}",idxs);
        log.info("Progress Update Success corrected:{}",corrected);
        return response.success(null,HttpStatus.OK);
    }
    public ResponseEntity<?> favoriteRemove(String email,Integer index){
        var userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        var user = userOptional.get();
        var pbs = user.getLibrary();
        var pb = problemRepository.findProblemByIdx(index);
        if(pbs.contains(pb)) {
            pbs.remove(pb);
        }
        else{
            throw new CustomException(ErrorCode.INVALID_ACCESS); //즐겨찾기에 없는 인덱스면 Error.
        }
        user.setLibrary(pbs);
        userRepository.save(user);
        log.info("Remove Favorite Success Idx:{}",index);
        return response.success(null,HttpStatus.OK);
    }
}
