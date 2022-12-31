-- create table chat_detail (
--                              chatdetail_id bigint not null,
--                              contents clob,
--                              reg_date timestamp,
--                              type varchar(255),
--                              chatmain_id bigint,
--                              user_id varchar(255),
--                              primary key (chatdetail_id)
-- )


delete from chat_detail;
delete from chat_user;
delete from chat_main;
delete from users;

insert into users(user_id)
values('user0');
insert into users(user_id)
values('user1');

insert into chat_main(chatmain_id,title,user_id,state)
values(hibernate_sequence.nextval,'test','user0','CREATE');

insert into chat_detail(chatdetail_id,contents,reg_date,type,chatmain_id,user_id)
values(hibernate_sequence.nextval,'1','2022-11-06T17:50:39.095','TEXT',(select chatmain_id from chat_main where title = 'test'),'user0');
insert into chat_detail(chatdetail_id,contents,reg_date,type,chatmain_id,user_id)
values(hibernate_sequence.nextval,'2','2022-11-06T17:55:40.095','TEXT',(select chatmain_id from chat_main where title = 'test'),'user0');
insert into chat_detail(chatdetail_id,contents,reg_date,type,chatmain_id,user_id)
values(hibernate_sequence.nextval,'마지막','2022-11-06T17:56:39.095','TEXT',(select chatmain_id from chat_main where title = 'test'),'user0');


insert into chat_user(chat_user_id,user_id,enter_date,chatmain_id,ban_state)
values(hibernate_sequence.nextval,'user0','2022-11-06T17:50:39.095',(select chatmain_id from chat_main where title = 'test'),'ALLOWED');
insert into chat_user(chat_user_id,user_id,enter_date,chatmain_id,ban_state)
values(hibernate_sequence.nextval,'user1','2022-11-06T17:55:39.095',(select chatmain_id from chat_main where title = 'test'),'ALLOWED');

