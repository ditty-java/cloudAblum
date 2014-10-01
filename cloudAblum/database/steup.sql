alter table b_ablum.CATEGORY
drop foreign key F_rel_user_categor;

alter table b_ablum.FRIENDS
drop foreign key F_rel_user_friends;

alter table b_ablum.PHOTO_ALBUM
drop foreign key F_rel_user_album;

alter table b_ablum.PHOTO_SHOP
drop foreign key F_rel_category_sho;

alter table b_ablum.PHOTO_SHOP
drop foreign key F_rel_shop_photo;

alter table b_ablum.SHARE_TO
drop foreign key F_rel_album_share;

alter table b_ablum.SHARE_TO
drop foreign key F_rel_friends_shar;

drop table b_ablum.CATEGORY;

drop table b_ablum.FRIENDS;

drop table b_ablum.PHOTO_ALBUM;

drop table b_ablum.PHOTO_SHOP;

drop table b_ablum.SHARE_TO;

drop table b_ablum.USER;

create table b_ablum.CATEGORY
(
CATEGORY_ID BIGINT  not null,
USER_ID  BIGINT,
CATEGORY_NAME  VARCHAR(255)  not null,
CATEGORY_DESC  VARCHAR(512),
CATEGORY_DATE  DATE,
constraint P_Identifier_1 primary key (CATEGORY_ID)
);

create table b_ablum.FRIENDS
(
USER_ID  BIGINT  not null,
FRIENDS_ID  BIGINT  not null,
constraint P_Identifier_1 primary key (FRIENDS_ID)
);

create table b_ablum.PHOTO_ALBUM
(
PHOTO_ID BIGINT  not null,
USER_ID  BIGINT,
PHOTO_NAME  VARCHAR(255)  not null,
PHOTO_DESC  VARCHAR(512),
P_DATE_TIME DATE,
P_CONTENT BLOB,
P_URL VARCHAR(2048),
constraint P_Identifier_1 primary key (PHOTO_ID)
);

create table b_ablum.PHOTO_SHOP
(
PHOTO_ID BIGINT  not null,
CATEGORY_ID BIGINT  not null,
constraint P_Identifier_1 primary key (PHOTO_ID, CATEGORY_ID)
);

create table b_ablum.SHARE_TO
(
PHOTO_ID BIGINT  not null,
FRIENDS_ID  BIGINT  not null,
constraint P_Identifier_1 primary key (PHOTO_ID, FRIENDS_ID)
);

create table b_ablum.USER
(
USER_NAME VARCHAR(55)not null,
USER_EMAIL  VARCHAR(255)  not null,
USER_PASSWORD  VARCHAR(64),
USER_MOBILE VARCHAR(32),
USER_HOME_PHONE VARCHAR(32),
USER_QQ  VARCHAR(32),
USER_NOTE VARCHAR(255),
USER_SELF_DESCRIPTION VARCHAR(512),
USER_ID  BIGINT  not null,
USER_IMG_URL VARCHAR(255),
constraint P_Identifier_1 primary key (USER_ID)
);

alter table b_ablum.CATEGORY
add constraint F_rel_user_categor foreign key (USER_ID)
references b_ablum.USER (USER_ID)
on delete restrict on update restrict;

alter table b_ablum.FRIENDS
add constraint F_rel_user_friends foreign key (USER_ID)
references b_ablum.USER (USER_ID)
on delete restrict on update restrict;

alter table b_ablum.PHOTO_ALBUM
add constraint F_rel_user_album foreign key (USER_ID)
references b_ablum.USER (USER_ID)
on delete restrict on update restrict;

alter table b_ablum.PHOTO_SHOP
add constraint F_rel_category_sho foreign key (CATEGORY_ID)
references b_ablum.CATEGORY (CATEGORY_ID)
on delete restrict on update restrict;

alter table b_ablum.PHOTO_SHOP
add constraint F_rel_shop_photo foreign key (PHOTO_ID)
references b_ablum.PHOTO_ALBUM (PHOTO_ID)
on delete restrict on update restrict;

alter table b_ablum.SHARE_TO
add constraint F_rel_album_share foreign key (PHOTO_ID)
references b_ablum.PHOTO_ALBUM (PHOTO_ID)
on delete restrict on update restrict;

alter table b_ablum.SHARE_TO
add constraint F_rel_friends_shar foreign key (FRIENDS_ID)
references b_ablum.FRIENDS (FRIENDS_ID)
on delete restrict on update restrict;