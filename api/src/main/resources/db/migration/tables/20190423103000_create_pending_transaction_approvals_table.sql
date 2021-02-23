create table pending_transaction_approvals(
	id                  		    serial primary key,
	pending_transaction_id          integer NOT NULL references pending_transactions (id),
	action                          varchar(50) NOT NULL,
	approver_name                   varchar(100) NOT NULL,
	approval_date_time              timestamp with time zone NOT NULL DEFAULT now(),
	approval_level                  integer NOT NULL,
	approval_notes                  varchar(255),
    created_at                      timestamp with time zone NOT NULL DEFAULT now(),
    created_by                      varchar(50) NOT NULL,
    updated_at                      timestamp with time zone,
    updated_by                      varchar(50)
);
