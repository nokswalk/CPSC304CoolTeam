drop table Fine;
drop table Borrowing;
drop table HoldRequest;
drop table HasSubject;
drop table HasAuthor;
drop table BookCopy;
drop table Book;
drop table Borrower;
drop table BorrowerType;

drop sequence bid_c;
drop sequence callNumber_c;
drop sequence hid_c;
drop sequence borid_c;
drop sequence fid_c;


create sequence bid_c
	start with 1000 
	increment by 1;

create sequence callNumber_c
	start with 1000
	increment by 1;

create sequence hid_c
	start with 1000
	increment by 1;

create sequence borid_c
	start with 1000
	increment by 1;

create sequence fid_c
	start with 1000
	increment by 1;


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
	FOREIGN KEY (type) references BorrowerType ON DELETE CASCADE);

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
	FOREIGN KEY (callNumber) references Book ON DELETE CASCADE);

create table HasAuthor
	(callNumber integer not null,
	name varchar(20) not null,
	PRIMARY KEY (callNumber, name),
	FOREIGN KEY (callNumber) references Book ON DELETE CASCADE);

create table HasSubject
	(callNumber integer not null,
	subject varchar(50) not null,
	PRIMARY KEY (callNumber, subject),
	FOREIGN KEY (callNumber) references Book ON DELETE CASCADE);

create table HoldRequest
	(hid integer not null PRIMARY KEY,
	bid integer not null,
	callNumber integer not null,
	issuedDate date null,
	FOREIGN KEY (bid) references Borrower,
	FOREIGN KEY (callNumber) references Book ON DELELTE CASCADE);

create table Borrowing
	(borid integer not null PRIMARY KEY,
	bid integer not null,
	callNumber integer not null,
	copyNo integer not null,
	outDate date null,
	inDate date null,
	FOREIGN KEY (bid) references Borrower,
	FOREIGN KEY (callNumber, copyNo) references BookCopy ON DELETE CASCADE);

create table Fine
	(fid integer not null PRIMARY KEY,
	amount number null,
	issuedDate date null,
	paidDate date null,
	borid integer not null,
	FOREIGN KEY (borid) references Borrowing ON DELETE CASCADE);


insert into BorrowerType values
('student', 2);
insert into BorrowerType values
('faculty', 12);
insert into BorrowerType values
('staff', 6);
insert into BorrowerType values
('general', 2);


insert into Borrower values
(111, 'aaaa', 'Daniel', '1466 robson st', null, 'dannyboy@hotmail.com', '11111111', TO_DATE('2016-04-01', 'YYYY-MM-DD'), 'student');
insert into Borrower values
(222, 'bbbb', 'Vicky', null, '6044441111', 'hello@gmaill.com', '22222222', TO_DATE('2016-04-01', 'YYYY-MM-DD'), 'staff');
insert into Borrower values
(333, 'cccc', 'Zet', null, null, 'bye@gmail.com', '33333333', TO_DATE('2016-04-01', 'YYYY-MM-DD'), 'general');
insert into Borrower values
(444, 'eeee', 'Rupert', '1234 granville st', '7789999999', 'o3i3@cs.ubc.ca', '44444444', TO_DATE('2016-04-01', 'YYYY-MM-DD'), 'staff');
insert into Borrower values
(555, 'dddd', 'Green', 'Surrey central st', '2744444222', 'leprechaun@hotmail.com', '55555555', TO_DATE('2016-04-01', 'YYYY-MM-DD'), 'student');


insert into Book values
(100, '01000000', 'Hello Java world', 'Laks', 'UBC','1999');
insert into Book values
(200, '02000000', 'Hi Database', 'Laks', 'SFU','2000');
insert into Book values
(300, '03000000', 'Advanced Data Structures and Algorithms', 'Michelle Ng', 'UBC','2008');
insert into Book values
(400, '04000000', 'Design Patterns', 'Enoch Choi', 'UBC','1999');
insert into Book values
(500, '05000000', 'Hi C++', 'Laks', 'UBC','2014');
insert into Book values
(600, '06000000', 'Hardware and Software', 'Tony Chu', 'PublisherA','2002');
insert into Book values
(999, '123456789', 'ABCs', 'Sammy', 'Foo Readers', 2000);


insert into BookCopy values
(100, 1, 'out');
insert into BookCopy values
(200, 1, 'in');
insert into BookCopy values
(200, 2, 'out');
insert into BookCopy values
(300, 1, 'out');
insert into BookCopy values
(400, 1, 'in');
insert into BookCopy values
(400, 2, 'out');
insert into BookCopy values
(500, 1, 'in');
insert into BookCopy values
(500, 2, 'in');
insert into BookCopy values
(600, 1, 'out');
insert into BookCopy values
(600, 2, 'out');
insert into BookCopy values
(999, 1, 'in');
insert into BookCopy values
(999, 2, 'out');
insert into BookCopy values
(999, 3, 'out');


insert into HasAuthor values
(200, 'Gregor');
insert into HasAuthor values
(200, 'Mike');
insert into HasAuthor values
(500, 'Patrice');
insert into HasAuthor values
(999, 'Bobby');


insert into HasSubject values
(100, 'java');
insert into HasSubject values
(100, 'computer');
insert into HasSubject values
(200, 'database');
insert into HasSubject values
(200, 'computer');
insert into HasSubject values
(300, 'computer');
insert into HasSubject values
(400, 'computer');
insert into HasSubject values
(500, 'c++');
insert into HasSubject values
(500, 'computer');
insert into HasSubject values
(600, 'computer');
insert into HasSubject values
(999, 'kindergarten');
insert into HasSubject values
(999, 'letters');


insert into Borrowing values
(100, 333, 100, 1, TO_DATE('2013-02-20', 'YYYY-MM-DD'), null);
insert into Borrowing values
(500, 111, 200, 2, TO_DATE('2010-02-20', 'YYYY-MM-DD'), null);
insert into Borrowing values
(600, 111, 300, 1, TO_DATE('2009-02-20', 'YYYY-MM-DD'), null);
insert into Borrowing values
(700, 111, 400, 2, TO_DATE('2013-05-20', 'YYYY-MM-DD'), null);
insert into Borrowing values
(800, 222, 600, 2, TO_DATE('2014-03-29', 'YYYY-MM-DD'), null);
insert into Borrowing values
(900, 222, 999, 2, TO_DATE('2012-02-22', 'YYYY-MM-DD'), null);
insert into Borrowing values
(300, 333, 600, 1, TO_DATE('2014-01-29', 'YYYY-MM-DD'), TO_DATE('2014-01-31', 'YYYY-MM-DD'));
insert into Borrowing values
(550, 555, 999, 3, TO_DATE('2014-03-29', 'YYYY-MM-DD'), null);


insert into HoldRequest values
(999, 111, 300, TO_DATE('2014-03-30', 'YYYY-MM-DD'));

insert into Fine values
(100, 100.00, TO_DATE('2013-09-01', 'YYYY-MM-DD'), null, 100);
