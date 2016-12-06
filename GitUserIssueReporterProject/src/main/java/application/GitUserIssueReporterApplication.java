package application;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan("controller")
public class GitUserIssueReporterApplication {

	private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(GitUserIssueReporterApplication.class);

	public static void main(String[] args) throws SchedulerException {
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

		Scheduler sched = schedFact.getScheduler();

		sched.start();
		LOG.debug("Quartz scheduler started!!");
		SpringApplication.run(GitUserIssueReporterApplication.class, args);
	}
}
