package br.com.alura.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;


public class ConsultaChatGPT {
    public static String obterTraducao(String texto) {
        // Retorna o próprio texto com um prefixo para simular a tradução
        return "[Tradução fake] " + texto;
    }
}
