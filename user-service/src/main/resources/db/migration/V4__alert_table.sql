create table alert(
id bigserial primary key,
user_id bigint not null references users(id),
sent boolean not null default false,
created_at timestamp not null default CURRENT_TIMESTAMP
);