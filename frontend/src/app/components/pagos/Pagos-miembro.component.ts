import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule, DatePipe, DecimalPipe, NgIf, NgFor, NgClass } from '@angular/common';
import { PagoService, PagoDetalle } from '../../services/pago.service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-pagos-miembro',
  standalone: true,
  imports: [
    RouterModule,
    CommonModule, 
    NgIf,
    NgFor,
    NgClass,
    DatePipe,     
    DecimalPipe     
  ],
  templateUrl: './pagos-miembro.component.html',
  styleUrls: ['./pagos-miembro.component.scss']
})
export class PagosMiembroComponent implements OnInit {
  pagos: PagoDetalle[] = [];
  miembroId!: number;
  nombreMiembro: string = '';

  constructor(
    private route: ActivatedRoute,
    private pagoService: PagoService,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.miembroId = Number(this.route.snapshot.paramMap.get('id'));
    this.cargarPagos();
  }

  cargarPagos(): void {
    this.pagoService.getPagosPorMiembro(this.miembroId).subscribe({
      next: (pagos) => {
        this.pagos = pagos;
        if (pagos.length > 0) {
          this.nombreMiembro = pagos[0].nombreMiembro;
        }
      },
      error: (error) => {
        console.error('Error cargando pagos:', error);
      }
    });
  }

  getTotalPagos(): number {
    return this.pagos.reduce((total, pago) => total + pago.monto, 0);
  }

  volver():void{
    this.location.back();
  }
}