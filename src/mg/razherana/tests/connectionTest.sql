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

INSERT INTO `user` VALUES (1, 'Herana'), (2, 'Rotsy');
INSERT INTO `post` VALUES (1, 1, 'Titre 1', 'Desc 1'), (2, 1, 'Titre 2', 'Desc 2');
INSERT INTO `post` VALUES (3, 2, 'Titre 3', 'Desc 3'), (4, 2, 'Titre 4', 'Desc 4');