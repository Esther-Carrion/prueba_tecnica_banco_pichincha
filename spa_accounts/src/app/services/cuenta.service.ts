import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject, of } from 'rxjs';
import { map, catchError, finalize, switchMap } from 'rxjs/operators';
import { Cuenta, CuentaRequest, CuentaResponse, ApiResponse } from '../models/cuenta.interface';
import { ClientService } from './client.service';

@Injectable({
  providedIn: 'root'
})
export class CuentaService {
  private readonly baseUrl = 'http://localhost:8081/api/cuentas';
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private cuentasSubject = new BehaviorSubject<Cuenta[]>([]);

  public loading$ = this.loadingSubject.asObservable();
  public cuentas$ = this.cuentasSubject.asObservable();

  constructor(private http: HttpClient, private clientService: ClientService) {}

  private adaptCuenta(dto: any): Cuenta {
    return {
      id: dto?.id ?? dto?.cuentaId ?? dto?.uuid,
      clienteId: dto?.clienteId,
      clienteNombre: dto?.clienteNombre ?? undefined,
      clienteIdentificacion: dto?.clienteIdentificacion ?? dto?.identificacionCliente ?? '',
      numeroCuenta: String(dto?.numeroCuenta ?? dto?.numero ?? ''),
      tipoCuenta: dto?.tipo ?? dto?.tipoCuenta ?? '',
      saldoInicial: Number(dto?.saldoInicial ?? dto?.saldo ?? 0),
      saldoActual: dto?.saldoActual != null ? Number(dto?.saldoActual) : undefined,
      estado: (dto?.estado ?? dto?.active ?? true) as boolean,
    };
  }

  getCuentas(): Observable<Cuenta[]> {
    this.setLoading(true);
    return this.http.get<any>(this.baseUrl).pipe(
      map((res: any) => Array.isArray(res) ? res : (res?.data ?? res?.items ?? res?.content ?? [])),
      map((items: any[]) => (Array.isArray(items) ? items : []) as any[]),
      map((items: any[]) => items.map((dto: any) => this.adaptCuenta(dto))),
      map((cuentas: Cuenta[]) => {
        this.cuentasSubject.next(cuentas);
        return cuentas;
      }),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }

  getCuentaById(id: string): Observable<Cuenta | null> {
    if (!id.trim()) {
      return throwError(() => new Error('El ID es requerido'));
    }
    this.setLoading(true);
    return this.http.get<any>(`${this.baseUrl}/${id}`).pipe(
      map((res: any) => (res && !Array.isArray(res)) ? res : (res?.data ?? null)),
      map(dto => dto ? this.adaptCuenta(dto) : null),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }

  getCuentasByClienteIdentificacion(identificacion: string): Observable<Cuenta[]> {
    if (!identificacion.trim()) {
      return throwError(() => new Error('La identificación del cliente es requerida'));
    }
    this.setLoading(true);
    return this.http.get<Cuenta[]>(`${this.baseUrl}/cliente/${identificacion}`).pipe(
      map(cuentas => cuentas || []),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }

  createCuenta(cuentaData: CuentaRequest): Observable<Cuenta> {
    this.setLoading(true);
    // Resolver clienteId a partir de la identificación que viene del form
    return this.clientService.findByIdentificacion(cuentaData.clienteIdentificacion).pipe(
      switchMap((client) => {
        if (!client?.id) {
          return throwError(() => new (class extends HttpErrorResponse {
            constructor(){ super({ status: 404, statusText: 'Not Found', error: { message: 'Cliente no encontrado por identificación' } }); }
          })());
        }
        const payload: any = {
          clienteId: client.id,
          tipo: cuentaData.tipoCuenta,
          saldoInicial: cuentaData.saldoInicial,
          estado: cuentaData.estado
        };
        return this.http.post<any>(this.baseUrl, payload).pipe(
          map((res: any) => (res && !Array.isArray(res)) ? res : (res?.data ?? res)),
          map((dto: any) => this.adaptCuenta(dto)),
          map((cuenta: Cuenta) => {
            this.refreshCuentasList();
            return cuenta;
          })
        );
      }),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }

  updateCuenta(id: string, cuentaData: CuentaRequest): Observable<Cuenta> {
    this.setLoading(true);
    const payload: any = {
      tipo: cuentaData.tipoCuenta,
      estado: cuentaData.estado
    };
    return this.http.put<any>(`${this.baseUrl}/${id}`, payload).pipe(
      map((res: any) => (res && !Array.isArray(res)) ? res : (res?.data ?? res)),
      map((dto: any) => this.adaptCuenta(dto)),
      map(cuenta => {
        this.refreshCuentasList();
        return cuenta;
      }),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }

  deleteCuenta(id: string): Observable<boolean> {
    this.setLoading(true);
    return this.http.delete(`${this.baseUrl}/${id}`).pipe(
      map(() => {
        this.refreshCuentasList();
        return true;
      }),
      catchError(this.handleError),
      finalize(() => this.setLoading(false))
    );
  }

  private refreshCuentasList(): void {
    this.getCuentas().subscribe({
      next: () => {},
      error: (error) => console.error('Error al refrescar la lista de cuentas:', error)
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
          errorMessage = 'Cuenta no encontrada';
          break;
        case 409:
          errorMessage = 'Ya existe una cuenta con ese número';
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
    
    console.error('Error en CuentaService:', error);
    return throwError(() => new Error(errorMessage));
  };
}
