import {Injectable} from '@angular/core';
import {Globals} from '../globals';
import {Observable} from 'rxjs';
import {ActuatorType} from './actuatorType';
import {HttpClient, HttpParams} from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class ActuatorService {
  private ActuatorsBackendURl = Globals.BackendUrl + '/actuators';

  constructor(private http: HttpClient) {
  }
  // Retrieve Actuators via HTTP GET() request.
  // Parameters: numberofactuators: number, -- Number of Actuators to fetch
  //            pagenumber: number, -- The current Page to fetch (offset)
  //            filter: object, -- The Filter criteria
  //            sort: string -- Sort By, sort direction
  // Returns: Observable<ActuatorType[]> -- an array of Actuators that can be observed
  getActuators(numberofactuators: string, pagenumber: number, filter: string): Observable<ActuatorType[]> {
    if (filter == null) {
      const params = new HttpParams().set('numberofactuators', numberofactuators).set('pagenumber', pagenumber.toString())
        /*.set('filter', /!*filter.toString()*!/ '').set('sort', /!*sort*!/'')*/;
      return this.http.get<ActuatorType[]>(this.ActuatorsBackendURl, {params: params});
      /*.pipe(tap(_ => log('Fetched Actuators')));*/
    } else if (filter != null) {
      const params = new HttpParams().set('numberofactuators', numberofactuators).set('pagenumber', pagenumber.toString())
        .set('filter', filter);
      return this.http.get<ActuatorType[]>(this.ActuatorsBackendURl, {params: params});
    }
  }
  getActuatorCount(numPerPage: string): Observable<number> {
    const params = new HttpParams().set('numPerPage', numPerPage);
    return this.http.get<number>(this.ActuatorsBackendURl + '/count', {params: params});
  }
  // Get Details of a specified Actuator via HTTP GET() request.
  // Parameters: id: number -- The ID of the Actuator to fetch
  // Returns: Observable<ActuatorType> -- a Actuator that can be observed
  getActuatorDetails(id: string): Observable<any> {
    const params = new HttpParams().set('id', id);
    return this.http.get<any>(this.ActuatorsBackendURl + '/details', {params: params})/*.pipe(_ => log('Fetched Actuator Details'))*/;
  }
  // Delete a virtual Actuator via HTTP DELETE() request.
  // Parameters: id: number -- the id of the actuator to delete
  // Returns: TO-DO:
  /*  deleteVA(id: number): Observable<any> {
      const params = new HttpParams().set('name', name);
      return this.http.delete(this.ActuatorsBackendURl, {params: params});
    }*/
  // Retrieve the location of the specified Actuators via HTTP GET() request.
  // Parameters: id: number, -- Acutator ID to fetch location for
  getLocations(id: number): Observable<any> {
    const params = new HttpParams().set('id', id.toString());
    return this.http.get(this.ActuatorsBackendURl + '/details/locations', {params: params});
  }

  getSensor(id: number): Observable<any> {
    const params = new HttpParams().set('id', id.toString());
    return this.http.get(this.ActuatorsBackendURl + '/details/sensor', {params: params});
  }
}
