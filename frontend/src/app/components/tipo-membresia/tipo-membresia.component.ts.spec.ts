import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TipoMembresiaComponentTs } from './tipo-membresia.component.ts.js';

describe('TipoMembresiaComponentTs', () => {
  let component: TipoMembresiaComponentTs;
  let fixture: ComponentFixture<TipoMembresiaComponentTs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TipoMembresiaComponentTs]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TipoMembresiaComponentTs);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
