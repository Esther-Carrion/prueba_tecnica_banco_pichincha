import { Component, OnInit, ViewChild, signal, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MovimientoDTO, MovimientoRequest } from '../../models/movimiento.interface';
import { MovimientoService } from '../../services/movimiento.service';
import { CuentaService } from '../../services/cuenta.service';
import { ClientService } from '../../services/client.service';
import { forkJoin } from 'rxjs';
import { Modal } from '../../components/modal/modal';
import { MovimientoForm } from '../../components/movimiento-form/movimiento-form';

interface ModalConfig {
  title: string;
  size?: 'small' | 'medium' | 'large';
  showCloseButton?: boolean;
  showFooter?: boolean;
  closeOnBackdropClick?: boolean;
  customClass?: string;
}

@Component({
  selector: 'app-movimientos',
  standalone: true,
  imports: [CommonModule, FormsModule, Modal, MovimientoForm],
  templateUrl: './movimientos.html',
  styleUrl: './movimientos.scss',
  encapsulation: ViewEncapsulation.None
})
export class Movimientos implements OnInit {
  @ViewChild('movimientoForm') movimientoForm!: MovimientoForm;

  movimientos = signal<MovimientoDTO[]>([]);
  filteredMovimientos = signal<MovimientoDTO[]>([]);
  searchTerm = signal<string>('');
  isLoading = signal<boolean>(false);
  error = signal<string>('');
  successMessage = signal<string>('');
  isCreateModalOpen = signal<boolean>(false);
  isDeleteModalOpen = signal<boolean>(false);
  selectedMovimiento = signal<MovimientoDTO | null>(null);

  readonly createModalConfig: ModalConfig = {
    title: 'Realizar Nuevo Movimiento',
    showFooter: false,
    showCloseButton: true
  };

  readonly deleteModalConfig: ModalConfig = {
    title: 'Confirmar Eliminación',
    showFooter: true,
    showCloseButton: true,
    closeOnBackdropClick: false
  };

  constructor(
    private movimientoService: MovimientoService,
    private cuentaService: CuentaService,
    private clientService: ClientService
  ) {}

  ngOnInit(): void {
    this.loadMovimientos();
  }

  protected loadMovimientos(): void {
    this.isLoading.set(true);
    this.movimientoService.getMovimientos().subscribe({
      next: (movimientos: MovimientoDTO[]) => {
        // Si vienen sin datos de cuenta/cliente pero hay cuentaId, intentamos hidratar
        const conCuentaId = movimientos.filter(m => m.cuentaId && (!m.cuenta?.numeroCuenta || !m.cuenta?.clienteIdentificacion));
        if (conCuentaId.length > 0) {
          const cuentaIds = Array.from(new Set(conCuentaId.map(m => m.cuentaId!)));
          // Pedimos cada cuenta y luego (si hiciera falta) el cliente
          const cuentaRequests = cuentaIds.map(id => this.cuentaService.getCuentaById(id));
          forkJoin(cuentaRequests).subscribe({
            next: (cuentas) => {
              const cuentasById = new Map((cuentas || []).filter(Boolean).map(c => [c!.id!, c!] as const));
              const enriquecidos = movimientos.map(m => {
                if (m.cuentaId) {
                  const c = cuentasById.get(m.cuentaId);
                  if (c) {
                    return {
                      ...m,
                      cuenta: {
                        numeroCuenta: c.numeroCuenta,
                        clienteNombre: c.clienteNombre || '',
                        clienteIdentificacion: c.clienteIdentificacion || ''
                      }
                    } as MovimientoDTO;
                  }
                }
                return m;
              });
              this.movimientos.set(enriquecidos);
              this.filterMovimientos();
              this.isLoading.set(false);
            },
            error: () => {
              this.movimientos.set(movimientos);
              this.filterMovimientos();
              this.isLoading.set(false);
            }
          });
        } else {
          this.movimientos.set(movimientos);
          this.filterMovimientos();
          this.isLoading.set(false);
        }
      },
      error: (error: any) => {
        this.error.set(error.message || 'Error al cargar los movimientos');
        this.isLoading.set(false);
      }
    });
  }

  onSearchChange(term: string): void {
    this.searchTerm.set(term);
    this.filterMovimientos();
  }

  clearSearch(): void {
    this.searchTerm.set('');
    this.filterMovimientos();
  }

  private filterMovimientos(): void {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) {
      this.filteredMovimientos.set(this.movimientos());
      return;
    }

    const filtered = this.movimientos().filter((movimiento: MovimientoDTO) => 
      (movimiento.cuenta?.numeroCuenta || '').toLowerCase().includes(term) ||
      (movimiento.cuenta?.clienteIdentificacion || '').toLowerCase().includes(term) ||
      movimiento.valor.toString().includes(term)
    );
    this.filteredMovimientos.set(filtered);
  }

  openCreateModal(): void {
    this.isCreateModalOpen.set(true);
  }

  closeCreateModal(): void {
    this.isCreateModalOpen.set(false);
  }

  openDeleteModal(movimiento: MovimientoDTO): void {
    this.selectedMovimiento.set(movimiento);
    this.isDeleteModalOpen.set(true);
  }

  closeDeleteModal(): void {
    this.isDeleteModalOpen.set(false);
    this.selectedMovimiento.set(null);
  }

  onCreateMovimiento(movimientoData: MovimientoRequest): void {
    this.movimientoService.realizarMovimiento(movimientoData.numeroCuenta, movimientoData.tipo, movimientoData.valor).subscribe({
      next: (response) => {
        this.successMessage.set(response.resultado);
        this.closeCreateModal();
        this.loadMovimientos();
        if (this.movimientoForm) {
          this.movimientoForm.setSubmitting(false);
        }
      },
      error: (error: any) => {
        this.error.set(error.message || 'Error al realizar el movimiento');
        if (this.movimientoForm) {
          this.movimientoForm.setSubmitting(false);
        }
      }
    });
  }

  confirmDelete(): void {
    const movimiento = this.selectedMovimiento();
    if (!movimiento?.id) return;

    this.movimientoService.deleteMovimiento(movimiento.id).subscribe({
      next: (response) => {
        this.successMessage.set(response.resultado);
        this.closeDeleteModal();
        this.loadMovimientos();
      },
      error: (error: any) => {
        this.error.set(error.message || 'Error al eliminar el movimiento');
      }
    });
  }

  protected formatDate(date: Date | string): string {
    if (!date) return '';
    try {
      const dateObj = typeof date === 'string' ? new Date(date) : date;
      return dateObj.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return date.toString();
    }
  }

  protected getMovimientoTypeClass(valor: number): string {
    return valor >= 0 ? 'text-green-600' : 'text-red-600';
  }

  protected getMovimientoType(valor: number): string {
    return valor >= 0 ? 'Depósito' : 'Retiro';
  }

  protected getEstadoBadgeClass(estado: boolean): string {
    return estado ? 'badge--success' : 'badge--danger';
  }

  protected getEstadoText(estado: boolean): string {
    return estado ? 'Activo' : 'Inactivo';
  }

  protected trackByMovimientoId(index: number, movimiento: MovimientoDTO): any {
    return movimiento.id;
  }

  protected clearMessages(): void {
    this.error.set('');
    this.successMessage.set('');
  }
}
