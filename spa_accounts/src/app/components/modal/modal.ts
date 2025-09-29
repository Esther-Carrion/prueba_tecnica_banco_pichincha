import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, state, style, transition, animate } from '@angular/animations';
export interface ModalConfig {
  title: string;
  size?: 'small' | 'medium' | 'large';
  showCloseButton?: boolean;
  showFooter?: boolean;
  closeOnBackdropClick?: boolean;
  customClass?: string;
}
@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal.html',
  styleUrl: './modal.scss',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('200ms ease-in', style({ opacity: 1 }))
      ]),
      transition(':leave', [
        animate('200ms ease-out', style({ opacity: 0 }))
      ])
    ]),
    trigger('slideIn', [
      transition(':enter', [
        style({ transform: 'translateY(-50px)', opacity: 0 }),
        animate('300ms ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ transform: 'translateY(-50px)', opacity: 0 }))
      ])
    ])
  ]
})
export class Modal implements OnInit {
  @Input() isOpen = false;
  @Input() config: ModalConfig = {
    title: '',
    size: 'medium',
    showCloseButton: true,
    showFooter: true,
    closeOnBackdropClick: true
  };
  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
  ngOnInit() {
    if (this.isOpen) {
      document.body.style.overflow = 'hidden';
    }
  }
  ngOnDestroy() {
    document.body.style.overflow = 'auto';
  }
  ngOnChanges() {
    if (this.isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
  }
  onBackdropClick() {
    if (this.config.closeOnBackdropClick) {
      this.onClose();
    }
  }
  onClose() {
    this.close.emit();
    document.body.style.overflow = 'auto';
  }
  onConfirm() {
    this.confirm.emit();
  }
  onCancel() {
    this.cancel.emit();
  }
  getModalSizeClass(): string {
    const sizeClasses = {
      small: 'modal__dialog--small',
      medium: 'modal__dialog--medium',
      large: 'modal__dialog--large'
    };
    return sizeClasses[this.config.size || 'medium'];
  }
  hasCustomFooter(): boolean {
    return false; 
  }
}
