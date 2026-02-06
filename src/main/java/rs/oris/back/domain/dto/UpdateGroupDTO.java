package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupDTO {

    private int groupId;
    private String name;
    private List<PrivilegeUpdateDTO> privilegeUpdateDTOList;

}
