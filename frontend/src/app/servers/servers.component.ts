import {Component, OnInit} from '@angular/core';
import {ServersService} from './servers.service';
import {ServerType} from './serverType';
// import {SERVERS} from './mock-servers';
// import {Observable} from 'rxjs';
@Component({
  selector: 'app-servers',
  templateUrl: './servers.component.html',
  styleUrls: ['./servers.component.css'],
})
export class ServersComponent implements OnInit {
  Servers2: ServerType[];
  // Initialize Objects to avoid nullpointer errors
  selectedServer = new ServerType('', '');
  NewServer = new ServerType('', '');
  error: any;
  connectSuccess: boolean;
  deleteSuccess: boolean;
  constructor(private serversService: ServersService) { }
  // Gets called, if item of list is clicked, will update selectedServer variable
  onSelect(server: ServerType): void {
    this.selectedServer = server;
  }
  // Gets called, if 'Delete Selected Server' button is pressed, will call service to delete item from backend
  onDelete() {
    this.serversService.deleteServer(this.selectedServer.url).subscribe(value => {
        this.getServers();
        this.deleteSuccess = true;
      },
      error => {
        this.deleteSuccess = false;
      });
    this.selectedServer = null;
  }
  // Gets called, if 'Connect to Server' button is pressed, will call service to connect to the server in backend
  onConnectButtonPressed() {
    this.serversService.connectToServer(this.selectedServer.url).subscribe(value => {
        this.connectSuccess = true;
      },
      error => {
        this.connectSuccess = false;
      });
  }
  // Gets called, if 'Add new Server' button is pressed, will call service to add new Server in backend
  onNewServerButtonPressed() {
    this.serversService.addNewServer(this.NewServer).subscribe(value => {
        this.getServers();
        this.connectSuccess = true;
      },
      error => {
        this.connectSuccess = false;
        this.error = error;
      });
  }
  // Will get Server Objects from service
  getServers(): void {
    this.serversService.getServers().subscribe(servers => {
      this.Servers2 = servers;
      console.log(this.Servers2);
      this.error = null;
    }, error => this.error = error);
  }
  ngOnInit() {
    this.getServers();
  }
}
