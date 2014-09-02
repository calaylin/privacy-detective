DROP TABLE IF EXISTS hashtags;
DROP TABLE IF EXISTS mentioned_users;
DROP TABLE IF EXISTS topic_assignment;
DROP TABLE IF EXISTS topics;
DROP TABLE IF EXISTS person_privacy_score;
DROP TABLE IF EXISTS convtweets;
DROP TABLE IF EXISTS conv_methods;
DROP TABLE IF EXISTS tweet;
DROP TABLE IF EXISTS followers;
DROP TABLE IF EXISTS person;

CREATE TABLE person (
  person_id bigint PRIMARY KEY,
  handle varchar(64) UNIQUE NOT NULL,
  person_name varchar(64),
  location varchar(64),
  verified boolean NOT NULL DEFAULT FALSE,
  url varchar(64),
  created timestamp
);

-- Empty person for uncheckable tweets
INSERT INTO person (person_id,handle) VALUES (0,'');

CREATE TABLE followers (
 person_id bigint REFERENCES person(person_id) NOT NULL,
 follower bigint REFERENCES person(person_id) NOT NULL,
 PRIMARY KEY (person_id, follower)
);

CREATE TABLE tweet (
  tweet_id bigint UNIQUE NOT NULL PRIMARY KEY,
  person_id bigint REFERENCES person(person_id) NOT NULL,
  tweet varchar(145) NOT NULL,
  reply_to_user bigint REFERENCES person(person_id) DEFAULT NULL,
  reply_to_status bigint REFERENCES tweet(tweet_id) DEFAULT NULL,
  location varchar(64) DEFAULT NULL,
  retweet_count integer NOT NULL DEFAULT 0,
  favorite_count integer NOT NULL DEFAULT 0,
  created date DEFAULT NULL,
  not_english integer NOT NULL DEFAULT 0
);

CREATE TABLE hashtags (
  tweet_id bigint REFERENCES tweet(tweet_id) NOT NULL,
  hashtag varchar(145) NOT NULL,
  PRIMARY KEY (tweet_id,hashtag)
);

CREATE TABLE mentioned_users (
  tweet_id bigint REFERENCES tweet(tweet_id) NOT NULL,
  person_id bigint REFERENCES person(person_id) NOT NULL
);

CREATE TABLE conv_methods (
  conv_method_id SERIAL PRIMARY KEY,
  conv_method_name varchar(256)
);

INSERT INTO conv_methods (conv_method_name) VALUES ('LANG');

CREATE TABLE convtweets (
  convtweet_id SERIAL NOT NULL,
  tweet_id bigint REFERENCES tweet(tweet_id) NOT NULL,
  tweet varchar(512) NOT NULL,
  conv_method_id integer NOT NULL REFERENCES conv_methods(conv_method_id),
  PRIMARY KEY (tweet_id,conv_method_id)
);

CREATE TABLE topics (
  topic_id SERIAL PRIMARY KEY,
  privacy_related int NOT NULL DEFAULT 0,
  topic_name varchar(255) NOT NULL
);

CREATE TABLE topic_assignment (
  person_id bigint REFERENCES person(person_id) NOT NULL,
  topic_id bigint REFERENCES topics(topic_id) NOT NULL,
  score double precision NOT NULL,  
  PRIMARY KEY (person_id,topic_id)
);

CREATE TABLE person_privacy_score (
   person_id bigint REFERENCES person(person_id) PRIMARY KEY,
   score integer NOT NULL
);
