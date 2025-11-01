import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule, DatePipe, DecimalPipe, NgIf, NgFor, NgClass } from '@angular/common';
import { PagoService, PagoDetalle } from '../../services/pago.service';
import { Location } from '@angular/common';
import { CustomAlertService } from '../../services/custom-alert.service';

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
  templateUrl: './Pagos-miembro.component.html',
  styleUrls: ['./Pagos-miembro.component.scss']
})
export class PagosMiembroComponent implements OnInit {
  pagos: PagoDetalle[] = [];
  miembroId!: number;
  nombreMiembro: string = '';

  constructor(
    private route: ActivatedRoute,
    private pagoService: PagoService,
    private location: Location,
    private customAlertService: CustomAlertService
  ) {}

  ngOnInit(): void {
    this.miembroId = Number(this.route.snapshot.paramMap.get('id'));
    this.cargarPagos();
  }

  //usar desesctructuracion, operador opcional para asi evitar condicionales innecesarias y usando optional chaining
  cargarPagos(): void {
    this.pagoService.getPagosPorMiembro(this.miembroId).subscribe({
      next:(pagos)=>{
        this.pagos = pagos;
        //se usa optional chaining para asignar el nombre si existe el primer pago
        this.nombreMiembro = pagos[0]?.nombreMiembro||'';
      },
      error: (error) =>{
        console.error('Error cargando pagos:',error);
        this.customAlertService.showError(
          'Error',
          'No se pudieron cargar los pagos. Por favor intenta más tarde'
        )
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