ALTER TABLE prp_production_plan_detail CHANGE progress_company_id actor_id varchar(50) NULL;

ALTER TABLE prp_production_plan_detail ADD receiver_id varchar(50) null;
