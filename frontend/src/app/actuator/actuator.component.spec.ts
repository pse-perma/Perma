import {ActuatorComponent} from './actuator.component';
import {ActuatorService} from './actuator.service';
import {Observable} from 'rxjs';
import {ActuatorType} from './actuatorType';
import createSpy = jasmine.createSpy;

describe('ActuatorComponent', () => {
  let service: ActuatorService;
  let component: ActuatorComponent;
  beforeEach(() => {
    service = new ActuatorService(null);
    component = new ActuatorComponent(service);
  });
  it('#getActuators', function () {
    const mockActuator: ActuatorType[] = [new ActuatorType()];
    mockActuator[0].id = 3;
    mockActuator[0].TaskingCapability = '';
    mockActuator[0].encodingType = 'application/json';
    mockActuator[0].metadata = 'none';
    mockActuator[0].name = 'kreativerName';
    service.getActuators = createSpy('getSpy').and.returnValue(new Observable<any>(observer => observer.next(mockActuator
    )));
    service.getActuatorCount = createSpy('countSpy').and.returnValue(new Observable(observer =>
      observer.next(3)));
    component.getActuators();
    expect(service.getActuators).toHaveBeenCalled();
    expect(service.getActuatorCount).toHaveBeenCalled();
    expect(component.ActuatorCount).toEqual(3);
    expect(component.Actuators).toEqual(mockActuator);
  });
  it('#doFilterActuatorsNoFilter', function () {
    service.getActuators = createSpy('getSpy').and.returnValue(new Observable<any>(observer => observer.next(mockActuator
    )));
    service.getActuatorCount = createSpy('countSpy').and.returnValue(new Observable(observer =>
      observer.next(3)));
    const mockActuator: ActuatorType[] = [new ActuatorType()];
    mockActuator[0].id = 3;
    mockActuator[0].TaskingCapability = '';
    mockActuator[0].encodingType = 'application/json';
    mockActuator[0].metadata = 'none';
    mockActuator[0].name = 'kreativerName';
    component.numPerPage = '5';
    component.filterChoice = 'Name';
    component.filterText = '';
    component.doFilter();
    expect(service.getActuators).toHaveBeenCalledWith('5', 1, null);
  });
  it('#doFilterActuatorsFilter', function () {
    service.getActuators = createSpy('getSpy').and.returnValue(new Observable<any>(observer => observer.next(mockActuator
    )));
    service.getActuatorCount = createSpy('countSpy').and.returnValue(new Observable(observer =>
      observer.next(3)));
    const mockActuator: ActuatorType[] = [new ActuatorType()];
    mockActuator[0].id = 3;
    mockActuator[0].TaskingCapability = '';
    mockActuator[0].encodingType = 'application/json';
    mockActuator[0].metadata = 'none';
    mockActuator[0].name = 'kreativerName';
    component.numPerPage = '5';
    component.filterChoice = 'Name';
    component.filterText = 'kreativerName';
    component.doFilter();
    expect(service.getActuators).toHaveBeenCalledWith('5', 1, 'substringof(\'kreativerName\',name)');
  });
});
