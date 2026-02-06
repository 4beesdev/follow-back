package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeUpdateDTO {

    private Integer groupPrivilegeId;
    private Integer privilegeId;
    private boolean toDelete;
}
