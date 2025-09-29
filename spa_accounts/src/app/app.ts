import { Component, signal, ViewChild } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { Sidebar } from './components/sidebar/sidebar';
import { Header } from './components/header/header';
import { TopBar } from './components/top-bar/top-bar';
import { Footer } from './components/footer/footer';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Sidebar, Header, TopBar, Footer, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  @ViewChild(Sidebar) sidebar!: Sidebar;
  protected readonly title = signal('Clientes');
  protected readonly section = signal('clientes');

  constructor(private router: Router) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      const path = event.url.replace('/', '');
      const currentSection = path || 'clientes';
      this.section.set(currentSection);
      const titleMap: Record<string,string> = { 
        clientes: 'Clientes', 
        cuentas: 'Cuentas', 
        movimientos: 'Movimientos', 
        reportes: 'Reportes' 
      };
      this.title.set(titleMap[currentSection] ?? 'Sistema Bancario');
    });
  }

  onSectionChange(section: string) {
    this.router.navigate([`/${section}`]);
  }

  onMobileMenuToggle() {
    this.sidebar?.toggleMobileMenu();
  }
}
