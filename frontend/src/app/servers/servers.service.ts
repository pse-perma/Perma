import {Injectable} from '@angular/core';
import {ServerType} from './serverType';
/*
import { SERVERS } from './mock-servers';
*/
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import {Globals} from '../globals';

@Injectable({
  providedIn: 'root'
})
export class ServersService {
  private ServersBackendUrl = Globals.BackendUrl + '/servers'; // URL to Backend API

  // Gets Servers from Backend via HTTP GET() request
  // Return: Observable<ServerType[]> -- an Array of Servers that is observable.
  getServers(): Observable<ServerType[]> {
    return this.http.get<ServerType[]>(this.ServersBackendUrl).pipe(tap(_ => console.log('Fetched Servers')));
  }

  // Deletes Sever from Backend via HTTP DELETE() request
  // Parameter: url: String -- the URL of the Server to delete
  deleteServer(url: String): Observable<any> {
    // return this.http.delete(this.ServersBackendUrl, {params: {url: url}});
    return this.http.request('delete', this.ServersBackendUrl, {body: {url: url}});
  }

  // Sets the active SensorThings-Server in the backend via HTTP POST() request
  // Parameters: url: String -- the URL of the Server to use
  connectToServer(url: String): Observable<any> {
    return this.http.post(this.ServersBackendUrl + '/connect', url);
  }

  // Adds new Server to the backend via HTTP POST() request
  // Parameters: newServer: ServerType -- a ServerType Object of the new Server (Name+URL)
  addNewServer(newServer: ServerType): Observable<boolean> {
    return this.http.post<boolean>(this.ServersBackendUrl, newServer);
  }
  constructor(
    private http: HttpClient,
  ) { }
}
