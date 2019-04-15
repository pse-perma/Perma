import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';
import {Subscription} from 'rxjs';
import {ActuatorService} from '../actuator/actuator.service';
import {TaskService} from '../task/task.service';

@Component({
  selector: 'app-actuator-detail',
  templateUrl: './actuator-detail.component.html',
  styleUrls: ['./actuator-detail.component.css']
})
export class ActuatorDetailComponent implements OnInit, OnDestroy {

  id: number;
  private route$: Subscription;

  Actuator: any;
  Things: any[];
  Sensor: any;
  constructor(private actuatorService: ActuatorService, private taskService: TaskService, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.getActuatorDetails();
      // this.getTasks();
    });
  }
  ngOnDestroy(): void {
    if (this.route$) {
      this.route$.unsubscribe();
    }
  }

  // Will retrieve Details and Location for given actuator from service
  getActuatorDetails(): void {
    console.log('Current ID = ' + this.id);
    this.actuatorService.getActuatorDetails(this.id.toString()).subscribe(actuator => this.Actuator = actuator);
    this.actuatorService.getLocations(this.id).subscribe(things => this.Things = things);
    this.actuatorService.getSensor(this.id).subscribe(sensor => this.Sensor = sensor);
  }
}
