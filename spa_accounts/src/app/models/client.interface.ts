export interface Client {
  id?: string;
  identificacion: string;
  nombre: string;
  genero?: string;
  edad?: number;
  direccion: string;
  telefono: string;
  contrasena?: string;
  estado: boolean;
}
export interface ClientResponse {
  data: Client[];
  message: string;
  success: boolean;
  total: number;
}
export interface ClientRequest {
  identificacion: string;
  nombre: string;
  genero: string;
  edad: number;
  direccion: string;
  telefono: string;
  contrasena: string;
  estado: boolean;
}
export interface ApiResponse<T> {
  data: T;
  message: string;
  success: boolean;
  errors?: string[];
}
