package com.naeggeodo.dto;

import com.naeggeodo.entity.user.Users;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AddressDTO {
    @NotBlank
    private final String address;
    @NotBlank
    private final String zonecode;
    @NotBlank
    private final String buildingCode;
    private String user_id;

    public static AddressDTO createFromUser(Users users) {
        return new AddressDTO(users.getAddress(), users.getZonecode(), users.getBuildingCode(), users.getId());
    }
}
