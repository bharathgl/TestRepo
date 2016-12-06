package application.job;

import org.apache.commons.logging.Log;
import org.joda.time.DateTime;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class UserGitJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(UserGitJob.class);

    private RestTemplate restTemplate ;


//    public static final String COUNT = "count";
    public static final String LAST_EXECUTION = "LAST_EXECUTION";

    protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap dataMap = ctx.getJobDetail().getJobDataMap();
        String userName = dataMap.getString("userName");
        JobKey jobKey = ctx.getJobDetail().getKey();
        log.info("UserName passed: {}",  userName);
//        log.info("cronExpression passed: {}",  cronExpressionString);

        dataMap.put(LAST_EXECUTION, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        ctx.setResult("ok");
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Autowired
    public void setRestTemplate(@Qualifier("restTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
