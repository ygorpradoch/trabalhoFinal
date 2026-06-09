import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TipoProduto, TipoProdutoRequest } from '../models/tipo-produto.model';

@Injectable({
  providedIn: 'root'
})
export class TipoProdutoService {

  private readonly apiUrl = '/api/tipos-produto';

  constructor(private http: HttpClient) {}

  listar(): Observable<TipoProduto[]> {
    return this.http.get<TipoProduto[]>(this.apiUrl);
  }

  salvar(request: TipoProdutoRequest): Observable<TipoProduto> {
    return this.http.post<TipoProduto>(this.apiUrl, request);
  }

  remover(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
