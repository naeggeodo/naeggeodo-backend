-- chatmain_id bigint not null,
--         address varchar(255),
--         bookmarks varchar(255),
--         bookmarks_date timestamp,
--         building_code varchar(255),
--         category varchar(255),
--         create_date timestamp,
--         end_date timestamp,
--         img_path varchar(255),
--         link varchar(255),
--         max_count integer not null,
--         order_time_type varchar(255),
--         place varchar(255),
--         state varchar(255),
--         title varchar(255),
--         user_id varchar(255),
--         primary key (chatmain_id)


-- create table users (
--                        user_id varchar(255) not null,
--                        address varchar(255),
--                        authority varchar(255),
--                        building_code varchar(255),
--                        email varchar(255),
--                        imgpath varchar(255),
--                        joindate timestamp,
--                        nickname varchar(255),
--                        password varchar(255),
--                        phone varchar(255),
--                        social_id varchar(255),
--                        token varchar(255),
--                        withdrawal_date timestamp,
--                        zonecode varchar(255),
--                        quick_chat_id bigint,
--                        primary key (user_id)
-- )

delete from tag;
delete from chat_user;
delete from chat_detail;
delete from chat_main;
delete from users;

insert into users(user_id) values('test');

insert into chat_main(chatmain_id,building_code,category,max_count,state) values(hibernate_sequence.nextval,'용산구','PIZZA',1,'CREATE');
insert into chat_main(chatmain_id,building_code,category,max_count,state) values(hibernate_sequence.nextval,'용산구','CHICKEN',1,'CREATE');

insert into chat_main(chatmain_id,state,max_count,bookmarks,user_id,building_code) values(hibernate_sequence.nextval,'END',1,'N','test','bookmarksTest');
insert into chat_main(chatmain_id,state,max_count,bookmarks,user_id,building_code) values(hibernate_sequence.nextval,'END',1,'N','test','deleteTest');
insert into chat_main(chatmain_id,state,max_count,bookmarks,user_id,building_code) values(hibernate_sequence.nextval,'CREATE',1,'N','test','tagTest');
insert into chat_main(chatmain_id,state,max_count,bookmarks,user_id,title) values(hibernate_sequence.nextval,'CREATE',1,'N','test','tag1Title');

insert into tag values(hibernate_sequence.nextval,'tag1',(select chatmain_id from chat_main where building_code = 'tagTest'));