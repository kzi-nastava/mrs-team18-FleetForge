package com.ognjen.fleetforge.fragments.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.user.UserInformationDTO;
import com.ognjen.fleetforge.dtos.admin.BlockUserRequestDTO;
import com.ognjen.fleetforge.dtos.admin.GetAllUsersDTO;
import com.ognjen.fleetforge.repository.AdminRepo;
import com.ognjen.fleetforge.repository.UsersRepo;

import java.util.List;

public class UsersListViewModel extends ViewModel {
    private AdminRepo adminRepo;
    private MutableLiveData<List<UserInformationDTO>> users = new MutableLiveData<>();
    private int page=0;
    private int size=3;
    private int pageCount;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public LiveData<List<UserInformationDTO>> getUsers() {
        return users;
    }

    public void setUsers(List<UserInformationDTO> users) {
        this.users.setValue(users);
    }

    public UsersRepo getRepo() {
        return repo;
    }

    public void setRepo(UsersRepo repo) {
        this.repo = repo;
    }

    private UsersRepo repo;

    public UsersListViewModel(){
        repo= new UsersRepo();
        adminRepo= new AdminRepo();
    }

    public LiveData<GetAllUsersDTO> getAllUsers(int page, int size,String email){
        return repo.getAllUsers(page,size,email);
    }

    public LiveData<Boolean> blockUser(Long id, String reason){
        BlockUserRequestDTO requestDTO= new BlockUserRequestDTO();
        requestDTO.setReason(reason);
        return adminRepo.blockUser(id,requestDTO);
    }
}
