create table Users(
    id serial PRIMARY KEY,
    login varchar(100) not null,
    username varchar(100) not null,
    password varchar(100) not null
);


create table Roles(
	id serial primary key,
	role varchar(100)
);


create table Users_to_Roles(
	userID smallint not null,
	roleID smallint not null,
	primary key(userID, roleID),
	foreign key (userID) references Users(id),
	foreign key (roleID) references Roles(id)
);

create table Department(
    id serial primary key,
    title varchar(100) not null unique,
    managerID smallint not null,
    foreign key (managerID) references Users(id)
);

create table Users_to_Departments(
    userID smallint not null,
    departmentID smallint not null,
    primary key(userID, departmentID),
    foreign key (userID) references Users(id),
    foreign key (departmentID) references Department(id)
);

create table Groups(
    id serial primary key,
    title varchar(100) unique,
    adminID smallint not null,
    password varchar(100) not null,
    foreign key (adminID) references Users(id)
);


create table Users_to_Groups(
    userID smallint not null,
    groupID smallint not null,
    primary key(userID, groupID),
    foreign key (userID) references Users(id),
    foreign key (groupID) references Groups(id)
);

create table Messages(
	id serial primary key,
	userID smallint not null,
	groupID smallint not null,
	dateMSG timestamptz not null,
	msg text not null,
	foreign key (userID) references Users(id),
    foreign key (groupID) references Groups(id)
);

alter table Messages add
	msg text not null;



select * from messages ;
insert into messages(id, userid, groupid, datemsg, msg) values(
3, 1, 1, 'Mon Oct 13 15:21:15 MSK 2024', 'hello'
);


--выбрать дату старше 1 часа interval '1 hour'
-- 2 недели - interval '14 day'
select * from messages
where datemsg < current_timestamp - interval '14 day'; 

insert into users_to_groups (userID, groupID) values(1, 1);

select max(id) as id from ('groups');


insert into roles (id, role) values(1, 'ADMIN'),
(2, 'USER');

insert into users (id, login, password, username) values(1, 'admin', 'admin', 'admin'),
(2, 'qwe', 'qwe', 'qwe1'), (3,'asd','asd', 'asd1');

insert into users_to_roles (userID, roleID) values(1,1),(2,2),(3,2);

select * from users u ;
select * from roles r ;
select * from department d ;
select * from users_to_roles utr;
select * from users_to_departments utd ;

insert into roles (id, role) values(3, 'MANADGER');

select max(id) as id from department;

delete from department where id = 2;
SELECT id FROM department d where title = 'otd';

select * from groups;
select * from users_to_groups
insert into groups(id, title, adminid) values(1, 'gr2', 1);
