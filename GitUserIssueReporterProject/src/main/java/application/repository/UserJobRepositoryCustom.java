package application.repository;

public interface UserJobRepositoryCustom {

    int updateUserJob(String jobName, String jobExpression, String userId, String jobId);
//    /**
//     * Bulk edit the requested users status.
//     *
//     * Returns the number of records updated.
//     */
//    int editUserStatuses(String projectId, List<String> userIds, ProjectStatus status);
//
//    /**
//     * Bulk edit the requested users role.
//     *
//     * Returns the number of records updated.
//     */
//    int editUsersRole(String projectId, List<String> userIds, RoleType roleType,
//                      String contentAccessGroupId, Long lastModifiedTime);
//
//    /**
//    * Returns the number of users with given status on the cag for the project .
//    */
//    Long getCountOfUsersOnRoleForProjectInStatus(String projectId, String groupId, ProjectStatus projectStatus);
//
//    /**
//     * Returns the list of users with given status on the cag for the project .
//     */
//    List<UserProjectMembership> getUsersOnRoleForProjectInStatus(String projectId, String groupId,
//                                                                 List<ProjectStatus> projectStatuses);
//

}
