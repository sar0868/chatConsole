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
    title varchar(100),
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


insert into roles (id, role) values(1, 'ADMIN'),
(2, 'MANAGER'),
(3, 'USER');

insert into users (id, login, password, username) values(1, 'admin', 'admin', 'admin'),
(2, 'qwe', 'qwe', 'qwe1'), (3,'asd','asd', 'asd1');

insert into users_to_roles (userID, roleID) values(1,1),(2,2),(3,2);

select * from users u ;
select * from roles r ;
select * from department d ;

insert into roles (id, role) values(3, 'MANADGER');






