create table company (
  id identity primary key,
  market varchar,
  symbol varchar,
  name varchar,
  sector varchar,
  industry varchar
);

create table article (
  id identity primary key,
  symbol varchar,
  url varchar,
  publishDate timestamp,
  content clob,
  score real,
  magnitude real
);
