
delete from users;
delete from chat_main;
delete from tag;
delete from chat_user;

insert into users(user_id) values('user0');
insert into users(user_id) values('user1');
insert into users(user_id) values('user2');
insert into users(user_id) values('bookmarker');




insert into chat_main(chatmain_id,category,state,building_code,title,user_id,create_date)
values(hibernate_sequence.nextval,'CHINESE','CREATE','111','1st','user0','2022-11-05T17:50:39.095');

insert into chat_main(chatmain_id,category,state,building_code,title,user_id,create_date)
values(hibernate_sequence.nextval,'CHINESE','CREATE','111','2nd','user1','2022-11-06T17:50:39.095');

insert into chat_main(chatmain_id,category,state,building_code,title,user_id,create_date)
values(hibernate_sequence.nextval,'CHINESE','FULL','111','3rd','user2','2022-11-07T17:50:39.095');

insert into chat_main(chatmain_id,category,state,building_code,title,user_id,create_date)
values(hibernate_sequence.nextval,'PIZZA','CREATE','112','4th','user2','2022-11-08T17:50:39.095');

// 11 row
insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date)
values(hibernate_sequence.nextval,'bookmarker','Y','2022-11-05T17:50:39.095','2022-11-05T17:50:39.095');

insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date)
values(hibernate_sequence.nextval,'bookmarker','Y','2022-11-06T17:50:39.095','2022-11-05T17:50:39.095');

insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date)
values(hibernate_sequence.nextval,'bookmarker','Y','2022-11-07T17:50:39.095','2022-11-05T17:50:39.095');

insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date)
values(hibernate_sequence.nextval,'bookmarker','Y','2022-11-08T17:50:39.095','2022-11-05T17:50:39.095');

insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date)
values(hibernate_sequence.nextval,'bookmarker','Y','2022-11-09T17:50:39.095','2022-11-05T17:50:39.095');

insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date)
values(hibernate_sequence.nextval,'bookmarker','Y','2022-11-10T17:50:39.095','2022-11-05T17:50:39.095');

insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date)
values(hibernate_sequence.nextval,'bookmarker','Y','2022-11-11T17:50:39.095','2022-11-05T17:50:39.095');

insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date)
values(hibernate_sequence.nextval,'bookmarker','Y','2022-11-12T17:50:39.095','2022-11-05T17:50:39.095');

insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date)
values(hibernate_sequence.nextval,'bookmarker','Y','2022-11-13T17:50:39.095','2022-11-05T17:50:39.095');

insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date)
values(hibernate_sequence.nextval,'bookmarker','Y','2022-11-14T17:50:39.095','2022-11-05T17:50:39.095');

insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date)
values(hibernate_sequence.nextval,'bookmarker','Y','2022-11-15T17:50:39.095','2022-11-05T17:50:39.095');


insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date,state)
values(hibernate_sequence.nextval,'bookmarker','N','2022-11-05T17:50:39.095','2022-11-05T17:50:39.095','END');
insert into chat_main(chatmain_id,user_id,bookmarks,bookmarks_date,create_date,state)
values(hibernate_sequence.nextval,'bookmarker','N','2022-11-06T17:50:39.095','2022-11-06T17:50:39.095','END');





insert into tag
values(hibernate_sequence.nextval,'tag0',
            select chatmain_id
            from chat_main
            where title = '1st'
       ) ;
insert into tag
values(hibernate_sequence.nextval,'tag1',
           select chatmain_id
           from chat_main
           where title = '1st'
       );
insert into tag
values(hibernate_sequence.nextval,'tag2',
           select chatmain_id
           from chat_main
           where title = '1st'
       );





insert into tag
values(hibernate_sequence.nextval,'tag1',
           select chatmain_id
           from chat_main
           where title = '2nd'
       );
insert into tag
values(hibernate_sequence.nextval,'tag2',
           select chatmain_id
           from chat_main
           where title = '2nd'
       );
insert into tag
values(hibernate_sequence.nextval,'tag3',
           select chatmain_id
           from chat_main
           where title = '2nd'
       );





insert into tag
values(hibernate_sequence.nextval,'tag2',
           select chatmain_id
           from chat_main
           where title = '3rd'
       );
insert into tag
values(hibernate_sequence.nextval,'tag3',
           select chatmain_id
           from chat_main
           where title = '3rd'
       );
insert into tag
values(hibernate_sequence.nextval,'tag4',
           select chatmain_id
           from chat_main
           where title = '3rd'
       );





insert into tag
values(hibernate_sequence.nextval,'tag0',
           select chatmain_id
           from chat_main
           where title = '4th'
       );
insert into tag
values(hibernate_sequence.nextval,'tag1',
           select chatmain_id
           from chat_main
           where title = '4th'
       );
insert into tag
values(hibernate_sequence.nextval,'tag2',
           select chatmain_id
           from chat_main
           where title = '4th'
       );
insert into tag
values(hibernate_sequence.nextval,'tag3',
           select chatmain_id
           from chat_main
           where title = '4th'
       );
insert into tag
values(hibernate_sequence.nextval,'tag4',
           select chatmain_id
           from chat_main
           where title = '4th'
       );




insert into chat_user(chat_user_id,user_id,chatmain_id,ban_state)
values(hibernate_sequence.nextval,'user0',
            select chatmain_id
            from chat_main
            where title = '1st',
       'ALLOWED'
       );
insert into chat_user(chat_user_id,user_id,chatmain_id,ban_state)
values(hibernate_sequence.nextval,'user1',
           select chatmain_id
           from chat_main
           where title = '2nd',
       'ALLOWED'
       );
insert into chat_user(chat_user_id,user_id,chatmain_id,ban_state)
values(hibernate_sequence.nextval,'user2',
           select chatmain_id
           from chat_main
           where title = '3rd'
           ,
       'ALLOWED'
       );

