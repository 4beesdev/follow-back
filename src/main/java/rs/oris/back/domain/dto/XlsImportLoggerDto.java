package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.logs.XlsImportLogger;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XlsImportLoggerDto {

    private List<XlsImportLogger> content;
    private long totalElements;

}
