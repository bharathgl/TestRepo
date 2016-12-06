package application.model;

import javax.validation.constraints.NotNull;

public class JobDetailsUpdateRequest {

    @NotNull(message = "Job name can not be null!")
    private String name;

    @NotNull(message = "Job expression can not be null!")
    private String jobCronExpression;

    public String getJobCronExpression() {
        return jobCronExpression;
    }

    public void setJobCronExpression(String jobCronExpression) {
        this.jobCronExpression = jobCronExpression;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}