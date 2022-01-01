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
    
    // Scheduler�� ������ ����� ����Ѵ�.
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
 * Quartz Scheduler ����
 */
class JobLuancher {
    public static void main(String[] args) {
        try {
            // JOB DataMap ��ü�� Quartz���� ����Ǵ� Job�� Key-Value �������� �����͸� �����Ѵ�.
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("message", "Hello, Quartz!!!");
            
            // JOB ����
            // ������ ������ �ۼ��� Job Ŭ������ �Ű������� �����Ͽ� ���ο� Job�� �����Ѵ�.
            JobDetail jobDetail = JobBuilder.newJob(SampleJobExecutor.class)
                                    .withIdentity("job_name", "job_group")
                                    .setJobData(jobDataMap)
                                    .build();
            
            // SimpleTrigger ����
            // 4�ʸ��� �ݺ��ϸ�, �ִ� 5ȸ ����
            SimpleScheduleBuilder simpleSch = SimpleScheduleBuilder.repeatSecondlyForTotalCount(5, 4);
            SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                                            .withIdentity("simple_trigger", "simple_trigger_group")
                                            .withSchedule(simpleSch)
                                            .forJob(jobDetail)
                                            .build();
 
            // CronTrigger ����
            // 15���ֱ�� �ݺ�( 0, 15, 30, 45 )
            CronScheduleBuilder cronSch = CronScheduleBuilder.cronSchedule(new CronExpression("0/15 * * * * ?"));
            CronTrigger cronTrigger = (CronTrigger) TriggerBuilder.newTrigger()
                                        .withIdentity("cron_trigger", "cron_trigger_group")
                                        .withSchedule(cronSch)
                                        .forJob(jobDetail)
                                        .build();
            
            // JobDtail : Trigger = 1 : N ����
            Set<Trigger> triggerSet = new HashSet<Trigger>();
            triggerSet.add(simpleTrigger);
            triggerSet.add(cronTrigger);

            
            // Scheduler ����
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            
            // Scheduler ���
            scheduler.scheduleJob(jobDetail, triggerSet, false);
            
        } catch (ParseException | SchedulerException e) {
            e.printStackTrace();
        }
    }
}