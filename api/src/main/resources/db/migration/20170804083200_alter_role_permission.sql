delete from role_permission where permission_id = (select id from permissions where name = 'wake_turbulence_category_view');
delete from permissions where name = 'wake_turbulence_category_view';
update permissions set name = 'route_cache_view' where name = 'manage_route_cache_view';
update permissions set name = 'route_cache_modify' where name = 'manage_route_cache_modify';
