package application.service;

import application.Entity.RoleType;
import application.Entity.User;
import application.Entity.UserJob;
import application.job.MyJobTwo;
import application.job.UserGitJob;
import application.model.JobDetailsRequest;
import application.model.JobDetailsUpdateRequest;
import application.repository.UserJobRepository;
import application.repository.UserRepository;
import org.joda.time.DateTime;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class UserJobServiceImpl implements UserJobsService {

    private static final Logger log = LoggerFactory.getLogger(UserJobServiceImpl.class);

    private UserJobRepository userJobRepository;

    private UserRepository userRepository;

    private SchedulerFactoryBean schedulerFactoryBean;

//    @Value("#{'${userAccess.permissions.reviewer}'.split(',')}")
//    private List<String> reviewerPermissions;
//
//    @Value("#{'${userAccess.permissions.admin}'.split(',')}")
//    private List<String> adminPermissions;

    @Autowired
    public UserJobServiceImpl(SchedulerFactoryBean schedulerFactoryBean,
                              UserJobRepository userJobRepository, UserRepository userRepository) {
        this.schedulerFactoryBean = schedulerFactoryBean;
        this.userJobRepository = userJobRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<UserJob> getScheduledUserJobs(String userId) {
        return userJobRepository.findJobsByUserId(userId);
    }

    @Override
    public UserJob createUserJob(String userId, JobDetailsRequest jobDetailsRequest) {
        UserJob userJob = new UserJob();
        userJob.setJobName(jobDetailsRequest.getJobName());
        userJob.setJobId(UUID.randomUUID().toString());
        userJob.setJobCronExpression(jobDetailsRequest.getJobCronExpression());
        userJob.setUserId(userId);
        userJob = userJobRepository.save(userJob);


        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setKey(new JobKey("userjob_"+UUID.randomUUID().toString(), "RecurringJob"));
        jobDetail.setJobClass(UserGitJob.class);
        jobDetail.setDurability(true);
        JobDataMap map = new JobDataMap();
        map.put("userName", userId);
        jobDetail.setJobDataMap(map);

        try {
            schedulerFactoryBean.getScheduler().addJob(jobDetail, true, true);

            String cronExpressionString = jobDetailsRequest.getJobCronExpression();
            log.info("cronExpression : {}",  cronExpressionString);
//            if (!schedulerFactoryBean.getScheduler().checkExists(new TriggerKey("trigger2", "mygroup"))) {
            Trigger trigger = newTrigger()
                    .forJob(jobDetail)
                    .withIdentity("trigger1", "RecurringJob")
                    .withPriority(50)
                    // Job is scheduled for 3+1 times with the interval of 30 seconds
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionString))
                    .startAt(DateTime.now().plusSeconds(3).toDate())
                    .build();
            schedulerFactoryBean.getScheduler().scheduleJob(trigger);
//            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return userJob;
    }

    @Override
    public List<UserJob> listAllUserJobs() {
        return userJobRepository.findAll();
    }

    @Override
    public List<UserJob> listJobsByUserId(String userId) {
        return userJobRepository.findJobsByUserId(userId);
    }

    @Override
    public void updateUserJob(String userId,String jobId, JobDetailsUpdateRequest jobDetailsUpdateRequest) {
        UserJob userJob = userJobRepository.findJobByIdForUser(jobId, userId);
        if(userJob == null){
            throw new NotFoundException("Job not found!!");
        } else if(!userJob.getUserId().equals(userId)){
            throw new NotFoundException("No Job found for this user!!");
        }
        userJob.setJobName(jobDetailsUpdateRequest.getName());
        userJob.setJobCronExpression(jobDetailsUpdateRequest.getJobCronExpression());
        userJobRepository.updateUserJob(userJob.getJobName(),userJob.getJobCronExpression(),
                userJob.getUserId(),userJob.getJobId());
    }

    @Override
    public void deleteUserJob(String jobId, String userId) {
        userJobRepository.delete(userJobRepository.findJobByIdForUser(jobId,userId));
    }

    @Override
    public UserJob getUserJob(String userId, String jobId) {
        return userJobRepository.findJobByIdForUser(jobId,userId);
    }

    @Override
    public User seedAdminUser() {
        User user  = userRepository.findUserByName("admin");
        if(user != null){
            return user;
        }
        user = new User();
        user.setUserName("admin");
        user.setPassword("password");
        user.setRoleType(RoleType.ADMIN);
        return userRepository.save(user);
    }

//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_USERS_LIST + "')")
//    public List<ProjectProfile> getRegisteredUsersForProject(String projectId) {
//        List<UserProjectMembership> memberships = userProjectMembershipRepository.findRegisteredUsersByProjectId(projectId);
//        return getUsersForProject(memberships);
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_CAG_READ + "')")
//    public Long getUsersCountOnRoleForProjectInStatus(String projectId, String groupId, ProjectStatus projectStatus){
//        return userProjectMembershipRepository.getCountOfUsersOnRoleForProjectInStatus(projectId,groupId,projectStatus);
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_CAG_READ + "')")
//    public List<UserProjectMembership> getUsersOnRoleForProjectInStatus(String projectId, String groupId){
//        return  userProjectMembershipRepository.getUsersOnRoleForProjectInStatus(projectId,groupId, projectStatuses);
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_USERS_LIST + "')")
//    public List<ProjectProfile> getUnRegisteredUsersForProject(String projectId) {
//        List<UserProjectMembership> memberships = userProjectMembershipRepository.findUnRegisteredUsersByProjectId(projectId);
//        return getUsersForProject(memberships);
//    }
//
//    private List<ProjectProfile> getUsersForProject(List<UserProjectMembership> memberships) {
//        List<ProjectProfile> profiles = new ArrayList<>();
//
//
//        Set<String> contentAccessGroupIds = new HashSet<>();
//        memberships.forEach(userProjectMembership ->
//                contentAccessGroupIds.add(userProjectMembership.getContentAccessGroupId()));
//
//        if(!memberships.isEmpty()) {
//            for (UserProjectMembership membership : memberships) {
//                ProjectProfile profile = new ProjectProfile();
//                profile.setProjectId(membership.getProjectId());
//                profile.setUserId(membership.getUserId());
//                profile.setStatus(membership.getStatus());
//                profile.setContentAccessGroupId(membership.getContentAccessGroupId());
//                Long lastModifiedTimeUTC = membership.getLastModifiedTime();
//                Long lastModifiedTimeCST = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastModifiedTimeUTC),
//                        ZoneId.of(ZoneId.SHORT_IDS.get("CST"))).toEpochSecond() * MILLISECONDS;
//                profile.setLastAccessTime(lastModifiedTimeCST);
//                profile.setPermissions(getPermissions(membership.getRoleType()));
//
//                profiles.add(profile);
//            }
//        }
//
//        return profiles;
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_USERS_LIST + "')")
//    public ProjectProfile getUserProfile(String projectId, String userId) {
//        return getUserProfileInternal(projectId, userId);
//    }
//
//    /**
//     * @deprecated
//     * @param projectId
//     * @param userId
//     * @return returns user's ProjectProfile
//     */
//    @Override
//    @Deprecated // Should generate a JWT for the caller.
//    public ProjectProfile getUserProfileInternal(String projectId, String userId) {
//        UserProjectMembership membership = userProjectMembershipRepository.findOne(
//                new UserProjectProfileKey(projectId, userId));
//
//        if(membership == null) {
//            throw new NotFoundException("User " + userId + " not found on project " + projectId);
//        }
//
//        ProjectProfile profile = new ProjectProfile();
//        profile.setProjectId(membership.getProjectId());
//        profile.setUserId(membership.getUserId());
//        profile.setStatus(membership.getStatus());
//        profile.setContentAccessGroupId(membership.getContentAccessGroupId());
//        profile.setLastAccessTime(ZonedDateTime.now(ZoneId.of("UTC"))
//                .minusHours(RandomUtils.nextInt(0,96))
//                .minusMinutes(RandomUtils.nextInt(0,60))
//                .toInstant().toEpochMilli());
//        profile.setPermissions(getPermissions(membership.getRoleType()));
//
//        return profile;
//    }
//
//    private List<String> getPermissions(RoleType type){
//        if(type == RoleType.ADMIN){
//            return adminPermissions;
//        } else if(type == RoleType.REVIEWER){
//            return reviewerPermissions;
//        } else {
//            return new ArrayList<>();
//        }
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_USERS_EDIT + "')")
//    public void deleteUserProfile(String projectId, String userId) {
//        UserProjectMembership membership = userProjectMembershipRepository.findOne(
//                new UserProjectProfileKey(projectId, userId));
//
//        if(membership == null) {
//            throw new NotFoundException("User " + userId + " not found on project " + projectId);
//        }
//
//        userProjectMembershipRepository.delete(membership);
//    }
//
//    @Override
//    public void deleteUserProfileInternal(String projectId, String userId) {
//        deleteUserProfile(projectId, userId);
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_USERS_EDIT + "')")
//    public UserProjectMembership updateUserProfile(String projectId, String userId, RoleType roleType,
//                                                   String contentAccessGroupId, String status) {
//        UserProjectMembership membership = userProjectMembershipRepository.findOne(
//                new UserProjectProfileKey(projectId, userId));
//        if(membership == null){
//            throw new NotFoundException("User " + userId + " not found on project " + projectId);
//        }
//        membership.setStatus(ProjectStatus.fromName(status));
//        membership.setRoleType(roleType);
//        membership.setContentAccessGroupId(contentAccessGroupId);
//        membership.setLastModifiedTime(ZonedDateTime.now(ZoneId.of("UTC"))
//                .toInstant().toEpochMilli());
//        //throws an exception if not found
//        userProjectMembershipRepository.findOne(new UserProjectProfileKey(projectId, userId));
//        return userProjectMembershipRepository.save(membership);
//    }
//
//     @Override
//     @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_USERS_ADD + "')")
//     public UserProjectMembership createProjectProfileForUser(String userId, String projectId, RoleType roleType,
//                                                              String contentAccessGroupId, String startingStatus) {
//         UserProjectMembership profile = new UserProjectMembership();
//         UserProjectProfileKey key = new UserProjectProfileKey();
//
//         key.setUserId(userId);
//         key.setProjectId(projectId);
//         profile.setId(key);
//         profile.setStatus(ProjectStatus.fromName(startingStatus));
//         profile.setContentAccessGroupId(contentAccessGroupId);
//         profile.setRoleType(roleType);
//         profile.setLastModifiedTime(ZonedDateTime.now(ZoneId.of("UTC"))
//                 .toInstant().toEpochMilli());
//         return saveProjectMembership(profile);
//     }
//
//    @Override
//    public UserProjectMembership createProjectProfileForUserInternal(String userId, String projectId, RoleType roleType,
//                                                                     String contentAccessGroupId, String status) {
//        return createProjectProfileForUser(userId, projectId, roleType, contentAccessGroupId, status);
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_USERS_EDIT + "')")
//    public BulkChangeRoleResult updateMultipleUsersProfile(String projectId, RoleType roleType,
//                                                           String contentAccessGroupId, UserIdsRequest userIds) {
//        BulkChangeRoleResult bulkChangeRoleResult;
//        List<String> unfitUsers = new ArrayList<>();
//
//        for(String userId: userIds.getUserIds()) {
//            UserProjectMembership membership = userProjectMembershipRepository.findOne(
//                    new UserProjectProfileKey(projectId, userId));
//            if (membership == null) {
//                unfitUsers.add(userId);
//            }
//        }
//        userIds.getUserIds().removeAll(unfitUsers);
//        long lastModifiedTime = ZonedDateTime.now(ZoneId.of("UTC"))
//                .toInstant().toEpochMilli();
//        userProjectMembershipRepository.editUsersRole(projectId, userIds.getUserIds(), roleType,
//                contentAccessGroupId,lastModifiedTime);
//        bulkChangeRoleResult = new BulkChangeRoleResult();
//        bulkChangeRoleResult.setProjectId(projectId);
//        bulkChangeRoleResult.setUsersUpdated(userIds.getUserIds());
//        bulkChangeRoleResult.setUsersNotFound(unfitUsers);
//        bulkChangeRoleResult.setContentAccessGroupId(contentAccessGroupId);
//        bulkChangeRoleResult.setRoleType(roleType);
//
//        return bulkChangeRoleResult;
//    }
//
//    @Override
//    public BulkChangeRoleResult assignUsersNoRole(String projectId, UserIdsRequest userIds){
//        BulkChangeRoleResult bulkChangeRoleResult;
//        List<String> unfitUsers = new ArrayList<>();
//        for(String userId: userIds.getUserIds()) {
//            UserProjectMembership membership = userProjectMembershipRepository.findOne(
//                    new UserProjectProfileKey(projectId, userId));
//            if (membership == null) {
//                unfitUsers.add(userId);
//            }
//        }
//        userIds.getUserIds().removeAll(unfitUsers);
//        long lastModifiedTime = ZonedDateTime.now(ZoneId.of("UTC"))
//                .toInstant().toEpochMilli();
//        userProjectMembershipRepository.editUsersRole(projectId, userIds.getUserIds(), null, null,lastModifiedTime);
//        bulkChangeRoleResult = new BulkChangeRoleResult();
//        bulkChangeRoleResult.setProjectId(projectId);
//        bulkChangeRoleResult.setUsersUpdated(userIds.getUserIds());
//        bulkChangeRoleResult.setUsersNotFound(unfitUsers);
//        bulkChangeRoleResult.setContentAccessGroupId(null);
//        bulkChangeRoleResult.setRoleType(null);
//
//        return bulkChangeRoleResult;
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_USERS_REMOVE + "')")
//    public BulkJobDeleteResult deleteMultipleUsersProfile(String projectId, List<String> userIds) {
//        BulkJobDeleteResult bulkDeleteResult;
//        List<String> unfitUsers = new ArrayList<>();
//
//        for(String userId: userIds) {
//            UserProjectMembership membership = userProjectMembershipRepository.findOne(
//                    new UserProjectProfileKey(projectId, userId));
//            if (membership == null) {
//                unfitUsers.add(userId);
//            } else {
//                userProjectMembershipRepository.delete(membership);
//            }
//        }
//        userIds.removeAll(unfitUsers);
//        bulkDeleteResult = new BulkJobDeleteResult();
//        bulkDeleteResult.setProjectId(projectId);
//        bulkDeleteResult.setUsersDeleted(userIds);
//        bulkDeleteResult.setUsersNotDeleted(unfitUsers);
//        bulkDeleteResult.setAction("DELETE");
//
//        return bulkDeleteResult;
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_USERS_ADD + "')")
//    public UserProjectMembership saveProjectMembership(UserProjectMembership profile) {
//        return userProjectMembershipRepository.save(profile);
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_USERS_EDIT + "')")
//    public BulkJobEditResult activateUsers(String projectId, List<String> userIds) {
//        return bulkEdit(projectId, userIds, "ACTIVATE", userProjectMembershipRepository.editUserStatuses(
//                projectId, userIds, ProjectStatus.ACTIVE));
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + ProjectPermissions.PROJECT_USERS_EDIT + "')")
//    public BulkJobEditResult deactivateUsers(String projectId, List<String> userIds) {
//        return bulkEdit(projectId, userIds, "DEACTIVATE", userProjectMembershipRepository.editUserStatuses(
//                projectId, userIds, ProjectStatus.DEACTIVATED));
//    }
//
//    private BulkJobEditResult bulkEdit(String projectId, List<String> userIds, String action, int usersUpdatedCount){
//        BulkJobEditResult result = new BulkJobEditResult();
//        result.setProjectId(projectId);
//        result.setAction(action);
//
//        if(usersUpdatedCount < userIds.size()){
//            List<UserProjectProfileKey> keys = new ArrayList<>();
//            userIds.forEach(s -> keys.add(new UserProjectProfileKey(projectId,s)));
//            List<UserProjectMembership> foundUserMemberships = Lists.newArrayList(userProjectMembershipRepository.findAll(keys));
//            List<String> foundUserIds = new ArrayList<>();
//            foundUserMemberships.forEach(m -> foundUserIds.add(m.getUserId()));
//            result.setUsersUpdated(foundUserIds);
//            List<String> notFoundUsers = new ArrayList<>();
//            notFoundUsers.addAll(userIds);
//            notFoundUsers.removeAll(foundUserIds);
//            userIds.retainAll(foundUserIds);
//            result.setUsersNotFound(notFoundUsers);
//        } else {
//            result.setUsersUpdated(userIds);
//        }
//
//        return result;
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + PlatformPermissions.PLATFORM_GAG_EDIT + "')")
//    public GlobalAccessGroup saveGlobalAccessGroup(GlobalAccessGroup globalAccessGroup) {
//        return globalAccessGroupRepository.save(globalAccessGroup);
//    }
//
//    @Override
//    @IsCurrentUserOrHasPermissionUsersList
//    public List<UserProjectMembership> getProjectsForUser(String userId) {
//        return userProjectMembershipRepository.findByUserId(userId);
//    }
//
//    @Override
//    public List<UserProjectMembership> getProjectsForUserInternal(String userId) {
//        return userProjectMembershipRepository.findByUserId(userId);
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + PlatformPermissions.PLATFORM_GAG_LIST + "')")
//    public List<GlobalAccessGroup> listAllGlobalAccessGroups() {
//        return globalAccessGroupRepository.findAll();
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + PlatformPermissions.PLATFORM_GAG_LIST + "')")
//    public GlobalAccessGroup getGlobalAccessGroup(String globalAccessGroupId) {
//        GlobalAccessGroup globalAccessGroup = globalAccessGroupRepository.findOne(
//                globalAccessGroupId.toUpperCase().trim());
//        if(globalAccessGroup == null) {
//            throw new NotFoundException("Global access group "+globalAccessGroupId+" does not exists!!.");
//
//        }
//        return globalAccessGroup;
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + PlatformPermissions.PLATFORM_GAG_LIST + "')")
//    public GlobalAccessGroup getGlobalAccessGroupByName(String globalAccessGroupName) {
//        GlobalAccessGroup globalAccessGroup = globalAccessGroupRepository.findByRoleName(
//                globalAccessGroupName.toUpperCase().trim());
//        if(globalAccessGroup == null) {
//            throw new NotFoundException("Global access group "+globalAccessGroupName+" does not exists!!.");
//
//        }
//        return globalAccessGroup;
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + PlatformPermissions.PLATFORM_GAG_ADD + "')")
//    public GlobalAccessGroup createGlobalAccessGroup(GlobalAccessGroupRequest globalAccessGroupRequest) {
//        GlobalAccessGroup existingGlobalAccessGroup = globalAccessGroupRepository.findOne(
//                globalAccessGroupRequest.getRoleId().toUpperCase().trim());
//        if(existingGlobalAccessGroup != null) {
//            throw new RoleAlreadyExistsException("Role "+ globalAccessGroupRequest.getRoleId()+" already exists!!.");
//        }
//        GlobalAccessGroup newGlobalAccessGroup = new GlobalAccessGroup();
//        newGlobalAccessGroup.setRoleId(globalAccessGroupRequest.getRoleId().toUpperCase().trim());
//        newGlobalAccessGroup.setRoleName(globalAccessGroupRequest.getName());
//        newGlobalAccessGroup.setPermissionList(globalAccessGroupRequest.getGlobalAccessGroupPermissionList());
//        return saveGlobalAccessGroup(newGlobalAccessGroup);
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + PlatformPermissions.PLATFORM_GAG_EDIT + "')")
//    public GlobalAccessGroup updateGlobalAccessGroup(String globalAccessGroupId , JobDetailsUpdateRequest
//            globalAccessGroupUpdateRequest) {
//        GlobalAccessGroup existingGlobalAccessGroup = globalAccessGroupRepository.findOne(
//                globalAccessGroupId.toUpperCase().trim());
//        if(existingGlobalAccessGroup == null) {
//            throw new NotFoundException("Global Role "+ globalAccessGroupId + " not found!!.");
//        }
//        existingGlobalAccessGroup.setPermissionList(globalAccessGroupUpdateRequest.
//                getGlobalAccessGroupPermissionList());
//        existingGlobalAccessGroup.setRoleName(globalAccessGroupUpdateRequest.getName());
//        return saveGlobalAccessGroup(existingGlobalAccessGroup);
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + PlatformPermissions.PLATFORM_GAG_REMOVE + "')")
//    public void deleteGlobalAccessGroup(String globalAccessGroupId) {
//        GlobalAccessGroup existingGlobalAccessGroup = globalAccessGroupRepository.findOne(
//                globalAccessGroupId.toUpperCase().trim());
//        if(existingGlobalAccessGroup == null) {
//            throw new NotFoundException("Global Role"+ globalAccessGroupId + "not found!!.");
//        }
//        globalAccessGroupRepository.delete(globalAccessGroupId);
//    }
//
//    @Override
//    @IsCurrentUserOrHasPermissionUsersList
//    public JobDetailsRequest getUserProfile(String userId) {
//        //We don't have GAGs wired up yet, so we know they have no special permissions.
//        return new JobDetailsRequest().setUserId(userId).setPermissions(new ArrayList<>());
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + PlatformPermissions.PLATFORM_GAG_LIST + "')")
//    public List<String> getGlobalAccessGroupPerms(String globalAccessGroupId) {
//        GlobalAccessGroup globalAccessGroup = globalAccessGroupRepository.findOne(
//                globalAccessGroupId.toUpperCase().trim());
//
//        if(globalAccessGroup == null) {
//            throw new NotFoundException("Global access group "+globalAccessGroupId+" does not exist!.");
//        }
//
//        return globalAccessGroup.getPermissionList().stream()
//                .map(GlobalAccessGroupPermission::getPermissionKey)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @PreAuthorize("hasAuthority('" + PlatformPermissions.PLATFORM_GAG_LIST + "')")
//    public List<String> getGlobalAccessGroupMembershipPerms(String serviceId) {
//        List<String> roles = gagMembershipRepository.findServicePermissions(serviceId).stream()
//                .map(GlobalAccessGroupMembership::getId)
//                .map(GlobalAccessGroupMembershipKey::getRoleId)
//                .collect(Collectors.toList());
//        if(roles.isEmpty()) {
//            return new ArrayList<>();
//        }
//        return globalAccessGroupRepository.getPermissionsForRoles(roles).stream()
//                .map(GlobalAccessGroup::getPermissionList)
//                .flatMap(List::stream)
//                .filter(permission -> permission != null)
//                .map(GlobalAccessGroupPermission::getPermissionKey)
//                .distinct()
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public GlobalAccessGroupMembership createGagMembership(String roleId, String serviceId) {
//        GlobalAccessGroupMembershipKey id = new GlobalAccessGroupMembershipKey(roleId, serviceId);
//        if(gagMembershipRepository.findOne(id) != null) {
//            throw new MembershipAlreadyExistsException(roleId, serviceId);
//        }
//        return gagMembershipRepository.save(new GlobalAccessGroupMembership().setId(id));
//    }
//
//    @Override
//    public GlobalAccessGroupMembership getGlobalAccessGroupMembership(String roleId, String serviceId) {
//        GlobalAccessGroupMembershipKey id = new GlobalAccessGroupMembershipKey(roleId, serviceId);
//        GlobalAccessGroupMembership gagMembership = gagMembershipRepository.findOne(id);
//        if(gagMembership == null) {
//            throw new NotFoundException("Service " + serviceId + " not found for role " + roleId + ".");
//        }
//        return gagMembership;
//    }
//
//    @Override
//    public List<GlobalAccessGroupMembership> getGlobalAccessGroupMemberships() {
//        return gagMembershipRepository.findAll();
//    }
//
//    @Override
//    public GlobalAccessGroupMembership deleteGagMembership(String roleId, String serviceId) {
//        GlobalAccessGroupMembershipKey id = new GlobalAccessGroupMembershipKey(roleId, serviceId);
//        GlobalAccessGroupMembership gagMembership = gagMembershipRepository.findOne(id);
//        if(gagMembership == null) {
//            throw new NotFoundException("Service " + serviceId + " not found for role " + roleId + ".");
//        }
//        gagMembershipRepository.delete(gagMembership);
//        return gagMembership;
//    }
//
//    //For test setters
//    UserJobServiceImpl setReviewerPermissions(List<String> reviewerPermissions) {
//        this.reviewerPermissions = reviewerPermissions;
//        return this;
//    }
//
//    //For test setters
//    UserJobServiceImpl setAdminPermissions(List<String> adminPermissions) {
//        this.adminPermissions = adminPermissions;
//        return this;
//    }
}
