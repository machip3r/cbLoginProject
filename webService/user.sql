CREATE TABLE IF NOT EXISTS users(
    id_user INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL,
    password VARCHAR(200) NOT NULL,
    gender VARCHAR(6) NOT NULL,
    PRIMARY KEY (id_user)
);