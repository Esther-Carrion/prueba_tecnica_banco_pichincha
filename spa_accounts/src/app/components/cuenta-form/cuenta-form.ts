import { Component, Input, Output, EventEmitter, OnInit, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Cuenta, CuentaRequest } from '../../models/cuenta.interface';
import { CuentaService } from '../../services/cuenta.service';

@Component({
  selector: 'app-cuenta-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cuenta-form.html',
  styleUrl: './cuenta-form.scss'
})
export class CuentaForm implements OnInit, OnChanges {
  @Input() cuenta: Cuenta | null = null;
  @Input() isEditMode = false;
  @Output() formSubmit = new EventEmitter<CuentaRequest>();
  @Output() formCancel = new EventEmitter<void>();

  cuentaForm!: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private cuentaService: CuentaService
  ) {
    this.initializeForm();
  }

  ngOnInit() {
    this.initializeForm();
  }

  ngOnChanges() {
    if (this.cuenta && this.isEditMode) {
      this.populateForm();
    } else {
      this.resetForm();
    }
  }

  private initializeForm() {
    this.cuentaForm = this.fb.group({
      clienteIdentificacion: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(15),
        Validators.pattern(/^[0-9]+$/)
      ]],
      numeroCuenta: ['', [
        Validators.required,
        Validators.minLength(9),
        Validators.maxLength(10),
        Validators.pattern(/^[0-9]+$/)
      ]],
      tipoCuenta: ['', [
        Validators.required
      ]],
      saldoInicial: [0, [
        Validators.required,
        Validators.min(0),
        Validators.max(999999999)
      ]],
      estado: [true, [
        Validators.required
      ]]
    });
  }

  private populateForm() {
    if (this.cuenta) {
      this.cuentaForm.patchValue({
        clienteIdentificacion: this.cuenta.clienteIdentificacion,
        numeroCuenta: this.cuenta.numeroCuenta,
        tipoCuenta: this.cuenta.tipoCuenta,
        saldoInicial: this.cuenta.saldoInicial || 0,
        estado: this.cuenta.estado
      });
    }
  }

  private resetForm() {
    this.cuentaForm.reset();
    // Las cuentas nuevas siempre están activas por defecto
    this.cuentaForm.patchValue({
      saldoInicial: 0,
      estado: true
    });
    Object.keys(this.cuentaForm.controls).forEach(key => {
      this.cuentaForm.get(key)?.setErrors(null);
    });
  }

  onSubmit() {
    if (this.cuentaForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      const formData: CuentaRequest = this.cuentaForm.value;
      
      // Asegurarse de que las cuentas nuevas siempre estén activas
      if (!this.isEditMode) {
        formData.estado = true;
      }
      
      this.formSubmit.emit(formData);
    } else {
      this.markFormGroupTouched();
    }
  }

  onCancel() {
    this.formCancel.emit();
  }

  private markFormGroupTouched() {
    Object.keys(this.cuentaForm.controls).forEach(key => {
      const control = this.cuentaForm.get(key);
      control?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string {
    const control = this.cuentaForm.get(fieldName);
    if (control?.errors && control.touched) {
      const errors = control.errors;
      if (errors['required']) return `${this.getFieldLabel(fieldName)} es requerido`;
      if (errors['pattern']) {
        switch (fieldName) {
          case 'clienteIdentificacion': return 'La identificación debe contener solo números';
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
      clienteIdentificacion: 'Identificación del Cliente',
      numeroCuenta: 'Número de Cuenta',
      tipoCuenta: 'Tipo de Cuenta',
      saldoInicial: 'Saldo Inicial',
      estado: 'Estado'
    };
    return labels[fieldName] || fieldName;
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.cuentaForm.get(fieldName);
    return !!(control?.invalid && control.touched);
  }

  setSubmitting(value: boolean) {
    this.isSubmitting = value;
  }
}
