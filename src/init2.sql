create table customer
(
   id int not null,
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
   id int not null,
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
   id int not null,
   merchantId int constraint merchmoney_fk references merchant,
   sumSent decimal(19,2),
   sentDate timestamp,
   status char(1),
   primary key (id)
);