import { Component, OnInit, ViewChild, signal, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Client, ClientRequest } from '../../models/client.interface';
import { ClientService } from '../../services/client.service';
import { Modal } from '../../components/modal/modal';
import { ClientForm } from '../../components/client-form/client-form';
interface ModalConfig {
  title: string;
  size?: 'small' | 'medium' | 'large';
  showCloseButton?: boolean;
  showFooter?: boolean;
  closeOnBackdropClick?: boolean;
  customClass?: string;
}
@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule, Modal, ClientForm],
  templateUrl: './clientes.html',
  styleUrl: './clientes.scss',
  encapsulation: ViewEncapsulation.None
})
export class Clientes implements OnInit {
  @ViewChild('createClientForm') createClientForm!: ClientForm;
  @ViewChild('editClientForm') editClientForm!: ClientForm;
  clients = signal<Client[]>([]);
  filteredClients = signal<Client[]>([]);
  searchTerm = signal<string>('');
  isLoading = signal<boolean>(false);
  error = signal<string>('');
  successMessage = signal<string>('');
  isCreateModalOpen = signal<boolean>(false);
  isEditModalOpen = signal<boolean>(false);
  isDeleteModalOpen = signal<boolean>(false);
  selectedClient = signal<Client | null>(null);
  readonly createModalConfig: ModalConfig = {
    title: 'Agregar Nuevo Cliente',
    showFooter: false,
    showCloseButton: true
  };
  readonly editModalConfig: ModalConfig = {
    title: 'Editar Cliente',
    showFooter: false,
    showCloseButton: true
  };
  readonly deleteModalConfig: ModalConfig = {
    title: 'Confirmar Eliminación',
    showFooter: true,
    showCloseButton: true,
    closeOnBackdropClick: false
  };
  constructor(private clientService: ClientService) {}
  ngOnInit(): void {
    this.loadClients();
  }
  protected loadClients(): void {
    this.clientService.getClients().subscribe({
      next: (clients: Client[]) => {
        this.clients.set(clients);
        this.filterClients();
      },
      error: (error: any) => {
        this.error.set(error.message || 'Error al cargar los clientes');
      }
    });
  }
  onSearchChange(term: string): void {
    this.searchTerm.set(term);
    this.filterClients();
  }
  clearSearch(): void {
    this.searchTerm.set('');
    this.filterClients();
  }
  private filterClients(): void {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) {
      this.filteredClients.set(this.clients());
      return;
    }
    const filtered = this.clients().filter((client: Client) => 
      client.identificacion.toLowerCase().includes(term) ||
      client.nombre.toLowerCase().includes(term) ||
      client.telefono.toLowerCase().includes(term)
    );
    this.filteredClients.set(filtered);
  }
  openCreateModal(): void {
    this.isCreateModalOpen.set(true);
  }
  closeCreateModal(): void {
    this.isCreateModalOpen.set(false);
  }
  openEditModal(client: Client): void {
    this.selectedClient.set(client);
    this.isEditModalOpen.set(true);
  }
  closeEditModal(): void {
    this.isEditModalOpen.set(false);
    this.selectedClient.set(null);
  }
  openDeleteModal(client: Client): void {
    this.selectedClient.set(client);
    this.isDeleteModalOpen.set(true);
  }
  closeDeleteModal(): void {
    this.isDeleteModalOpen.set(false);
    this.selectedClient.set(null);
  }
  onCreateClient(clientData: ClientRequest): void {
    this.clientService.createClient(clientData).subscribe({
      next: (newClient: Client) => {
        this.successMessage.set('Cliente creado exitosamente');
        this.closeCreateModal();
        this.loadClients();
        if (this.createClientForm) {
          this.createClientForm.setSubmitting(false);
        }
      },
      error: (error: any) => {
        this.error.set(error.message || 'Error al crear el cliente');
        if (this.createClientForm) {
          this.createClientForm.setSubmitting(false);
        }
      }
    });
  }
  onEditClient(clientData: ClientRequest): void {
    const client = this.selectedClient();
    if (!client?.id) return;
    this.clientService.updateClient(client.id, clientData).subscribe({
      next: (updatedClient: Client) => {
        this.successMessage.set('Cliente actualizado exitosamente');
        this.closeEditModal();
        this.loadClients();
        if (this.editClientForm) {
          this.editClientForm.setSubmitting(false);
        }
      },
      error: (error: any) => {
        this.error.set(error.message || 'Error al actualizar el cliente');
        if (this.editClientForm) {
          this.editClientForm.setSubmitting(false);
        }
      }
    });
  }
  confirmDelete(): void {
    const client = this.selectedClient();
    if (!client?.id) return;
    this.clientService.deleteClient(client.id).subscribe({
      next: () => {
        this.successMessage.set('Cliente eliminado exitosamente');
        this.closeDeleteModal();
        this.loadClients();
      },
      error: (error: any) => {
        this.error.set(error.message || 'Error al eliminar el cliente');
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
  protected trackByClientId(index: number, client: Client): any {
    return client.id;
  }
  protected clearMessages(): void {
    this.error.set('');
    this.successMessage.set('');
  }
}