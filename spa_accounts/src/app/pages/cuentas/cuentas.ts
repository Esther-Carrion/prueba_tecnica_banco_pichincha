import { Component, OnInit, ViewChild, signal, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Cuenta, CuentaRequest } from '../../models/cuenta.interface';
import { CuentaService } from '../../services/cuenta.service';
import { Modal } from '../../components/modal/modal';
import { CuentaForm } from '../../components/cuenta-form/cuenta-form';
import { ClientService } from '../../services/client.service';
import { forkJoin, of } from 'rxjs';

interface ModalConfig {
  title: string;
  size?: 'small' | 'medium' | 'large';
  showCloseButton?: boolean;
  showFooter?: boolean;
  closeOnBackdropClick?: boolean;
  customClass?: string;
}

@Component({
  selector: 'app-cuentas',
  standalone: true,
  imports: [CommonModule, FormsModule, Modal, CuentaForm],
  templateUrl: './cuentas.html',
  styleUrl: './cuentas.scss',
  encapsulation: ViewEncapsulation.None
})
export class Cuentas implements OnInit {
  @ViewChild('createCuentaForm') createCuentaForm!: CuentaForm;
  @ViewChild('editCuentaForm') editCuentaForm!: CuentaForm;

  cuentas = signal<Cuenta[]>([]);
  filteredCuentas = signal<Cuenta[]>([]);
  searchTerm = signal<string>('');
  isLoading = signal<boolean>(false);
  error = signal<string>('');
  successMessage = signal<string>('');
  isCreateModalOpen = signal<boolean>(false);
  isEditModalOpen = signal<boolean>(false);
  isDeleteModalOpen = signal<boolean>(false);
  selectedCuenta = signal<Cuenta | null>(null);

  readonly createModalConfig: ModalConfig = {
    title: 'Agregar Nueva Cuenta',
    showFooter: false,
    showCloseButton: true
  };

  readonly editModalConfig: ModalConfig = {
    title: 'Editar Cuenta',
    showFooter: false,
    showCloseButton: true
  };

  readonly deleteModalConfig: ModalConfig = {
    title: 'Confirmar Eliminación',
    showFooter: true,
    showCloseButton: true,
    closeOnBackdropClick: false
  };

  constructor(private cuentaService: CuentaService, private clientService: ClientService) {}

  ngOnInit(): void {
    this.loadCuentas();
  }

  protected loadCuentas(): void {
    this.cuentaService.getCuentas().subscribe({
      next: (cuentas: Cuenta[]) => {
        // Si falta info del cliente pero tenemos clienteId, intentamos hidratar nombre/identificación
        const cuentasConClienteId = cuentas.filter(c => c.clienteId && (!c.clienteIdentificacion || !c.clienteNombre));
        if (cuentasConClienteId.length > 0) {
          const requests = cuentasConClienteId.map(c => this.clientService.getClientById(c.clienteId!));
          forkJoin(requests).subscribe({
            next: (clientes) => {
              const clientesById = new Map(clientes.map(cl => [cl.id, cl] as const));
              const enriquecidas = cuentas.map(c => {
                if (c.clienteId && (!c.clienteIdentificacion || !c.clienteNombre)) {
                  const cl = clientesById.get(c.clienteId);
                  if (cl) {
                    return {
                      ...c,
                      clienteNombre: cl.nombre ?? c.clienteNombre,
                      clienteIdentificacion: cl.identificacion ?? c.clienteIdentificacion,
                    } as Cuenta;
                  }
                }
                return c;
              });
              this.cuentas.set(enriquecidas);
              this.filterCuentas();
            },
            error: () => {
              // Si falla la hidratación, al menos mostramos lo que hay
              this.cuentas.set(cuentas);
              this.filterCuentas();
            }
          });
        } else {
          this.cuentas.set(cuentas);
          this.filterCuentas();
        }
      },
      error: (error: any) => {
        this.error.set(error.message || 'Error al cargar las cuentas');
      }
    });
  }

  onSearchChange(term: string): void {
    this.searchTerm.set(term);
    this.filterCuentas();
  }

  clearSearch(): void {
    this.searchTerm.set('');
    this.filterCuentas();
  }

  private filterCuentas(): void {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) {
      this.filteredCuentas.set(this.cuentas());
      return;
    }

    const filtered = this.cuentas().filter((cuenta: Cuenta) => 
      cuenta.clienteNombre?.toLowerCase().includes(term) ||
      (cuenta.numeroCuenta&& cuenta.numeroCuenta.includes(term)) ||
      cuenta.clienteIdentificacion.toLowerCase().includes(term) ||
      cuenta.tipoCuenta.toLowerCase().includes(term)
    );
    this.filteredCuentas.set(filtered);
  }

  openCreateModal(): void {
    this.isCreateModalOpen.set(true);
  }

  closeCreateModal(): void {
    this.isCreateModalOpen.set(false);
  }

  openEditModal(cuenta: Cuenta): void {
    this.selectedCuenta.set(cuenta);
    this.isEditModalOpen.set(true);
  }

  closeEditModal(): void {
    this.isEditModalOpen.set(false);
    this.selectedCuenta.set(null);
  }

  openDeleteModal(cuenta: Cuenta): void {
    this.selectedCuenta.set(cuenta);
    this.isDeleteModalOpen.set(true);
  }

  closeDeleteModal(): void {
    this.isDeleteModalOpen.set(false);
    this.selectedCuenta.set(null);
  }

  onCreateCuenta(cuentaData: CuentaRequest): void {
    this.cuentaService.createCuenta(cuentaData).subscribe({
      next: (newCuenta: Cuenta) => {
        this.successMessage.set('Cuenta creada exitosamente');
        this.closeCreateModal();
        this.loadCuentas();
        if (this.createCuentaForm) {
          this.createCuentaForm.setSubmitting(false);
        }
      },
      error: (error: any) => {
        this.error.set(error.message || 'Error al crear la cuenta');
        if (this.createCuentaForm) {
          this.createCuentaForm.setSubmitting(false);
        }
      }
    });
  }

  onEditCuenta(cuentaData: CuentaRequest): void {
    const cuenta = this.selectedCuenta();
    if (!cuenta?.id) return;

    this.cuentaService.updateCuenta(cuenta.id, cuentaData).subscribe({
      next: (updatedCuenta: Cuenta) => {
        this.successMessage.set('Cuenta actualizada exitosamente');
        this.closeEditModal();
        this.loadCuentas();
        if (this.editCuentaForm) {
          this.editCuentaForm.setSubmitting(false);
        }
      },
      error: (error: any) => {
        this.error.set(error.message || 'Error al actualizar la cuenta');
        if (this.editCuentaForm) {
          this.editCuentaForm.setSubmitting(false);
        }
      }
    });
  }

  confirmDelete(): void {
    const cuenta = this.selectedCuenta();
    if (!cuenta?.id) return;

    this.cuentaService.deleteCuenta(cuenta.id).subscribe({
      next: () => {
        this.successMessage.set('Cuenta eliminada exitosamente');
        this.closeDeleteModal();
        this.loadCuentas();
      },
      error: (error: any) => {
        this.error.set(error.message || 'Error al eliminar la cuenta');
      }
    });
  }

  protected formatDate(dateString: string): string {
    if (!dateString) return '';
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      });
    } catch {
      return dateString;
    }
  }

  protected trackByCuentaId(index: number, cuenta: Cuenta): any {
    return cuenta.id;
  }

  protected clearMessages(): void {
    this.error.set('');
    this.successMessage.set('');
  }

  protected getEstadoBadgeClass(cuenta: Cuenta): string {
    return cuenta.estado ? 'badge--success' : 'badge--danger';
  }

  protected getEstadoText(cuenta: Cuenta): string {
    return cuenta.estado ? 'Activa' : 'Inactiva';
  }
}
