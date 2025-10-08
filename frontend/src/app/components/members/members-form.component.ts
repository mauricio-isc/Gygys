import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Miembro } from '../../models/miembro.model';
import { MiembroService } from '../../services/miembro.service';
import { Router, ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-member-form',
  templateUrl: './members-form.component.html',
  styleUrls: ['./members-form.component.scss'],
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class MemberFormComponent implements OnInit {

  selectedMember: Miembro = {} as Miembro;

  constructor(
    private miembroService: MiembroService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const memberId = this.route.snapshot.paramMap.get('id');
    if (memberId) {
      this.loadMember(Number(memberId));
    }
  }

  loadMember(id: number): void {
    this.miembroService.findById(id).subscribe({
      next: (miembro) => this.selectedMember = miembro,
      error: (err) => console.error(err)
    });
  }

  saveMember(): void {
    if (this.selectedMember.id) {
      this.miembroService.update(this.selectedMember.id, this.selectedMember).subscribe({
        next: () => {
          Swal.fire('Éxito', 'Miembro actualizado', 'success');
          this.router.navigate(['/members']);
        }
      });
    } else {
      this.miembroService.create(this.selectedMember).subscribe({
        next: () => {
          Swal.fire('Éxito', 'Miembro creado', 'success');
          this.router.navigate(['/members']);
        }
      });
    }
  }
}
