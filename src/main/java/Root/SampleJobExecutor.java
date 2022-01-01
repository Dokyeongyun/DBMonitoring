package root;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException; 
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
 
/**
 * Quartz Job
 */
public class SampleJobExecutor implements Job {
    
    private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS"); 
    
    // Scheduler가 수행할 기능을 명시한다.
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap jobDataMap = ctx.getJobDetail().getJobDataMap();
        
        String currentDate = TIMESTAMP_FMT.format(new Date());
        String triggerKey = ctx.getTrigger().getKey().toString();
        String message = jobDataMap.getString("message");
        
        System.out.println(String.format("[%s][%s] %s", currentDate, triggerKey, message ));
    }
}
 
/**
 * Quartz Scheduler 실행
 */
class JobLuancher {
    public static void main(String[] args) {
        try {
            // JOB DataMap 객체는 Quartz에서 실행되는 Job에 Key-Value 형식으로 데이터를 전달한다.
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("message", "Hello, Quartz!!!");
            
            // JOB 생성
            // 실행할 내용이 작성된 Job 클래스를 매개변수로 전달하여 새로운 Job을 생성한다.
            JobDetail jobDetail = JobBuilder.newJob(SampleJobExecutor.class)
                                    .withIdentity("job_name", "job_group")
                                    .setJobData(jobDataMap)
                                    .build();
            
            // SimpleTrigger 생성
            // 4초마다 반복하며, 최대 5회 실행
            SimpleScheduleBuilder simpleSch = SimpleScheduleBuilder.repeatSecondlyForTotalCount(5, 4);
            SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                                            .withIdentity("simple_trigger", "simple_trigger_group")
                                            .withSchedule(simpleSch)
                                            .forJob(jobDetail)
                                            .build();
 
            // CronTrigger 생성
            // 15초주기로 반복( 0, 15, 30, 45 )
            CronScheduleBuilder cronSch = CronScheduleBuilder.cronSchedule(new CronExpression("0/15 * * * * ?"));
            CronTrigger cronTrigger = (CronTrigger) TriggerBuilder.newTrigger()
                                        .withIdentity("cron_trigger", "cron_trigger_group")
                                        .withSchedule(cronSch)
                                        .forJob(jobDetail)
                                        .build();
            
            // JobDtail : Trigger = 1 : N 설정
            Set<Trigger> triggerSet = new HashSet<Trigger>();
            triggerSet.add(simpleTrigger);
            triggerSet.add(cronTrigger);

            
            // Scheduler 생성
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            
            // Scheduler 등록
            scheduler.scheduleJob(jobDetail, triggerSet, false);
            
        } catch (ParseException | SchedulerException e) {
            e.printStackTrace();
        }
    }
}