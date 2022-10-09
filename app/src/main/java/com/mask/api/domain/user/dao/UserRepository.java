package com.mask.api.domain.user.dao;

import com.mask.api.domain.user.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {

}
