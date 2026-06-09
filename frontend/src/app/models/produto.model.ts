export interface Produto {
  id: number;
  nome: string;
  preco: number;
  tipoProdutoId: number;
  tipoProdutoNome: string;
}

export interface ProdutoRequest {
  nome: string;
  preco: number;
  tipoProdutoId: number;
}
