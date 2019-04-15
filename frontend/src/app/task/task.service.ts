import {Injectable} from '@angular/core';
import {Globals} from '../globals';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {TaskType} from './taskType';
import {NewTaskType} from '../task-create-simple/NewTaskType';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private TasksBackendURl = Globals.BackendUrl + '/tasks';

  constructor(private http: HttpClient) { }

  // Retrieve Actuators via HTTP GET() request.
  // Parameters: numberOfTasks: number, -- Number of Tasks to fetch
  //              pagenumber: number, -- The current Page to fetch (offset)
  //              filter: object, -- The Filter criteria
  //              sort: string -- Sort By, sort direction
  // Returns: Observable<TaskType[]> -- an array of Tasks that can be observed
  getTasks(numberOfTasks: string, pageNumber: number, filter: string, sort: object): Observable<TaskType[]> {
    let params = new HttpParams();
    if (filter != null) {
      params = new HttpParams().set('numberoftasks', numberOfTasks)
        .set('pagenumber', pageNumber.toString()).set('filter', filter)/*.set('sort', '')*/;
    } else {
      params = new HttpParams().set('numberoftasks', numberOfTasks)
        .set('pagenumber', pageNumber.toString());
    }
    return this.http.get<TaskType[]>(this.TasksBackendURl, {params: params});
  }
  // Create a new Task via HTTP POST() request.
  // Parameters: task: object -- The Task Object
  // Returns: TO-DO:
  createNewTask(task: NewTaskType, dry: string): Observable<any> {
    const params = new HttpParams().set('dry', dry);
    console.log('Created Task');
    return this.http.post(this.TasksBackendURl, task, {params: params});
  }

  // Import a Task from File via HTTP POST() request.
  // Parameters: task: object -- The Task Object
  // Returns: TO-DO:
  /*
    importTask(task: object): Observable<any> {
      return this.http.post(this.TasksBackendURl + 'import', task);
    }
  */

  // Get Details of a specific Task via HTTP GET() request.
  // Parameters: taskName: string -- The name of the Task
  getTaskDetails(id: string): Observable<any> {
    const params = new HttpParams().set('id', id);
    return this.http.get(this.TasksBackendURl + '/details', {params: params});
  }

  // Modify a specific planned Task via HTTP PUT() request.
  // Parameters: taskName: string -- The name of the Task to modify
  //              task: object -- the modified Task object
  // Returns: TO-DO:
  /*
    modifyPlannedTask(taskName: string, task: object): Observable<any> {
      const params = new HttpParams().set('taskName', taskName);
      return this.http.put(this.TasksBackendURl, {params: params, body: {task: task}});
    }
  */

  // Delete a specific Task via HTTP DELETE() request.
  // Parameters: taskName: string -- The name of the Task to delete
  deleteTask(id: string): Observable<any> {
    const params = new HttpParams().set('id', id);
    return this.http.delete(this.TasksBackendURl, {params: params});
  }

  // Create a new virtual Actuator via HTTP POST() request.
  // Parameters: virtual Actuator -- The virtual Actuator Object
  createNewVA(virtualActuator: object, actuatorID: string): Observable<any> {
    const params = new HttpParams().set('id', actuatorID);
    return this.http.post(this.TasksBackendURl + '/virtual', virtualActuator, {params: params});
  }

  // Get specified number of tasks
  // Parameters: numPerPage: string - number of tasks to fetch
  getTaskCount(numPerPage: string): Observable<number> {
    const params = new HttpParams().set('numPerPage', numPerPage);
    return this.http.get<number>(this.TasksBackendURl + '/count', {params: params});
  }
}
