import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutComponent } from './layout/layout.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import {RouterModule} from '@angular/router';
import {SidebarModule} from 'ng-sidebar';

@NgModule({
  declarations: [LayoutComponent, HeaderComponent, FooterComponent],
  imports: [
    RouterModule,
    CommonModule,
    SidebarModule.forRoot()
  ],
  exports: [LayoutComponent]
})
export class UiModule { }
