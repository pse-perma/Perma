import {ActuatorDetailComponent} from './actuator-detail.component';
import {Observable} from 'rxjs';
import {ActuatorService} from '../actuator/actuator.service';
import {TaskService} from '../task/task.service';
import {ActivatedRoute} from '@angular/router';
import {ActuatorType} from '../actuator/actuatorType';
import createSpy = jasmine.createSpy;

describe('ActuatorDetailComponent', () => {
  let service: ActuatorService;
  let service2: TaskService;
  let component: ActuatorDetailComponent;
  beforeEach(() => {
    service = new ActuatorService(null);
    service2 = new TaskService(null);
    component = new ActuatorDetailComponent(service, service2, new ActivatedRoute());
  });
  it('#ActuatorDetails', () => {
    const mockActuator: ActuatorType[] = [new ActuatorType()];
    mockActuator[0].id = 3;
    mockActuator[0].TaskingCapability = '';
    mockActuator[0].encodingType = 'application/json';
    mockActuator[0].metadata = 'none';
    mockActuator[0].name = 'kreativerName';
    service.getActuatorDetails = createSpy('detailSpy').and.returnValue(new Observable<any>(observerable =>
      observerable.next(mockActuator)));
    service.getLocations = createSpy('locationSpy').and.returnValue(new Observable<any>(observerable =>
      observerable.next('location')));
    component.id = 3;
    component.getActuatorDetails();
    expect(service.getActuatorDetails).toHaveBeenCalledWith('3');
    expect(component.Actuator).toEqual(mockActuator);
    expect(service.getLocations).toHaveBeenCalled();
  });
});
