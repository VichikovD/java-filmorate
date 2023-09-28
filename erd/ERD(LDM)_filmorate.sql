CREATE TABLE "users" (
  "user_id" integer PRIMARY KEY,
  "user_name" varchar,
  "email" varchar NOT NULL,
  "login" varchar NOT NULL,
  "birthday" date NOT NULL
);

CREATE TABLE "films" (
  "film_id" integer PRIMARY KEY,
  "film_name" varchar NOT NULL,
  "description" varchar(200) NOT NULL,
  "releaseDate" date NOT NULL,
  "duration" integer NOT NULL,
  "rating_id" integer NOT NULL,
  "likesQuantity" integer
);

CREATE TABLE "genres" (
  "genre_id" integer PRIMARY KEY,
  "genre_name" varchar NOT NULL
);

CREATE TABLE "ratings" (
  "rating_id" integer PRIMARY KEY,
  "rating_name" varchar NOT NULL
);

CREATE TABLE "films_genres" (
  "film_id" integer,
  "genre_id" integer,
  PRIMARY KEY ("film_id", "genre_id")
);

CREATE TABLE "friends" (
  "user_id" integer,
  "friend_id" integer,
  "is_approved" boolean NOT NULL,
  PRIMARY KEY ("user_id", "friend_id")
);

CREATE TABLE "likes" (
  "film_id" integer,
  "user_id" integer,
  PRIMARY KEY ("film_id", "user_id")
);

COMMENT ON COLUMN "users"."birthday" IS 'in the past';

COMMENT ON COLUMN "films"."releaseDate" IS 'not earlier earlier then 28.12.1895';

COMMENT ON COLUMN "films"."duration" IS 'positive';

ALTER TABLE "friends" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "friends" ADD FOREIGN KEY ("friend_id") REFERENCES "users" ("user_id");

ALTER TABLE "likes" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "likes" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "films_genres" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "films_genres" ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("genre_id");

ALTER TABLE "films" ADD FOREIGN KEY ("rating_id") REFERENCES "ratings" ("rating_id");
