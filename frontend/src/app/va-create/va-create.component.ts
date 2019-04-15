import {Component, OnInit, TemplateRef} from '@angular/core';
import {VaType} from './VaType';
import {TaskService} from '../task/task.service';
import {VaCreateService} from './va-create.service';
import {FileItem, FileUploader, ParsedResponseHeaders} from 'ng2-file-upload';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {VaCapabilityType} from './VaCapabailityType';
import {ActuatorService} from '../actuator/actuator.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-va-create',
  templateUrl: './va-create.component.html',
  styleUrls: ['./va-create.component.css']
})
export class VaCreateComponent implements OnInit {
  taskingParameter: VaType;
  // id = 76;
  selectedVAS: any;
  availableVAS: any[];
  capabilityList: VaCapabilityType[];
  selectedCapability: VaCapabilityType;
  uploader: FileUploader = new FileUploader({url: 'api/files', removeAfterUpload: false, autoUpload: false});
  modalRef: BsModalRef;
  uploadError = false;
  createError: any;
  successful: boolean;
  constructor(private taskService: TaskService,
              private vaCreateService: VaCreateService,
              private modalService: BsModalService,
              private actuatorService: ActuatorService,
              private router: Router) {
    this.uploader.onErrorItem = ((item: FileItem, response: string, status: number, headers: ParsedResponseHeaders): any => {
      this.uploadError = true;
    });
  }

  ngOnInit() {
    this.taskingParameter = {
      vaName: '', vaDescription: '', thingName: '', thingDescription: '',
      capabilityList: ''
    };
    this.getCapabilities();
    this.actuatorService.getActuators('-1', 1,
      'substringof(\'application/perma.virtualactuator.server\',encodingType)')
      .subscribe(vas => this.availableVAS = vas);
  }

  // Will get capabilities of the selected actuator from service
  getCapabilities(): void {
    this.vaCreateService.getCapabilities().subscribe(capabilities => this.capabilityList = capabilities);
  }

  // Will call service to create actuator and redirect to actuator overview page
  createVa(): void {
    this.taskingParameter.capabilityList = this.selectedCapability.url;
    this.taskService.createNewVA(this.taskingParameter, this.selectedVAS.id).subscribe(value => {
        this.successful = true;
        this.createError = null;
      },
      error => {
        this.createError = error;
        this.successful = false;
      });
  }

  // Handler for the popup menu
  openModal(template: TemplateRef<any>) {
    this.modalRef = this.modalService.show(template);
  }
}
