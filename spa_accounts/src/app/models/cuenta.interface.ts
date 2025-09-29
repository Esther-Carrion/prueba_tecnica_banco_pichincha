export interface Cuenta {
  id?: string;
  // Nuevo backend entrega clienteId; lo guardamos y también exponemos nombre/identificación derivados
  clienteId?: string;
  clienteNombre?: string;
  clienteIdentificacion: string;
  numeroCuenta: string;
  // En el backend nuevo viene como "tipo"
  tipoCuenta: string;
  saldoInicial: number;
  // Backend nuevo provee saldoActual
  saldoActual?: number;
  estado: boolean;
}

export interface CuentaResponse {
  data: Cuenta[];
  message: string;
  success: boolean;
  total: number;
}

export interface CuentaRequest {
  clienteIdentificacion: string;
  numeroCuenta: string;
  tipoCuenta: string;
  saldoInicial: number;
  estado: boolean;
}

export interface ApiResponse<T> {
  data: T;
  message: string;
  success: boolean;
  errors?: string[];
}
