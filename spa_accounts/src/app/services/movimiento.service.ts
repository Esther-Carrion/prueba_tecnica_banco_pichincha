import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { 
  Movimiento, 
  MovimientoDTO, 
  MovimientoRequest, 
  MovimientoOperacionResponse,
  ReporteRequest,
  ApiResponse 
} from '../models/movimiento.interface';
import { CuentaService } from './cuenta.service';

@Injectable({
  providedIn: 'root'
})
export class MovimientoService {
  private apiUrl = 'http://localhost:8081/api/movimientos';

  constructor(private http: HttpClient, private cuentaService: CuentaService) {}

  // Obtener todos los movimientos
  getMovimientos(): Observable<MovimientoDTO[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map((res: any) => Array.isArray(res) ? res : (res?.data ?? res?.items ?? res?.content ?? [])),
      map((items: any[]) => (Array.isArray(items) ? items : []) as any[]),
      map((items: any[]) => items.map((dto: any) => this.adaptMovimiento(dto))),
      catchError(this.handleError)
    );
  }

  private adaptMovimiento(dto: any): MovimientoDTO {
    const fechaVal = dto?.fecha;
    // Algunos backends envían epoch (segundos/milisegundos) o número decimal
    let fecha: Date;
    if (fechaVal instanceof Date) {
      fecha = fechaVal as Date;
    } else if (typeof fechaVal === 'number') {
      // Si es muy grande, asumimos milisegundos; si es pequeño/decimal, convertimos a ms
      const ms = fechaVal > 1e12 ? fechaVal : Math.floor(fechaVal * 1000);
      fecha = new Date(ms);
    } else if (typeof fechaVal === 'string') {
      const num = Number(fechaVal);
      if (!isNaN(num)) {
        const ms = num > 1e12 ? num : Math.floor(num * 1000);
        fecha = new Date(ms);
      } else {
        fecha = new Date(fechaVal);
      }
    } else {
      fecha = new Date();
    }

    const saldoInicial = typeof dto?.saldoInicial === 'number' ? dto.saldoInicial : (dto?.saldoPreMovimiento ?? 0);
    const saldoFinal = typeof dto?.saldo === 'number' ? dto.saldo : (dto?.saldoPostMovimiento ?? 0);

    return {
      id: dto?.id ?? dto?.movimientoId ?? dto?.uuid,
      fecha,
      cuentaId: dto?.cuentaId,
      cuenta: {
        numeroCuenta: dto?.numeroCuenta ?? '',
        clienteNombre: dto?.clienteNombre ?? '',
        clienteIdentificacion: dto?.clienteIdentificacion ?? ''
      },
      saldoInicial,
      valor: Number(dto?.valor ?? 0),
      saldo: Number(saldoFinal),
      estado: (dto?.estado ?? true) as boolean
    };
  }

  // Realizar una operación (depósito o retiro)
  realizarMovimiento(numeroCuenta: string, tipo: 'DEBITO' | 'CREDITO', valor: number): Observable<MovimientoOperacionResponse> {
    // Resolver cuentaId por numeroCuenta
    return this.cuentaService.getCuentas().pipe(
      map((cuentas) => cuentas.find(c => c.numeroCuenta === String(numeroCuenta))),
      switchMap((cuenta) => {
        if (!cuenta?.id && !cuenta?.clienteId) {
          return throwError(() => new Error('Cuenta no encontrada'));
        }
        const payload = {
          cuentaId: cuenta.id || cuenta.clienteId, // preferimos id de cuenta; fallback improbable
          tipo,
          valor: Number(valor)
        };
        return this.http.post<{ resultado: string }>(`${this.apiUrl}`, payload).pipe(
          map(response => ({ resultado: response.resultado ?? 'Movimiento registrado' }))
        );
      }),
      catchError(this.handleError)
    );
  }

  // Eliminar movimiento
  deleteMovimiento(id: string): Observable<{ resultado: string }> {
    return this.http.delete<{ resultado: string }>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  // Obtener reporte de movimientos (JSON) del backend nuevo
  obtenerReporte(clienteId: string, fechaInicio: string, fechaFin: string): Observable<MovimientoDTO[]> {
    const params = new HttpParams()
      .set('clienteId', clienteId)
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin);

    return this.http.get<any>('http://localhost:8081/api/reportes', { params }).pipe(
      map((res: any) => Array.isArray(res) ? res : (res?.data ?? res?.items ?? res?.content ?? [])),
      map((items: any[]) => (Array.isArray(items) ? items : []) as any[]),
      map((items: any[]) => items.map((dto: any) => this.adaptMovimiento(dto))),
      catchError(this.handleError)
    );
  }

  // Generar reporte PDF (Blob)
  generarReportePdf(clienteId: string, fechaInicio: string, fechaFin: string): Observable<Blob> {
    const params = new HttpParams()
      .set('clienteId', clienteId)
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin);

    return this.http.get('http://localhost:8081/api/reportes/pdf', {
      params,
      responseType: 'blob',
      headers: { Accept: 'application/pdf' }
    }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Ha ocurrido un error desconocido';
    
    if (error.error instanceof ErrorEvent) {
      // Error del lado del cliente
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Error del lado del servidor
      if (error.error?.message) {
        errorMessage = error.error.message;
      } else if (error.error?.details) {
        errorMessage = error.error.details;
      } else {
        errorMessage = `Error ${error.status}: ${error.message}`;
      }
    }
    
    return throwError(() => new Error(errorMessage));
  }
}
