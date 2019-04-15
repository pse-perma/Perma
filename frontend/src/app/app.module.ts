import {BrowserModule} from '@angular/platform-browser';
import {LOCALE_ID, NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {ServersComponent} from './servers/servers.component';
import {FormsModule} from '@angular/forms';
// import { BrowserAnimationsModule } from '@angular/platform-browser/animations'; // <-- NgModel lives here
import {FrontpageComponent} from './frontpage/frontpage.component';
import {AppRoutingModule} from './app-routing.module';
import {HttpClientModule} from '@angular/common/http';
import {LayoutModule} from '@angular/cdk/layout';
import {ActuatorComponent} from './actuator/actuator.component';
import {TaskComponent} from './task/task.component';
import {UiModule} from './ui/ui.module';
import {BsDropdownModule, ModalModule, PaginationModule} from 'ngx-bootstrap';
import {TaskCreateSimpleComponent} from './task-create-simple/task-create-simple.component';
import {ActuatorDetailComponent} from './actuator-detail/actuator-detail.component';
import {ErrorComponent} from './error/error.component';
import {registerLocaleData} from '@angular/common';
import localeDE from '@angular/common/locales/de';
import {VaCreateComponent} from './va-create/va-create.component';
import {TaskDetailComponent} from './task-detail/task-detail.component';
import {FileUploadModule} from 'ng2-file-upload';
import {AlertModule} from 'ngx-bootstrap/alert';

registerLocaleData(localeDE, 'de');
@NgModule({
  declarations: [
    AppComponent,
    ServersComponent,
    FrontpageComponent,
    ActuatorComponent,
    TaskComponent,
    TaskCreateSimpleComponent,
    ActuatorDetailComponent,
    ErrorComponent,
    VaCreateComponent,
    TaskDetailComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    // BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    LayoutModule,
    UiModule,
    BsDropdownModule.forRoot(),
    ModalModule.forRoot(),
    PaginationModule.forRoot(),
    FileUploadModule,
    AlertModule.forRoot()
  ],
  providers: [
    // {provide: ErrorHandler, useClass: GlobalErrorHandlerService}, // TODO: Enable ErrorHandler again
    {provide: LOCALE_ID, useValue: 'de'}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
