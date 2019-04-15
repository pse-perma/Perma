import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ServersComponent} from './servers/servers.component';
import {FrontpageComponent} from './frontpage/frontpage.component';
import {ActuatorComponent} from './actuator/actuator.component';
import {TaskComponent} from './task/task.component';
import {TaskCreateSimpleComponent} from './task-create-simple/task-create-simple.component';
import {ActuatorDetailComponent} from './actuator-detail/actuator-detail.component';
import {ErrorComponent} from './error/error.component';
import {VaCreateComponent} from './va-create/va-create.component';
import {TaskDetailComponent} from './task-detail/task-detail.component';

const routes: Routes = [
  {path: '', redirectTo: '/frontpage', pathMatch: 'full'},
  {path: 'frontpage', component: FrontpageComponent},
  {path: 'servers', component: ServersComponent},
  {path: 'actuators', component: ActuatorComponent},
  {path: 'tasks', component: TaskComponent},
  {path: 'task-create-simple', component: TaskCreateSimpleComponent},
  {path: 'actuators/:id', component: ActuatorDetailComponent},
  {path: 'virtual-actuator', component: VaCreateComponent},
  {path: 'tasks/:id', component: TaskDetailComponent},
  {path: 'error', component: ErrorComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
