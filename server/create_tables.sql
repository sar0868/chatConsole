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