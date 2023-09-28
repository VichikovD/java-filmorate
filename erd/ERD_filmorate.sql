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
  "likesQuantity" integer
);

CREATE TABLE "friends" (
  "user_id" integer,
  "friend_id" integer,
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
