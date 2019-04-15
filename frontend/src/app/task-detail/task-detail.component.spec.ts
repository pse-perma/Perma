import {TaskDetailComponent} from './task-detail.component';
import {TaskService} from '../task/task.service';
import {Observable} from 'rxjs';
import createSpy = jasmine.createSpy;

describe('TaskDetailComponent', () => {
  let service: TaskService;
  let component: TaskDetailComponent;
  beforeEach(() => {
    service = new TaskService(null);
    component = new TaskDetailComponent(service, null);
  });
  it('#getTaskDetails', function () {
    service.getTaskDetails = createSpy('taskDetailsSpy').and.returnValue(new Observable());
    component.id = 5;
    component.getTaskDetails();
    expect(service.getTaskDetails).toHaveBeenCalledWith('5');
  });
});
