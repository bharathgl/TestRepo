package application.repository;

import application.Entity.User;
import application.Entity.UserJob;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ 'userName' : ?0}")
    User findUserByName(String userName);

    @Query("{ 'userId' : ?0}")
    User findUserById(String userId);
}
