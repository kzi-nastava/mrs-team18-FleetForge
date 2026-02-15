import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BlockUserRequestDTO, GetAllUsersDTO, UserInformationDTO } from '../../../shared/dtos/users.dtos';

interface PagedResponse<T> {
  content: T[];
}

@Injectable({
  providedIn: 'root',
})
export class UsersListService {
  private apiUsersUrl = 'http://localhost:8080/api/users';
  private adminUrl = 'http://localhost:8080/api/admin';
  constructor(private http: HttpClient) {}

  getUsers(email: string, page: number): Observable<GetAllUsersDTO> {
    return this.http.get<GetAllUsersDTO>(
      `${this.apiUsersUrl}?page=${page}&size=3&email=${email}`
    );
  }

  blockUser(id: number, reason: string): Observable<any> {
    const request: BlockUserRequestDTO = {
      reason: reason
    };
    return this.http.put(`${this.adminUrl}/block/${id}`, request);
  }
}
