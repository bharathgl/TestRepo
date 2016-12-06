package application.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BulkJobDeleteResult {
    private String projectId;
    private List<String> usersDeleted;
    private List<String> usersNotDeleted;
    private String action;

    public BulkJobDeleteResult() {
        usersDeleted = new ArrayList<>();
        usersNotDeleted = new ArrayList<>();
    }

    public String getProjectId() {
        return projectId;
    }

    public BulkJobDeleteResult setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public List<String> getUsersDeleted() {
        return usersDeleted;
    }

    public BulkJobDeleteResult setUsersDeleted(List<String> usersDeleted) {
        this.usersDeleted = usersDeleted;
        return this;
    }

    public List<String> getUsersNotDeleted() {
        return usersNotDeleted;
    }

    public BulkJobDeleteResult setUsersNotDeleted(List<String> usersNotDeleted) {
        this.usersNotDeleted = usersNotDeleted;
        return this;
    }

    public String getAction() {
        return action;
    }

    public BulkJobDeleteResult setAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        BulkJobDeleteResult compareObj = (BulkJobDeleteResult)object;
        return this.getProjectId().equals(compareObj.getProjectId())
                && this.getAction().equals(compareObj.getAction())
                && this.getUsersDeleted().containsAll(compareObj.getUsersDeleted())
                && this.getUsersNotDeleted().containsAll(compareObj.getUsersNotDeleted());
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId,usersDeleted,usersNotDeleted,action);
    }
}
