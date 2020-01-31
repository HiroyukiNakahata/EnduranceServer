create table if not exists project
(
    project_id serial not null
        constraint project_pk
            primary key,
    project_name text not null,
    client text not null
);

alter table project owner to postgres;

create table if not exists users
(
    user_id serial not null
        constraint user_pk
            primary key,
    first_name text not null,
    last_name text not null,
    mail_address text not null,
    password text not null
);

alter table users owner to postgres;

create unique index if not exists user_mail_address_uindex
    on users (mail_address);

create table if not exists minutes
(
    minutes_id serial not null
        constraint minutes_pk
            primary key,
    user_id integer not null
        constraint minutes_user_user_id_fk
            references users
            on delete cascade,
    project_id integer not null
        constraint minutes_project_project_id_fk
            references project
            on delete cascade,
    place text not null,
    theme text not null,
    summary text not null,
    body_text text not null,
    time_stamp timestamp with time zone not null
);

alter table minutes owner to postgres;

create table if not exists picture
(
    picture_id serial not null
        constraint picture_pk
            primary key,
    minutes_id integer not null
        constraint picture_minutes_minutes_id_fk
            references minutes
            on delete cascade,
    picture_path text not null,
    time_stamp timestamp with time zone not null
);

alter table picture owner to postgres;

create table if not exists attendee
(
    attendee_id serial not null
        constraint attendee_pk
            primary key,
    minutes_id integer not null
        constraint attendee_minutes_minutes_id_fk
            references minutes
            on delete cascade,
    attendee_name text not null,
    organization text not null
);

alter table attendee owner to postgres;

create table if not exists todo
(
    todo_id serial not null
        constraint todo_pk
            primary key,
    minutes_id integer not null
        constraint todo_minutes_minutes_id_fk
            references minutes
            on delete cascade,
    project_id integer not null
        constraint todo_project_project_id_fk
            references project
            on delete cascade,
    user_id integer not null
        constraint todo_user_user_id_fk
            references users
            on delete cascade,
    task_title text not null,
    task_body text not null,
    start_time_stamp timestamp with time zone not null,
    end_time_stamp timestamp with time zone not null,
    status boolean not null
);

alter table todo owner to postgres;

