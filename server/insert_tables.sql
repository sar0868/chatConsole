insert into users (id, login, password, username) values(1, 'admin', 'admin', 'admin'),
(2, 'qwe', 'qwe', 'qwe1'), (3,'asd','asd', 'asd1');

insert into users (id, login, password, username) values(4, 'user', 'password', 'user1');

insert into roles (id, role) values(1, 'ADMIN'),
(2, 'USER');

insert into users_to_roles (userid, roleid) values(1,1),(2,2),(3,2);
insert into users_to_roles (userid, roleid) values(4,1);





insert into users_to_groups (userid, groupid) values ((select id from users where username = 'qwe1'),
(select id from "groups" where title = 'gr1'));


insert into request_add_group (id, userid, groupid) values
(2, 1, 2),
(3, 3, 2),
(4, 4, 2);

delete from request_add_group where groupid = (select id from "groups" where title = 'team1');

insert into messages (id, userid, groupid, datemsg, msg) values(1,3,1,'Fri Nov 08 14:48:57 MSK 2024', 'text msg');
insert into messages (id, userid, groupid, datemsg, msg) values(2,3,1, (select now()), 'text msg2');


select rag.id, username, title from request_add_group rag 
inner join users u on rag.userid = u.id 
inner join "groups" g ON rag.groupid = g.id 
where rag.groupid = 1;

delete from request_add_group 
where id in (select rag.id from request_add_group rag 
inner join users u on rag.userid = u.id 
inner join "groups" g ON rag.groupid = g.id 
where rag.groupid = 1;

insert into groups (id, title, adminid) values(3, 'group', 1);
select * from "groups" g ;
delete from "groups" where title = 'group';
-- id = полученному id запроса

select utg.userid from users_to_groups utg;

select username from users_to_groups utg 
inner join users u on utg.userid = u.id 
inner join "groups" g on utg.groupid = g.id 
where g.title = 'gr1';


select * from messages ;
insert into messages(id, userid, groupid, msg) values(
3, 1, 1, 'gr1-asd: hello. time: Sat Nov 09 12:38:35 MSK 2024'
);

insert into messages(id, userid, groupid, msg) values(
1, (select id from users where username = 'qwe1'), (select id from "groups" where title = 'gr1'), 
'gr1-asd: hello. time: Sat Nov 09 12:38:35 MSK 2024');

select m.id, msg from messages m 
inner join users u on m.userid = u.id
inner join groups g on m.groupid = g.id 
where u.username = 'qwe1' and g.title = 'gr1' 
order by m.id;

delete from messages where id in (2, 4);

delete from users_to_groups where userid = 
(select userid from users_to_groups ug 
inner join users u on ug.userid = u.id
inner join "groups" g on ug.groupid = g.id
where u.username = 'admin' and g.title = 'gr1');

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



select * from users_to_groups utg ;
select count(userid) from users_to_groups utg 
where utg.userid = 2 and groupid = 3;

select id from "groups" g where title = 'gr2';


select * from request_add_group rag ;
insert into request_add_group (id, userid, groupid) values(2, 2, 2);

select id from groups where title = ('gr1');

select rag.id from request_add_group rag 
inner join users u on rag.userid = u.id
where username = 'qwe1' and groupid = 2;


select username from groups g 
inner join users u on g.adminid = u.id 
where title = 'gr1';

select rag.id, u.username from request_add_group rag
inner join users u on rag.userid = u.id 
inner join groups g on rag.groupid  = g.id 
where title = 'gr1'; 






-- min day

select username from request_add_group rag 
inner join users u on rag.userid = u.id 
inner join groups g on rag.groupid  = g.id 
where title = 'gr1';

UPDATE users SET "password" = 'new'
where username = 'user1';
delete from users where username = 'user';
select * from users u ;


