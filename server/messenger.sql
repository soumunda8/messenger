create table userinfo(
    id varchar(100) primary key,					-- 아이디
    pw varchar(500) not null,						-- 비밀번호
    usernm varchar(100) not null					-- 이름
);

create table chatroom(
    id serial primary key,                          -- 방 번호
    roomnm varchar(100),				            -- 방 이름
    resdate timestamp default current_timestamp,	-- 방 생성일
    status boolean default true					-- 채팅방 상태
);

create table enterchat(
    id serial primary key,							-- 자동증가
    roomid integer not null,					    -- 채팅방번호
    userid varchar(100) not null,					-- 참여자 id
    status boolean default true,					-- 참여 상태
    chatoffset integer default 0					-- 입장 시작 offsets [ default 0, chatting id값 가져옴]
);

create table chatting(
    id serial primary key,							-- 채팅 번호
    enterid integer not null,                       -- 채팅 참여 번호
    senddate timestamp default current_timestamp,	-- 채팅 보낸 일정
    message text not null,							-- 채팅 내용
    messagetype varchar(100) not null,				-- 채팅 타입 [ text - 메세지, upload - 파일, create, ... ]
    fid varchar(100)                                -- 채팅 파일 아이디
);

create table fileinfo(
    id varchar(100) primary key,					    -- 아이디
    saveFolder varchar(1000) not null,					-- 저장 폴더
    originName varchar(500) not null,					-- 원본 파일 이름
    saveName varchar(500) not null,					    -- 저장 파일 이름
    fileType varchar(100) not null,				        -- 파일 타입
    uploadDate timestamp default current_timestamp		-- 업로드 일정
);

create view chatMessage as (
    select
        c.id as chattingid,
        e.userid as senderid,
        m.usernm as sendernm,
        c.senddate as senddate,
        c.message as message,
        cr.id as roomid,
        c.fid as fid,
        cr.roomnm as roomnm,
        cr.status as roomStatus,
        c.messagetype as messagetype,
        e.chatoffset as chatoffset
    from
        chatting c, userinfo m, enterchat e, chatroom cr
    where
        e.userid = m.id
        and e.id = c.enterid
        and cr.id = e.roomid
    order by c.senddate
);

INSERT INTO userinfo values('0010', '0010pw', '테스터1');
INSERT INTO userinfo values('0020', '0020pw', '테스터2');
INSERT INTO userinfo values('0030', '0030pw', '테스터3');
INSERT INTO userinfo values('0040', '0040pw', '테스터4');
INSERT INTO userinfo values('0050', '0050pw', '테스터5');
INSERT INTO userinfo values('0060', '0060pw', '테스터6');
INSERT INTO userinfo values('0070', '0070pw', '테스터7');
INSERT INTO userinfo values('0080', '0080pw', '테스터8');
INSERT INTO userinfo values('0090', '0090pw', '테스터9');

INSERT INTO chatroom (roomnm, resdate) values('240328001', current_timestamp);

INSERT INTO enterchat values(default, 1, '0010', default, default);
INSERT INTO enterchat values(default, 1, '0020', default, default);