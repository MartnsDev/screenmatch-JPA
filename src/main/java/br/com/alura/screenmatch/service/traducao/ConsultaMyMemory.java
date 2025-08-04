package br.com.alura.screenmatch.service.traducao;

import br.com.alura.screenmatch.service.ConsumoApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ConsultaMyMemory {
    public static String obterTraducao(String text) {
        if (text == null || text.isEmpty()) {
            return "Tradução indisponível";
        }
        ObjectMapper mapper = new ObjectMapper();
        ConsumoApi consumo = new ConsumoApi();

        try {
            String texto = URLEncoder.encode(text, "UTF-8");
            String langpair = URLEncoder.encode("en|pt-br", "UTF-8");

            String url = "https://api.mymemory.translated.net/get?q=" + texto + "&langpair=" + langpair;

            String json = consumo.obterDados(url);

            DadosTraducao traducao = mapper.readValue(json, DadosTraducao.class);

            return traducao.dadosResposta().textoTraduzido();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Erro ao codificar a URL", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar JSON da resposta", e);
        }
    }
}
