import {Component, OnInit} from '@angular/core';
import {TaskService} from './task.service';
import {TaskType} from './taskType';

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {
  Tasks: any[];
  numPerPage: number = 20;
  currentPage = 1;
  filter: any;
  orderBy: any;
  selectedTask: any;
  TaskCount: number;
  filterText = '';
  filterChoice = '';
  error: any;
  constructor(private taskService: TaskService) { }

  ngOnInit() {
    this.getTasks();
  }
  // Click Handler for Table
  onClickTaskTableItem(Task: TaskType) {
    this.selectedTask = Task;
  }
  // Retrieve Tasks from Service
  getTasks(): void {
    this.taskService.getTaskCount(this.numPerPage.toString())
      .subscribe(count => this.TaskCount = count,
          error => this.error = error);
    this.taskService.getTasks(this.numPerPage.toString(), this.currentPage, this.filter, this.orderBy)
      .subscribe(task => this.Tasks = task,
      error => this.error = error);
  }
  // Call Service to delete selected Task
  deleteTask(): void {
    this.taskService.deleteTask(this.selectedTask.id).subscribe(_ => this.getTasks());
  }

  // EventHandler for changing the current page
  pageChanged(event: any): void {
    this.currentPage = event.page;
    this.getTasks();
  }

  // Will assemble filter string to filter list of tasks, disables filter, if filtertext is empty
  doFilter(): void {
    if (this.filterChoice === 'Task-ID') {
      this.filter = '@iot.id eq \'' + this.filterText + '\'';
    } else if (this.filterChoice === 'Status') {
      this.filter = 'substringof(\'' + this.filterText + '\',taskingParameters/task_status)';
    }
    if (this.filterText === '') {
      this.filter = null;
    }

    this.currentPage = 1;
    this.getTasks();
  }
}
