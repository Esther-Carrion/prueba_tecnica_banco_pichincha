import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { map, catchError, finalize } from 'rxjs/operators';
import { Client, ClientRequest, ClientResponse, ApiResponse } from '../models/client.interface';
@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private readonly baseUrl = 'http://localhost:8081/api/clientes';
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private clientsSubject = new BehaviorSubject<Client[]>([]);
  public loading$ = this.loadingSubject.asObservable();
  public clients$ = this.clientsSubject.asObservable();
  constructor(private http: HttpClient) {}
  /**
   * Adapt a backend client DTO (including new backend with nested persona) to our Client interface
   */
  private adaptClient(dto: any): Client {
    const persona = dto?.persona ?? {};
    const firstName = dto?.nombre ?? persona?.nombre ?? '';
    const lastName = persona?.apellido ?? persona?.apellidos ?? '';
    const fullName = [firstName, lastName].filter(Boolean).join(' ').trim() || firstName || '';

    return {
      id: dto?.id ?? dto?.clienteId ?? dto?.idCliente ?? dto?.uuid,
      identificacion: dto?.identificacion ?? persona?.identificacion ?? dto?.cedula ?? dto?.identification ?? '',
      nombre: fullName,
      genero: dto?.genero ?? persona?.genero ?? dto?.sexo ?? dto?.gender,
      edad: dto?.edad ?? persona?.edad,
      direccion: dto?.direccion ?? persona?.direccion ?? dto?.address ?? '',
      telefono: dto?.telefono ?? persona?.telefono ?? dto?.phone ?? '',
      // contrasena is never returned by backend for listing; leave undefined
      estado: (dto?.estado ?? dto?.active ?? true) as boolean,
    };
  }

  getClients(): Observable<Client[]> {
    this.setLoading(true);
    return this.http.get<any>(this.baseUrl).pipe(
      // Unwrap common envelopes: raw array | { data: [] } | { items: [] } | { content: [] }
      map((res: any) => Array.isArray(res) ? res : (res?.data ?? res?.items ?? res?.content ?? [])),
      map((items: any[]) => (Array.isArray(items) ? items : []) as any[]),
      map((items: any[]) => items.map((dto: any) => this.adaptClient(dto))),
      map((clients: Client[]) => {
        this.clientsSubject.next(clients);
        return clients;
      }),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }

  /** Buscar cliente por identificación (cedula) recorriendo la lista */
  findByIdentificacion(identificacion: string): Observable<Client | null> {
    const ident = (identificacion || '').trim();
    if (!ident) {
      return throwError(() => new Error('La identificación es requerida'));
    }
    // Reutilizamos getClients para obtener la lista actualizada
    return this.getClients().pipe(
      map((clients) => clients.find(c => c.identificacion === ident) || null)
    );
  }

  /** Obtener cliente por ID del backend nuevo */
  getClientById(id: string): Observable<Client> {
    if (!id?.trim()) {
      return throwError(() => new Error('El ID es requerido'));
    }
    this.setLoading(true);
    return this.http.get<any>(`${this.baseUrl}/${id}`).pipe(
      // Podría venir como objeto directo o envuelto
      map((res: any) => (res && !Array.isArray(res) && (res.id || res.persona)) ? res : (res?.data ?? res)),
      map((dto: any) => this.adaptClient(dto)),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }
  getClientByCedula(cedula: string): Observable<Client | null> {
    if (!cedula.trim()) {
      return throwError(() => new Error('La cédula es requerida'));
    }
    this.setLoading(true);
    const params = new HttpParams().set('cedula', cedula.trim());
    return this.http.get<ApiResponse<Client>>(`${this.baseUrl}/buscar`, { params }).pipe(
      map(response => {
        if (response.success && response.data) {
          return response.data;
        }
        return null;
      }),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }
  createClient(clientData: ClientRequest): Observable<Client> {
    this.setLoading(true);
    // Mapear al payload esperado por el backend nuevo
    const usernameFallback = (clientData.nombre || '').toLowerCase().replace(/\s+/g, '.').slice(0, 20) || clientData.identificacion;
    const payload: any = {
      persona: {
        nombre: clientData.nombre,
        // Si el backend soporta apellido separado y tienes el dato, podrías dividirlo aquí
        // apellido: '',
        genero: clientData.genero,
        identificacion: clientData.identificacion,
        telefono: clientData.telefono,
        direccion: clientData.direccion
        // edad: clientData.edad // Solo si el backend lo acepta en persona
      },
      username: usernameFallback,
      password: clientData.contrasena,
      estado: clientData.estado
    };

    return this.http.post<any>(this.baseUrl, payload).pipe(
      // Respuesta puede venir como objeto o envuelta en { data }
      map((res: any) => (res && !Array.isArray(res) && (res.id || res.persona)) ? res : (res?.data ?? res)),
      map((dto: any) => this.adaptClient(dto)),
      map(client => {
        this.refreshClientsList();
        return client;
      }),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }
  updateClient(id: string, clientData: ClientRequest): Observable<Client> {
    this.setLoading(true);
    const payload: any = {
      persona: {
        nombre: clientData.nombre,
        genero: clientData.genero,
        identificacion: clientData.identificacion,
        telefono: clientData.telefono,
        direccion: clientData.direccion
      },
      // Si el backend permite actualizar username/password, podrías incluirlos condicionalmente
      // username: ..., password: ...,
      estado: clientData.estado
    };

    return this.http.put<any>(`${this.baseUrl}/${id}`, payload).pipe(
      map((res: any) => (res && !Array.isArray(res) && (res.id || res.persona)) ? res : (res?.data ?? res)),
      map((dto: any) => this.adaptClient(dto)),
      map(client => {
        this.refreshClientsList();
        return client;
      }),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }
  deleteClient(id: string): Observable<boolean> {
    this.setLoading(true);
    return this.http.delete(`${this.baseUrl}/${id}`).pipe(
      map(() => {
        this.refreshClientsList();
        return true;
      }),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }
  existsByCedula(cedula: string, excludeId?: number): Observable<boolean> {
    const params = new HttpParams()
      .set('cedula', cedula)
      .set('excludeId', excludeId?.toString() || '');
    return this.http.get<ApiResponse<boolean>>(`${this.baseUrl}/exists`, { params }).pipe(
      map(response => response.data || false),
      catchError(() => throwError(() => new Error('Error al validar la cédula')))
    );
  }
  private refreshClientsList(): void {
    this.getClients().subscribe({
      next: () => {},
      error: (error) => console.error('Error al refrescar la lista de clientes:', error)
    });
  }
  private setLoading(loading: boolean): void {
    this.loadingSubject.next(loading);
  }
  private handleError = (error: HttpErrorResponse): Observable<never> => {
    let errorMessage = 'Ha ocurrido un error inesperado';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      switch (error.status) {
        case 400:
          errorMessage = error.error?.message || 'Datos inválidos';
          break;
        case 404:
          errorMessage = 'Cliente no encontrado';
          break;
        case 409:
          errorMessage = 'Ya existe un cliente con esa cédula';
          break;
        case 500:
          errorMessage = 'Error interno del servidor';
          break;
        case 0:
          errorMessage = 'No se puede conectar con el servidor. Verifique su conexión.';
          break;
        default:
          errorMessage = error.error?.message || `Error del servidor: ${error.status}`;
      }
    }
    console.error('Error en ClientService:', error);
    return throwError(() => new Error(errorMessage));
  };
}
