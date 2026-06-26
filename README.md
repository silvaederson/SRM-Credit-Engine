📄 ARQUIVO README.md COMPLETO PARA BAIXAR
Abaixo está o conteúdo completo do README. Você pode copiar todo o texto e salvar como README.md no seu projeto.

markdown
# SRM Credit Engine

**Plataforma de Cessão de Crédito Multimoedas para FIDCs**

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-20.10+-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![CI/CD](https://github.com/seu-usuario/SRM-Credit-Engine/actions/workflows/ci-cd-pipeline.yml/badge.svg)](https://github.com/seu-usuario/SRM-Credit-Engine/actions)

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Contexto Empresarial](#contexto-empresarial)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Execução](#instalação-e-execução)
- [Endpoints da API](#endpoints-da-api)
- [Modelagem de Dados](#modelagem-de-dados)
- [Testes](#testes)
- [Observabilidade](#observabilidade)
- [CI/CD](#cicd)
- [Decisões Arquiteturais (ADRs)](#decisões-arquiteturais-adrs)
- [Design de Alta Escala](#design-de-alta-escala-1m-transaçõesminuto)
- [Git Workflow](#git-workflow)
- [Como Contribuir](#como-contribuir)
- [Licença](#licença)

---

## 🎯 Sobre o Projeto

O **SRM Credit Engine** é uma plataforma robusta para precificação e liquidação de ativos financeiros (duplicatas, contratos, recebíveis) com suporte a multimoedas (BRL/USD). Desenvolvido para atender às necessidades da **SRM Asset**, referência em fundos de investimento, especialmente em FIDCs (Fundos de Investimento em Direitos Creditórios).

### **Principais Funcionalidades**

- ✅ **Gestão de Câmbio**: Armazenamento e consulta de taxas de câmbio com cache distribuído (Redis)
- ✅ **Motor de Precificação**: Strategy Pattern para diferentes tipos de recebíveis
- ✅ **Transações ACID**: Garantia de integridade com Optimistic Locking
- ✅ **API RESTful**: Documentada com OpenAPI/Swagger
- ✅ **Consultas Analíticas**: Relatórios otimizados com SQL nativo
- ✅ **Multimoeda**: Suporte a BRL, USD, EUR, GBP, JPY
- ✅ **Observabilidade**: Logs estruturados, métricas Prometheus, Grafana
- ✅ **Resiliência**: Circuit Breaker, Retry e Timeout com Resilience4j

---

## 🏢 Contexto Empresarial

A **SRM Asset** é uma referência em fundos de investimento, especialmente em FIDCs (Fundos de Investimento em Direitos Creditórios). Nossa operação envolve a aquisição de ativos (duplicatas, contratos, recebíveis) de empresas cedentes, provendo liquidez ao mercado.

Com a globalização do portfólio, o fundo passou a operar com caixa multimoedas (BRL e USD). O time de mesas de operação necessita de um sistema robusto, o **SRM Credit Engine**, para precificar e liquidar esses ativos com segurança e precisão decimal.

### **O Problema de Negócio:**
Precisamos de uma plataforma que receba um lote de recebíveis, calcule o "deságio" (desconto) baseado no risco do ativo e na moeda de pagamento, e registre a transação de forma auditável.

### **Fórmula Base:**
Valor Presente = Valor Face / (1 + Taxa Base + Spread) ^ Prazo

text

### **Variações de Risco:**
| Tipo de Recebível | Spread Mensal |
|-------------------|---------------|
| Duplicata Mercantil | 1.5% |
| Cheque Pré-datado | 2.5% |
| Título Público | 0.8% |
| Título Corporativo | 1.2% |
| Empréstimo Consumidor | 3.5% |

---

## 🏗️ Arquitetura

### **Diagrama C4 - Nível 1 (Contexto)**
┌─────────────────────────────────────────────────────────────────┐
│ SRM Credit Engine │
│ │
│ ┌─────────────┐ ┌─────────────┐ ┌───────────────────┐ │
│ │ Operador │────▶│ Frontend │────▶│ API Gateway │ │
│ │ (Usuário) │ │ (React) │ │ (Spring Boot) │ │
│ └─────────────┘ └─────────────┘ └─────────┬─────────┘ │
│ │ │
│ ┌─────────────────────────────────────────────────┐│ │
│ │ Backend Core ││ │
│ │ ┌──────────┐ ┌──────────┐ ┌──────────┐ ││ │
│ │ │Service │ │Strategy │ │Repository│ ││ │
│ │ │Layer │◀─▶│Layer │◀─▶│Layer │ ││ │
│ │ └──────────┘ └──────────┘ └──────────┘ ││ │
│ └─────────────────────────────────────────────────┘│ │
│ │ │ │
│ ┌───────────────────────┼──────────────────────────┐│ │
│ │ ▼ ││ │
│ │ ┌──────────────┐ ┌──────────────┐ ││ │
│ │ │ PostgreSQL │ │ Redis │ ││ │
│ │ │ (Database) │ │ (Cache) │ ││ │
│ │ └──────────────┘ └──────────────┘ ││ │
│ └─────────────────────────────────────────────────┘│ │
└─────────────────────────────────────────────────────┘│ │
│ │
┌───────────────────────────────────────────┘ │
▼ │
┌────────────────────────────────────────────────────────────────────┐│
│ APIs Externas ││
│ ┌──────────────────────────────────────────────────────────────┐ ││
│ │ API de Câmbio (OpenExchangeRates / BCB) │ ││
│ └──────────────────────────────────────────────────────────────┘ ││
└────────────────────────────────────────────────────────────────────┘│

text

### **Diagrama C4 - Nível 2 (Container)**
┌─────────────────────────────────────────────────────────────────────┐
│ SRM Credit Engine │
├─────────────────────────────────────────────────────────────────────┤
│ │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Frontend (React SPA) │ │
│ │ ┌──────────┐ ┌──────────┐ ┌──────────┐ │ │
│ │ │ UI │ │ State │ │ API │ │ │
│ │ │Components│ │ Mgmt │ │ Client │ │ │
│ │ │ (MUI) │ │ (Zustand)│ │ (Axios) │ │ │
│ │ └──────────┘ └──────────┘ └──────────┘ │ │
│ └──────────────────┬──────────────────────────────────────────┘ │
│ │ REST API / WebSocket │
│ ┌──────────────────┴──────────────────────────────────────────┐ │
│ │ API Gateway (Spring Boot) │ │
│ │ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │ │
│ │ │ Controllers │ │ Exception │ │ Security │ │ │
│ │ │ │ │ Handling │ │ (JWT) │ │ │
│ │ └──────────────┘ └──────────────┘ └──────────────┘ │ │
│ └──────────────────┬──────────────────────────────────────────┘ │
│ │ │
│ ┌──────────────────┴──────────────────────────────────────────┐ │
│ │ Service Layer │ │
│ │ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │ │
│ │ │ Settlement │ │ Exchange │ │ Base Rate │ │ │
│ │ │ Service │ │ Rate Service│ │ Service │ │ │
│ │ └──────────────┘ └──────────────┘ └──────────────┘ │ │
│ │ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │ │
│ │ │ Report │ │ Pricing │ │ Cache │ │ │
│ │ │ Service │ │ Service │ │ Service │ │ │
│ │ └──────────────┘ └──────────────┘ └──────────────┘ │ │
│ └──────────────────┬──────────────────────────────────────────┘ │
│ │ │
│ ┌──────────────────┴──────────────────────────────────────────┐ │
│ │ Strategy Pattern (Pricing) │ │
│ │ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │ │
│ │ │ Commercial │ │ Post-Dated │ │ Government │ │ │
│ │ │ Invoice │ │ Check │ │ Bond │ │ │
│ │ │ (1.5%) │ │ (2.5%) │ │ (0.8%) │ │ │
│ │ └──────────────┘ └──────────────┘ └──────────────┘ │ │
│ └──────────────────┬──────────────────────────────────────────┘ │
│ │ │
│ ┌──────────────────┴──────────────────────────────────────────┐ │
│ │ Repository Layer │ │
│ │ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │ │
│ │ │ JPA │ │ SQL Native │ │ Cache │ │ │
│ │ │ Repositories│ │ (Reports) │ │ Repository │ │ │
│ │ └──────────────┘ └──────────────┘ └──────────────┘ │ │
│ └──────────────────┬──────────────────────────────────────────┘ │
│ │ │
│ ┌──────────────────┴──────────────────────────────────────────┐ │
│ │ Data Layer │ │
│ │ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │ │
│ │ │ PostgreSQL │ │ Redis │ │ Flyway │ │ │
│ │ │ (ACID) │ │ (Cache) │ │ (Migration) │ │ │
│ │ └──────────────┘ └──────────────┘ └──────────────┘ │ │
│ └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘

text

---

## 🛠️ Tecnologias

### **Backend**
| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.2.5 | Framework principal |
| Spring Data JPA | - | ORM e persistência |
| Spring Security | - | Segurança e autenticação |
| PostgreSQL | 15 | Banco de dados relacional |
| H2 | - | Banco em memória para testes |
| Flyway | 9.22.3 | Migrações de banco |
| Redis | 7 | Cache distribuído |
| Resilience4j | - | Circuit Breaker e Retry |
| Micrometer | - | Métricas e observabilidade |
| SpringDoc OpenAPI | 2.3.0 | Documentação API |
| Lombok | - | Redução de boilerplate |
| MapStruct | 1.5.5 | Mapeamento DTO/Entity |
| Logstash | - | Logs estruturados em JSON |
| JUnit 5 | - | Testes unitários |
| Mockito | - | Mocks para testes |
| TestContainers | - | Testes de integração |

### **Frontend (Sugerido)**
| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| React | 18.x | Framework SPA |
| TypeScript | 5.x | Tipagem estática |
| Axios | - | Cliente HTTP |
| React Router | 6.x | Roteamento |
| Material-UI | 5.x | Componentes UI |
| Zustand | - | Gerenciamento de estado |
| React Hook Form | - | Validação de formulários |

### **Infraestrutura**
| Tecnologia | Descrição |
|------------|-----------|
| Docker | Containerização |
| Docker Compose | Orquestração multi-container |
| Prometheus | Coleta de métricas |
| Grafana | Visualização de métricas |
| GitHub Actions | CI/CD |
| SonarCloud | Qualidade de código |

---

## 📋 Pré-requisitos

- **Java 17** ou superior ([Download](https://adoptium.net/))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **Docker** e **Docker Compose** ([Download](https://www.docker.com/products/docker-desktop))
- **Git** ([Download](https://git-scm.com/downloads))
- **Postman** (opcional, para testar a API)

---

## 🚀 Instalação e Execução

### **1. Clonar o Repositório**
```bash
git clone https://github.com/seu-usuario/SRM-Credit-Engine.git
cd SRM-Credit-Engine
2. Subir os Serviços com Docker (Opcional)
bash
# Subir PostgreSQL, Redis, Prometheus e Grafana
docker-compose up -d

# Verificar containers
docker ps

# Ver logs
docker-compose logs -f
3. Configurar o Banco de Dados
Para desenvolvimento (H2 em memória):

yaml
# O application.yml já está configurado com H2
spring:
  datasource:
    url: jdbc:h2:mem:srmdb
    username: sa
    password: 
Para produção (PostgreSQL):

yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/srm
    username: admin
    password: admin
4. Executar a Aplicação
Via Maven
bash
# Compilar
mvn clean compile

# Rodar
mvn spring-boot:run
Via JAR
bash
# Gerar JAR
mvn clean package -DskipTests

# Executar JAR
java -jar target/SRM-Credit-Engine-1.0-SNAPSHOT.jar
Via Docker
bash
# Build da imagem
docker build -t srm-credit-engine .

# Rodar container
docker run -p 8080:8080 srm-credit-engine
5. Acessar a Aplicação
Serviço	URL	Credenciais
API	http://localhost:8080	-
Swagger UI	http://localhost:8080/swagger-ui/index.html	-
H2 Console	http://localhost:8080/h2-console	JDBC: jdbc:h2:mem:srmdb, User: sa
Actuator	http://localhost:8080/actuator	-
Prometheus	http://localhost:9090	-
Grafana	http://localhost:3000	admin/admin
📡 Endpoints da API
Health Check
Método	Endpoint	Descrição
GET	/api/v1/health/status	Status detalhado da aplicação
GET	/api/v1/health/simple	Health check simples
GET	/actuator/health	Health padrão Spring
GET	/actuator/metrics	Métricas da aplicação
GET	/actuator/prometheus	Métricas no formato Prometheus
Transações
Método	Endpoint	Descrição
POST	/api/v1/transactions	Criar nova transação
GET	/api/v1/transactions	Listar todas transações
GET	/api/v1/transactions/{id}	Buscar transação por ID
GET	/api/v1/transactions/reference/{ref}	Buscar por referência externa
Exemplo de Request (POST):

json
{
  "creditorId": 1,
  "receivableTypeId": 1,
  "faceValue": 10000.00,
  "currencyCode": "BRL",
  "dueDate": "2026-09-30",
  "paymentCurrency": "USD",
  "externalReference": "INV-001",
  "notes": "Test transaction"
}
Exemplo de Response:

json
{
  "id": 1,
  "creditorId": 1,
  "creditorName": "ABC Corporation",
  "receivableTypeId": 1,
  "receivableTypeName": "Commercial Invoice",
  "faceValue": 10000.00,
  "presentValue": 9274.15,
  "currencyCode": "BRL",
  "paymentCurrency": "USD",
  "exchangeRateUsed": 5.2345,
  "dueDate": "2026-09-30",
  "settlementDate": "2026-06-26",
  "baseRate": 0.00875,
  "appliedSpread": 0.015,
  "status": "SETTLED",
  "createdAt": "2026-06-26T16:00:00",
  "externalReference": "INV-001"
}
Taxas de Câmbio
Método	Endpoint	Descrição
PUT	/api/v1/exchange-rates	Atualizar taxa de câmbio
GET	/api/v1/exchange-rates/current	Consultar taxa atual
Exemplo de Request (PUT):

json
{
  "fromCurrency": "USD",
  "toCurrency": "BRL",
  "rate": 5.50,
  "effectiveDate": "2026-06-26"
}
Relatórios
Método	Endpoint	Descrição
GET	/api/v1/reports/settlements	Relatório de liquidações
GET	/api/v1/reports/settlements/count	Contar transações
Parâmetros:

startDate: Data inicial (YYYY-MM-DD)

endDate: Data final (YYYY-MM-DD)

creditorId: ID do cedente

currencyCode: Código da moeda (BRL, USD, EUR)

status: Status da transação (SETTLED, PENDING, FAILED)

page: Número da página (default: 0)

size: Itens por página (default: 20)

Exemplo:

text
GET /api/v1/reports/settlements?startDate=2026-01-01&endDate=2026-12-31&creditorId=1&currencyCode=BRL&status=SETTLED&page=0&size=10
🗄️ Modelagem de Dados
Diagrama Entidade-Relacionamento
text
┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
│      currencies     │     │    exchange_rates   │     │   receivable_types  │
├─────────────────────┤     ├─────────────────────┤     ├─────────────────────┤
│ code (PK)           │◄────│ from_currency (FK)  │     │ id (PK)             │
│ name                │     │ to_currency (FK)    │     │ name                │
│ is_active           │     │ rate                │     │ spread              │
└─────────────────────┘     │ effective_date      │     │ is_active           │
        │                   │ created_at          │     └─────────────────────┘
        │                   └─────────────────────┘              │
        │                            │                          │
        │                            │                          │
        ▼                            ▼                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          transactions                                  │
├─────────────────────────────────────────────────────────────────────────┤
│ id (PK)                                                                │
│ creditor_id (FK) ──────────────────────────────────────────────────┐   │
│ receivable_type_id (FK) ──────────────────────────────────────────┐│   │
│ currency_code (FK) ──────────────────────────────────────────────┐││   │
│ face_value                                                        │││   │
│ present_value                                                     │││   │
│ due_date                                                          │││   │
│ settlement_date                                                   │││   │
│ base_rate                                                         │││   │
│ applied_spread                                                    │││   │
│ exchange_rate_used                                                │││   │
│ payment_currency                                                  │││   │
│ status (PENDING/SETTLED/FAILED)                                   │││   │
│ version (Optimistic Lock)                                         │││   │
│ created_at                                                        │││   │
│ updated_at                                                        │││   │
│ external_reference                                                │││   │
│ notes                                                             │││   │
└───────────────────────────────────────────────────────────────────┘││   │
                                                                    ││   │
┌─────────────────────┐                                            ││   │
│      creditors      │◄───────────────────────────────────────────┘│   │
├─────────────────────┤                                             │   │
│ id (PK)             │                                             │   │
│ name                │                                             │   │
│ document (UK)       │                                             │   │
│ email               │                                             │   │
│ phone               │                                             │   │
│ created_at          │                                             │   │
│ is_active           │                                             │   │
└─────────────────────┘                                             │   │
                                                                    │   │
┌───────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
Índices para Performance
sql
-- Índices para consultas frequentes
CREATE INDEX idx_transactions_creditor ON transactions(creditor_id);
CREATE INDEX idx_transactions_currency ON transactions(currency_code);
CREATE INDEX idx_transactions_settlement_date ON transactions(settlement_date);
CREATE INDEX idx_transactions_due_date ON transactions(due_date);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_external_reference ON transactions(external_reference);

-- Índice composto para relatórios
CREATE INDEX idx_transactions_report ON transactions(settlement_date, creditor_id, currency_code, status);
Scripts SQL (Flyway)
As migrações estão em src/main/resources/db/migration/:

V1__initial_schema.sql: Estrutura inicial do banco

V2__insert_initial_data.sql: Dados de exemplo (moedas, tipos, cedentes)

V3__add_indexes_for_performance.sql: Índices para otimização

🧪 Testes
Executar Testes
bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=SettlementControllerTest

# Testes de uma classe
mvn test -Dtest=org.example.srm.controller.*

# Cobertura de código
mvn test jacoco:report
Cobertura de Testes
✅ Controllers: Testes de integração com MockMvc

✅ Services: Testes unitários com Mockito

✅ Strategies: Testes de lógica de precificação

✅ Repositories: Testes com TestContainers

✅ Integração: Testes de fluxo completo

Relatório de Cobertura
text
target/site/jacoco/index.html
Exemplo de Teste Unitário
java
@Test
void shouldCalculatePresentValue() {
    BigDecimal faceValue = BigDecimal.valueOf(10000);
    BigDecimal baseRate = BigDecimal.valueOf(0.00875);
    int months = 3;
    
    PricingStrategy strategy = new CommercialInvoiceStrategy();
    BigDecimal result = strategy.calculatePresentValue(faceValue, baseRate, months);
    
    assertEquals(new BigDecimal("9274.15"), result);
}
📊 Observabilidade
Logs Estruturados
Formato JSON com Logstash

Correlation ID para rastreamento distribuído

Níveis configuráveis (DEBUG, INFO, WARN, ERROR)

Inclui contexto da requisição

Métricas (Prometheus)
Métrica	Descrição	Tipo
srm.transactions.settled.total	Total de transações liquidadas	Counter
srm.transactions.failed.total	Total de transações com falha	Counter
srm.transactions.settlement.duration	Tempo de liquidação	Timer
srm.transactions.by.currency	Transações por moeda	Counter
srm.transactions.by.status	Transações por status	Counter
srm.transactions.active	Transações ativas no momento	Gauge
srm.transactions.pending	Transações pendentes	Gauge
srm.cache.hit.ratio	Taxa de acerto do cache	Gauge
Grafana Dashboard
Acesse Grafana: http://localhost:3000

Login: admin / admin

Adicione datasource:

Tipo: Prometheus

URL: http://prometheus:9090

Importe o dashboard:

Dashboard ID: 1860 (Spring Boot Statistics)

Ou crie um dashboard customizado

Endpoints de Monitoramento
text
/actuator/health      - Health check
/actuator/info        - Informações da aplicação
/actuator/metrics     - Todas as métricas
/actuator/prometheus  - Métricas no formato Prometheus
/actuator/env         - Variáveis de ambiente
/actuator/loggers     - Configuração de logs
🔄 CI/CD Pipeline
GitHub Actions
📁 .github/workflows/ci-cd-pipeline.yml

yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop, feature/* ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Cache Maven dependencies
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-m2
      
      - name: Run tests
        run: mvn clean test
      
      - name: Generate coverage report
        run: mvn jacoco:report
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./target/site/jacoco/jacoco.xml
          fail_ci_if_error: false
      
      - name: Build with Maven
        run: mvn clean package -DskipTests
      
      - name: Build Docker image
        run: |
          docker build -t srm-credit-engine:${{ github.sha }} .
          docker tag srm-credit-engine:${{ github.sha }} srm-credit-engine:latest
      
      - name: Run container tests
        run: |
          docker run -d -p 8080:8080 --name app-test srm-credit-engine:latest
          sleep 30
          curl -f http://localhost:8080/actuator/health || exit 1
      
      - name: Log in to Docker Hub
        if: github.ref == 'refs/heads/main'
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_TOKEN }}
      
      - name: Push to Docker Hub
        if: github.ref == 'refs/heads/main'
        run: |
          docker tag srm-credit-engine:${{ github.sha }} ${{ secrets.DOCKER_USERNAME }}/srm-credit-engine:latest
          docker push ${{ secrets.DOCKER_USERNAME }}/srm-credit-engine:latest
      
      - name: Deploy to production
        if: github.ref == 'refs/heads/main'
        run: |
          echo "Deploying to production..."
          # Adicione aqui seus comandos de deploy (AWS, GCP, Azure, etc.)
📐 Decisões Arquiteturais (ADRs)
ADR-001: Escolha do Banco de Dados
Contexto: Precisamos de um banco que suporte transações ACID e consultas analíticas.

Decisão: Adotar PostgreSQL como banco principal e H2 para testes.

Consequências:

✅ ACID completo

✅ Suporte a índices avançados

✅ JSONB para logs e dados não estruturados

❌ Mais complexo que NoSQL

❌ Necessita de gerenciamento de conexões

ADR-002: Monólito vs Microserviços
Contexto: Time pequeno, necessidade de entrega rápida e consistência transacional.

Decisão: Monólito modular com camadas bem definidas.

Consequências:

✅ Simplicidade inicial

✅ Transações ACID garantidas

✅ Facilidade de testes e debugging

❌ Escalabilidade limitada horizontalmente

❌ Acoplamento maior entre módulos

ADR-003: Estratégia de Cache
Contexto: Taxas de câmbio e base rates são consultadas frequentemente.

Decisão: Redis com TTL configurável e invalidação em updates.

Consequências:

✅ Redução de latência (5-10ms vs 100ms)

✅ Menos carga no banco de dados

❌ Complexidade adicional

❌ Possibilidade de stale data (aceitável para este caso)

ADR-004: Strategy Pattern para Precificação
Contexto: Diferentes tipos de recebíveis têm regras de cálculo diferentes.

Decisão: Implementar Strategy Pattern com Factory.

Consequências:

✅ Fácil adicionar novos tipos

✅ Código desacoplado e testável

✅ Open/Closed Principle seguido

❌ Overhead de classes adicionais

❌ Complexidade de configuração

🚀 Design de Alta Escala (1M Transações/Minuto)
Arquitetura Proposta
text
                    ┌─────────────────────────────────────┐
                    │      Load Balancer (HAProxy)        │
                    │      - Round Robin                  │
                    │      - Health Checks                │
                    │      - Rate Limiting                │
                    └──────────────┬──────────────────────┘
                                   │
                ┌──────────────────┼──────────────────┐
                ▼                  ▼                  ▼
         ┌───────────┐     ┌───────────┐     ┌───────────┐
         │  App 1    │     │  App 2    │     │  App 3    │
         │ (Spring)  │     │ (Spring)  │     │ (Spring)  │
         │ 4 vCPUs   │     │ 4 vCPUs   │     │ 4 vCPUs   │
         │ 8GB RAM   │     │ 8GB RAM   │     │ 8GB RAM   │
         └─────┬─────┘     └─────┬─────┘     └─────┬─────┘
               │                 │                 │
               └─────────────────┼─────────────────┘
                                 │
                        ┌────────┴────────┐
                        │  Redis Cluster  │
                        │  (6 nós - HA)   │
                        │  90% Cache Hit  │
                        └────────┬────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              ▼                  ▼                  ▼
       ┌───────────┐     ┌───────────┐     ┌───────────┐
       │ Postgres  │     │ Postgres  │     │ Postgres  │
       │ (Primary) │────▶│ (Replica1)│────▶│ (Replica2)│
       │  16 vCPUs │     │  8 vCPUs  │     │  8 vCPUs  │
       │  32GB RAM │     │  16GB RAM │     │  16GB RAM │
       └───────────┘     └───────────┘     └───────────┘
                                 │
                          ┌──────┴──────┐
                          │  Kafka      │
                          │  (Event Bus)│
                          └─────────────┘
Estratégias de Escalabilidade
1. Cache Distribuído (Redis)
Taxas de câmbio e base rates

TTL: 5 minutos

90% de cache hit rate esperado

Cluster com 6 nós para alta disponibilidade

2. Sharding (PostgreSQL)
Shard por creditor_id

16 shards iniciais

Read replicas para relatórios

Connection pooling com HikariCP (50 conexões/instância)

3. Event-Driven (Kafka)
Eventos: TransactionCreated, RateUpdated

Desacopla processamento assíncrono

Relatórios eventualmente consistentes

3 particionamentos por tópico

4. Circuit Breaker (Resilience4j)
Timeout: 5 segundos

Retry: 3 tentativas

Fallback para dados em cache

Half-open state para recuperação

5. Rate Limiting
1000 req/seg por instância

Token bucket algorithm

Redis para rate limiting distribuído

🔧 Git Workflow
Estratégia de Branching: GitHub Flow
text
feature/calculadora-spread ─────┐
feature/gestao-cambio ──────────┤
feature/report-analitico ───────┤
feature/tests-unitarios ────────┤
                                ▼
                         ┌──────────┐
                         │ develop  │
                         └────┬─────┘
                              │
                              ▼
                         ┌──────────┐
                         │  main    │ ←──── hotfix/urgent-fix
                         └──────────┘
Conventional Commits
bash
feat: add currency strategy
fix: calculation rounding issue
docs: update API documentation
test: add unit tests for settlement
refactor: extract pricing strategy
perf: improve report query performance
Git Hooks (Husky + Pre-commit)
bash
# Antes do commit
- Rodar linters
- Executar testes unitários
- Validar formato dos commits

# Antes do push
- Executar testes de integração
- Verificar cobertura de código
Tags Semânticas
bash
# Criar tag
git tag -a v1.0.0 -m "Release: First stable version"

# Enviar tags
git push --tags

# Versões
v1.0.0 - Primeira versão estável
v1.1.0 - Nova feature (câmbio automático)
v1.1.1 - Bug fix (arredondamento de valores)
Interactive Rebase
bash
# Reorganizar commits antes do merge
git rebase -i HEAD~5

# Comandos:
# pick - manter commit
# squash - combinar com anterior
# reword - editar mensagem
# drop - remover commit
Gestão de Crise (Hotfix)
bash
# 1. Identificar bug crítico na main
# 2. Criar hotfix branch
git checkout -b hotfix/urgent-fix main

# 3. Aplicar correção
git add .
git commit -m "fix: resolve currency conversion issue"

# 4. Merge na main com --no-ff
git checkout main
git merge --no-ff hotfix/urgent-fix

# 5. Cherry-pick para develop
git checkout develop
git cherry-pick <commit-hash>
📝 Como Contribuir
Fork o projeto

Crie uma branch para sua feature: git checkout -b feature/minha-feature

Commit suas mudanças: git commit -m 'feat: add minha feature'

Push para a branch: git push origin feature/minha-feature

Abra um Pull Request

Padrões de Código
Seguir princípios SOLID

Escrever testes unitários

Documentar com JavaDoc

Usar Conventional Commits

Manter cobertura de código > 80%

📄 Licença
Este projeto está sob a licença MIT - veja o arquivo LICENSE para detalhes.

👥 Equipe
Nome	Papel
Ederson Aguiar da Silva / Senior Developer
📞 Contato
Email: ederson.1999.es@hotmail.com

LinkedIn: linkedin.com/in/edersonaguiardasilva
o

© 2026 SRM Asset - Todos os direitos reservados
