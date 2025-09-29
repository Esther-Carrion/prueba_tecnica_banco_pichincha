export interface Movimiento {
  id?: string;
  fecha: Date;
  cliente: string;
  numeroCuenta: string;
  tipoMovimiento?: string;
  saldoInicial: number;
  valor: number;
  saldo: number;
  estado: boolean;
}

export interface MovimientoDTO {
  id?: string;
  fecha: Date;
  // Para hidratar datos de cuenta desde el backend nuevo
  cuentaId?: string;
  cuenta: {
    numeroCuenta: string;
    clienteNombre: string;
    clienteIdentificacion: string;
  };
  saldoInicial: number;
  valor: number;
  saldo: number;
  estado: boolean;
}

export interface MovimientoRequest {
  numeroCuenta: string;
  tipo: 'DEBITO' | 'CREDITO';
  valor: number;
}

export interface MovimientoOperacionResponse {
  resultado: string;
}

export interface ReporteRequest {
  identificacion: string;
  desde: string;
  hasta: string;
}

export interface ApiResponse<T> {
  data: T;
  message: string;
  success: boolean;
  errors?: string[];
}
