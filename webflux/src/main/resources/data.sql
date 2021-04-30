insert into users (username, password, role)
values ('user', '{noop}user', 'USER')
on conflict DO nothing;

insert into users (username, password, role)
values ('admin', '{noop}admin', 'ADMIN')
on conflict do nothing;