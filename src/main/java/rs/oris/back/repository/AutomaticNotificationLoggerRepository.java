package rs.oris.back.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.logs.AutomaticNotificationLogger;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AutomaticNotificationLoggerRepository extends PagingAndSortingRepository<AutomaticNotificationLogger,Long> {

    List<AutomaticNotificationLogger> findAllByTimeAfterAndTimeBeforeAndProvider(LocalDateTime from, LocalDateTime to,int provider,Pageable pageable);
    List<AutomaticNotificationLogger> findAllByTimeAfterAndTimeBefore(LocalDateTime from, LocalDateTime to, Pageable pageable);
    Long countAllByTimeAfterAndTimeBefore(LocalDateTime from, LocalDateTime to);
    Long countAllByTimeAfterAndTimeBeforeAndAndSuccess(LocalDateTime from, LocalDateTime to,Boolean success);

    Long countAllByTimeAfterAndTimeBeforeAndProvider(LocalDateTime from, LocalDateTime to, int provider);

    Long countAllByTimeAfterAndTimeBeforeAndProviderAndSuccess(LocalDateTime from, LocalDateTime to, int provider, boolean success);
}
