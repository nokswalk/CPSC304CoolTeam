create table Borrower
	(bid integer not null PRIMARY KEY,
	password varchar(20),
	name varchar(20),
	address varchar(50),
	phone varchar(10),
	emailAddress varchar(50),
	sinOrStNo integer,
	expiryDate date,
	type varchar(10) not null,
	FOREIGN KEY (type) references BorrowerType);

create table BorrowerType
	(type varchar(10) not null PRIMARY KEY,
	bookTimeLimit integer);

create table Book
	(callNumber integer not null PRIMARY KEY,
	isbn integer,
	title varchar(50),
	mainAuthor varchar(20),
	publisher varchar(20),
	year integer);

create table HasAuthor
	(callNumber integer not null,
	name varchar(20) not null,
	PRIMARY KEY (callNumber, name),
	FOREIGN KEY (callNumber) references Book);

create table HasSubject
	(callNumber integer not null,
	subject varchar(50) not null,
	PRIMARY KEY (callNumber, subject)
	FOREIGN KEY (callNumber) references Book);

create table BookCopy 
	(callNumber integer not null,
	copyNo integer not null,
	status varchar(10),
	PRIMARY KEY (callNumber, copyNo),
	FOREIGN KEY (callNumber) references Book);

create table HoldRequest
	(hid integer not null PRIMARY KEY,
	bid integer not null,
	callNumber integer not null,
	issuedDate date,
	FOREIGN KEY (bid) references Borrower,
	FOREIGN KEY (callNumber) references Book);

create table Borrowing
	(borid integer not null PRIMARY KEY,
	bid integer not null,
	callNumber integer not null,
	copyNo integer not null,
	outDate date,
	inDate date,
	FOREIGN KEY (bid) references Borrower,
	FOREIGN KEY (callNumber, copyNo) references BookCopy);

create table Fine
	(fid integer not null PRIMARY KEY,
	amount number,
	issuedDate date,
	paidDate date,
	borid integer not  null,
	FOREIGN KEY (borid) references Borrowing);
