package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Episodio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EpisodioRepository extends JpaRepository<Episodio, Long> {
    @Query("SELECT DISTINCT e FROM Episodio e JOIN e.serie s WHERE LOWER(s.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) AND e.dataLancamento >= :data")
    List<Episodio> findDistinctBySerieTituloContainingIgnoreCaseAndDataLancamentoGreaterThanEqual(
            @Param("titulo") String titulo, @Param("data") LocalDate data);


    List<Episodio> findBySerie_TituloContainingIgnoreCaseAndDataLancamentoGreaterThanEqual (String nomeSerie, LocalDate dataEpisodio);


    List<Episodio> findByTituloContainingIgnoreCase (String trecho);
}
