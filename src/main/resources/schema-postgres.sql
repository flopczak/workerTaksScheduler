DROP TABLE IF EXISTS workers;
CREATE TABLE workers(id serial PRIMARY KEY, name integer);

DROP TABLE IF EXISTS tasks;
CREATE TABLE tasks(id serial PRIMARY KEY, type integer);

DROP TABLE IF EXISTS statistics;
CREATE TABLE statistics(id serial PRIMARY KEY, type integer, name integer, time integer);
