package application.model;

public class JobDetailsRequest {


    private String jobName;

    private String jobCronExpression;

    public JobDetailsRequest() {
    }

    public JobDetailsRequest(String jobName, String jobCronExpression) {
        this.jobName = jobName;
        this.jobCronExpression = jobCronExpression;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobCronExpression() {
        return jobCronExpression;
    }

    public void setJobCronExpression(String jobCronExpression) {
        this.jobCronExpression = jobCronExpression;
    }
}
