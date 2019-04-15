import {VaCreateComponent} from './va-create.component';
import {TaskService} from '../task/task.service';
import {VaCreateService} from './va-create.service';
import {ActuatorService} from '../actuator/actuator.service';
import {Observable} from 'rxjs';
import {VaType} from './VaType';
import {VaCapabilityType} from './VaCapabailityType';
import createSpy = jasmine.createSpy;

describe('VaCreateComponent', () => {
  let taskService: TaskService;
  let vaCreateService: VaCreateService;
  let actuatorService: ActuatorService;
  let component: VaCreateComponent;
  beforeEach(() => {
    taskService = new TaskService(null);
    vaCreateService = new VaCreateService(null);
    actuatorService = new ActuatorService(null);
    component = new VaCreateComponent(taskService, vaCreateService, null, actuatorService);
  });
  it('#getCapabilities', function () {
    vaCreateService.getCapabilities = createSpy('getCapabilitiesSpy').and.returnValue(new Observable<any>());
    component.getCapabilities();
    expect(vaCreateService.getCapabilities).toHaveBeenCalled();
  });
  it('#createVA', function () {
    taskService.createNewVA = createSpy('createSpy').and.returnValue(new Observable<any>());
    component.taskingParameter = new VaType();
    component.taskingParameter.capabilityList = '';
    component.selectedCapability = new VaCapabilityType();
    component.selectedCapability.url = '';
    component.selectedVAS = new VaType();
    component.selectedVAS.id = '5';
    component.createVa();
    expect(taskService.createNewVA).toHaveBeenCalled();
  });
});
