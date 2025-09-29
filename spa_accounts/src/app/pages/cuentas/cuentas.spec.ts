import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Cuentas } from './cuentas';
import { CuentaService } from '../../services/cuenta.service';

describe('Cuentas', () => {
  let component: Cuentas;
  let fixture: ComponentFixture<Cuentas>;

  beforeEach(async () => {
    const mockCuentaService = {
      getCuentas: jest.fn(),
      createCuenta: jest.fn(),
      updateCuenta: jest.fn(),
      deleteCuenta: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [Cuentas],
      providers: [
        { provide: CuentaService, useValue: mockCuentaService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Cuentas);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
