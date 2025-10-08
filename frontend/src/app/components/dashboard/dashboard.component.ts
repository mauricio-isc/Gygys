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

  // Configuración de gráficos
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
    if (this.stats && this.stats.ingresosPorMes) {
      this.incomeChartData.labels = this.stats.ingresosPorMes.map(item => item.mes);
      this.incomeChartData.datasets[0].data = this.stats.ingresosPorMes.map(item => Number(item.ingreso));
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