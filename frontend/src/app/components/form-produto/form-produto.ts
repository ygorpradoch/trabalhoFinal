import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TipoProduto } from '../../models/tipo-produto.model';
import { ProdutoRequest } from '../../models/produto.model';
import { TipoProdutoService } from '../../services/tipo-produto';
import { ProdutoService } from '../../services/produto';

@Component({
  selector: 'app-form-produto',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './form-produto.html',
  styleUrl: './form-produto.css'
})
export class FormProduto implements OnInit {

  tipos: TipoProduto[] = [];
  produto: ProdutoRequest = { nome: '', preco: 0, tipoProdutoId: 0 };
  mensagemSucesso: string = '';
  mensagemErro: string = '';
  errosCampos: { [key: string]: string } = {};
  salvando: boolean = false;

  constructor(
    private tipoProdutoService: TipoProdutoService,
    private produtoService: ProdutoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.tipoProdutoService.listar().subscribe({
      next: (lista) => (this.tipos = lista),
      error: () => (this.mensagemErro = 'Erro ao carregar tipos de produto.')
    });
  }

  salvar(): void {
    this.mensagemErro = '';
    this.mensagemSucesso = '';
    this.errosCampos = {};
    this.salvando = true;

    this.produtoService.salvar(this.produto).subscribe({
      next: () => {
        this.mensagemSucesso = 'Produto cadastrado com sucesso!';
        this.salvando = false;
        this.produto = { nome: '', preco: 0, tipoProdutoId: 0 };
        setTimeout(() => this.router.navigate(['/produtos']), 1500);
      },
      error: (err) => {
        this.salvando = false;
        if (err.status === 400 && err.error?.campos) {
          this.errosCampos = err.error.campos;
          this.mensagemErro = 'Corrija os erros de validação.';
        } else if (err.status === 422) {
          this.mensagemErro = err.error?.mensagem ?? 'Regra de negócio violada.';
        } else {
          this.mensagemErro = 'Erro ao salvar produto.';
        }
      }
    });
  }
}
