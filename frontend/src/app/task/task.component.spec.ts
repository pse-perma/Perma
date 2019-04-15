import {TaskComponent} from './task.component';
import {Observable} from 'rxjs';
import {TaskService} from './task.service';
import {TaskType} from './taskType';
import createSpy = jasmine.createSpy;

describe('TaskComponent', () => {
  let service: TaskService;
  let component: TaskComponent;
  beforeEach(() => {
    service = new TaskService(null);
    component = new TaskComponent(service);
  });
  it('#getTasks', function () {
    const mockTasks: TaskType[] = [new TaskType()];
    mockTasks[0].id = '3';
    mockTasks[0].taskingParameters = 'Parameters';
    mockTasks[0].taskingCapability = 'TaskingCapability';
    mockTasks[0].creationTime = '2017-03-04 01:11:00:000';
    service.getTasks = createSpy('getSpy').and.returnValue(new Observable(observerable => {
      observerable.next(mockTasks);
    }));
    service.getTaskCount = createSpy('countSpy').and.returnValue(new Observable(observerable => {
      observerable.next(1);
    }));
    component.getTasks();
    expect(service.getTaskCount).toHaveBeenCalled();
    expect(service.getTasks).toHaveBeenCalled();
    expect(component.TaskCount).toEqual(1);
    expect(component.Tasks).toEqual(mockTasks);
  });
  it('#doFilterTasksNoFilter', function () {
    service.getTaskCount = createSpy('countSpy').and.returnValue(new Observable(observerable => {
      observerable.next(1);
    }));
    const mockTasks: TaskType[] = [new TaskType()];
    mockTasks[0].id = '3';
    mockTasks[0].taskingParameters = 'Parameters';
    mockTasks[0].taskingCapability = 'TaskingCapability';
    mockTasks[0].creationTime = '2017-03-04 01:11:00:000';
    service.getTasks = createSpy('getSpy').and.returnValue(new Observable(observerable => {
      observerable.next(mockTasks);
    }));
    component.numPerPage = '5';
    component.currentPage = 1;
    component.filterChoice = 'Task-ID';
    component.filterText = '';
    component.doFilter();
    expect(service.getTasks).toHaveBeenCalledWith('5', 1, null, undefined);
  });
  it('#doFilterTasksFilter', function () {
    service.getTaskCount = createSpy('countSpy').and.returnValue(new Observable(observerable => {
      observerable.next(1);
    }));
    const mockTasks: TaskType[] = [new TaskType()];
    mockTasks[0].id = '3';
    mockTasks[0].taskingParameters = 'Parameters';
    mockTasks[0].taskingCapability = 'TaskingCapability';
    mockTasks[0].creationTime = '2017-03-04 01:11:00:000';
    service.getTasks = createSpy('getSpy').and.returnValue(new Observable(observerable => {
      observerable.next(mockTasks);
    }));
    component.numPerPage = '5';
    component.currentPage = 1;
    component.filterChoice = 'Task-ID';
    component.filterText = '1';
    component.doFilter();
    expect(service.getTasks).toHaveBeenCalledWith('5', 1, '@iot.id eq \'1\'', undefined);
  });
  it('#deleteTasks', function () {
    service.deleteTask = createSpy('deleteSpy').and.returnValue(new Observable());
    component.selectedTask = new TaskType();
    component.selectedTask.id = 3;
    component.deleteTask();
    expect(service.deleteTask).toHaveBeenCalledWith(3);
  });
});
