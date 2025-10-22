import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

export interface AlertData {
  type: 'error' | 'success' | 'warning' | 'info' | 'confirm';
  title: string;
  message: string;
  duration?: number;
  icon?: string;
  confirmText?: string;
  cancelText?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CustomAlertService {
  private alertSubject = new BehaviorSubject<AlertData | null>(null);
  public alert$ = this.alertSubject.asObservable();
  
  private confirmResult = new Subject<boolean>();

  showError(title: string, message: string, duration: number = 5000) {
    this.showAlert({
      type: 'error',
      title,
      message,
      duration,
      icon: '❌'
    });
  }

  showSuccess(title: string, message: string, duration: number = 3000) {
    this.showAlert({
      type: 'success',
      title,
      message,
      duration,
      icon: '✅'
    });
  }

  showWarning(title: string, message: string, duration: number = 4000) {
    this.showAlert({
      type: 'warning',
      title,
      message,
      duration,
      icon: '⚠️'
    });
  }

  showInfo(title: string, message: string, duration: number = 4000) {
    this.showAlert({
      type: 'info',
      title,
      message,
      duration,
      icon: 'ℹ️'
    });
  }

  showConfirm(
    title: string, 
    message: string, 
    confirmText: string = 'Aceptar', 
    cancelText: string = 'Cancelar'
  ): Observable<boolean> {
    this.showAlert({
      type: 'confirm',
      title,
      message,
      confirmText,
      cancelText,
      icon: '❓'
    });
    
    return this.confirmResult.asObservable();
  }

  private showAlert(alertData: AlertData) {
    this.alertSubject.next(alertData);

    // Auto-ocultar solo si no es confirmación y tiene duración
    if (alertData.type !== 'confirm' && alertData.duration && alertData.duration > 0) {
      setTimeout(() => {
        this.hideAlert();
      }, alertData.duration);
    }
  }

  hideAlert() {
    this.alertSubject.next(null);
  }

  confirm() {
    this.confirmResult.next(true);
    this.hideAlert();
  }

  cancel() {
    this.confirmResult.next(false);
    this.hideAlert();
  }
}