import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { MembersComponent } from './components/members/members.component';
import { MembershipsComponent } from './components/memberships/memberships.component';
import { NotificationsComponent } from './components/notifications/notifications.component';
import { MemberFormComponent } from './components/members/members-form.component';
import { PagosMiembroComponent } from './components/pagos/Pagos-miembro.component';
import { TipoMembresiaComponentTs } from './components/tipo-membresia/tipo-membresia.component.ts';
import { CustomAlertComponent } from './components/custom-alert/custom-alert.component';

export const appRoutes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'members', component: MembersComponent, canActivate: [AuthGuard] },
  { path: 'memberships', component: MembershipsComponent, canActivate: [AuthGuard] },
  { path: 'notifications', component: NotificationsComponent, canActivate: [AuthGuard] },
  { path: 'members/new', component: MemberFormComponent, canActivate: [AuthGuard] },
  { path: 'members/:id', component: MemberFormComponent, canActivate: [AuthGuard] },
  { path: 'pagos/miembro/:id', component: PagosMiembroComponent, canActivate: [AuthGuard] },
  { path: 'tipos-membresia', component: TipoMembresiaComponentTs},
  {path: 'custom-alert', component: CustomAlertComponent},
  { path: '**', redirectTo: '/dashboard' },
];

@NgModule({
  imports: [RouterModule.forRoot(appRoutes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }
