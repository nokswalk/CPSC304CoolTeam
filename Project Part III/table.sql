drop table Fine;
drop table Borrowing;
drop table HoldRequest;
drop table HasSubject;
drop table HasAuthor;
drop table BookCopy;
drop table Book;
drop table Borrower;
drop table BorrowerType;


create table BorrowerType
	(type varchar(10) not null PRIMARY KEY,
	bookTimeLimit integer null);

create table Borrower
	(bid integer not null PRIMARY KEY,
	password varchar(20) null,
	name varchar(20) null,
	address varchar(50) null,
	phone varchar(10) null,
	emailAddress varchar(50) null,
	sinOrStNo integer not null,
	expiryDate date null,
	type varchar(10) not null,
	FOREIGN KEY (type) references BorrowerType);

create table Book
	(callNumber integer not null PRIMARY KEY,
	isbn integer not null,
	title varchar(50) null,
	mainAuthor varchar(20) null,
	publisher varchar(20) null,
	year integer null);

create table BookCopy 
	(callNumber integer not null,
	copyNo integer not null,
	status varchar(10) null,
	PRIMARY KEY (callNumber, copyNo),
	FOREIGN KEY (callNumber) references Book);

create table HasAuthor
	(callNumber integer not null,
	name varchar(20) not null,
	PRIMARY KEY (callNumber, name),
	FOREIGN KEY (callNumber) references Book);

create table HasSubject
	(callNumber integer not null,
	subject varchar(50) not null,
	PRIMARY KEY (callNumber, subject),
	FOREIGN KEY (callNumber) references Book);

create table HoldRequest
	(hid integer not null PRIMARY KEY,
	bid integer not null,
	callNumber integer not null,
	issuedDate date null,
	FOREIGN KEY (bid) references Borrower,
	FOREIGN KEY (callNumber) references Book);

create table Borrowing
	(borid integer not null PRIMARY KEY,
	bid integer not null,
	callNumber integer not null,
	copyNo integer not null,
	outDate date null,
	inDate date null,
	FOREIGN KEY (bid) references Borrower,
	FOREIGN KEY (callNumber, copyNo) references BookCopy);

create table Fine
	(fid integer not null PRIMARY KEY,
	amount number null,
	issuedDate date null,
	paidDate date null,
	borid integer not null,
	FOREIGN KEY (borid) references Borrowing);


insert into BorrowerType values
('student', 2);
insert into BorrowerType values
('faculty', 12);
insert into BorrowerType values
('staff', 6);
insert into BorrowerType values
('general', 2);