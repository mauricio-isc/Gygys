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
      const iconMap: Record<string, string> = {
        error: 'fas fa-exclamation-circle',
        success: 'fas fa-check-circle',
        warning: 'fas fa-exclamation-triangle',
        info: 'fas fa-info-circle',
        confirm: 'fas fa-question-circle'
      };
      return iconMap[this.alertData.type] || 'fas fa-bell'
    }

    getAlertSubtitle(): string {
      if (!this.alertData) return '';
        const iconMap: Record<string, string> ={
            error: 'Error del sistema',
            success: 'Operación exitosa',
            warning: 'Advertencia importante',
            info: 'Información del sistema',
        }

        return iconMap[this.alertData.type] || 'Notificación'
      }
    

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}