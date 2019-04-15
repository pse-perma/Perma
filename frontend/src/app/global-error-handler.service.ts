import {ErrorHandler, Injectable, Injector} from '@angular/core';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable()
export class GlobalErrorHandlerService implements ErrorHandler {

  constructor(private injector: Injector) {
  }

  handleError(error: any): void {
    const router = this.injector.get(Router);
    if (error instanceof HttpErrorResponse) {
      console.error('Backend returned status code:' + error.status);
      console.error('Error Message:' + error.message);
      console.error('An error occurred');
    }
    // router.navigate(['error']); // TODO: Enable Again
  }
}
