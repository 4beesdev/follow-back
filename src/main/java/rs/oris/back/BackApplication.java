package rs.oris.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import rs.oris.back.schedule.ScheduledTask;
import rs.oris.back.service.NotificationVehicleService;
import rs.oris.back.service.PrivilegeService;
import rs.oris.back.service.UserService;

@SpringBootApplication
@EnableScheduling
public class BackApplication implements ApplicationRunner {

    @Autowired
    private UserService userService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private NotificationVehicleService notificationVehicleService;
    @Autowired
    private ScheduledTask scheduledTasks;

    @EventListener(ApplicationReadyEvent.class)
    public void setUpInternalParams() {
        privilegeService.setUp();
    }

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        scheduledTasks.sendReports();
    }
}
