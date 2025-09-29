import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Movimientos } from './movimientos';
import { MovimientoService } from '../../services/movimiento.service';

describe('Movimientos', () => {
  let component: Movimientos;
  let fixture: ComponentFixture<Movimientos>;

  beforeEach(async () => {
    const mockMovimientoService = {
      getMovimientos: jest.fn(),
      createMovimiento: jest.fn(),
      updateMovimiento: jest.fn(),
      deleteMovimiento: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [Movimientos],
      providers: [
        { provide: MovimientoService, useValue: mockMovimientoService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Movimientos);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
