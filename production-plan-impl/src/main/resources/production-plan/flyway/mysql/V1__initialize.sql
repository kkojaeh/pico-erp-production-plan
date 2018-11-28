create table oda_order_acceptance (
	id binary(16) not null,
	accepted_date datetime,
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	customer_id varchar(50),
	customer_name varchar(50),
	deleted bit,
	deleted_date datetime,
	delivery_address_detail varchar(50),
	delivery_address_postal_code varchar(10),
	delivery_address_street varchar(50),
	delivery_mobile_phone_number varchar(20),
	delivery_telephone_number varchar(20),
	due_date datetime,
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	manager_id varchar(50),
	manager_name varchar(50),
	name varchar(50),
	ordered_date datetime,
	project_id binary(16),
	project_name varchar(50),
	purchase_order_number varchar(20),
	purchaser_id varchar(50),
	purchaser_name varchar(50),
	receiver_id varchar(50),
	receiver_name varchar(50),
	status varchar(20),
	primary key (id)
) engine=InnoDB;

create table oda_order_acceptance_item (
	id binary(16) not null,
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	item_id binary(16),
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	quantity decimal(19,2),
	status varchar(20),
	unit_price decimal(19,2),
	order_acceptance_id binary(16),
	primary key (id)
) engine=InnoDB;

alter table oda_order_acceptance_item
	add constraint FKfd5e84brcpaiy3y4upu4q9vu2 foreign key (order_acceptance_id)
	references oda_order_acceptance (id);
