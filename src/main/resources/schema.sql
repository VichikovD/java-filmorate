CREATE TABLE IF NOT EXISTS users (
  user_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  user_name varchar,
  email varchar NOT NULL,
  login varchar NOT NULL,
  birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS mpas (
  mpa_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  mpa_name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
  film_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  film_name varchar NOT NULL,
  description varchar(200) NOT NULL,
  release_date date NOT NULL,
  duration integer NOT NULL,
  mpa_id integer NOT NULL REFERENCES mpas (mpa_id)
);

CREATE TABLE IF NOT EXISTS genres (
  genre_id integer GENERATED BY DEFAULT AS IDENTITY  PRIMARY KEY,
  genre_name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS films_genres (
  film_id integer REFERENCES films (film_id),
  genre_id integer REFERENCES genres (genre_id),
  PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS director (
  director_id integer GENERATED BY DEFAULT AS IDENTITY  PRIMARY KEY,
  director_name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS films_director (
  film_id integer REFERENCES films (film_id),
  director_id integer REFERENCES director (director_id),
  PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS friends (
  user_id integer REFERENCES users (user_id),
  friend_id integer REFERENCES users (user_id),
  PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes (
  film_id integer REFERENCES films (film_id),
  user_id integer REFERENCES users (user_id),
  PRIMARY KEY (film_id, user_id)
);
