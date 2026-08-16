create table device(
id bigserial primary key,
name varchar(255),
type varchar(255),
location varchar(255),
user_id bigint not null references users(id) on delete cascade
);

create index idx_device_user_id on device(user_id);