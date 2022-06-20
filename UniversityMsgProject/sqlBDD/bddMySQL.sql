/* Reconstruction de la base de donn�e */
DROP DATABASE IF EXISTS bdd;

CREATE DATABASE bdd;
USE bdd;
/* creation des tables */
CREATE TABLE T_Groupe(
    NumG INTEGER PRIMARY KEY,
    NameG CHAR(20) NOT NULL
); 
CREATE TABLE T_User(
    LoginU CHAR(30) PRIMARY KEY,
    Password CHAR(30),
    NameU CHAR(30),
    Firstname CHAR(30),
    TypeU CHAR(20) CHECK
        (TypeU IN('autre', 'technicien'))
);
CREATE TABLE T_Compose(
    LoginU VARCHAR(30) NOT NULL,
    NumG INTEGER NOT NULL,
    PRIMARY KEY(LoginU, NumG),
    FOREIGN KEY(LoginU) REFERENCES T_User(LoginU),
    FOREIGN KEY(NumG) REFERENCES T_Groupe(NumG)
);
/* modification mld : renseignement de l'utilisateur au message 
ecrit par ce dernier (chaque message ecrit par un utilisateur) */
CREATE TABLE T_Message(
    NumM INTEGER PRIMARY KEY,
    Msg CHAR(255),
    StatusM INTEGER CHECK
        (StatusM IN(0, 1, 2)),
        LoginU CHAR(30) NOT NULL,
        FOREIGN KEY(LoginU) REFERENCES T_User(LoginU)
);
CREATE TABLE T_Ticket(
    TitleT CHAR(50),
    NumG INTEGER NOT NULL,
    NumFirstMsg INTEGER NOT NULL,
    PRIMARY KEY(TitleT, NumG, NumFirstMsg),
    FOREIGN KEY(NumG) REFERENCES T_Groupe(NumG),
    FOREIGN KEY(NumFirstMsg) REFERENCES T_Message(NumM)
);
/* modification mld : creation classe d'association entre un ticket et un message */
CREATE TABLE T_Toget(
    TitleT CHAR(50),
    NumG INTEGER,
    NumFirstMsg INTEGER,
    NumM INTEGER,
    PRIMARY KEY(
        TitleT,
        NumG,
        NumFirstMsg,
        NumM
    ),
    FOREIGN KEY(TitleT, NumG, NumFirstMsg) REFERENCES T_Ticket(TitleT, NumG, NumFirstMsg),
    FOREIGN KEY(NumM) REFERENCES T_Message(NumM)
);
/* modele relationnel modifié pour pouvoir contraindre comme il faut les 
données de la table T_Toaccess */
CREATE TABLE T_Toaccess(
    LoginU CHAR(30),
    TitleT CHAR(50),
    NumG INTEGER,
    NumFirstMsg INTEGER,
    PRIMARY KEY(LoginU, NumG, TitleT),
    FOREIGN KEY(LoginU) REFERENCES T_User(LoginU),
    FOREIGN KEY(TitleT, NumG, NumFirstMsg) REFERENCES T_Ticket(TitleT, NumG, NumFirstMsg)
);
/* insertion de données dans les tables pour les tests */
INSERT INTO T_Groupe
VALUES(0, 'TDA12');
INSERT INTO T_Groupe
VALUES(1, 'Etudiants');
INSERT INTO T_Groupe
VALUES(2, 'Professeurs');
INSERT INTO T_Groupe
VALUES(3, 'Techniciens');
INSERT INTO T_User
VALUES(
    'fok2381b',
    'phoque',
    'fourcaud',
    'kevin',
    'autre'
);
INSERT INTO T_User
VALUES(
    'pin1538a',
    'code',
    'pinceau',
    'nicolas',
    'autre'
);
INSERT INTO T_User
VALUES(
    'ADR3542Z',
    'adresse',
    'adam',
    'romaric',
    'technicien'
);
INSERT INTO T_User
VALUES(
    'BIC2436Y',
    'stylo',
    'binaud',
    'corentin',
    'technicien'
);
INSERT INTO T_User
VALUES(
    'tam2153b',
    'caisse',
    'taricket',
    'mathieu',
    'autre'
);
INSERT INTO T_User
VALUES(
    'arv6842a',
    'arrivee',
    'argne',
    'valentin',
    'autre'
);
INSERT INTO T_Compose
VALUES('pin1538a', 2);
INSERT INTO T_Compose
VALUES('arv6842a', 2);
INSERT INTO T_Compose
VALUES('fok2381b', 1);
INSERT INTO T_Compose
VALUES('tam2153b', 1);
INSERT INTO T_Compose
VALUES('pin1538a', 0);
INSERT INTO T_Compose
VALUES('fok2381b', 0);
INSERT INTO T_Compose
VALUES('ADR3542Z', 3);
INSERT INTO T_Compose
VALUES('BIC2436Y', 3);
INSERT INTO T_Message
VALUES(
    0,
    'Probleme rencontré avec la lumiere de la salle 118',
    0,
    'fok2381b'
);
INSERT INTO T_Message
VALUES(
    1,
    'Incident de plomberie dans les toilettes du deuxieme etage',
    1,
    'pin1538a'
);
INSERT INTO T_Ticket
VALUES('pb lumiere', 3, 0);
INSERT INTO T_Ticket
VALUES('pb plomberie', 3, 1);
INSERT INTO T_Toget
VALUES('pb lumiere', 3, 0, 0);
INSERT INTO T_Toget
VALUES('pb plomberie', 3, 1, 1);
INSERT INTO T_Toaccess
VALUES('fok2381b', 'pb lumiere', 3, 0);
INSERT INTO T_Toaccess
VALUES('pin1538a', 'pb plomberie', 3, 1);
