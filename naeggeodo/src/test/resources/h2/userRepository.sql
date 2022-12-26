
delete from quick_chat;
delete from users;

insert into quick_chat(quick_chat_id) values(hibernate_sequence.nextval);
insert into users(user_id,quick_chat_id) values('user0',select quick_chat_id from quick_chat where rownum() = 1)

