import {ServersComponent} from './servers.component';
import {ServersService} from './servers.service';
import {Observable} from 'rxjs';
import {ServerType} from './serverType';
import createSpy = jasmine.createSpy;

describe('ServersComponent', () => {
  let service: ServersService;
  let component: ServersComponent;
  beforeEach(() => {
    service = new ServersService(null);
    component = new ServersComponent(service);
  });
  it('#connectToServer', () => {
    service.connectToServer = createSpy('connectSpy').and.returnValue(new Observable<any>());
    component.selectedServer = new ServerType('http://test.de', 'test');
    component.onConnectButtonPressed();
    expect(service.connectToServer).toHaveBeenCalledWith('http://test.de');
  });
  it('#addNewServer', () => {
    service.addNewServer = createSpy('addSpy').and.returnValue(new Observable<any>());
    component.NewServer = new ServerType('http://test2.de', 'test2');
    component.onNewServerButtonPressed();
    expect(service.addNewServer).toHaveBeenCalledWith(new ServerType('http://test2.de', 'test2'));
  });
  it('#getServers', () => {
    const mockServer: ServerType[] = [new ServerType('http://test.de', 'test')];
    service.getServers = createSpy('getSpy').and.returnValue(new Observable(observerable =>
      observerable.next(mockServer)));
    component.getServers();
    expect(service.getServers).toHaveBeenCalled();
    expect(component.Servers2).toContain(new ServerType('http://test.de', 'test'));
  });
  it('#deleteServer', () => {
    service.deleteServer = createSpy('deleteSpy').and.returnValue(new Observable());
    component.onDelete();
    expect(service.deleteServer).toHaveBeenCalled();
    // expect(component.Servers2).toContain(new ServerType('http://test.de', 'test'));
  });
  it('#selectServer', () => {
    component.onSelect(new ServerType('http:test.de', 'test'));
    expect(component.selectedServer).toEqual(new ServerType('http:test.de', 'test'));
  });
});
