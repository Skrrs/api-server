package com.mask.api.domain.problem.dao;

import com.mask.api.domain.problem.domain.Problem;
import com.mask.api.domain.user.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProblemRepository extends MongoRepository<Problem,String> {
    List<Problem> findProblemsByLevel(String level);
    Problem findProblemsByLevelAndIdx(String level,Integer idx);
}
