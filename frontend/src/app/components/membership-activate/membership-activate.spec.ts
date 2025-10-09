import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembershipActivate } from './membership-activate';

describe('MembershipActivate', () => {
  let component: MembershipActivate;
  let fixture: ComponentFixture<MembershipActivate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MembershipActivate]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MembershipActivate);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
