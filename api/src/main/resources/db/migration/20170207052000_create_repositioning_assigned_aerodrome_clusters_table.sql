create table repositioning_assigned_aerodrome_clusters (
        id                                 serial primary key,
        repositioning_aerodrome_cluster_id int not null references repositioning_aerodrome_clusters (id),
        aerodrome_identifier               varchar(100) not null,                
        created_at                         timestamp not null default now(),
        created_by                         varchar(50) not null,
        updated_at                         timestamp,
        updated_by                         varchar(50)
)
