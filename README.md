#FEATURE 2 - DESENVOLVIMENTO AVANÇADO COM SPRING E MICROSERVIÇOS 


# 🚀 Integração ViaCEP + Tabela FIPE — **JulioJubilado**

> Integração entre **microsserviços** com **Java 21 + Spring Boot 3.x + OpenFeign** para consumo das APIs públicas **ViaCEP** e **Tabela FIPE**.  
> Objetivo: listar **fabricantes, modelos e anos** (FIPE) e consultar **endereços via CEP** (ViaCEP) para apoiar cadastros (clientes/funcionários).


---

## 📚 Índice
- [Visão geral](#-visão-geral)
- [Arquitetura](#-arquitetura)
- [Conectores & Tratamento de Dados](#-conectores--tratamento-de-dados)
- [Endpoints REST](#-endpoints-rest)
- [Fluxo de Integração](#-fluxo-de-integração)
- [Configuração & Execução](#-configuração--execução)
- [Testes Rápidos (cURL)](#-testes-rápidos-curl)
- [Próximos Passos](#-próximos-passos)
- [Conformidade com Diretrizes](#-conformidade-com-diretrizes)

---

## 🧭 Visão geral
Disponibiliza endpoints para:
- **FIPE**: fabricantes, modelos e anos de veículos.
- **ViaCEP**: endereço padronizado a partir de **CEP**.

> No futuro, alimentará um **banco histórico** relacionando **veículos atendidos** às suas **localidades de origem**.

---

## 🏗️ Arquitetura

```text
[ Front-End / Consumidores ]
            │
            ▼
   ┌───────────────────────┐        Delegação (Feign)
   │   JulioJubiladoapi    │────────────────────────────┐
   │  (porta 8081)         │                            │
   │  • API de negócios    │                            │
   └───────────────────────┘                            │
            ▲                                           ▼
            │                                ┌───────────────────────┐
            │                                │    juliopedidoapi     │
            │                                │     (porta 8080)      │
            │                                │ • OpenFeign p/ FIPE   │
            │                                │ • OpenFeign p/ ViaCEP │
            │                                └───────────────────────┘
            │                                           │
            │                                           ├── FIPE (marcas/modelos/anos)
            │                                           └── ViaCEP (endereços por CEP)
