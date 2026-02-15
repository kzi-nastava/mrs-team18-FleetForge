package com.ognjen.fleetforge.dtos.admin;

import com.ognjen.fleetforge.dtos.user.UserInformationDTO;

import java.util.List;

public class GetAllUsersDTO {
    private List<UserInformationDTO> content;
    private boolean first;
    private boolean last;
    private int totalPages;

    public List<UserInformationDTO> getContent() {
        return content;
    }

    public void setContent(List<UserInformationDTO> content) {
        this.content = content;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
