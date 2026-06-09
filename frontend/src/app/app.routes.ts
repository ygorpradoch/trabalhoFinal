import { Routes } from '@angular/router';
import { ListaProdutos } from './components/lista-produtos/lista-produtos';
import { FormProduto } from './components/form-produto/form-produto';
import { TipoProdutoComponent } from './components/tipo-produto/tipo-produto';

export const routes: Routes = [
  { path: '', redirectTo: 'produtos', pathMatch: 'full' },
  { path: 'produtos', component: ListaProdutos },
  { path: 'produtos/novo', component: FormProduto },
  { path: 'tipos-produto', component: TipoProdutoComponent },
];
