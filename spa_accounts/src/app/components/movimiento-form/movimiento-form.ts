import { Component, Input, Output, EventEmitter, OnInit, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MovimientoRequest } from '../../models/movimiento.interface';

@Component({
  selector: 'app-movimiento-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './movimiento-form.html',
  styleUrl: './movimiento-form.scss'
})
export class MovimientoForm implements OnInit {
  @Output() formSubmit = new EventEmitter<MovimientoRequest>();
  @Output() formCancel = new EventEmitter<void>();

  movimientoForm!: FormGroup;
  isSubmitting = false;

  constructor(private fb: FormBuilder) {
    this.initializeForm();
  }

  ngOnInit() {
    this.initializeForm();
  }

  private initializeForm() {
    this.movimientoForm = this.fb.group({
      numeroCuenta: ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(10),
        Validators.pattern(/^[0-9]+$/)
      ]],
      tipoOperacion: ['CREDITO', [
        Validators.required
      ]],
      valor: [0, [
        Validators.required,
        Validators.min(0.01),
        Validators.max(999999999)
      ]]
    });
  }

  private resetForm() {
    this.movimientoForm.reset();
    this.movimientoForm.patchValue({
      tipoOperacion: 'CREDITO',
      valor: 0
    });
    Object.keys(this.movimientoForm.controls).forEach(key => {
      this.movimientoForm.get(key)?.setErrors(null);
    });
  }

  onSubmit() {
    if (this.movimientoForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      const tipoOperacion = (this.movimientoForm.get('tipoOperacion')?.value || 'CREDITO') as 'DEBITO' | 'CREDITO';
      const valorAbsoluto = Math.abs(this.movimientoForm.get('valor')?.value || 0);

      const formData: MovimientoRequest = {
        numeroCuenta: this.movimientoForm.get('numeroCuenta')?.value,
        tipo: tipoOperacion,
        valor: valorAbsoluto
      };
      
      this.formSubmit.emit(formData);
    } else {
      this.markFormGroupTouched();
    }
  }

  onCancel() {
    this.resetForm();
    this.formCancel.emit();
  }

  private markFormGroupTouched() {
    Object.keys(this.movimientoForm.controls).forEach(key => {
      const control = this.movimientoForm.get(key);
      control?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string {
    const control = this.movimientoForm.get(fieldName);
    if (control?.errors && control.touched) {
      const errors = control.errors;
      if (errors['required']) return `${this.getFieldLabel(fieldName)} es requerido`;
      if (errors['pattern']) {
        switch (fieldName) {
          case 'numeroCuenta': return 'El número de cuenta debe contener solo números';
          default: return 'Formato inválido';
        }
      }
      if (errors['minlength']) return `Mínimo ${errors['minlength'].requiredLength} caracteres`;
      if (errors['maxlength']) return `Máximo ${errors['maxlength'].requiredLength} caracteres`;
      if (errors['min']) return `Valor mínimo: ${errors['min'].min}`;
      if (errors['max']) return `Valor máximo: ${errors['max'].max}`;
    }
    return '';
  }

  private getFieldLabel(fieldName: string): string {
    const labels: Record<string, string> = {
      numeroCuenta: 'Número de Cuenta',
      tipoOperacion: 'Tipo de Operación',
      valor: 'Valor'
    };
    return labels[fieldName] || fieldName;
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.movimientoForm.get(fieldName);
    return !!(control?.invalid && control.touched);
  }

  setSubmitting(value: boolean) {
    this.isSubmitting = value;
  }

  getTipoOperacion(): string {
    const tipo = this.movimientoForm.get('tipoOperacion')?.value || 'CREDITO';
    return tipo === 'CREDITO' ? 'Depósito' : 'Retiro';
  }

  getTipoOperacionSymbol(): string {
    const tipo = this.movimientoForm.get('tipoOperacion')?.value || 'CREDITO';
    return tipo === 'CREDITO' ? '+' : '-';
  }

  getValorConSimbolo(): string {
    const valor = this.movimientoForm.get('valor')?.value || 0;
    const simbolo = this.getTipoOperacionSymbol();
    return `${simbolo}${Math.abs(valor).toFixed(2)}`;
  }
}
