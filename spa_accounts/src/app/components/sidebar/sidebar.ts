import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar {
  @Input() active: string = 'clientes';
  @Output() sectionChange = new EventEmitter<string>();
  isMobileMenuOpen = false;

  constructor(private router: Router) {}

  select(section: string) {
    this.sectionChange.emit(section);
    this.closeMobileMenu(); 
  }

  navigateTo(route: string) {
    this.router.navigate([`/${route}`]);
    this.closeMobileMenu();
  }

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu() {
    this.isMobileMenuOpen = false;
  }
}
