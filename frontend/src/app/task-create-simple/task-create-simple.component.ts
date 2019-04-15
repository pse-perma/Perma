import {Component, OnInit, TemplateRef} from '@angular/core';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {ActuatorType} from '../actuator/actuatorType';
import {ActuatorService} from '../actuator/actuator.service';
import {TaskService} from '../task/task.service';
import {NewTaskType} from 'src/app/task-create-simple/NewTaskType';
import {Router} from '@angular/router';

@Component({
  selector: 'app-task-create-simple',
  templateUrl: './task-create-simple.component.html',
  styleUrls: ['./task-create-simple.component.css']
})
export class TaskCreateSimpleComponent implements OnInit {

  constructor(private modalService: BsModalService, private actuatorService: ActuatorService, private taskService: TaskService,
              private router: Router) {
  }

  name: '';
  Actuators: ActuatorType[];
  selectedActuator: any;
  // capabilities = Object;
  filter: any;
//  DetailedActuator: any[];
  selectedCapability: any;
//  selectedParameter: any;
  value: string | number;
  value2: string | number;
  modalRef: BsModalRef;
  NewTask: NewTaskType;
  valueArray: string[] | number[];
//  ParametersValid: boolean;
  error: any;
  successful: boolean;
  modalError: any;
  ngOnInit() {
  }

  // Will get actuators for popup from service
  getActuators() {
    this.actuatorService.getActuators('-1', 1, null)
      .subscribe(actuators => this.Actuators = actuators, error => this.modalError = error);
  }

  /*  getActuatorDetails() {
      this.actuatorService.getActuatorDetails(this.selectedActuator.id.toString()).subscribe(details => this.DetailedActuator = details);
    }*/

  // Handler for popup
  openModal(template: TemplateRef<any>) {
    this.modalRef = this.modalService.show(template);
  }

  // Will submit task to backend and redirect to task overview page
  submitTask() {
    this.NewTask = null;
    this.NewTask = new NewTaskType();
    this.NewTask.actuatorID = this.selectedActuator.id;
    this.NewTask.capability = this.selectedCapability.name;
    // this.NewTask.parameters = new Map();
    // this.NewTask.parameters.set(this.selectedCapability.taskingParameters.name, this.value.toString());
    // console.log(this.NewTask.parameters);
    this.NewTask.parameters = new Object();
    if (this.value2 != null) {
      if (this.value != null) {
        this.valueArray[0] = this.value;
      }
      this.valueArray[1] = this.value2;
      this.NewTask.parameters[this.selectedCapability.taskingParameters.name] = this.valueArray;
      this.taskService.createNewTask(this.NewTask, 'false').subscribe(value => {
          this.successful = true;
          this.error = null;
        },
        error => {
          this.error = error;
          this.successful = false;
        });
    } else {
      this.NewTask.parameters[this.selectedCapability.taskingParameters.name] = this.value.toString();
      this.taskService.createNewTask(this.NewTask, 'false').subscribe(value => {
          this.successful = true;
          this.error = null;
        },
        error => {
          this.error = error;
          this.successful = false;
        });
      this.value2 = null;
    }
  }

  // Will check, if entered parameters are according to constraint
  checkParams() {
    this.taskService.createNewTask(this.NewTask, 'true').subscribe();
  }
}
