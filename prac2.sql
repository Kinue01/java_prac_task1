create table if not exists tb_msgtypes
(
	type_id smallserial primary key,
	type_name varchar(10) not null
);

insert into tb_msgtypes (type_name) values ('МР-231'), ('МР-231-3');

create table if not exists tb_target_type
(
	type_id smallserial primary key,
	type_name varchar(20) not null
);

insert into tb_target_type (type_name) values ('Поверхность'), ('Воздух'), ('Неизвестно');

create table if not exists tb_target_status 
(
	status_id smallserial primary key,
	status_name varchar(50) not null
);

insert into tb_target_status (status_name) values ('Сопровождавшаяся, но потерянная (L)'), ('Недостоверные данные (Q)'), ('Сопровождается (T)');

create table if not exists tb_iff
(
	iff_id smallserial primary key,
	iff_name varchar(20) not null
);

insert into tb_iff (iff_name) values ('Своя (b)'), ('Чужая (p)'), ('Неопределённая (d)');

create table if not exists tb_distance_unit
(
	unit_id smallserial primary key,
	unit_name varchar(15) not null
);

insert into tb_distance_unit (unit_name) values ('Километры (K)'), ('Мили (N)');

create table if not exists tb_display_orientation
(
	orientation_id smallserial primary key,
	orientation_name varchar(30) not null
);

insert into tb_display_orientation (orientation_name) values ('Курс стабилизированный (C)'), ('По курсу (H)'), ('По истинному меридиану (N)');

create table if not exists tb_working_mode
(
	mode_id smallserial primary key,
	mode_name varchar(40) not null
);

insert into tb_working_mode (mode_name) values ('Подготовка или режим КОНТРОЛЬ (S)'), ('Работа (P)');

create table if not exists tb_ttm 
(
	msgRecTime timestamp primary key,
	msgType int not null,
	msgTime bigint not null,
	targettNum smallint not null,
	distance decimal not null,
	bearing decimal not null,
	course decimal not null,
	speed decimal not null,
	target_type int default 3,
	status int not null,
	iff int not null,
	foreign key (msgType) references tb_msgtypes (type_id),
	foreign key (target_type) references tb_target_type (type_id),
	foreign key (status) references tb_target_status (status_id),
	foreign key (iff) references tb_iff (iff_id)
);

create table if not exists tb_vhw
(
	msgRecTime timestamp primary key,
	course decimal not null,
	courseAttr char(1) default 'T',
	speed decimal not null,
	speedUnit char(1) default 'N'
);

create table if not exists tb_rsd
(
	msgRecTime timestamp primary key,
	initialDistance decimal not null,
	initialBearing decimal not null,
	movingCircleOfDistance decimal not null,
	bearing decimal not null,
	distanceFromShip decimal not null,
	bearing2 decimal not null,
	distanceScale decimal not null,
	distanceUnit int not null,
	displayOrientation int not null,
	workingMode int not null,
	foreign key (distanceUnit) references tb_distance_unit (unit_id),
	foreign key (displayOrientation) references tb_display_orientation (orientation_id),
	foreign key (workingMode) references tb_working_mode (mode_id)
);