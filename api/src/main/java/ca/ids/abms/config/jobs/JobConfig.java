package ca.ids.abms.config.jobs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync(proxyTargetClass=true)
public class JobConfig extends AsyncConfigurerSupport {

    @Value("${abms.jobs.core-pool-size}")
    private Integer corePoolSize;

    @Value("${abms.jobs.max-pool-size}")
    private Integer maxPoolSize;

    @Value("${abms.jobs.queue-capacity}")
    private Integer queueCapacity;

    @Override
    public Executor getAsyncExecutor() {

        // initialize thread pool task executor using application properties
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("ARMS-Job-");
        executor.initialize();

        // wrap executor with security context async delegation to prevent unsafe
        // security context threading issue when reusing threads (see US93009)
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler getTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("ARMS-Schedulr-");
        return taskScheduler;
    }
}
