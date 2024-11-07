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


create table Date_visit (
    id serial primary key,
	userID smallint not null,
	datevisit timestamptz not null,
	foreign key (userID) references Users(id)
);

select * from date_visit ;

create table Request_add_group (
	id serial primary key,
	userID smallint not null,
	groupID smallint not null,
	daterequest timestamptz not null,
	foreign key (userID) references Users(id),
    foreign key (groupID) references Groups(id)
);

select * from date_visit dv ;
insert into date_visit (id, userid, datevisit) values(1, 1, (select now()));

select now();

select rag.id, username, title from request_add_group rag 
inner join users u on rag.userid = u.id 
inner join "groups" g ON rag.groupid = g.id 
where rag.groupid = 1 and 
rag.daterequest > 'Mon Oct 13 15:21:15 MSK 2024';

delete from request_add_group 
where id in (select rag.id from request_add_group rag 
inner join users u on rag.userid = u.id 
inner join "groups" g ON rag.groupid = g.id 
where rag.groupid = 1 and 
rag.daterequest > 'Mon Oct 13 15:21:15 MSK 2024');
-- id = полученному id запроса


select * from request_add_group rag ;
select * from date_visit dv ;


insert into date_visit(id , userid, datevisit) values(1,1, (select now()));
insert into date_visit(id , userid) values(2,2);

update date_visit set datevisit = (select now()) where userid = 2;

--Thu Oct 31 14:10:04 MSK 2024
insert into date_visit(id , userid, datevisit) values(2,2, 'Thu Oct 31 14:10:04 MSK 2024');
update date_visit set datevisit = (select now()) where userid = 2;


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
select * from users_to_groups;

insert into groups(id, title, adminid) values(1, 'gr2', 1);

insert into users_to_groups (userid, groupid) values
(1, 1),(2, 1);

select title from "groups" gr
inner join "users_to_groups" utg on gr.id = utg.groupid 
inner join users u on utg.userid = u.id 
where login = 'asda';

select now();
insert into date_visit(datevisit, id , userid) values((select now()), 3, 6);
select * from date_visit dv;

drop t

select * from users_to_groups utg ;
select count(userid) from users_to_groups utg 
where utg.userid = 2 and groupid = 3;

select id from "groups" g where title = 'gr2';


select * from request_add_group rag ;
insert into request_add_group (id, userid, groupid, daterequest) values(2, 2, 2, (select now()));

select id from groups where title = ('gr1');

select rag.id from request_add_group rag 
inner join users u on rag.userid = u.id
where username = 'qwe1' and groupid = 2;

insert into date_visit (id, userid, datevisit) values(2, 3, (select now())), (3, 2, (select now()));

select username from groups g 
inner join users u on g.adminid = u.id 
where title = 'gr1';

--with dv as 
--(select datevisit from date_visit dv 
--inner join users u2 on dv.userid = u2.id
--where u2.username = 'asd2'
--)
select rag.id, u.username from request_add_group rag
inner join users u on rag.userid = u.id 
inner join groups g on rag.groupid  = g.id 
where title = 'gr1' 
and daterequest > (
select datevisit from date_visit dv 
inner join users u2 on dv.userid = u2.id
where u2.username = 'asd2'
);



--(
--select datevisit from date_visit dv 
--inner join users u2 on dv.userid = u2.id
--where u2.username = 'asd2'
--);


-- min day






