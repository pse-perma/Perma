import {TaskCreateSimpleComponent} from './task-create-simple.component';
import {TaskService} from '../task/task.service';
import {ActuatorService} from '../actuator/actuator.service';
import {Observable} from 'rxjs';
import {NewTaskType} from './NewTaskType';
import createSpy = jasmine.createSpy;

describe('TaskCreateSimpleComponent', () => {
  let actuatorService: ActuatorService;
  let taskService: TaskService;
  let component: TaskCreateSimpleComponent;
  beforeEach(() => {
    actuatorService = new ActuatorService(null);
    taskService = new TaskService(null);
    component = new TaskCreateSimpleComponent(null, actuatorService, taskService);
  });
  it('#getActuators', function () {
    actuatorService.getActuators = createSpy('getSpy').and.returnValue(new Observable<any>());
    component.getActuators();
    expect(actuatorService.getActuators).toHaveBeenCalled();
  });
  it('#checkParams', function () {
    taskService.createNewTask = createSpy('checkSpy').and.returnValue(new Observable<any>());
    component.NewTask = new NewTaskType();
    component.NewTask.actuatorID = '3';
    component.NewTask.capability = '';
    component.NewTask.parameters = [];
    const mockTask = new NewTaskType();
    mockTask.actuatorID = '3';
    mockTask.capability = '';
    mockTask.parameters = [];
    component.checkParams();
    expect(taskService.createNewTask).toHaveBeenCalledWith(mockTask, 'true');
  });
  // xit('#submitTask', function () {
  //   taskService.createNewTask = createSpy('createSpy').and.returnValue(new Observable<any>());
  //   component.selectedCapability = {taskingParameters: {name: ''}, };
  //   component.selectedActuator = new ActuatorType();
  //   component.selectedActuator.id = 3;
  //   component.selectedCapability.name = '';
  //   component.NewTask = new NewTaskType();
  //   // component.NewTask.actuatorID = '3';
  //   component.NewTask.parameters = [];
  //   component.submitTask();
  //   expect(taskService.createNewTask).toHaveBeenCalled();
  // });
});
