package br.com.alura.screenmatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {

	@Query("SELECT s FROM Serie s")
	List<Serie> findAllSeries();

	Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

	List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, double avaliacao);

	List<Serie> findTop5ByOrderByAvaliacaoDesc();

	List<Serie> findByGenero(Categoria categoria);

	@Query("select s FROM Serie s WHERE :totalTemporadas >= totalTemporadas")
	List<Serie> seriesPorTemporada(int totalTemporadas);

	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoEp% ")
	List<Episodio> episodiosPorTrecho(String trechoEp);

	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
	List<Episodio> topEpsPorSerie(Serie serie);

	List<Serie> findTop5ByOrderByEpisodiosDataLancamentoDesc();

	@Query("SELECT s FROM Serie s " + "JOIN s.episodios e " + "GROUP BY s "
			+ "ORDER BY MAX(e.dataLancamento) DESC LIMIT 5")
	List<Serie> lancamentosMaisRecentes();

	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :numero")
	List<Episodio> obterEpisodiosPorTemporada(Long id, Long numero);
}
