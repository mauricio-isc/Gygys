import { Component, OnDestroy } from "@angular/core";
import { Subscription } from "rxjs";
import { AlertData, CustomAlertService } from "../../services/custom-alert.service";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";

@Component({
    selector: 'app-custom-alert',
    templateUrl: 'custom-alert.component.html',
    styleUrls: ['custom-alert.component.scss'],
    standalone: true,
    imports: [CommonModule, RouterModule] 
})
export class CustomAlertComponent implements OnDestroy {
    alertData: AlertData | null = null;
    private subscription: Subscription;

    constructor(private alertService: CustomAlertService){
        this.subscription = this.alertService.alert$.subscribe(data => {
            this.alertData = data;
        });
    }

    close(){
        this.alertService.hideAlert();
    }

    onConfirm() {
        this.alertService.confirm();
    }

    onCancel() {
        this.alertService.cancel();
    }

    getAlertIcon(): string {
      if (!this.alertData) return '';
      
      switch (this.alertData.type) {
        case 'error':
          return 'fas fa-exclamation-circle';
        case 'success':
          return 'fas fa-check-circle';
        case 'warning':
          return 'fas fa-exclamation-triangle';
        case 'info':
          return 'fas fa-info-circle';
        case 'confirm':
          return 'fas fa-question-circle';
        default:
          return 'fas fa-bell';
      }
    }

    getAlertSubtitle(): string {
      if (!this.alertData) return '';
      
      switch (this.alertData.type) {
        case 'error':
          return 'Error del sistema';
        case 'success':
          return 'Operación exitosa';
        case 'warning':
          return 'Advertencia importante';
        case 'info':
          return 'Información del sistema';
        default:
          return 'Notificación';
      }
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}