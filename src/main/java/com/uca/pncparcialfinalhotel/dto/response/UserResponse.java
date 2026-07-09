package com.uca.pncparcialfinalhotel.dto.response;

import com.uca.pncparcialfinalhotel.model.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
}