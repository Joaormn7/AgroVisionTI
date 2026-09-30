# AgroVisionTI API

Backend REST do AgroVisionTI, migrado do app desktop (Java Swing + JDBC puro)
para Spring Boot. Primeira etapa da modernização do projeto para uma
arquitetura web (Spring Boot + React).

## O que mudou em relação ao desktop

| Desktop (Swing) | API (Spring Boot) |
|---|---|
| `AtivoDAO`, `UsuarioDAO`, etc. com JDBC/`PreparedStatement` na mão | Spring Data JPA (`AtivoRepository`, ...) + Hibernate |
| Lógica de negócio dentro das telas (`PainelAtivos`, ...) | Camada de `service/` isolada da apresentação |
| Login com `JOptionPane` e `UsuarioDAO.autenticar` | `POST /api/auth/login` retornando um JWT |
| Senha em SHA-256 + salt manual (`SenhaUtil`) | BCrypt, com **upgrade automático** do hash legado no primeiro login (ver `SenhaService`/`AuthService`) |
| Uma aplicação única, rodando na máquina do usuário | API stateless, pronta para um front-end React (ou qualquer outro client HTTP) consumir |

O banco de dados **é o mesmo** (`agrovisionti`, MySQL) — não precisa migrar
dados, só apontar a API para ele. As tabelas usadas por Ativo, Unidade,
Colaborador, Usuário e Movimentação continuam as mesmas; a API só troca a
forma de acessá-las.

## Rodando localmente

Pré-requisitos: Java 21+, Maven, e o mesmo MySQL que o app desktop já usa
(banco `agrovisionti` rodando em `localhost:3306`).

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. Documentação interativa (Swagger UI)
em `http://localhost:8080/docs`.

Se as credenciais do banco forem diferentes das que estão em
`src/main/resources/application.yml`, ajuste `spring.datasource.username` /
`password` (ou passe como variável de ambiente `SPRING_DATASOURCE_USERNAME` /
`SPRING_DATASOURCE_PASSWORD`).

## Testando o login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seu-usuario-admin@exemplo.com","senha":"sua-senha-atual"}'
```

Use qualquer usuário que já existe na tabela `usuarios` — a senha atual
(hash legado SHA-256+salt) continua funcionando normalmente; no primeiro
login bem-sucedido ela é silenciosamente re-gravada em BCrypt.

A resposta traz um `token` JWT. Use-o nas próximas chamadas:

```bash
curl http://localhost:8080/api/ativos \
  -H "Authorization: Bearer <token>"
```

## Estrutura

```
entity/       -> mapeamento JPA das tabelas existentes
repository/   -> Spring Data JPA (substitui os DAOs)
service/      -> regras de negócio (equivalente ao que hoje mora nas telas)
security/     -> JWT + verificação de senha (legado e BCrypt)
controller/   -> endpoints REST
dto/          -> objetos de entrada/saída da API (nunca expõe a entity direto)
exception/    -> tratamento global de erros (nunca devolve um 500 cru)
```

## Próximos passos (roadmap da migração)

1. ~~Backend Spring Boot consumindo o banco existente~~ ✅ este projeto
2. Front-end React consumindo esta API (substituindo as telas Swing)
3. Docker Compose (API + front + MySQL) subindo tudo com um comando
4. Deploy de uma versão pública (Render/Railway/Fly.io) para colocar o link
   no portfólio
