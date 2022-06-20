/* suppression des tables */
drop table T_Toaccess;
drop table T_Compose;
drop table T_Toget;
drop table T_Ticket;
drop table T_Message;
drop table T_User;
drop table T_Groupe;

/* creation des tables */
create table T_Groupe(
NumG number,
NameG varchar2(20),

constraint pk_groupe PRIMARY KEY(NumG),

constraint nn_groupe_nameg CHECK(NameG IS NOT NULL)
);

create table T_User(
LoginU varchar2(30),
Password varchar2(30),
NameU varchar2(30),
Firstname varchar2(30),
Type varchar2(20),

constraint pk_user PRIMARY KEY(LoginU),

constraint ck_user_type CHECK(Type in ('autre','technicien'))
);

create table T_Compose(
LoginU varchar2(30),
NumG number,

constraint pk_compose PRIMARY KEY(LoginU, NumG),

constraint fk_compose_user FOREIGN KEY(LoginU) REFERENCES T_User(LoginU),
constraint fk_compose_groupe FOREIGN KEY(NumG) REFERENCES T_Groupe(NumG),

constraint nn_compose_loginu CHECK(LoginU IS NOT NULL),
constraint nn_compose_numg CHECK(NumG IS NOT NULL)

);

/* modification mld : renseignement de l'utilisateur au message 
ecrit par ce dernier (chaque message ecrit par un utilisateur) */
create table T_Message(
NumM number,
Msg varchar2(500),
Status number,
LoginU varchar2(30),

constraint pk_message PRIMARY KEY(NumM),

constraint fk_message_user FOREIGN KEY(LoginU) REFERENCES T_User(LoginU),

constraint nn_message_loginu CHECK(LoginU IS NOT NULL),

constraint ck_message_status CHECK(Status in (0,1,2))
);



create table T_Ticket(
TitleT varchar2(50),
NumG number,
NumFirstMsg number,

constraint pk_ticket PRIMARY KEY(TitleT, NumG, NumFirstMsg),

constraint fk_ticket_groupe FOREIGN KEY(NumG) REFERENCES T_Groupe(NumG),
constraint fk_ticket_Firstmessage FOREIGN KEY(NumFirstMsg) REFERENCES T_Message(NumM),

constraint nn_ticket_numg CHECK(NumG IS NOT NULL),
constraint nn_ticket_numfirstmsg CHECK(NumFirstMsg IS NOT NULL)
);

/* modification mld : creation classe d'association entre un ticket et un message */
create table T_Toget(
TitleT varchar2(50),
NumG number,
NumFirstMsg number,
NumM number,

constraint pk_toget PRIMARY KEY(TitleT, NumG, NumFirstMsg, NumM),

constraint fk_toget_ticket FOREIGN KEY(TitleT, NumG, NumFirstMsg) REFERENCES T_Ticket(TitleT, NumG, NumFirstMsg),
constraint fk_toget_message FOREIGN KEY(NumM) REFERENCES T_Message(NumM),

constraint nn_toget_numg CHECK(NumG IS NOT NULL),
constraint nn_toget_titlet CHECK(TitleT IS NOT NULL),
constraint nn_toget_numfirstmsg CHECK(NumFirstMsg IS NOT NULL),
constraint nn_toget_numm CHECK(NumM IS NOT NULL)

);

/* modele relationnel modifié pour pouvoir contraindre comme il faut les 
données de la table T_Toaccess */
create table T_Toaccess(
LoginU varchar2(30),
TitleT varchar2(50),
NumG number,
NumFistMsg number,

constraint pk_toaccess PRIMARY KEY(LoginU, NumG, TitleT),

constraint fk_toaccess_user FOREIGN KEY(LoginU) REFERENCES T_User(LoginU),
constraint fk_toaccess_ticket FOREIGN KEY(TitleT, NumG, NumFistMsg) REFERENCES T_Ticket(TitleT, NumG, NumFirstMsg),

constraint nn_toaccess_loginu CHECK(LoginU IS NOT NULL),
constraint nn_toaccess_numg CHECK(NumG IS NOT NULL),
constraint nn_toaccess_titlet CHECK(TitleT IS NOT NULL),
constraint nn_toaccess_numfirstmsg CHECK(NumFistMsg IS NOT NULL)
);

/* insertion de données dans les tables pour les tests */
INSERT INTO T_Groupe VALUES(1, 'TDA12');
INSERT INTO T_Groupe VALUES(2, 'Etudiants');
INSERT INTO T_Groupe VALUES(3, 'Professeurs');
INSERT INTO T_Groupe VALUES(4, 'Techniciens');

INSERT INTO T_User VALUES('fok2381b', 'phoque', 'fourcaud', 'kevin', 'autre');
INSERT INTO T_User VALUES('pin1538a', 'code', 'pinceau', 'nicolas', 'autre');
INSERT INTO T_User VALUES('ADR3542Z', 'adresse', 'adam', 'romaric', 'technicien');
INSERT INTO T_User VALUES('BIC2436Y', 'stylo', 'binaud', 'corentin', 'technicien');
INSERT INTO T_User VALUES('tam2153b', 'caisse', 'taricket', 'mathieu', 'autre');
INSERT INTO T_User VALUES('arv6842a', 'arrivee', 'argne', 'valentin', 'autre');

INSERT INTO T_Compose VALUES('pin1538a', 3);
INSERT INTO T_Compose VALUES('arv6842a', 3);
INSERT INTO T_Compose VALUES('fok2381b', 2);
INSERT INTO T_Compose VALUES('tam2153b', 2);
INSERT INTO T_Compose VALUES('pin1538a', 1);
INSERT INTO T_Compose VALUES('fok2381b', 1);
INSERT INTO T_Compose VALUES('ADR3542Z', 4);
INSERT INTO T_Compose VALUES('BIC2436Y', 4);

INSERT INTO T_Message VALUES(1, 'Probleme rencontré avec la lumiere de la salle 118', 0, 'fok2381b');
INSERT INTO T_Message VALUES(2, 'Incident de plomberie dans les toilettes du deuxieme etage', 1, 'pin1538a');

INSERT INTO T_Ticket VALUES('pb lumiere', 4, 1);
INSERT INTO T_Ticket VALUES('pb plomberie', 4, 2);

INSERT INTO T_Toget VALUES('pb lumiere', 4, 1, 1);
INSERT INTO T_Toget VALUES('pb plomberie', 4, 2, 2);

INSERT INTO T_Toaccess VALUES('fok2381b', 'pb lumiere', 4, 1);
INSERT INTO T_Toaccess VALUES('pin1538a', 'pb plomberie', 4, 2);

select table_name from all_tables where owner='SYSTEM' and table_name like 'T_%';
select * from T_User;
select * from T_Groupe;
select * from T_Compose;
select * from T_Message;
select * from T_Ticket;
select * from T_Toget;
select * from T_Toaccess;

