package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.EpisodioRepository;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import java.util.stream.Collectors;

@Component
public class Principal {


    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=5c4d8354";
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;
    private EpisodioRepository episodioRepository;
    private List<Serie> series = new ArrayList<>();

    public Principal (SerieRepository repositorio, EpisodioRepository episodioRepository) {
        this.repositorio = repositorio;
        this.episodioRepository = episodioRepository;
    }

    public void exibeMenu ( ) {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                   1 - Buscar séries
                   2 - Buscar episódios
                   3 - Listar séries buscadas
                   4 - Buscar série por título
                   5 - Buscar séries por ator
                   6 - Top 5 Séries
                   7 - Buscar séries por categoria
                   8 - Filtrar séries
                   9 - Buscar episódios por trecho
                   10 - Top 5 episódios por série
                   11 - Buscar episódios a partir de uma data
                   
                   0 - Sair""";

            try {
                System.out.println("\n" + menu);
                opcao = leitura.nextInt();
                leitura.nextLine();

                switch (opcao) {
                    case 1 -> buscarSerieWeb();
                    case 2 -> buscarEpisodioPorSerie();
                    case 3 -> listarSeriesBuscadas();
                    case 4 -> buscarSeriePorTitulo();
                    case 5 -> buscarSeriePorAtor();
                    case 6 -> topCincoSeries();
                    case 7 -> buscarSeriesPorCategoria();
                    case 8 -> filtrarSeries();
                    case 9 -> buscarEpisodioPorTrecho();
                    case 10 -> top5EpisodiosPorSeries();
                    case 11 -> buscarEpisodiosDepoisDeUmaData();

                    case 0 -> System.out.println("Saindo...");
                    default -> System.out.println("Opção inválida");
                }
            } catch (InputMismatchException i) {
                System.out.println("⚠️ Entrada inválida. Por favor, digite um número.");
                leitura.nextLine();
            }
        }

    }



    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        String titulo = dados.titulo();

        Optional<Serie> serieExistente = repositorio.findByTituloContainingIgnoreCase(titulo);

        if (serieExistente.isPresent()) {
            Serie serie = serieExistente.get();
            serie.setPoster(dados.poster());
            repositorio.save(serie);
            System.out.println("Série já existente. Link do poster atualizado.");
        } else {
            // Série nova, salvar normalmente
            Serie serie = new Serie(dados);
            repositorio.save(serie);
            System.out.println("Série salva com sucesso.");
            System.out.println(serie);
        }
    }



    private DadosSerie getDadosSerie ( ) {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie ( ) {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas ( ) {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo ( ) {
        System.out.println("Escolha uma Série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if (serieBuscada.isPresent()) {
            System.out.println("Dados da série: " + serieBuscada.get());
        } else {
            System.out.println("Série não Encontrada");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.print("Digite o nome do ator (ou parte do nome): ");
        var nomeAutor = leitura.nextLine();

        List<Serie> seriesEncontradas = repositorio.buscarSeriesPorAtores(nomeAutor);

        if (seriesEncontradas.isEmpty()) {
            System.out.println("Nenhuma série encontrada com o ator: " + nomeAutor);
        } else {
            System.out.println("Séries em que " + nomeAutor + " trabalhou:");
            seriesEncontradas.forEach(s ->
                    System.out.println("- " + s.getTitulo() + " | Avaliação: " + s.getAvaliacao())
            );
        }
    }

    private void topCincoSeries ( ) {
        System.out.println("Top 5 Séries: ");
        List<Serie> series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparingDouble(Serie::getAvaliacao).reversed())
                .limit(5)
                .forEach(s -> System.out.println(s.getTitulo() + " Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria() {
        try {
            System.out.println("Deseja buscar Série de que categoria/genero: ");
            String nomeGenero = leitura.nextLine().toLowerCase().toUpperCase();
            Categoria categoria = Categoria.fromPortugues(nomeGenero);
            List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);

            System.out.println("Séries da categoria: " + nomeGenero);
            seriesPorCategoria.forEach(System.out::println);
        } catch (IllegalArgumentException i) {
            System.out.println(i.getMessage() + " - " + i.getCause());
        }
    }

    //case 8 - a fazer
    private void filtrarSeries() {
        System.out.println("Deseja assitir Séries com até quantas temporadas? ");
        int temporadasMaxima = leitura.nextInt();
        System.out.println("Avaliação minima: ");
        double avaliacao = leitura.nextInt();

        List<Serie> seriesFiltradas = repositorio.seriesPorTemporadaEAvaliacao(temporadasMaxima, avaliacao);

        seriesFiltradas.forEach(s -> System.out.println("Série: " + s.getTitulo() + " | Avaliação: " + s.getAvaliacao() + " | Temporadas: " + s.getTotalTemporadas()));
    }

    private void buscarEpisodioPorTrecho() {

        try {
            System.out.println("Digite o trecho do título do episódio: ");
            String trecho = leitura.nextLine();

            List<Episodio> episodiosEncontrados = episodioRepository
                    .findByTituloContainingIgnoreCase(trecho)
                    .stream()
                    .distinct()
                    .toList();

            if (episodiosEncontrados.isEmpty()) {
                System.out.println("Nenhum episódio encontrado com esse trecho.");
            } else {
                episodiosEncontrados.forEach(ep -> System.out.println(
                        "Série: " + ep.getSerie().getTitulo() + "\n" +
                                "Temporada: " + ep.getTemporada() + "\n" +
                                "Título do episódio: " + ep.getTitulo() + "\n" +
                                "Data de lançamento: " + ep.getDataLancamento() + "\n" +
                                "------------------------"
                ));
            }
        } catch (DataIntegrityViolationException e) {
            System.out.println("Erro: essa série já existe no banco de dados!");
        }
    }



    private void top5EpisodiosPorSeries() {
        System.out.print("Digite o nome da série: ");
        String nomeSerie = leitura.nextLine().trim();

        Optional<Serie> serieOptional = repositorio.findFirstByTituloContainingIgnoreCase(nomeSerie);

        if (serieOptional.isEmpty()) {
            System.out.println("Nenhuma série encontrada com esse nome.");
            return;
        }

        Serie serieEscolhida = serieOptional.get();

        System.out.println("\n Série encontrada: " + serieEscolhida.getTitulo());
        System.out.println("Buscando os 5 episódios mais bem avaliados...\n");

        Serie serie = serieEscolhida;
        List<Episodio> episodios = serie.getEpisodios().stream()
                .sorted(Comparator.comparing(Episodio::getAvaliacao).reversed())
                .limit(10)
                .toList();

        System.out.println("\nTop 5 episódios da série: " + serie.getTitulo());
        episodios.forEach(e ->
                System.out.println("Título: " + e.getTitulo() + " | Avaliação: " + e.getAvaliacao() + " | Episodio: " + e.getNumeroEpisodio() + " | Temporada: " + e.getTemporada() ));
    }


    private void buscarEpisodiosDepoisDeUmaData() {
        System.out.println("Digite o nome da série:");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            System.out.println("Serie Encontrada: " + serie.getTitulo());
            System.out.println("Digite o ano limite de lançamento:");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        } else {
            System.out.println("Série não encontrada.");
        }
    }


}




