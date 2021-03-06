package edu.neu.cs5200.orm.jpa.daos;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import edu.neu.cs5200.orm.jpa.entities.Actor;
import edu.neu.cs5200.orm.jpa.entities.Comment;
import edu.neu.cs5200.orm.jpa.entities.Critique;
import edu.neu.cs5200.orm.jpa.entities.Director;
import edu.neu.cs5200.orm.jpa.entities.Movie;
import edu.neu.cs5200.orm.jpa.entities.Producer;
import edu.neu.cs5200.orm.jpa.entities.User;

public class MovieDao extends BaseDao {
	public MovieDao() {
		super();
	}

	public int createMovie(Movie movie) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.flush();	
		em.persist(movie);
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
		return movie.getId();
	}

	public Movie findMovieById(int id) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Movie movie = em.find(Movie.class, id);
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
		return movie;
	}

	public List<Movie> findAllMovie() {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Query query = em.createQuery("select a from Movie a", Movie.class);
		List<Movie> movies = query.getResultList();
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
		return movies;
	}
	
	public List<Movie> getMoviesWithName(String name) {
		List<Movie> movies = findAllMovie();
		List<Movie> res = new ArrayList<>();
		for (Movie movie : movies) {
			if (movie.getTitle().toLowerCase().contains(name.toLowerCase())) {
				res.add(movie);
			}
		}
		return res;
	}

	public void updateMovie(int id, Movie newMovie) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		Movie oldMovie = em.find(Movie.class, id);
		oldMovie.setTitle(newMovie.getTitle());
		oldMovie.setActors(newMovie.getActors());
		oldMovie.setDirectors(newMovie.getDirectors());
		oldMovie.setDescription(newMovie.getDescription());
		em.merge(oldMovie);
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
	}

	public void deleteAllMovies() {
		List<Movie> list = findAllMovie();
		for (Movie movie : list) {
			deleteMovie(movie.getId());
		}
	}

	public void deleteMovie(int id) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.remove(em.find(Movie.class, id));
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
	}
	
	public void addActor(Movie movie, Actor actor) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		movie.getActors().add(actor);
		actor.getMovies().add(movie);
		em.merge(movie);
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
	}
	
	public void addDirector(Movie movie, Director director) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		movie.getDirectors().add(director);
		director.getMovies().add(movie);
		em.merge(movie);
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
	}
	
	
	public void deleteDirector(Movie movie, Director director) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		List<Director> dlist = movie.getDirectors();
		Director toRemoveDirector = null;
		for(Director dir : dlist) {
			if (dir.getId() == director.getId()) {
				toRemoveDirector = dir;
			}
		}
		movie.getDirectors().remove(toRemoveDirector);

		
		List<Movie> mList = director.getMovies();
		Movie toRemoveMovie = null;
		for (Movie m: mList) {
			if (m.getId() == movie.getId()) {
				toRemoveMovie = m;
			}
		}
		director.getMovies().remove(toRemoveMovie);
		
		em.merge(director);
		em.merge(movie);
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
	}
	
	
	public void deleteActor(Movie movie, Actor actor) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		List<Actor> alist = movie.getActors();
		Actor toRemoveActor = null;
		for(Actor act : alist) {
			if (act.getId() == actor.getId()) {
				toRemoveActor = act;
			}
		}
		movie.getActors().remove(toRemoveActor);

		
		List<Movie> mList = actor.getMovies();
		Movie toRemoveMovie = null;
		for (Movie m: mList) {
			if (m.getId() == movie.getId()) {
				toRemoveMovie = m;
			}
		}
		actor.getMovies().remove(toRemoveMovie);
		
		em.merge(actor);
		em.merge(movie);
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
	}
	
	
	public List<Movie> getMoviesByProducer(Producer producer) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Query query = em.createQuery("select m from Movie m where m.producer =:producer ", Comment.class)
				.setParameter("producer", producer);
		List<Movie> movies = query.getResultList();
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
		return movies;
	}
	
	public double getRegularRate(int movieId) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Movie movie = em.find(Movie.class, movieId);
		double rst =  0.0;
		double sum = 0;
		int count = 0;
		
		Query query = em.createQuery("select c from Comment c where c.movie =:movieObject ", Comment.class)
				.setParameter("movieObject", movie);
		List<Comment> comments = query.getResultList();
		for (Comment c : comments) {
			if (!(c.getUser() instanceof Critique)) {
				count++;
				sum += c.getRate();
			}
		}
		if (count == 0) {
			rst =  0;
		} else {
			rst = sum / count;
		}
		
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
		DecimalFormat df = new DecimalFormat("#.##");      
		rst = Double.valueOf(df.format(rst));
		return rst;
	}

	public double getCriticRate(int movieId) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Movie movie = em.find(Movie.class, movieId);
		double rst =  0.0;
		
		Query query = em.createQuery("select c from Comment c where c.movie =:movieObj ", Comment.class)
				.setParameter("movieObj", movie);
		List<Comment> comments = query.getResultList();
		
		double sum = 0;
		int count = 0;
		for (Comment c : comments) {
			if (c.getUser() instanceof Critique) {
				count++;
				sum += c.getRate();
			}
		}
		if (count == 0) {
			rst = 0;
		} else {
			rst = sum / count;
		}
				
		try {
			em.getTransaction().commit();
		} catch (RollbackException ex) {
			ex.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
		DecimalFormat df = new DecimalFormat("#.##");      
		rst = Double.valueOf(df.format(rst));
		return rst;
	}
	
	

	public static void test() {
		MovieDao movieDao = new MovieDao();		
		//a. remove all movies
		movieDao.deleteAllMovies();

		ActorDao aDao = new ActorDao();
		DirectorDao dDao = new DirectorDao();
		// create all directors and actors
		Director rs = new Director();
		rs.setFirstName("Ridley");
		rs.setLastName("Scott");
		dDao.createDirector(rs);

		Director ss = new Director();
		ss.setFirstName("Steven");
		ss.setLastName("Spielberg");
		dDao.createDirector(ss);

		// Harrison Ford
		Actor hf = new Actor();
		hf.setFirstName("Harrison");
		hf.setLastName("Ford");
		aDao.createActor(hf);

		// Rutger Huaer
		Actor rh = new Actor();
		rh.setFirstName("Rutger");
		rh.setLastName("Hauer");
		aDao.createActor(rh);

		// Karren Allen
		Actor ka = new Actor();
		ka.setFirstName("Karren");
		ka.setLastName("Allen");
		aDao.createActor(ka);

		// Richard Dreyfus
		Actor rd = new Actor();
		rd.setFirstName("Richard");
		rd.setLastName("Dreyfus");
		aDao.createActor(rd);

		// Melinda Dillon
		Actor md = new Actor();
		md.setFirstName("Melinda");
		md.setLastName("Dillon");
		aDao.createActor(md);
		

		//b. create movie Blade Runner
		// actors: Harrison Ford, Rutger Hauer
		// director: Ridley Scott
		ArrayList<Actor> aList = new ArrayList<>();
		aList.add(hf);
		aList.add(rh);
		ArrayList<Director> dList = new ArrayList<>();
		dList.add(rs);
		Movie br = new Movie();
		br.setTitle("Blade Runner");
		br.setDirectors(dList);
		br.setActors(aList);
		movieDao.createMovie(br);/*

		//c. create movie Raiders of The Lost Ark
		// actors: Harrison Ford, Karen Allen
		// director: Steven Spielberg
		ArrayList<Actor> a2List = new ArrayList<>();
		a2List.add(hf);
		a2List.add(ka);
		ArrayList<Director> d2List = new ArrayList<>();
		d2List.add(ss);
		
 		//TODO: every movie creates new person.		
		Movie mv2 = new Movie();
		mv2.setTitle("Raiders of The Lost Ark");
		mv2.setDirectors(d2List);
		mv2.setActors(a2List);
		movieDao.createMovie(mv2);
		

		//d. create movie Close Encounters of the Third Kind
		// actors: Richard Dreyfus, Melinda Dillon
		// director: Steven Spielberg
		ArrayList<Actor> a3List = new ArrayList<>();
		a3List.add(rd);
		a3List.add(md);
		ArrayList<Director> d3List = new ArrayList<>();
		d3List.add(ss);
		Movie mv3 = new Movie();
		mv3.setTitle("Close Encounters of the Third Kind");
		mv3.setDirectors(d3List);
		mv3.setActors(a3List);
		movieDao.createMovie(mv3);
		
		//e. retrieve all movies
		List<Movie> list = movieDao.findAllMovie();
		Set<String> movies = new HashSet<String>();
		System.out.println(list.size());
		for(Movie m : list) {
			if (!movies.contains(m.getTitle())) {
				movies.add(m.getTitle());	
			System.out.println(m.getTitle());
			//System.out.println("Actors: ");
			for(Actor a : m.getActors()) {
				System.out.println(a.getFirstName() + " " + a.getLastName());
			}
			//System.out.println("Director: ");
			for (Director d : m.getDirectors()) {
				System.out.println(d.getFirstName() + " " + d.getLastName());
			}
			//System.out.println();
			}
		}
		
		
		//f. retrieve all Harrison Ford  movies
		ActorDao actorDao = new ActorDao();
		Actor harrison = actorDao.findActorById(hf.getId());
		System.out.println(harrison.getFirstName() + " " + harrison.getLastName());
		for(Movie m : harrison.getMovies()) {
			System.out.println(m.getTitle());
		}
		
		//g. retrieve all Steven Spilberg movies
		DirectorDao directorDao = new DirectorDao();
		Director spilberg = directorDao.findDirectorById(ss.getId());
		System.out.println(spilberg.getFirstName() + " " + spilberg.getLastName());
		
		Set<String> mv = new HashSet<String>();
		for(Movie m : spilberg.getMovies()) {
				mv.add(m.getTitle());
				System.out.println(m.getTitle());
		}*/
		
	}

	public static void main(String[] args) {
//		test();
		MovieDao dao = new MovieDao();
		Movie m1 = dao.findMovieById(1);
		System.out.println(m1.getRegularRate());
		System.out.println(m1.getCritiqueRate());
	}

}
