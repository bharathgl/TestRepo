package application.service;


import application.Entity.RoleType;
import application.Entity.User;
import application.Entity.UserJob;
import application.model.JobDetailsRequest;
import application.model.JobDetailsUpdateRequest;

import java.util.List;

public interface UserJobsService {

    List<UserJob> getScheduledUserJobs(String userId);

    UserJob createUserJob(String userId, JobDetailsRequest jobDetailsRequest);

    List<UserJob> listAllUserJobs();

    List<UserJob> listJobsByUserId(String userId);

    void updateUserJob(String userId,String jobId, JobDetailsUpdateRequest jobDetailsUpdateRequest);

    void deleteUserJob(String userId, String jobId);

    UserJob getUserJob(String userId, String jobId);

    User seedAdminUser();
}
