package rs.oris.back.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterventionUpdateRequest {
    private Intervention intervention;
    private List<MultipartFile> files;

}