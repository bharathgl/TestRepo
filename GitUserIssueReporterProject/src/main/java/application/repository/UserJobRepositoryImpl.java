package application.repository;

import application.Entity.UserJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class UserJobRepositoryImpl implements UserJobRepositoryCustom {

    private MongoTemplate template;

    @Autowired
    public UserJobRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public int updateUserJob(String jobName, String jobExpression, String userId, String jobId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("jobId").in(jobId));

        Update update = new Update();
        update.set("jobName", jobName );
        update.set("jobCronExpression", jobExpression);

        return template.updateMulti(query, update, UserJob.class).getN();
    }

//    @Override
//    public int editUserStatuses(String projectId, List<String> userIds, ProjectStatus status) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("_id.projectId").is(projectId));
//        query.addCriteria(Criteria.where("_id.userId").in(userIds));
//
//        Update update = new Update();
//        update.set("status", status);
//
//        return template.updateMulti(query, update, UserProjectMembership.class).getN();
//    }
//
//    @Override
//    public int editUsersRole(String projectId, List<String> userIds, RoleType roleType, String contentAccessGroupId, Long lastModifiedTime) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("_id.projectId").is(projectId));
//        query.addCriteria(Criteria.where("_id.userId").in(userIds));
//
//        Update update = new Update();
//        update.set("contentAccessGroupId", contentAccessGroupId);
//        update.set("roleType", roleType);
//        update.set("lastModifiedTime",lastModifiedTime);
//
//        return template.updateMulti(query, update, UserProjectMembership.class).getN();
//    }
//
//    @Override
//    public Long getCountOfUsersOnRoleForProjectInStatus(String projectId, String groupId, ProjectStatus projectStatus) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("_id.projectId").is(projectId));
//        query.addCriteria(Criteria.where("contentAccessGroupId").is(groupId));
//        query.addCriteria(Criteria.where("status").is(projectStatus.name()));
//        return template.count(query, UserProjectMembership.class);
//    }
//
//    @Override
//    public List<UserProjectMembership> getUsersOnRoleForProjectInStatus(String projectId, String groupId, List<ProjectStatus> projectStatuses) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("_id.projectId").is(projectId));
//        query.addCriteria(Criteria.where("contentAccessGroupId").is(groupId));
//        query.addCriteria(Criteria.where("status").in(projectStatuses));
//        return template.find(query, UserProjectMembership.class);
//    }
}
