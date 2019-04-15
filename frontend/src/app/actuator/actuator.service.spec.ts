import {getTestBed, TestBed} from '@angular/core/testing';

import {ActuatorService} from './actuator.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {BrowserDynamicTestingModule, platformBrowserDynamicTesting} from '@angular/platform-browser-dynamic/testing';
import {ActuatorType} from './actuatorType';
import {Globals} from '../globals';

describe('ActuatorService', () => {
  let injector: TestBed;
  let service: ActuatorService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.resetTestEnvironment();
    TestBed.initTestEnvironment(BrowserDynamicTestingModule,
      platformBrowserDynamicTesting());
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ActuatorService]
    });
    injector = getTestBed();
    service = injector.get(ActuatorService);
    httpMock = injector.get(HttpTestingController);
  });
  afterEach(() => {
    httpMock.verify();
  });
  it('#getActuatorsNoFilter', () => {
    const ServerMock: ActuatorType[] = [
      {id: 1, name: 'Test', metadata: 'none', description: 'a Description', encodingType: 'application/json', TaskingCapability: ''},
      {id: 2, name: 'Test2', metadata: 'none', description: 'another Description', encodingType: 'application/json', TaskingCapability: ''}
    ];
    service.getActuators('10', 1, null).subscribe(actuators => {
      expect(actuators).toEqual(ServerMock);
    });
    const req = httpMock.expectOne(Globals.BackendUrl + '/actuators?numberofactuators=10&pagenumber=1');
    expect(req.request.method).toBe('GET');
    req.flush(ServerMock);
  });
  it('#getActuatorsFilter', () => {
    const ServerMock: ActuatorType[] = [
      {id: 1, name: 'Test', metadata: 'none', description: 'a Description', encodingType: 'application/json', TaskingCapability: ''},
      {id: 2, name: 'Test2', metadata: 'none', description: 'another Description', encodingType: 'application/json', TaskingCapability: ''}
    ];
    service.getActuators('10', 1, 'filtertext').subscribe(actuators => {
      expect(actuators).toEqual(ServerMock);
    });
    const req = httpMock.expectOne(Globals.BackendUrl + '/actuators?numberofactuators=10&pagenumber=1&filter=filtertext');
    expect(req.request.method).toBe('GET');
    req.flush(ServerMock);
  });
  it('#getActuatorCount', () => {
    const ServerMock = 3;
    service.getActuatorCount('1').subscribe(count => {
      expect(count).toEqual(3);
    });
    const req = httpMock.expectOne(Globals.BackendUrl + '/actuators/count?numPerPage=1');
    expect(req.request.method).toBe('GET');
    req.flush(ServerMock);
  });
  it('#getActuatorDetails', () => {
    const ServerMock: ActuatorType[] = [
      {id: 1, name: 'Test', metadata: 'none', description: 'a Description', encodingType: 'application/json', TaskingCapability: ''},
      {id: 2, name: 'Test2', metadata: 'none', description: 'another Description', encodingType: 'application/json', TaskingCapability: ''}
    ];
    service.getActuatorDetails('2').subscribe(details => {
      expect(details).toEqual(ServerMock);
    });
    const req = httpMock.expectOne(Globals.BackendUrl + '/actuators/details?id=2');
    expect(req.request.method).toBe('GET');
    req.flush(ServerMock);
  });
  it('#getLocations', () => {
    const ServerMock = {null: null};
    service.getLocations(1).subscribe(location => {
      expect(location).toEqual(ServerMock);
    });
    const req = httpMock.expectOne(Globals.BackendUrl + '/actuators/details/locations?id=1');
    expect(req.request.method).toBe('GET');
    req.flush(ServerMock);
  });
});
