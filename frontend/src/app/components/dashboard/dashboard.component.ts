import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardStats } from '../../models/dashboard.model';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/auth.model';
import { ChartConfiguration } from 'chart.js';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: true,
  imports:[
    CommonModule,
    NgChartsModule
  ]
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats | null = null;
  currentUser: User | null = null;
  loading = true;

  
  incomeChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: {
        display: false
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function(value) {
            return '$' + value.toLocaleString();
          }
        }
      }
    }
  };

  incomeChartData: ChartConfiguration['data'] = {
    labels: [],
    datasets: [{
      label: 'Ingresos',
      data: [],
      backgroundColor: 'rgba(102, 126, 234, 0.8)',
      borderColor: 'rgba(102, 126, 234, 1)',
      borderWidth: 1
    }]
  };

  constructor(
    private dashboardService: DashboardService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadDashboardStats();
  }

  loadDashboardStats(): void {
    this.loading = true;
    this.dashboardService.getDashboardStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.updateIncomeChart();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading dashboard stats:', error);
        this.loading = false;
      }
    });
  }

updateIncomeChart(): void {
  console.log('Actualizando gráfica...');
  console.log('Stats disponibles:', this.stats);
  
  if (this.stats && this.stats.ingresosPorMes && this.stats.ingresosPorMes.length > 0) {
    console.log('Datos de ingresosPorMes:', this.stats.ingresosPorMes);
    
    // crear objetos para forzar el change detection
    const labels = this.stats.ingresosPorMes.map(item => item.mes);
    const data = this.stats.ingresosPorMes.map(item => Number(item.ingreso));
    
    this.incomeChartData = {
      labels: labels,
      datasets: [{
        label: 'Ingresos Mensuales',
        data: data,
        backgroundColor: 'rgba(102, 126, 234, 0.8)',
        borderColor: 'rgba(102, 126, 234, 1)',
        borderWidth: 2,
        borderRadius: 8,
        borderSkipped: false,
      }]
    };
    
    console.log('Gráfica actualizada con:', { labels, data });
    
  } else {
    console.warn('No hay datos para la gráfica, usando datos de ejemplo');
    
    // ejemplo de datos
    this.incomeChartData = {
      labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
      datasets: [{
        label: 'Ingresos Mensuales',
        data: [1200000, 1500000, 1350000, 1800000, 1600000, 1750000],
        backgroundColor: 'rgba(102, 126, 234, 0.8)',
        borderColor: 'rgba(102, 126, 234, 1)',
        borderWidth: 2,
        borderRadius: 8,
      }]
    };
  }
  
}

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP'
    }).format(value);
  }

  refreshData(): void {
    this.loadDashboardStats();
  }
}