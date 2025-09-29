import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Header } from './header';

describe('Header', () => {
  let component: Header;
  let fixture: ComponentFixture<Header>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Header]
    }).compileComponents();

    fixture = TestBed.createComponent(Header);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the title', () => {
    const testTitle = 'Test Title';
    component.title = testTitle;
    fixture.detectChanges();

    const titleElement = fixture.nativeElement.querySelector('h1');
    expect(titleElement).toBeTruthy();
    expect(titleElement.textContent.trim()).toBe(`Gestión de ${testTitle}`);
  });

  it('should have default title when none provided', () => {
    fixture.detectChanges();

    const titleElement = fixture.nativeElement.querySelector('h1');
    expect(titleElement).toBeTruthy();
    expect(titleElement.textContent.trim()).toBe('Gestión de Título');
  });

  it('should update title when input changes', () => {
    const initialTitle = 'Initial Title';
    const updatedTitle = 'Updated Title';

    component.title = initialTitle;
    fixture.detectChanges();

    let titleElement = fixture.nativeElement.querySelector('h1');
    expect(titleElement.textContent.trim()).toBe(`Gestión de ${initialTitle}`);

    component.title = updatedTitle;
    fixture.detectChanges();

    titleElement = fixture.nativeElement.querySelector('h1');
    expect(titleElement.textContent.trim()).toBe(`Gestión de ${updatedTitle}`);
  });

  it('should have correct CSS classes', () => {
    fixture.detectChanges();
    
    const headerElement = fixture.nativeElement.querySelector('.page-header');
    expect(headerElement).toBeTruthy();

    const titleElement = fixture.nativeElement.querySelector('.page-header__title');
    expect(titleElement).toBeTruthy();

    const subtitleElement = fixture.nativeElement.querySelector('.page-header__subtitle');
    expect(subtitleElement).toBeTruthy();
  });
});
