package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.NotificationApp;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDTO {
    List<NotificationApp> data;
    Long totalPages;
}
