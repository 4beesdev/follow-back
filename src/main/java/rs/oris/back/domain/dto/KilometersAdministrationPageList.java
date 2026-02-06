package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.KilometersAdministration;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KilometersAdministrationPageList {

    private Long totalItems;
    private List<KilometersAdministrationDTO> result;

}
