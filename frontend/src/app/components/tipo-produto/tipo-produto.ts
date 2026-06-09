import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TipoProduto } from '../../models/tipo-produto.model';
import { TipoProdutoService } from '../../services/tipo-produto';

@Component({
  selector: 'app-tipo-produto',
  imports: [CommonModule, FormsModule],
  templateUrl: './tipo-produto.html',
  styleUrl: './tipo-produto.css'
})
export class TipoProdutoComponent implements OnInit {

  tipos: TipoProduto[] = [];
  novoNome: string = '';
  mensagemSucesso: string = '';
  mensagemErro: string = '';
  salvando: boolean = false;

  constructor(private tipoProdutoService: TipoProdutoService) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.tipoProdutoService.listar().subscribe({
      next: (lista) => (this.tipos = lista),
      error: () => (this.mensagemErro = 'Erro ao carregar tipos de produto.')
    });
  }

  salvar(): void {
    if (!this.novoNome.trim()) {
      this.mensagemErro = 'O nome é obrigatório.';
      return;
    }
    this.salvando = true;
    this.mensagemErro = '';
    this.mensagemSucesso = '';

    this.tipoProdutoService.salvar({ nome: this.novoNome.trim() }).subscribe({
      next: () => {
        this.mensagemSucesso = 'Tipo de produto cadastrado com sucesso!';
        this.novoNome = '';
        this.salvando = false;
        this.carregar();
        setTimeout(() => (this.mensagemSucesso = ''), 3000);
      },
      error: (err) => {
        this.salvando = false;
        if (err.status === 422) {
          this.mensagemErro = err.error?.mensagem ?? 'Regra de negócio violada.';
        } else if (err.status === 400 && err.error?.campos?.nome) {
          this.mensagemErro = err.error.campos.nome;
        } else {
          this.mensagemErro = 'Erro ao salvar tipo de produto.';
        }
      }
    });
  }

  remover(id: number, nome: string): void {
    if (!confirm(`Deseja remover o tipo "${nome}"?`)) return;
    this.tipoProdutoService.remover(id).subscribe({
      next: () => {
        this.mensagemSucesso = `Tipo "${nome}" removido.`;
        this.mensagemErro = '';
        this.carregar();
        setTimeout(() => (this.mensagemSucesso = ''), 3000);
      },
      error: (err) => {
        this.mensagemErro = err.error?.mensagem ?? 'Erro ao remover. Verifique se há produtos vinculados.';
        this.mensagemSucesso = '';
      }
    });
  }
}
