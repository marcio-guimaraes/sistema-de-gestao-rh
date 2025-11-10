package com.example.sistemarh.financeiro.service;

import com.example.sistemarh.financeiro.model.FolhaPagamento;
import com.example.sistemarh.financeiro.model.Funcionario;
import com.example.sistemarh.financeiro.model.RegraSalario;
import com.example.sistemarh.financeiro.repository.FuncionarioRepository;
import com.example.sistemarh.financeiro.repository.RegraSalarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
           /* List<String> funcionarios = listarFuncionarios();
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
            this.valorTotalLiquido = totalLiquido; */

    }

    public void salvarNovoFuncionario(Funcionario f){
        funcionarioRepository.salvar(f);
    }

    public void salvarRegras(RegraSalario r){
        regraSalarioRepository.salvar(r);
    }

}
