package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.service.traducao.ConsultaMyMemory;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 500, unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria genero;
    @Column(columnDefinition = "TEXT")
    private String atores;
    @Column(name = "link_poster")
    private String poster;
    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();

    public Serie() {}

    public Serie(DadosSerie dadosSerie){
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        String avaliacaoStr = dadosSerie.avaliacao();

        double avaliacao = 0;

        if (avaliacaoStr != null && !avaliacaoStr.equalsIgnoreCase("N/A") && !avaliacaoStr.isBlank()) {
            try {
                avaliacao = Double.parseDouble(avaliacaoStr);
            } catch (NumberFormatException e) {
                // pode logar aqui se quiser
            }
        }

        this.avaliacao = avaliacao;

        String generoRaw = dadosSerie.genero();

        if (generoRaw == null || generoRaw.isEmpty()) {
            this.genero = Categoria.DESCONHECIDO;
        } else {
            this.genero = Categoria.fromString(generoRaw.split(",")[0].trim());
        }





        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        String textoBruto = dadosSerie.sinopse();
        if (textoBruto != null && textoBruto.startsWith("valorEsperado")) {
            // lógica aqui
        } else {
            // tratar caso nulo ou diferente
        }

        this.sinopse = ConsultaMyMemory.obterTraducao(textoBruto).trim();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e -> e.setSerie(this));
        this.episodios = episodios;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    @Override
    public String toString() {
        return
                "genero=" + genero +
                        ", titulo='" + titulo + '\'' +
                        ", totalTemporadas=" + totalTemporadas +
                        ", avaliacao=" + avaliacao +
                        ", atores='" + atores + '\'' +
                        ", poster='" + poster + '\'' +
                        ", sinopse='" + sinopse + '\'' +
                        ", episodios='" + episodios + '\'';
    }
}
