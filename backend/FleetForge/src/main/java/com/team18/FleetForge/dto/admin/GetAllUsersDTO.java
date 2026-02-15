package com.team18.FleetForge.dto.admin;

import com.team18.FleetForge.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllUsersDTO {
    private List<User> content;
    private boolean first;
    private boolean last;
    private int totalPages;
}
