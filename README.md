Integração ViaCEP + Tabela FIPE — JulioJubilado


Visão geral
Esta versão consolida a integração entre os microsserviços do ecossistema JulioJubilado e as APIs públicas ViaCEP e Tabela FIPE. O objetivo atual é disponibilizar endpoints que permitam listar fabricantes, modelos e anos de veículos atendidos, além de consultar endereços a partir de CEP para apoiar cadastros de clientes e funcionários. No futuro, essas informações servirão de base para um banco que relacione veículos atendidos às suas localidades de origem.

Arquitetura de microsserviços
Projeto 1 — juliopedidoapi
Expõe clientes declarativos OpenFeign para consumir ViaCEP e FIPE diretamente.

Desserializa as respostas em conectores simples (EnderecoRetorno, EnderecoLocalidadeQueryResult, CarrosRetornoQueryResult, CarrosRetornoModelo, CarrosRetornoAno) que espelham a estrutura JSON de cada API externa.

Projeto 2 — JulioJubiladoapi
Consome o microsserviço anterior via PedidoFeignClient e LocalidadeClient, mantendo o acoplamento fraco entre os contextos.

Mapeia os retornos em objetos próprios (LocalidadePedido, EnderecoLocalidadeQueryResult) usados na API principal.

Persiste endereços obtidos via CEP nos cadastros de clientes e funcionários graças às entidades Endereco, Cliente e Funcionario com relacionamento 1:1 para endereço.

Conectores e tratamento de dados
CarrosService encapsula a lógica para localizar marcas e modelos por nome, aplicando filtros, estratégias de fallback e validações antes de consultar a FIPE, garantindo respostas limpas para consumo interno.

LocalidadeService transforma o JSON do ViaCEP em um agregado que destaca município, UF e logradouro, permitindo pré-preenchimento de endereços a partir do CEP.

O serviço principal PedidoService expõe chamadas consolidadas (localidade, fabricantes, modelos e anos), normalizando valores nulos de CEP e reduzindo o payload dos fabricantes à chave name para consumo pela UI ou por outros serviços.

A aplicação conta com um GlobalExceptionHandler para retornar códigos e mensagens semânticas em cenários de validação ou falhas inesperadas.

Endpoints REST expostos
juliopedidoapi (porta 8080)
GET /api/veiculos/{vehicleType}/marcas — lista fabricantes por tipo de veículo.

GET /api/veiculos/{vehicleType}/{brandName}/modelos — retorna modelos do fabricante (tratamento 404/500).

GET /api/veiculos/{vehicleType}/{brandName}/{modelName}/anos — lista anos disponíveis para um modelo.

GET /api/localidades/{cep} — consulta ViaCEP e devolve endereço padronizado.

JulioJubiladoapi (porta 8081)
GET /api/pedidos/cep/{cep} — proxy para ViaCEP com fallback de CEP preenchido.

GET /api/pedidos/fabricantes/{tipoVeiculo} — lista fabricantes simplificados para telas de seleção.

GET /api/pedidos/fabricantes/{tipoVeiculo}/{nomeFabricante} — retorna modelos do fabricante via nome.

GET /api/pedidos/fabricantes/{tipoVeiculo}/{nomeFabricante}/{nomeModelo}/anos — expõe anos disponíveis para seleção de veículo atendido.

Fluxo de integração entre projetos
O front-end (ou outro consumidor) aciona JulioJubiladoapi, que funciona como API de negócios do domínio Oficina Júlio Jubilado.

PedidoService delega as chamadas externas a PedidoFeignClient ou LocalidadeClient, mantendo a lógica de orquestração no projeto principal.

O microsserviço juliopedidoapi comunica-se com as APIs da ViaCEP e da FIPE, transformando os payloads e devolvendo resultados prontos para persistência ou exibição.

Os dados de endereço retornados alimentam as entidades Endereco, Cliente e Funcionario, permitindo gravar os cadastros completos no H2 local.

Configuração e execução
Inicie juliopedidoapi na porta padrão 8080 (o endpoint ViaCEP/FIPE usa as URLs configuradas em application.properties).

Em seguida, suba JulioJubiladoapi na porta 8081; o arquivo de configuração referencia automaticamente o microsserviço anterior através da propriedade juliopedidoapi.url.

Utilize ferramentas como Postman ou o JulioJubiladoApi.postman_collection para testar o fluxo end-to-end.

Próximos passos
Persistir as listas de fabricantes, modelos e anos retornadas pela FIPE para compor o banco histórico de veículos atendidos com sua localidade de origem.

Automatizar o consumo de CEP durante o cadastro de clientes/funcionários, preenchendo campos de endereço a partir dos dados retornados pela API.

Conformidade com as diretrizes
Integração de microsserviços e consumo de APIs externas — ViaCEP e FIPE são consumidas via OpenFeign no juliopedidoapi, justificando a escolha por enriquecer cadastros e oferecer catálogo de veículos.

Modelagem de conectores — Os DTOs EnderecoRetorno, EnderecoLocalidadeQueryResult, CarrosRetornoQueryResult, CarrosRetornoModelo e CarrosRetornoAno espelham os contratos externos, simplificando a manipulação interna.

Orquestração e resiliência — CarrosService, LocalidadeService e PedidoService concentram as regras de negócio, filtros e validações, enquanto o GlobalExceptionHandler padroniza os retornos de erro.

Exposição RESTful — Ambos os microsserviços oferecem endpoints REST claros, com URLs semânticas e tratamento de exceções adequado.

Integração interprojetos — JulioJubiladoapi utiliza PedidoFeignClient para consumir os endpoints REST de juliopedidoapi, fechando o ciclo entre os projetos e disponibilizando os dados consolidados para demais consumidores.
