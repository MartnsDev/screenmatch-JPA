package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime"),
    AVENTURA("Adventure", "Aventura"),
    ANIMACAO("Animation", "Animação"),
    DOCUMENTARIO("Documentary", "Documentário"),
    REALITY_TV("Reality-TV", "Reality TV"),
    CIENCIA_FICCION("Science Fiction", "Ficção Científica"),
    FANTASIA("Fantasy", "Fantasia"),
    SUSPENSE("Thriller", "Suspense"),
    TERROR("Horror", "Terror"),
    DESCONHECIDO("Unknown", "Desconhecido");
    private String categoriaOmdb;
    private String categoriaPortugues;

    Categoria(String categoriaOmdb, String categoriaPortugues) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }

    public String getCategoriaOmdb() {
        return categoriaOmdb;
    }

    public String getCategoriaPortugues() {
        return categoriaPortugues;
    }

    /**
     * Retorna o enum Categoria com base no texto em português.
     * Ignora maiúsculas/minúsculas.
     * Lança IllegalArgumentException se não encontrar correspondência.
     */
    public static Categoria fromPortugues(String text) {
        String t = text.trim().toLowerCase();

        for (Categoria categoria : Categoria.values()) {
            if (categoria.name().toLowerCase().equals(t)) {
                return categoria;
            }
            if (categoria.categoriaPortugues.toLowerCase().equals(t)) {
                return categoria;
            }
            if (categoria == ACAO && (t.equals("acao") || t.equals("açao") || t.equals("acão"))) return ACAO;
            if (categoria == COMEDIA && (t.equals("comedia") || t.equals("comédia") || t.equals("comedi"))) return COMEDIA;
            if (categoria == ANIMACAO && (t.equals("animacao") || t.equals("animação") || t.equals("animacão"))) return ANIMACAO;
            if (categoria == DOCUMENTARIO && (t.equals("documentario") || t.equals("documentário") || t.equals("documetario"))) return DOCUMENTARIO;
            if (categoria == CIENCIA_FICCION && (t.equals("ciencia ficcao") || t.equals("ciencia ficção") || t.equals("ciência ficção"))) return CIENCIA_FICCION;
            if (categoria == ROMANCE && (t.equals("romance") || t.equals("romançe") || t.equals("romanse"))) return ROMANCE;
            if (categoria == DRAMA && (t.equals("drama") || t.equals("drâmma") || t.equals("dram"))) return DRAMA;
            if (categoria == CRIME && (t.equals("crime") || t.equals("críme") || t.equals("crim"))) return CRIME;
            if (categoria == AVENTURA && (t.equals("aventura") || t.equals("aventúra") || t.equals("aventua"))) return AVENTURA;
            if (categoria == REALITY_TV && (t.equals("reality tv") || t.equals("reality-tv") || t.equals("realitytv"))) return REALITY_TV;
            if (categoria == FANTASIA && (t.equals("fantasia") || t.equals("fantásia") || t.equals("fantasya"))) return FANTASIA;
            if (categoria == SUSPENSE && (t.equals("suspense") || t.equals("suspensee") || t.equals("suspens"))) return SUSPENSE;
            if (categoria == TERROR && (t.equals("terror") || t.equals("teror") || t.equals("terorr"))) return TERROR;
            if (categoria == DESCONHECIDO && (t.equals("desconhecido") || t.equals("desconhecida") || t.equals("unknown"))) return DESCONHECIDO;
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
    public static Categoria fromNomeEnum(String nome) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.name().equalsIgnoreCase(nome.trim())) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + nome);
    }
    public static Categoria fromString(String texto) {
        String textoTrimmed = texto.trim();
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPortugues.equalsIgnoreCase(textoTrimmed) ||
                    categoria.categoriaOmdb.equalsIgnoreCase(textoTrimmed)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + texto);
    }

    public static Categoria encontrarCategoriaMaisProxima(String texto) {
        Categoria melhorCategoria = null;
        int menorDistancia = Integer.MAX_VALUE;

        for (Categoria categoria : Categoria.values()) {
            int distancia = distanciaLevenshtein(texto, categoria.getCategoriaPortugues());
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                melhorCategoria = categoria;
            }
        }
        return melhorCategoria;
    }

    public static int distanciaLevenshtein(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    private int calcularDistanciaLevenshtein(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int custo = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1,     // remoção
                                    dp[i][j - 1] + 1),    // inserção
                            dp[i - 1][j - 1] + custo       // substituição
                    );
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }
}
