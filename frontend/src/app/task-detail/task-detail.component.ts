import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {TaskType} from '../task/taskType';
import {TaskService} from '../task/task.service';
import {ActivatedRoute, Params} from '@angular/router';

@Component({
  selector: 'app-task-detail',
  templateUrl: './task-detail.component.html',
  styleUrls: ['./task-detail.component.css']
})
export class TaskDetailComponent implements OnInit, OnDestroy {
  id: number;
  private route$: Subscription;
  Task: TaskType;

  constructor(private taskService: TaskService, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.getTaskDetails();
    });
  }

  ngOnDestroy(): void {
    if (this.route$) {
      this.route$.unsubscribe();
    }
  }

  // Will get details of selected task from service
  getTaskDetails(): void {
    console.log('Current ID = ' + this.id);
    this.taskService.getTaskDetails(this.id.toString()).subscribe(task => this.Task = task);
  }
}
