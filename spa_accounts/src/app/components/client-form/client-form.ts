import { Component, Input, Output, EventEmitter, OnInit, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Client, ClientRequest } from '../../models/client.interface';
import { ClientService } from '../../services/client.service';
@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './client-form.html',
  styleUrl: './client-form.scss'
})
export class ClientForm implements OnInit, OnChanges {
  @Input() client: Client | null = null;
  @Input() isEditMode = false;
  @Output() formSubmit = new EventEmitter<ClientRequest>();
  @Output() formCancel = new EventEmitter<void>();
  clientForm!: FormGroup;
  isSubmitting = false;
  constructor(
    private fb: FormBuilder,
    private clientService: ClientService
  ) {
    this.initializeForm();
  }
  ngOnInit() {
    this.initializeForm();
  }
  ngOnChanges() {
    if (this.client && this.isEditMode) {
      this.populateForm();
    } else {
      this.resetForm();
    }
  }
  private initializeForm() {
    this.clientForm = this.fb.group({
      identificacion: ['', [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(10),
        Validators.pattern(/^[0-9]{10}$/)
      ]],
      nombre: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100),
        Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)
      ]],
      genero: ['', [
        Validators.required
      ]],
      edad: [18, [
        Validators.required,
        Validators.min(18),
        Validators.max(120)
      ]],
      direccion: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(200)
      ]],
      telefono: ['', [
        Validators.required,
        Validators.pattern(/^[0-9]{9,10}$/)
      ]],
      contrasena: ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(20)
      ]],
      estado: [true, [
        Validators.required
      ]]
    });
  }
  private populateForm() {
    if (this.client) {
      this.clientForm.patchValue({
        identificacion: this.client.identificacion,
        nombre: this.client.nombre,
        genero: this.client.genero || '',
        edad: this.client.edad || 18,
        direccion: this.client.direccion,
        telefono: this.client.telefono,
        contrasena: '', 
        estado: this.client.estado
      });
    }
  }
  private resetForm() {
    this.clientForm.reset();
    Object.keys(this.clientForm.controls).forEach(key => {
      this.clientForm.get(key)?.setErrors(null);
    });
  }
  onSubmit() {
    if (this.clientForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      const raw = this.clientForm.value;
      const formData: ClientRequest = {
        identificacion: raw.identificacion,
        nombre: raw.nombre,
        genero: raw.genero,
        edad: raw.edad,
        direccion: raw.direccion,
        telefono: raw.telefono,
        contrasena: raw.contrasena,
        estado: raw.estado
      };
      this.formSubmit.emit(formData);
    } else {
      this.markFormGroupTouched();
    }
  }
  onCancel() {
    this.formCancel.emit();
  }
  private markFormGroupTouched() {
    Object.keys(this.clientForm.controls).forEach(key => {
      const control = this.clientForm.get(key);
      control?.markAsTouched();
    });
  }
  getFieldError(fieldName: string): string {
    const control = this.clientForm.get(fieldName);
    if (control?.errors && control.touched) {
      const errors = control.errors;
      if (errors['required']) return `${this.getFieldLabel(fieldName)} es requerido`;
      if (errors['pattern']) {
        switch (fieldName) {
          case 'identificacion': return 'La identificación debe contener solo números';
          case 'telefono': return 'El teléfono debe tener un formato válido';
          case 'nombre': return 'Solo se permiten letras y espacios';
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
      identificacion: 'Identificación',
      nombre: 'Nombre completo',
      genero: 'Género',
      edad: 'Edad',
      telefono: 'Teléfono',
      direccion: 'Dirección',
      contrasena: 'Contraseña',
      estado: 'Estado'
    };
    return labels[fieldName] || fieldName;
  }
  isFieldInvalid(fieldName: string): boolean {
    const control = this.clientForm.get(fieldName);
    return !!(control?.invalid && control.touched);
  }
  setSubmitting(value: boolean) {
    this.isSubmitting = value;
  }
}
