create table users (
  id                    bigserial,
  locked                int default 3, -- количество неудачных попыток входа
  name                  varchar(30) not null unique,
  password              varchar(80) not null,
  primary key (id)
);

create table roles (
  id                    serial,
  name                  varchar(50) not null,
  primary key (id)
);

CREATE TABLE users_roles (
  user_id               bigint not null,
  role_id               int not null,
  primary key (user_id, role_id),
  foreign key (user_id) references users (id),
  foreign key (role_id) references roles (id)
);

insert into roles (name)
values
('ROLE_USER'), ('ROLE_ADMIN');

insert into users (name, password)
values
('user', '$2a$10$4f3v1C83Jqb5BYd41Lo59etf/WFh34xLCF2lK.5U0YJjgfL29nmdO'),
('admin', '$2a$10$4f3v1C83Jqb5BYd41Lo59etf/WFh34xLCF2lK.5U0YJjgfL29nmdO');
-- password = password

insert into users_roles (user_id, role_id)
values
(1, 1),
(2, 2);