package com.example.sistemarh.financeiro.service;

import com.example.sistemarh.financeiro.model.FolhaPagamento;
import com.example.sistemarh.financeiro.model.Funcionario;
import com.example.sistemarh.financeiro.model.RegraSalario;
import com.example.sistemarh.financeiro.repository.FuncionarioRepository;
import com.example.sistemarh.financeiro.repository.RegraSalarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate; // ADICIONE ESTE IMPORT
import java.util.List;
import java.util.concurrent.atomic.AtomicLong; // ADICIONE ESTE IMPORT (para gerar ID da folha)


@Service
public class FinanceiroService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private RegraSalarioRepository regraSalarioRepository;

    // Usado para gerar um ID único para cada folha processada
    private static final AtomicLong contadorFolhaId = new AtomicLong(System.currentTimeMillis());


    /**
     * Processa a folha de pagamento para um determinado mês e ano.
     */
    public FolhaPagamento processarFolha(int mes, int ano) {

        // 1. Carregar as regras salariais
        RegraSalario regras = regraSalarioRepository.carregar();
        if (regras == null) {
            System.err.println("ERRO FATAL: Regras salariais não encontradas. Impossível processar folha.");
            // Em um app real, lançaríamos uma exceção customizada aqui
            throw new RuntimeException("Regras salariais não configuradas.");
        }

        // 2. Buscar todos os funcionários
        List<Funcionario> funcionarios = funcionarioRepository.buscarTodos();

        // 3. Inicializar totais
        double totalBruto = 0;
        double totalDescontos = 0;
        double totalLiquido = 0;

        // 4. Iterar e calcular
        for (Funcionario func : funcionarios) {

            // Requisito: A folha de pagamento deve incluir apenas funcionários ativos
            if (!func.getStatus().equalsIgnoreCase("Ativo")) {
                continue; // Pula funcionário inativo
            }

            // Atribui a regra ao funcionário para o cálculo
            // (O método calcularSalario() no modelo Funcionario depende disso)
            func.setRegraSalario(regras);

            // Calcula o salário líquido individual
            double salarioLiquidoIndividual = func.calcularSalario();

            // Precisamos recalcular os componentes Bruto e Desconto aqui
            // para podermos somar nos totais da folha.
            double base = func.getBaseSalario();
            double beneficios = regras.getValorValeAlimentacao() + regras.getValorValeTransporte();

            double descontos = (base * regras.getPercentualINSS() / 100)
                    + (base * regras.getPercentualRRF() / 100)
                    + (regras.getValorValeTransporte() * regras.getPercentualDescVT() / 100);

            // 5. Acumular os totais
            totalBruto += (base + beneficios);
            totalDescontos += descontos;
            totalLiquido += salarioLiquidoIndividual;
        }

        // 6. Construir o objeto FolhaPagamento

        // NOTA: O seu Builder de FolhaPagamento
        // pede um 'Funcionario' no construtor.
        // Isso parece ser um erro de design, pois a Folha de Pagamento é um
        // agregado de TODOS os funcionários.
        // Vamos passar 'null' por enquanto.
        FolhaPagamento folhaProcessada = new FolhaPagamento.Builder(
                contadorFolhaId.incrementAndGet(), // Gera um ID único para esta folha
                mes,
                ano,
                LocalDate.now(),
                null // Passando null aqui, pois esta folha é o total, não de um único funcionário
        )
                .valorTotalBruto(totalBruto)
                .valorTotalDescontos(totalDescontos)
                .valorTotalLiquido(totalLiquido)
                .build();

        return folhaProcessada;
    }

    /**
     * Salva um novo funcionário.
     */
    public void salvarNovoFuncionario(Funcionario f) {
        funcionarioRepository.salvar(f);
    }

    /**
     * Salva as regras salariais.
     */
    public void salvarRegras(RegraSalario r) {
        regraSalarioRepository.salvar(r);
    }

    // --- Métodos da Parte 2 (já movidos) ---

    /**
     * Formata um objeto FolhaPagamento para exibição simples em tela/console.
     */
    public String formatarFolhaParaTela(FolhaPagamento folha) {
        return "Folha de Pagamento - " + folha.getMesReferencia() + "/" + folha.getAnoReferencia() + "\n" +
                "Data de Processamento: " + folha.getDataProcessamento() + "\n" +
                "Valor Total Bruto: R$ " + folha.getValorTotalBruto() + "\n" +
                "Total de Descontos: R$ " + folha.getValorTotalDescontos() + "\n" +
                "Valor Total Líquido: R$ " + folha.getValorTotalLiquido();
    }

    /**
     * Exporta os dados de uma FolhaPagamento para um arquivo CSV.
     */
    public File exportarFolhaParaCSV(FolhaPagamento folha) {

        String nomeArquivo = "relatorio_folha_" + folha.getMesReferencia() + "_" + folha.getAnoReferencia() + ".csv";
        File arquivo = new File(nomeArquivo);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write("ID;Mês Referência;Ano Referência;Data Processamento;Valor Bruto;Valor Descontos;Valor Líquido");
            writer.newLine();

            writer.write(
                    folha.getId() + ";" +
                            folha.getMesReferencia() + ";" +
                            folha.getAnoReferencia() + ";" +
                            folha.getDataProcessamento() + ";" +
                            folha.getValorTotalBruto() + ";" +
                            folha.getValorTotalDescontos() + ";" +
                            folha.getValorTotalLiquido()
            );
            writer.newLine();

            System.out.println("Relatório CSV exportado com sucesso: " + arquivo.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao exportar relatório: " + e.getMessage());
        }

        return arquivo;
    }
}


/*package com.example.sistemarh.financeiro.service;

import com.example.sistemarh.financeiro.model.FolhaPagamento;
import com.example.sistemarh.financeiro.model.Funcionario;
import com.example.sistemarh.financeiro.model.RegraSalario;
import com.example.sistemarh.financeiro.repository.FuncionarioRepository;
import com.example.sistemarh.financeiro.repository.RegraSalarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class FinanceiroService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private RegraSalarioRepository regraSalarioRepository;


    public FolhaPagamento processarFolha(int mes, int ano){

        funcionarioRepository.buscarTodos();
        FolhaPagamento folha = null;

        return folha;
           List<String> funcionarios = listarFuncionarios();
            double totalBruto = 0;
            double totalDescontos = 0;
            double totalLiquido = 0;

            for (String linha : funcionarios) {
                String[] dados = linha.split(";");
                if (dados.length < 5) continue;

                String status = dados[4];
                if (!status.equalsIgnoreCase("Ativo")) continue;

                double baseSalario = Double.parseDouble(dados[3]);
                totalBruto += baseSalario;
                totalDescontos += baseSalario * 0.10; // Adicionar regras de calculo aqui
                totalLiquido += baseSalario * 0.90;
            }

            this.valorTotalBruto = totalBruto;
            this.valorTotalDescontos = totalDescontos;
            this.valorTotalLiquido = totalLiquido;

    }

    public void salvarNovoFuncionario(Funcionario f){
        funcionarioRepository.salvar(f);
    }

    public void salvarRegras(RegraSalario r){
        regraSalarioRepository.salvar(r);
    }

    public String exportarRelatórioTela() {
        return "Folha de Pagamento - " + mesReferencia + "/" + anoReferencia + "\n" +
                "Funcionários processados: " + listarFuncionarios().size() + "\n" +
                "Valor Total Bruto: R$ " + valorTotalBruto + "\n" +
                "Total de Descontos: R$ " + valorTotalDescontos + "\n" +
                "Valor Total Líquido: R$ " + valorTotalLiquido;
        //adicionar uma tabela com todos os funcionarios, com nome e valores
    }

    public File exportarRelatorioArquivo() {

        File arquivo = new File("relatorio_folha_" + mesReferencia + "_" + anoReferencia + ".csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write("ID;Mês Referência;Ano Referência;Data Processamento;Valor Bruto;Valor Descontos;Valor Líquido");
            writer.newLine();

            // Linha de dados
            writer.write(
                    id + ";" +
                            mesReferencia + ";" +
                            anoReferencia + ";" +
                            dataProcessamento + ";" +
                            valorTotalBruto + ";" +
                            valorTotalDescontos + ";" +
                            valorTotalLiquido
            );
            writer.newLine();

            System.out.println("Relatório CSV exportado com sucesso: " + arquivo.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao exportar relatório: " + e.getMessage());
        }

        return arquivo;
    }

}
*/