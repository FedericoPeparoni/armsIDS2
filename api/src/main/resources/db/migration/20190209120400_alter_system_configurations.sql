-- for daily penalties calculations, sys config "Apply interest penalty on" set as "Daily" should be used
DELETE FROM system_configurations WHERE item_name = 'Calculate overdue invoice penalties daily';
