package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao,
						 @JsonAlias("Genre") String genero,
						 @JsonAlias("Actors") String atores,
						 @JsonAlias("Poster") String poster,
						 @JsonAlias("Plot") String sinopse){

	@Override
	public String toString() {
		return "Titulo -> " + titulo + "\nTemporadas -> " + totalTemporadas + "\navaliacao -> " + avaliacao
				+ "\ngenero -> " + genero + "\natores -> " + atores + "\nposter -> " + poster + "\nsinopse -> " + sinopse + "\n\n";
	}
}

