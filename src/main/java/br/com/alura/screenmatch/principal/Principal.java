package br.com.alura.screenmatch.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {

	private Scanner leitura = new Scanner(System.in);
	private ConsumoApi consumo = new ConsumoApi();
	private ConverteDados conversor = new ConverteDados();
	private final String ENDERECO = "https://www.omdbapi.com/?t=";
	private final String API_KEY = "&apikey=6585022c";

	private SerieRepository repositorio;

	private List<Serie> series = new ArrayList<>();

	private Optional<Serie> serieBusca;

	public Principal(SerieRepository repositorio) {
		this.repositorio = repositorio;
	}


	public void exibeMenu() {

		var opcao = -1;
		while (opcao != 0) {
			var menu = """
					1 - Buscar séries
					2 - Buscar episódios
					3 - Listar series buscadas
					4 - Buscar serie por titulo
					5 - Buscar series por ator
					6 - Top5 Series
					7 - Buscar series por categoria
					8 - Buscar series pelo numero de temporadas
					9 - Buscar Ep por trecho

					0 - Sair
					""";

			System.out.println(menu);
			opcao = leitura.nextInt();
			leitura.nextLine();

			switch (opcao) {
			case 1:
				buscarSerieWeb();
				break;
			case 2:
				buscarEpisodioPorSerie();
				break;
			case 3:
				listarSeriesBuscadas();
				break;
			case 4:
				buscarSeriePorTitulo();
				break;
			case 5:
				buscarSeriePorAtor();
				break;
			case 6:
				buscarTopSeries();
				break;
			case 7:
				buscarSeriesPorCategoria();
				break;
			case 8:
				buscarPeloNumDeTemporadas();
				break;
			case 9:
				buscarEpPorTrecho();
				break;
			case 10:
				buscarTop5Eps();
				break;
			case 0:
				System.out.println("Saindo...");
				break;
			default:
				System.out.println("Opção inválida");
			}
		}
	}

	private void buscarTop5Eps() {
		buscarSeriePorTitulo();
		if (serieBusca.isPresent()) {
			Serie serie = serieBusca.get();
			List<Episodio> topEps = repositorio.topEpsPorSerie(serie);
			topEps.forEach(e -> System.out.printf("Serie:  %s  Temporada: %s Avaliacao: %s - Episodio: %s - %s\n",
					e.getSerie().getTitulo(), e.getTemporada(), e.getAvaliacao(), e.getNumeroEpisodio(),
					e.getTitulo()));
		}
	}

	private void buscarEpPorTrecho() {

		System.out.println("Qual o nome do ep para busca: ");
		String trechoEp = leitura.nextLine();
		List<Episodio> epsodioEncontrado = repositorio.episodiosPorTrecho(trechoEp);

		epsodioEncontrado.forEach(e -> System.out.printf("Serie:  %s  Temporada: %s  - Episodio: %s - %s\n",
				e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()));

	}

	private void buscarPeloNumDeTemporadas() {
		System.out.println("Buscar series com ate quantas temporadas?");
		int totalTemporadas = leitura.nextInt();
		leitura.nextLine();
		List<Serie> seriePorTemporada = repositorio.seriesPorTemporada(totalTemporadas);
		seriePorTemporada.forEach(System.out::println);
	}

	private void buscarSeriesPorCategoria() {
		System.out.println("Qual categoria voce deseja buscar? ");
		String nomeGenero = leitura.nextLine();
		Categoria categoria = Categoria.fromPortugues(nomeGenero);
		List<Serie> seriePorCategoria = repositorio.findByGenero(categoria);
		System.out.println("series da Categoria " + nomeGenero);
		seriePorCategoria.forEach(System.out::println);
	}

	private void buscarTopSeries() {
		List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
		serieTop.forEach(s -> System.out.println(s.getTitulo() + " avaliacao " + s.getAvaliacao()));

	}

	private void buscarSeriePorAtor() {
		System.out.println("Busque series pelo autor: ");
		String nomeAtor = leitura.nextLine();
		System.out.println("Avaliacoes a partir de qual nota: ");
		double avaliacao = leitura.nextDouble();
		List<Serie> seriesEncontradas = repositorio
				.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
		System.out.println("Series em que " + nomeAtor + " trabalhou");
		seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliacao:  " + s.getAvaliacao()));

	}

	private void buscarSeriePorTitulo() {
		System.out.println("Escolha uma Serie pelo nome: ");
		String nomeSerie = leitura.nextLine();
		serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

		if (serieBusca.isPresent()) {
			System.out.println("Dados da Serie: " + serieBusca.get());
		} else {
			System.out.println("Serie nao encontrada");
		}

	}

	private void listarSeriesBuscadas() {
		series = repositorio.findAll();
		series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);
	}

	private void buscarSerieWeb() {
		DadosSerie dados = getDadosSerie();
		Serie serie = new Serie(dados);
		repositorio.save(serie);
		System.out.println(dados);
	}

	private DadosSerie getDadosSerie() {
		System.out.println("Digite o nome da série para busca");
		var nomeSerie = leitura.nextLine();
		var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		return dados;
	}

	private void buscarEpisodioPorSerie() {
		listarSeriesBuscadas();
		System.out.println("Escolha uma Serie pelo nome: ");
		String nomeSerie = leitura.nextLine();

		Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

		if (serie.isPresent()) {
			var serieEncontrada = serie.get();
			List<DadosTemporada> temporadas = new ArrayList<>();

			for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
				var json = consumo.obterDados(
						ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
				DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
				temporadas.add(dadosTemporada);
			}
			temporadas.forEach(System.out::println);

			List<Episodio> episodios = temporadas.stream()
					.flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e)))
					.collect(Collectors.toList());
			serieEncontrada.setEpisodios(episodios);
			repositorio.save(serieEncontrada);
		} else {
			System.out.println("Serie nao encontrada! ");
		}
	}

}