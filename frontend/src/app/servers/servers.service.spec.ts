import {ServersService} from './servers.service';
import {ServerType} from './serverType';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {getTestBed, TestBed} from '@angular/core/testing';
import {Globals} from '../globals';
import {BrowserDynamicTestingModule, platformBrowserDynamicTesting} from '@angular/platform-browser-dynamic/testing';

describe('ServersService', () => {
  let injector: TestBed;
  let service: ServersService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.resetTestEnvironment();
    TestBed.initTestEnvironment(BrowserDynamicTestingModule,
      platformBrowserDynamicTesting());
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ServersService]
    });
    injector = getTestBed();
    service = injector.get(ServersService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });
  it('#getServers', () => {
    const ServerMock: ServerType[] = [
      {'url': 'http://dasisteintest.de', 'name': 'Test1'},
      {'url': 'http://mehrtesten.de', 'name': 'Test2'}
    ];
    service.getServers().subscribe(servers => {
      expect(servers.length).toBe(2);
      expect(servers).toEqual(ServerMock);
    });
    const req = httpMock.expectOne(Globals.BackendUrl + '/servers');
    expect(req.request.method).toBe('GET');
    req.flush(ServerMock);
  });
  it('#deleteServer', () => {
    service.deleteServer('http://dasisteintest.de').subscribe();
    const req = httpMock.expectOne(Globals.BackendUrl + '/servers');
    expect(req.request.method).toBe('DELETE');
    expect(req.request.body.url).toBe('http://dasisteintest.de');
  });
  it('#connectToServer', () => {
    service.connectToServer('http://dasisteintest.de').subscribe();
    const req = httpMock.expectOne(Globals.BackendUrl + '/servers' + '/connect');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual('http://dasisteintest.de');
  });
  it('#addNewServer', () => {
    service.addNewServer(new ServerType('http://dasisteintest.de', 'Test')).subscribe();
    const req = httpMock.expectOne(Globals.BackendUrl + '/servers');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(new ServerType('http://dasisteintest.de', 'Test'));
  });
});

