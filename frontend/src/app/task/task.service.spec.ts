import {getTestBed, TestBed} from '@angular/core/testing';
import {TaskService} from './task.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {BrowserDynamicTestingModule, platformBrowserDynamicTesting} from '@angular/platform-browser-dynamic/testing';
import {Globals} from '../globals';
import {TaskType} from './taskType';
import {NewTaskType} from '../task-create-simple/NewTaskType';

describe('TaskService', () => {
  let injector: TestBed;
  let service: TaskService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.resetTestEnvironment();
    TestBed.initTestEnvironment(BrowserDynamicTestingModule,
      platformBrowserDynamicTesting());
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskService]
    });
    injector = getTestBed();
    service = injector.get(TaskService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });
  it('#getTasksNoFilter', () => {
    const ServerMock: TaskType[] = [
      {'taskingParameters': 'null', 'creationTime': '2019-02-13T14:09:57.56', 'id': '3', 'taskingCapability': 'null'},
      {'taskingParameters': 'null', 'creationTime': '2018-02-13T14:09:57.56', 'id': '300', 'taskingCapability': 'null'}
    ];
    service.getTasks('10', 1, null, null).subscribe(tasks => {
      expect(tasks.length).toBe(2);
      expect(tasks).toEqual(ServerMock);
    });
    const req = httpMock.expectOne(Globals.BackendUrl + '/tasks?numberoftasks=10&pagenumber=1');
    expect(req.request.method).toBe('GET');
    req.flush(ServerMock);
  });
  it('#getTasksFilter', () => {
    const ServerMock: TaskType[] = [
      {'taskingParameters': 'null', 'creationTime': '2019-02-13T14:09:57.56', 'id': '3', 'taskingCapability': 'null'},
      {'taskingParameters': 'null', 'creationTime': '2018-02-13T14:09:57.56', 'id': '300', 'taskingCapability': 'null'}
    ];
    service.getTasks('10', 1, 'filtertext', null).subscribe(tasks => {
      expect(tasks.length).toBe(2);
      expect(tasks).toEqual(ServerMock);
    });
    const req = httpMock.expectOne(Globals.BackendUrl + '/tasks?numberoftasks=10&pagenumber=1&filter=filtertext');
    expect(req.request.method).toBe('GET');
    req.flush(ServerMock);
  });
  it('#createNewTask', () => {
    const newTask: NewTaskType = {actuatorID: '1', parameters: {'test': true}, capability: 'TestCapability'};
    service.createNewTask(newTask, 'false').subscribe();
    const req = httpMock.expectOne(Globals.BackendUrl + '/tasks?dry=false');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newTask);
  });
  it('#getTaskDetails', () => {
    const ServerMock: TaskType = {
      taskingParameters: {status: 'success'}, creationTime: '2019-02-13T14:09:57.56', id: '3',
      taskingCapability: {}
    };
    service.getTaskDetails('3').subscribe(task => {
      expect(task).toEqual(ServerMock);
    });
    const req = httpMock.expectOne(Globals.BackendUrl + '/tasks/details?id=3');
    expect(req.request.method).toBe('GET');
  });
  it('#deleteTask', () => {
    service.deleteTask('3').subscribe();
    const req = httpMock.expectOne(Globals.BackendUrl + '/tasks?id=3');
    expect(req.request.method).toBe('DELETE');
  });
  it('#createNewVA', () => {
    const virtualActuator = {
      vaName: 'Test', vaDescription: 'Description', thingName: 'Thing', thingDescription: 'a Thing',
      capabilityList: 'http://adownloadurlforajarfile.com', VASid: '3'
    };
    service.createNewVA(virtualActuator, '3').subscribe();
    const req = httpMock.expectOne(Globals.BackendUrl + '/tasks/virtual?id=3');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(virtualActuator);
  });
});
