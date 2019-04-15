import {Component, OnInit} from '@angular/core';
import {ActuatorType} from './actuatorType';
import {ActuatorService} from './actuator.service';

@Component({
  selector: 'app-actuator',
  templateUrl: './actuator.component.html',
  styleUrls: ['./actuator.component.css']
})
export class ActuatorComponent implements OnInit {
  Actuators: ActuatorType[];
  numPerPage = '20';
  currentPage = 1;
  filter: string;
  // orderBy: any;
  selectedActuator: ActuatorType;
  ActuatorCount = 1;
  filterChoice = '';
  filterText = '';
  error: any;
  constructor(private actuatorService: ActuatorService) { }

  ngOnInit() {
    this.getActuators();
  }
  // Retrieve Actuators from Service
  getActuators(): void {
    this.actuatorService.getActuatorCount(this.numPerPage).subscribe(count => this.ActuatorCount = count, error => this.error = error);
    this.actuatorService.getActuators(this.numPerPage, this.currentPage, this.filter)
      .subscribe(actuators => {
        this.Actuators = actuators;
        console.log(actuators[1]);
      }, error => this.error = error);
  }
  // Call Service to delete virtual Actuator
  /*  onDeleteVAButtonPressed(): void {
      // this.actuatorService.deleteVA(this.selectedActuator.id)
      //   .subscribe(_ => this.getActuators());
    }*/
  // Show selected Actuators on Map
  /*  onShowActuatorOnMapButtonPressed(): void {
      // return;
    }*/

  // EventHandler for changing the current page
  pageChanged(event: any): void {
    this.currentPage = event.page;
    this.getActuators();
  }

  // Will assemble filter string to filter list of actuators, disables filter, if filtertext is empty
  doFilter(): void {
    if (this.filterChoice === 'Name') {
      this.filter = 'substringof(\'' + this.filterText + '\',name)';
    }
    if (this.filterText === '') {
      this.filter = null;
    }
    this.currentPage = 1;
    this.getActuators();
  }
}
