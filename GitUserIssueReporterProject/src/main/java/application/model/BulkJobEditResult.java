package application.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BulkJobEditResult {
    private String projectId;
    private List<String> usersUpdated;
    private List<String> usersNotFound;
    private String action;

    public BulkJobEditResult() {
        usersUpdated = new ArrayList<>();
        usersNotFound = new ArrayList<>();
    }

    public String getProjectId() {
        return projectId;
    }

    public BulkJobEditResult setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public List<String> getUsersUpdated() {
        return usersUpdated;
    }

    public BulkJobEditResult setUsersUpdated(List<String> usersUpdated) {
        this.usersUpdated = usersUpdated;
        return this;
    }

    public List<String> getUsersNotFound() {
        return usersNotFound;
    }

    public BulkJobEditResult setUsersNotFound(List<String> usersNotFound) {
        this.usersNotFound = usersNotFound;
        return this;
    }

    public String getAction() {
        return action;
    }

    public BulkJobEditResult setAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;

        BulkJobEditResult compareObject = (BulkJobEditResult)object;
        return this.getProjectId().equals(compareObject.getProjectId())
                && this.getUsersNotFound().equals(compareObject.getUsersNotFound())
                && this.getUsersUpdated().equals(compareObject.getUsersUpdated())
                && this.getAction().equals(compareObject.getAction());
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId,usersNotFound,usersUpdated,action);
    }
}
