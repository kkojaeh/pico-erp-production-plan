create table prp_production_plan (
	id binary(16) not null,
	canceled_date datetime,
	code varchar(20),
	completed_date datetime,
	completed_quantity decimal(19,2),
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	determined_date datetime,
	due_date datetime,
	item_id binary(16),
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	progress_rate decimal(7,5),
	project_id binary(16),
	quantity decimal(19,2),
	spare_quantity decimal(19,2),
	status varchar(20),
	primary key (id)
) engine=InnoDB;

create table prp_production_plan_detail (
	id binary(16) not null,
	canceled_date datetime,
	completed_date datetime,
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	determined_date datetime,
	end_date datetime,
	group_id binary(16),
	item_id binary(16),
	item_spec_id binary(16),
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	dependency_order integer,
	plan_id binary(16),
	process_id binary(16),
	process_preparation_id binary(16),
	progress_company_id varchar(50),
	progress_type varchar(20),
	progressed_quantity decimal(19,2),
	quantity decimal(19,2),
	spare_quantity decimal(19,2),
	start_date datetime,
	status varchar(20),
	primary key (id)
) engine=InnoDB;

create table prp_production_plan_detail_dependency (
	plan_detail_id binary(16) not null,
	plan_detail_dependency_id binary(50) not null,
	primary key (plan_detail_id,plan_detail_dependency_id)
) engine=InnoDB;

create index IDX4u38vicd6x51xqmoya88xyexh
	on prp_production_plan_detail (plan_id);

alter table prp_production_plan_detail_dependency
	add constraint FKm1ku03g9i8gw2te2vw2t6kp3h foreign key (plan_detail_id)
	references prp_production_plan_detail (id);
