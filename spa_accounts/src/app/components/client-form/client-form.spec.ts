import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClientForm } from './client-form';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ClientForm', () => {
  let component: ClientForm;
  let fixture: ComponentFixture<ClientForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientForm, HttpClientTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(ClientForm);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
