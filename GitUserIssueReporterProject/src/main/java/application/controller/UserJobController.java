package application.controller;

import application.Entity.RoleType;
import application.Entity.User;
import application.Entity.UserJob;
import application.model.JobDetailsRequest;
import application.model.JobDetailsUpdateRequest;
import application.service.UserJobsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value="api/v1/user", produces= MediaType.APPLICATION_JSON_VALUE)
public class UserJobController {

    private UserJobsService service;

    @Autowired
    public UserJobController(UserJobsService userJobsService) {
        this.service = userJobsService;
    }

    @RequestMapping(value="/{userId}/job/{jobId}", method = RequestMethod.GET)
    public UserJob getUserJobById(@PathVariable String userId, @PathVariable String jobId) {
        return service.getUserJob(userId,jobId);
    }

    @RequestMapping(value="/{userId}/jobs", method = RequestMethod.GET)
    public List<UserJob> getUserJobsById(@PathVariable String userId) {
        return service.listJobsByUserId(userId);
    }

    @RequestMapping(value="/all/jobs", method = RequestMethod.GET)
    public List<UserJob> getAllUserJobs() {
        return service.listAllUserJobs();
    }

    /**
     * Deprecated public delete user job method
     * @param userId
     * @param jobId
     */
    @Deprecated
    @RequestMapping(value="/{userId}/jobs/{jobId}", method = RequestMethod.DELETE)
    public void deleteUserJob(@PathVariable String userId, @PathVariable String jobId){
        service.deleteUserJob(userId, jobId);
    }

    @RequestMapping(value="/{userId}/job/{jobId}", method = RequestMethod.PUT)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "Authorization", value = "Javelin Authorization token",
//                    required = true, dataType = "string", paramType = "header", defaultValue = "Bearer X")
//    })
    public void updateUserJob(@PathVariable String userId,
                                  @PathVariable String jobId,
                                  @RequestBody JobDetailsUpdateRequest jobDetailsUpdateRequest){
        service.updateUserJob(userId, jobId, jobDetailsUpdateRequest);
    }

    @RequestMapping(value = "/{userId}/job/create", method = RequestMethod.POST)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "Authorization", value = "Javelin Authorization token",
//                    required = true, dataType = "string", paramType = "header", defaultValue = "Bearer X")
//    })
    public UserJob createJob(@PathVariable String userId, @Valid @RequestBody JobDetailsRequest jobDetailsRequest){
        return service.createUserJob(userId,jobDetailsRequest);
    }

    @RequestMapping(value = "/admin/seed", method = RequestMethod.GET)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "Authorization", value = "Javelin Authorization token",
//                    required = true, dataType = "string", paramType = "header", defaultValue = "Bearer X")
//    })
    public User seedAdmin(){
        return service.seedAdminUser();
    }

}
