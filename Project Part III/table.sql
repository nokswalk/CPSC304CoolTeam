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
	phone char(10) null,
	emailAddress varchar(50) null,
	sinOrStNo varchar(10) not null,
	expiryDate date null,
	type varchar(10) not null,
	FOREIGN KEY (type) references BorrowerType);

create table Book
	(callNumber integer not null PRIMARY KEY,
	isbn char(9) not null,
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


insert into Borrower values
('12345678', 'aaaa', 'Daniel', '1466 robson st', '6044443333', null, '12345678', '2015-09-09', 'faculty');
insert into Borrower values
('56781234', 'bbbb', 'Vicky', 'vancouver', '6044441111', 'hello@gmaill.com', '12345678', '2014-09-09', 'staff');
insert into Borrower values
('87654321', 'cccc', 'Zet', 'UBC', '6041233333', 'bye@gmail.com', '12345678', '2015-12-12', 'general');
insert into Borrower values
('11111111', 'eeee', 'Rupert', '1234 granville st', '7789999999', 'o3i3@cs.ubc.ca', '12345678', '2015-09-09', 'staff');
insert into Borrower values
('22222222', 'dddd', 'Green', 'Surrey central st', '2744444222', ' ', '12345678', '2015-09-09', 'student');

insert into Book values
(1500, '01000000', 'Hello Java world', 'Laks', 'UBC','1999');
insert into Book values
(2000, '02000000', 'Hi Database', 'Laks', 'SFU','2000');
insert into Book values
(3000, '03000000', 'Advanced Data Structure and Algorithm', 'Michelle Ng', 'UBC','2008');
insert into Book values
(4000, '04000000', 'Design Patterns', 'Enoch Choi', 'UBC','1999');
insert into Book values
(5000, '05000000', 'Hi C++', 'Laks', 'UBC','2014');
insert into Book values
(6000, '06000000', 'Hardware and Software', 'Tony Chu', 'PublisherA','2002');
insert into Book values
(1000, '123456789', 'ABCs', 'Sammy Foo', 'Foo Readers', 2000);


insert into BookCopy values
(1000, 1, 'in');
insert into BookCopy values
(1000, 2, 'out');
insert into BookCopy values
(1000, 3, 'on hold');

insert into HasSubject values
(1000, 'kindergarten');

insert into Borrowing values
(11111,12345678,1000,2,'2014-03-29',null);
insert into Borrowing values
(22222,12345678,1000,1,'2014-02-22','2014-03-29');
insert into Borrowing values
(33333,12345678,1000,1,'2014-01-29','2014-01-31');
insert into Borrowing values
(44444,12345678,1000,1,'2013-09-01','2013-10-10');

insert into HoldRequest values
(1111,12345678,1000, '2014-03-30');
