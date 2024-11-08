insert into users (id, login, password, username) values(1, 'admin', 'admin', 'admin'),
(2, 'qwe', 'qwe', 'qwe1'), (3,'asd','asd', 'asd1');

insert into users (id, login, password, username) values(4, 'user', 'password', 'user1');

insert into roles (id, role) values(1, 'ADMIN'),
(2, 'USER');

insert into users_to_roles (userid, roleid) values(1,1),(2,2),(3,2);
insert into users_to_roles (userid, roleid) values(4,1);

--with prev as (
--select datevisit from date_visit dv where userid = 3
--)
update date_visit set  prevvisit = datevisit, datevisit = (select now()) where userid = 3;
select * from date_visit dv where userid = 3;




insert into date_visit (id, userid, datevisit, prevvisit) values(1,1, (select now()), (select now()));
insert into date_visit (id, userid, datevisit, prevvisit) values(2,2, (select now()), (select now())),
(3,3, (select now()), (select now())), (4,4, (select now()), (select now()));


insert into users_to_groups (userid, groupid) values ((select id from users where username = 'qwe1'),
(select id from "groups" where title = 'gr1'));


insert into request_add_group (id, userid, groupid) values
(2, 1, 2),
(3, 3, 2),
(4, 4, 2);

delete from request_add_group where groupid = (select id from "groups" where title = 'team1');


select * from date_visit ;


select * from date_visit dv ;

insert into date_visit (id, userid, datevisit) values(1, 1, (select now()));


select rag.id, username, title from request_add_group rag 
inner join users u on rag.userid = u.id 
inner join "groups" g ON rag.groupid = g.id 
where rag.groupid = 1;

delete from request_add_group 
where id in (select rag.id from request_add_group rag 
inner join users u on rag.userid = u.id 
inner join "groups" g ON rag.groupid = g.id 
where rag.groupid = 1;
-- id = полученному id запроса


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
where title = 'gr1'; 


--and daterequest > (
--select datevisit from date_visit dv 
--inner join users u2 on dv.userid = u2.id
--where u2.username = 'asd2'
--);



--(
--select datevisit from date_visit dv 
--inner join users u2 on dv.userid = u2.id
--where u2.username = 'asd2'
--);


-- min day

select username from request_add_group rag 
inner join users u on rag.userid = u.id 
inner join groups g on rag.groupid  = g.id 
where title = 'gr1';




