import {getTestBed, TestBed} from '@angular/core/testing';

import {VaCreateService} from './va-create.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {BrowserDynamicTestingModule, platformBrowserDynamicTesting} from '@angular/platform-browser-dynamic/testing';
import {VaCapabilityType} from './VaCapabailityType';
import {Globals} from '../globals';

describe('VaCreateService', () => {
  let injector: TestBed;
  let service: VaCreateService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.resetTestEnvironment();
    TestBed.initTestEnvironment(BrowserDynamicTestingModule,
      platformBrowserDynamicTesting());
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [VaCreateService]
    });
    injector = getTestBed();
    service = injector.get(VaCreateService);
    httpMock = injector.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });
  it('#getCapabilities', function () {
    const ServerMock: VaCapabilityType[] = [
      {'name': 'test', 'url': 'http://test.com'},
      {'name': 'moreTests', 'url': 'http://moreTests.de'}];
    service.getCapabilities().subscribe(capabilities => {
      expect(capabilities.length).toBe(2);
      expect(capabilities).toEqual(ServerMock);
    });
    const req = httpMock.expectOne(Globals.BackendUrl + '/files');
    expect(req.request.method).toBe('GET');
    req.flush(ServerMock);
  });
});
