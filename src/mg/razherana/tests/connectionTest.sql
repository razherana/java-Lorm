-- Active: 1738011862925@@127.0.0.1@3306@test
CREATE TABLE user (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL
);

CREATE TABLE post (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user INT REFERENCES user(id),
  title VARCHAR(50) NOT NULL,
  description TEXT NOT NULL
);

CREATE TABLE comments (
  id INT PRIMARY KEY AUTO_INCREMENT,
  post INT REFERENCES post(id),
  user INT REFERENCES user(id),
  content TEXT NOT NULL
);  

CREATE TABLE test_like (
  id INT PRIMARY KEY AUTO_INCREMENT,
  post INT REFERENCES post(id),
  user_id INT REFERENCES user(id),
  date DATETIME NOT NULL
);

INSERT INTO `user` VALUES (1, 'Herana'), (2, 'Rotsy');
INSERT INTO `post` VALUES (1, 1, 'Titre 1', 'Desc 1'), (2, 1, 'Titre 2', 'Desc 2');
INSERT INTO `post` VALUES (3, 2, 'Titre 3', 'Desc 3'), (4, 2, 'Titre 4', 'Desc 4');

INSERT INTO `comments` VALUES (1, 1, 1, 'Comment 1'), (2, 1, 2, 'Comment 2');
INSERT INTO `comments` VALUES (3, 2, 1, 'Comment 3'), (4, 2, 2, 'Comment 4');

INSERT INTO `test_like` VALUES (1, 1, 1, '2020-01-01 00:00:00'), (2, 1, 2, '2020-01-01 00:00:00');
INSERT INTO `test_like` VALUES (3, 2, 1, '2020-01-01 00:00:00'), (4, 2, 2, '2020-01-01 00:00:00');


DELETE FROM `post`;
DELETE FROM `user`;