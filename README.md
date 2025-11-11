# Sistema de Gestão de RH (SistemaRH)

Projeto acadêmico de um Sistema de Gestão de Recursos Humanos (RH) desenvolvido em Spring Boot e Java. O sistema gerencia o ciclo de vida completo do funcionário, desde a candidatura até a folha de pagamento, utilizando arquivos de texto (`.txt`, `.csv`) para persistência de dados (conforme requisito do projeto).

## 🚀 Funcionalidades

O sistema é dividido em 4 módulos principais:

* **Módulo de Administração:** Gerenciamento de usuários (Admin, Gestor, Recrutador), criação e edição de contas.
* **Módulo de Cadastro:** Cadastro e gerenciamento de Candidatos, e a ação de aplicar um candidato a uma vaga.
* **Módulo de Recrutamento:** Criação e gestão de Vagas, agendamento de entrevistas, e o fluxo de aprovação de candidatos (Recrutador -> Gestor).
* **Módulo Financeiro:** Admissão de funcionários (convertendo candidatos aprovados), geração de relatórios de folha de pagamento e consulta de contracheques individuais.

## 💻 Tecnologias Utilizadas

* **Backend:** Java 21, Spring Boot (Spring Web, Spring MVC)
* **Frontend:** Thymeleaf, HTML5, Bootstrap 5
* **Build:** Apache Maven
* **Persistência:** Arquivos de texto (`.txt` e `.csv`).

## ⚡ Como Executar

1.  **Pré-requisitos:** É necessário ter o Java JDK 21 (ou superior) instalado.

2.  **Executar pela IDE (Recomendado):**
    * Abra o projeto na sua IDE (IntelliJ, Eclipse, etc.).
    * Localize a classe `SistemarhApplication.java`.
    * Execute o método `main()` para iniciar o servidor.

3.  **Executar pelo Terminal:**
    * Navegue até a pasta raiz do submódulo `sistemarh`:
        ```bash
        cd sistema-de-gestao-rh-projeto-funcional/sistemarh
        ```
    * Execute o Maven Wrapper:
        * No Linux/Mac: `./mvnw spring-boot:run`
        * No Windows: `mvnw.cmd spring-boot:run`

4.  **Acessar:**
    * Abra seu navegador e acesse: `http://localhost:8080`

## 📖 Guia de Uso e Credenciais

O sistema utiliza arquivos de texto que são criados na raiz da pasta `/sistemarh` (onde o comando de execução é disparado).

### Credenciais de Administrador

Ao iniciar a aplicação pela primeira vez, o arquivo `usuarios.txt` é criado automaticamente com um usuário administrador padrão:

* **Usuário:** `admin`
* **Senha:** `admin123`

[Fonte: `UsuarioRepository.java`]

### Fluxo de Uso Recomendado (End-to-End)

Para testar todas as funcionalidades na ordem correta, siga este fluxo:

1.  **Login como Admin:**
    * Acesse: `http://localhost:8080/Administração/Login`
    * Use as credenciais `admin` / `admin123`.

2.  **Criar Usuários (Admin):**
    * Vá para: `http://localhost:8080/Administração/Gestão`
    * Crie dois novos usuários:
        1.  Um com **Perfil: Gestor** (ex: `gestor` / `senha12345`).
        2.  Um com **Perfil: Recrutador** (ex: `recrutador` / `senha12345`).

3.  **Criar Vaga (Gestor/Admin):**
    * Vá para: `http://localhost:8080/recrutamento/gestao-vagas`
    * Crie uma nova vaga (ex: "Desenvolvedor Java", Status "Aberta").

4.  **Cadastrar Candidato (RH/Admin):**
    * Vá para: `http://localhost:8080/cadastro/gestao-candidatos`
    * Clique em "+ Novo Candidato" e cadastre uma pessoa (ex: "Candidato Teste", CPF "123.456.789-00").

5.  **Realizar Candidatura (RH/Admin):**
    * Vá para: `http://localhost:8080/cadastro/candidatura`
    * Associe o "Candidato Teste" à "Vaga Dev Java". O status inicial será "Pendente".

6.  **Agendar Entrevista (Recrutador):**
    * Vá para: `http://localhost:8080/recrutamento/marcar-entrevista`
    * Selecione a candidatura "Candidato Teste - Dev Java" e agende a entrevista. O status da candidatura mudará para "Em Análise".

7.  **Avaliar Candidato (Recrutador):**
    * Vá para: `http://localhost:8080/recrutamento/avaliar-candidatos`
    * Localize o "Candidato Teste" e clique em "Aprovar". O status mudará para "Aprovado".

8.  **Solicitar Contratação (Recrutador):**
    * Vá para: `http://localhost:8080/recrutamento/solicitar-contratacao`
    * O "Candidato Teste" (aprovado) estará na lista. Clique em "Solicitar Contratação".

9.  **Aprovar Contratação (Gestor):**
    * Vá para: `http://localhost:8080/recrutamento/consultar-contratacoes`
    * Localize a solicitação e clique em "Aprovar". O status mudará para "Aprovada pelo Gestor".

10. **Admitir Funcionário (Financeiro):**
    * Vá para: `http://localhost:8080/financeiro/cadastrar-funcionario`
    * Selecione a contratação "Candidato Teste - Vaga: Dev Java" no dropdown.
    * Preencha as informações (Cargo: "Dev Pleno", Salário: 5000, etc.) e clique em "Efetivar Contratação".

11. **Verificar Resultado (Financeiro):**
    * Acesse: `http://localhost:8080/financeiro/relatorio` (O "Candidato Teste" deve estar na lista de funcionários).
    * Acesse: `http://localhost:8080/financeiro/contracheques` (Selecione o "Candidato Teste" para ver seu contracheque).
