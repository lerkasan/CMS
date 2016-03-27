create table merchant
(
   id int not null autoincrement,
   name varchar(60) not null,
   charge decimal(5,2) not null,
   period smallint not null,
   minSum decimal(19,2) not null,
   bankName varchar(100) not null,
   swift varchar(40) not null,
   account varchar(20) not null,
   needToSend decimal(19,2),
   sent decimal(19,2),
   lastSent date,
   primary key (id)
);

create table customer
(
   id int not null autoincrement,
   name varchar(60) not null,
   address varchar(300) not null,
   email varchar(90) not null,
   ccNo varchar(20) not null,
   ccType varchar(60) not null,
   maturity date,
   primary key (id)
);

create table payment
(
   id int not null autoincrement,
   dt timestamp not null,
   merchantId int constraint merchant_fk references merchant,
   customerId int constraint customer_fk references customer,
   goods varchar(500),
   sumPayed decimal(15,2),
   chargePayed decimal(15,2),
   primary key (id)
);

create table transMoney
(
   id int not null autoincrement,
   merchantId int constraint merchmoney_fk references merchant,
   sumSent decimal(19,2),
   sentDate timestamp,
   status char(1),
   primary key (id)
);