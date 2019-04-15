import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Globals} from '../globals';
import {HttpClient} from '@angular/common/http';
import {VaCapabilityType} from './VaCapabailityType';

@Injectable({
  providedIn: 'root'
})
export class VaCreateService {
  private VABackendURl = Globals.BackendUrl + '/virtualActuator';

  constructor(private http: HttpClient) {
  }

  // Get all the capabiltiy JARs stored in backend
  // Returns: a list of JAR Names and URLs stored in backend
  getCapabilities(): Observable<VaCapabilityType[]> {
    return this.http.get<VaCapabilityType[]>(Globals.BackendUrl + '/files');
  }
}
