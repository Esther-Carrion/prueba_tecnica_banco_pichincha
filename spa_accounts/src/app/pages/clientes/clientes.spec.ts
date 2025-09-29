import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Clientes } from './clientes';
import { ClientService } from '../../services/client.service';

describe('Clientes', () => {
  let component: Clientes;
  let fixture: ComponentFixture<Clientes>;

  beforeEach(async () => {
    const mockClientService = {
      getClients: jest.fn(),
      createClient: jest.fn(),
      updateClient: jest.fn(),
      deleteClient: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [Clientes],
      providers: [
        { provide: ClientService, useValue: mockClientService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Clientes);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
