DROP ALL OBJECTS;

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
  film_id integer REFERENCES films (film_id) ON DELETE CASCADE,
  genre_id integer REFERENCES genres (genre_id),
  PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS friends (
  user_id integer REFERENCES users (user_id) ON DELETE CASCADE,
  friend_id integer REFERENCES users (user_id) ON DELETE CASCADE,
  PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes (
  film_id integer REFERENCES films (film_id) ON DELETE CASCADE,
  user_id integer REFERENCES users (user_id) ON DELETE CASCADE,
  PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS directors (
  director_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  director_name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS films_directors (
  film_id integer REFERENCES films (film_id) ON DELETE CASCADE,
  director_id integer REFERENCES directors (director_id) ON DELETE CASCADE,
  PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS reviews (
  review_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  content varchar NOT NULL,
  is_positive boolean NOT NULL,
  film_id integer REFERENCES films (film_id) ON DELETE CASCADE,
  user_id integer REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS review_likes (
  review_id integer REFERENCES reviews (review_id) ON DELETE CASCADE,
  user_id integer REFERENCES users (user_id) ON DELETE CASCADE,
  is_like boolean NOT NULL,
  PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS events (
  event_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  user_id integer REFERENCES users (user_id) ON DELETE CASCADE,
  event_type varchar NOT NULL,
  operation varchar NOT NULL,
  timestamp bigint NOT NULL,
  entity_id integer NOT NULL
);
