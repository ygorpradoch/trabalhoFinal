import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Produto } from '../../models/produto.model';
import { ProdutoService } from '../../services/produto';

@Component({
  selector: 'app-lista-produtos',
  imports: [CommonModule, RouterLink],
  templateUrl: './lista-produtos.html',
  styleUrl: './lista-produtos.css'
})
export class ListaProdutos implements OnInit {

  produtos: Produto[] = [];
  mensagemErro: string = '';
  mensagemSucesso: string = '';
  carregando: boolean = false;

  constructor(private produtoService: ProdutoService) {}

  ngOnInit(): void {
    this.carregarProdutos();
  }

  carregarProdutos(): void {
    this.carregando = true;
    this.mensagemErro = '';
    this.produtoService.listar().subscribe({
      next: (lista) => {
        this.produtos = lista;
        this.carregando = false;
      },
      error: () => {
        this.mensagemErro = 'Erro ao carregar produtos. Verifique se o backend está em execução.';
        this.carregando = false;
      }
    });
  }

  remover(id: number, nome: string): void {
    if (!confirm(`Deseja realmente remover o produto "${nome}"?`)) {
      return;
    }
    this.produtoService.remover(id).subscribe({
      next: () => {
        this.mensagemSucesso = `Produto "${nome}" removido com sucesso.`;
        this.mensagemErro = '';
        this.carregarProdutos();
        setTimeout(() => (this.mensagemSucesso = ''), 3000);
      },
      error: (err) => {
        this.mensagemErro = err.error?.mensagem ?? 'Erro ao remover produto.';
        this.mensagemSucesso = '';
      }
    });
  }
}
