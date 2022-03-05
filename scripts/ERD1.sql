
/* Drop Triggers */

DROP TRIGGER TRI_chatmain_chatmain_id;
DROP TRIGGER TRI_DealDetail_dealDetail_id;
DROP TRIGGER TRI_DealHistory_deal_id;
DROP TRIGGER TRI_NOTICE_notice_id;
DROP TRIGGER TRI_REPORT_QNA_RQ_id;



/* Drop Tables */

DROP TABLE chatdetail CASCADE CONSTRAINTS;
DROP TABLE DealDetail CASCADE CONSTRAINTS;
DROP TABLE DealHistory CASCADE CONSTRAINTS;
DROP TABLE chatmain CASCADE CONSTRAINTS;
DROP TABLE NOTICE CASCADE CONSTRAINTS;
DROP TABLE REPORT_QNA CASCADE CONSTRAINTS;
DROP TABLE Users CASCADE CONSTRAINTS;



/* Drop Sequences */

DROP SEQUENCE SEQ_chatmain_chatmain_id;
DROP SEQUENCE SEQ_DealDetail_dealDetail_id;
DROP SEQUENCE SEQ_DealHistory_deal_id;
DROP SEQUENCE SEQ_NOTICE_notice_id;
DROP SEQUENCE SEQ_REPORT_QNA_RQ_id;




/* Create Sequences */

CREATE SEQUENCE SEQ_chatmain_chatmain_id INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE SEQ_DealDetail_dealDetail_id INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE SEQ_DealHistory_deal_id INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE SEQ_NOTICE_notice_id INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE SEQ_REPORT_QNA_RQ_id INCREMENT BY 1 START WITH 1;



/* Create Tables */

CREATE TABLE chatdetail
(
	chatmain_id number(10) NOT NULL,
	-- 작성자
	user_id varchar2(100),
	chatdetail_contents clob NOT NULL,
	chatdetail_date timestamp NOT NULL,
	PRIMARY KEY (chatmain_id)
);


CREATE TABLE chatmain
(
	chatmain_id number(10) NOT NULL,
	-- 작성자
	user_id varchar2(100) NOT NULL,
	chatmain_title varchar2(100) NOT NULL,
	chatmain_create timestamp NOT NULL,
	chatmain_addr varchar2(100) NOT NULL,
	chatmain_imgpath varchar2(200),
	chatmain_link varchar2(300),
	-- check in ('진행','완료','실패')
	chatmain_state varchar2(10) NOT NULL,
	chatmain_place varchar2(100),
	chatmain_enddate date,
	chatmain_category varchar2(30) NOT NULL,
	PRIMARY KEY (chatmain_id)
);


CREATE TABLE DealDetail
(
	dealDetail_id number(10) NOT NULL,
	-- FK
	deal_id number(10) NOT NULL,
	-- 작성자
	user_id varchar2(100) NOT NULL,
	-- check in ('현금','카카오페이')
	dealdetail_type varchar2(15) NOT NULL,
	-- 방장이 돈받고 확인 누르면 sysdate
	dealdetail_date timestamp,
	PRIMARY KEY (dealDetail_id)
);


CREATE TABLE DealHistory
(
	-- FK
	deal_id number(10) NOT NULL,
	deal_chatId number(10) NOT NULL,
	-- 작성자
	user_id varchar2(100),
	-- 수령완료시 sysdate
	deal_compDate timestamp,
	deal_totalPrice number(10,0),
	PRIMARY KEY (deal_id)
);


CREATE TABLE NOTICE
(
	notice_id number(10) NOT NULL,
	-- 작성자
	user_id varchar2(100) NOT NULL,
	notice_title varchar2(100) NOT NULL,
	notice_contents varchar2(500),
	notice_date timestamp NOT NULL,
	notice_state varchar2(10) NOT NULL,
	notice_deldate timestamp,
	notice_imgpath varchar2(500),
	PRIMARY KEY (notice_id)
);


CREATE TABLE REPORT_QNA
(
	RQ_id number(10) NOT NULL,
	-- 작성자
	user_id varchar2(100) NOT NULL,
	-- 신고대상
	-- 
	user_reportid varchar2(100),
	RQ_title varchar2(100) NOT NULL,
	RQ_contents varchar2(500),
	RQ_date timestamp NOT NULL,
	RQ_type varchar2(10) NOT NULL,
	RQ_state varchar2(10),
	PRIMARY KEY (RQ_id)
);


CREATE TABLE Users
(
	-- 작성자
	user_id varchar2(100) NOT NULL,
	user_pw varchar2(100) NOT NULL,
	user_token varchar2(200),
	user_phone varchar2(30) NOT NULL,
	user_addr varchar2(200) NOT NULL,
	user_nickname varchar2(30) NOT NULL,
	user_joindate timestamp NOT NULL,
	user_withdrawalDate timestamp,
	-- check in('Y','N')
	user_TosCheck varchar2(2) NOT NULL,
	-- Check in ('user','admin','master')
	user_authority varchar2(15) NOT NULL,
	PRIMARY KEY (user_id)
);



/* Create Foreign Keys */

ALTER TABLE chatdetail
	ADD FOREIGN KEY (chatmain_id)
	REFERENCES chatmain (chatmain_id)
;


ALTER TABLE DealHistory
	ADD FOREIGN KEY (deal_chatId)
	REFERENCES chatmain (chatmain_id)
;


ALTER TABLE DealDetail
	ADD FOREIGN KEY (deal_id)
	REFERENCES DealHistory (deal_id)
;


ALTER TABLE chatdetail
	ADD FOREIGN KEY (user_id)
	REFERENCES Users (user_id)
;


ALTER TABLE chatmain
	ADD FOREIGN KEY (user_id)
	REFERENCES Users (user_id)
;


ALTER TABLE DealDetail
	ADD FOREIGN KEY (user_id)
	REFERENCES Users (user_id)
;


ALTER TABLE DealHistory
	ADD FOREIGN KEY (user_id)
	REFERENCES Users (user_id)
;


ALTER TABLE NOTICE
	ADD FOREIGN KEY (user_id)
	REFERENCES Users (user_id)
;


ALTER TABLE REPORT_QNA
	ADD FOREIGN KEY (user_id)
	REFERENCES Users (user_id)
;


ALTER TABLE REPORT_QNA
	ADD FOREIGN KEY (user_reportid)
	REFERENCES Users (user_id)
;



/* Create Triggers */

CREATE OR REPLACE TRIGGER TRI_chatmain_chatmain_id BEFORE INSERT ON chatmain
FOR EACH ROW
BEGIN
	SELECT SEQ_chatmain_chatmain_id.nextval
	INTO :new.chatmain_id
	FROM dual;
END;

/

CREATE OR REPLACE TRIGGER TRI_DealDetail_dealDetail_id BEFORE INSERT ON DealDetail
FOR EACH ROW
BEGIN
	SELECT SEQ_DealDetail_dealDetail_id.nextval
	INTO :new.dealDetail_id
	FROM dual;
END;

/

CREATE OR REPLACE TRIGGER TRI_DealHistory_deal_id BEFORE INSERT ON DealHistory
FOR EACH ROW
BEGIN
	SELECT SEQ_DealHistory_deal_id.nextval
	INTO :new.deal_id
	FROM dual;
END;

/

CREATE OR REPLACE TRIGGER TRI_NOTICE_notice_id BEFORE INSERT ON NOTICE
FOR EACH ROW
BEGIN
	SELECT SEQ_NOTICE_notice_id.nextval
	INTO :new.notice_id
	FROM dual;
END;

/

CREATE OR REPLACE TRIGGER TRI_REPORT_QNA_RQ_id BEFORE INSERT ON REPORT_QNA
FOR EACH ROW
BEGIN
	SELECT SEQ_REPORT_QNA_RQ_id.nextval
	INTO :new.RQ_id
	FROM dual;
END;

/




/* Comments */

COMMENT ON COLUMN chatdetail.user_id IS '작성자';
COMMENT ON COLUMN chatmain.user_id IS '작성자';
COMMENT ON COLUMN chatmain.chatmain_state IS 'check in (''진행'',''완료'',''실패'')';
COMMENT ON COLUMN DealDetail.deal_id IS 'FK';
COMMENT ON COLUMN DealDetail.user_id IS '작성자';
COMMENT ON COLUMN DealDetail.dealdetail_type IS 'check in (''현금'',''카카오페이'')';
COMMENT ON COLUMN DealDetail.dealdetail_date IS '방장이 돈받고 확인 누르면 sysdate';
COMMENT ON COLUMN DealHistory.deal_id IS 'FK';
COMMENT ON COLUMN DealHistory.user_id IS '작성자';
COMMENT ON COLUMN DealHistory.deal_compDate IS '수령완료시 sysdate';
COMMENT ON COLUMN NOTICE.user_id IS '작성자';
COMMENT ON COLUMN REPORT_QNA.user_id IS '작성자';
COMMENT ON COLUMN REPORT_QNA.user_reportid IS '신고대상
';
COMMENT ON COLUMN Users.user_id IS '작성자';
COMMENT ON COLUMN Users.user_TosCheck IS 'check in(''Y'',''N'')';
COMMENT ON COLUMN Users.user_authority IS 'Check in (''user'',''admin'',''master'')';



