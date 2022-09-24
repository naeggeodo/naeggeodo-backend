create sequence hibernate_sequence start with 1 increment by 1;

create table chat_detail (
                             chatdetail_id bigint not null,
                             contents clob,
                             reg_date timestamp,
                             type varchar(255),
                             chatmain_id bigint,
                             user_id varchar(255),
                             primary key (chatdetail_id)
);

create table chat_main (
                           chatmain_id bigint not null,
                           address varchar(255),
                           bookmarks varchar(255),
                           bookmarks_date timestamp,
                           building_code varchar(255),
                           category varchar(255),
                           create_date timestamp,
                           end_date timestamp,
                           img_path varchar(255),
                           link varchar(255),
                           max_count integer not null,
                           order_time_type varchar(255),
                           place varchar(255),
                           state varchar(255),
                           title varchar(255),
                           user_id varchar(255),
                           primary key (chatmain_id)
);

create table chat_user (
                           chat_user_id bigint not null,
                           ban_state varchar(255),
                           enter_date timestamp,
                           session_id varchar(255),
                           state varchar(255),
                           chatmain_id bigint,
                           user_id varchar(255),
                           primary key (chat_user_id)
);

create table deal (
                      deal_id bigint not null,
                      reg_date timestamp,
                      chatmain_id bigint,
                      user_id varchar(255),
                      primary key (deal_id)
);
create table likes (
                       id bigint not null,
                       count integer not null,
                       primary key (id)
);

create table quick_chat (
                            quick_chat_id bigint not null,
                            msg1 varchar(255) default '반갑습니다 *^ㅡ^*',
                            msg2 varchar(255) default '주문 완료했습니다! 송금 부탁드려요 *^ㅡ^*',
                            msg3 varchar(255) default '음식이 도착했어요!',
                            msg4 varchar(255) default '맛있게 드세요 *^ㅡ^*',
                            msg5 varchar(255) default '주문내역 확인해주세요!',
                            primary key (quick_chat_id)
);

create table report (
                        report_id bigint not null,
                        contents varchar(255),
                        reg_date timestamp,
                        type varchar(255),
                        user_id varchar(255),
                        primary key (report_id)
);

create table tag (
                     tag_id bigint not null,
                     name varchar(255),
                     chatmain_id bigint,
                     primary key (tag_id)
);

create table users (
                       user_id varchar(255) not null,
                       address varchar(255),
                       authority varchar(255),
                       building_code varchar(255),
                       email varchar(255),
                       imgpath varchar(255),
                       joindate timestamp,
                       nickname varchar(255),
                       password varchar(255),
                       phone varchar(255),
                       social_id varchar(255),
                       token varchar(255),
                       withdrawal_date timestamp,
                       zonecode varchar(255),
                       quick_chat_id bigint,
                       primary key (user_id)
);

alter table chat_detail
    add constraint FK8t0qlopjqkwwsrhdkyv7sg20m
        foreign key (chatmain_id)
            references chat_main;

alter table chat_detail
    add constraint FK4qfnktfxavm0gfw63wmkxmvyj
        foreign key (user_id)
            references users;

alter table chat_main
    add constraint FKhbdchqkdb3jiephpf1d66ptyg
        foreign key (user_id)
            references users;

alter table chat_user
    add constraint FK5r5169yivti3y588pjjfalj9c
        foreign key (chatmain_id)
            references chat_main;

alter table chat_user
    add constraint FKb1gw4q5ahnprgk3f47gj5o3nw
        foreign key (user_id)
            references users;

alter table deal
    add constraint FK4peu8jja2t8uo4qf15gaswi5n
        foreign key (chatmain_id)
            references chat_main;

alter table deal
    add constraint FKq15mbt3ui6hpeiqdthaw1w56e
        foreign key (user_id)
            references users;

alter table report
    add constraint FKq50wsn94sc3mi90gtidk0k34a
        foreign key (user_id)
            references users;

alter table tag
    add constraint FKcdui37goen12drngwek41sckw
        foreign key (chatmain_id)
            references chat_main;

alter table users
    add constraint FK73u9duqx08a8qxcb3y9xrcy4m
        foreign key (quick_chat_id)
            references quick_chat;


-- Users
insert into users(user_id) values ('kmh1');
insert into users(user_id) values ('kmh2');


-- ChatMain
insert into chat_main values (hibernate_sequence.nextval,'address','Y',SYSDATE,'123456789','PIZZA',SYSDATE,null,'c://',null,2,'QUICK',null,'CREATE','hello','kmh1'  );
insert into chat_main values (hibernate_sequence.nextval,'address','Y',SYSDATE,'123456789','CHICKEN',SYSDATE,null,'c://',null,2,'QUICK',null,'CREATE','hello','kmh1'  );
insert into chat_main values (hibernate_sequence.nextval,'address','Y',SYSDATE,'123456789','PIZZA',SYSDATE,null,'c://',null,3,'QUICK',null,'CREATE','hello','kmh1'  );
insert into chat_main values (hibernate_sequence.nextval,'address','Y',SYSDATE,'123456789','CHICKEN',SYSDATE,null,'c://',null,3,'QUICK',null,'CREATE','hello','kmh1'  );

-- ChatDetail , ChatUser
insert into chat_user values (hibernate_sequence.nextval,'ALLOWED',SYSDATE,'1STSESSION','Y',1,'kmh1');
INSERT into chat_detail values (hibernate_sequence.nextval,'1st',SYSDATE,'TEXT',1,'kmh1');
insert into chat_user values (hibernate_sequence.nextval,'ALLOWED',SYSDATE,'2NDSESSION','Y',1,'kmh2');
INSERT into chat_detail values (hibernate_sequence.nextval,'2nd',SYSDATE,'TEXT',1,'kmh1');
INSERT into chat_detail values (hibernate_sequence.nextval,'3rd',SYSDATE,'TEXT',1,'kmh1');
INSERT into chat_detail values (hibernate_sequence.nextval,'4th',SYSDATE,'TEXT',2,'kmh1');
INSERT into chat_detail values (hibernate_sequence.nextval,'5th',SYSDATE,'TEXT',2,'kmh1');
