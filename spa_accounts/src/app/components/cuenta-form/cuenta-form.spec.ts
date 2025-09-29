import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CuentaForm } from './cuenta-form';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('CuentaForm', () => {
  let component: CuentaForm;
  let fixture: ComponentFixture<CuentaForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CuentaForm, HttpClientTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(CuentaForm);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
