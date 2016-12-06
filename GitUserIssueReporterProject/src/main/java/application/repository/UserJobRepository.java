package application.repository;

import application.Entity.UserJob;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserJobRepository extends MongoRepository<UserJob, String>, UserJobRepositoryCustom {

    @Query("{ 'jobName' : ?0}")
    UserJob findJobByName(String jobName);

    @Query("{ 'jobId' : ?0 , 'userId' : ?1}")
    UserJob findJobByIdForUser(String jobId, String userId);

    @Query("{'userId':?0}")
    List<UserJob> findJobsByUserId(String userId);

}
