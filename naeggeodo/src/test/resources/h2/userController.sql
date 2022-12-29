delete from deal;
delete from chat_user;
delete from chat_main;
delete from users;
delete from quick_chat;

insert into quick_chat(quick_chat_id) values(hibernate_sequence.nextval);

insert into users(user_id,quick_chat_id,nickname)
values('user0',select quick_chat_id from quick_chat where rownum() = 1,'도봉산-왕주먹');

insert into chat_main(chatmain_id,category,state,building_code,title,user_id,create_date)
values(hibernate_sequence.nextval,'CHINESE','CREATE','111','1st','user0','2022-11-05T17:50:39.095');

insert into chat_user(chat_user_id,user_id,chatmain_id,ban_state)
values(hibernate_sequence.nextval,'user0',
          select chatmain_id
       from chat_main
       where title = '1st',
       'ALLOWED'
      );

insert into deal(deal_id,user_id,chatmain_id)
values (
        hibernate_sequence.nextval,
        'user0',
        select chatmain_id
        from chat_main
        where title = '1st'
);