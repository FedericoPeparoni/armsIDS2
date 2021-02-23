/**
    Data Analysis and Statistics - Air Traffic

    Note: this queries the view `charge_view`

    Required parameters
    ===================
    @param temporal_group   week | month | quarter | year
    @param start_date       ex. 2013-01-01
    @param end_date         ex. 2018-01-1

    Optional Parameters
    ===================
    Where In (comma separated)
    Note: these are all pluralized to differentiate from the `group by`
    --------
    @param aircraft_types
    @param aerodromes       departure | destination aerodrome
    @param flight_rules
    @param flight_types
    @param flight_scopes
    @param routes
    @param mtow_categories
    @param accounts

    Group by
    Note: these are all singular to differentiate from the `where in`
    -------
    @param bill_aerodrome
    @param route
    @param movement_type
    @param flight_type
    @param flight_scope
    @param aircraft_type
    @param flight_level
    @param flight_rule
    @param mtow_category
    @param account

    Order by
    -------
    @param sort     column to sort by `&sort=aircraft_type

 */

select * from (
                  select
                      distinct

                      case when :temporal_group = 'week'
                          then
                              extract(year from date_of_flight) || '-' || extract(week from date_of_flight)
                      when :temporal_group = 'month'
                          then
                              extract(year from date_of_flight) || '-' || extract(month from date_of_flight)
                      when :temporal_group = 'quarter'
                          then
                              extract(year from date_of_flight) || '-' || extract(quarter from date_of_flight)
                      when :temporal_group = 'year'
                          then
                              extract(year from date_of_flight) || '-' || extract(year from date_of_flight)
                      end
                                                           as date,
                      max(name)                            as billing_centre,
                      max(account_name)                    as account,
                      max(bill_aerodrome)                  as bill_aerodrome,
                      max(route)                           as route,
                      max(movement_type)                   as movement_type,
                      max(flight_type)                     as flight_type,
                      max(flight_scope)                    as flight_scope,
                      max(aircraft_type)                   as aircraft_type,
                      max(flight_level)                    as flight_level,
                      max(flight_rules)                    as flight_rules,
                      max(mtow_category)                   as mtow_category,

                      count(*)                             as count,
                      sum(passengers_chargeable_domestic)  as sum_passengers_chargeable_domestic,
                      sum(passengers_chargeable_intern)    as sum_passengers_chargeable_intern,
                      sum(enroute_charges)                 as sum_enroute_charges,
                      sum(approach_charges)                as sum_approach_charges,
                      sum(aerodrome_charges)               as sum_aerodrome_charges,
                      sum(late_arrival_charges)            as sum_late_arrival_charges,
                      sum(late_departure_charges)          as sum_late_departure_charges,
                      sum(domestic_passenger_charges)      as sum_domestic_passenger_charges,
                      sum(international_passenger_charges) as sum_international_passenger_charges,
                      sum(parking_charges)                 as sum_parking_charges,
                      sum(total_charges)                   as sum_total_charges

                  from charge_view

                  where
                      date_of_flight between cast(:start_date as date) and cast(:end_date as date)

                      and case
                          when
                              :accounts is not null
                              then
                                  charge_view.account_name = any (string_to_array(:accounts, ','))

                          else
                              TRUE
                          end

                      and case
                          when
                              :aircraft_types is not null
                              then
                                  charge_view.aircraft_type = any (string_to_array(:aircraft_types, ','))
                          else
                              TRUE
                          end

                      and case
                          when
                              :aerodromes is not null
                              then
                                  charge_view.bill_aerodrome = any (string_to_array(:aerodromes, ','))
                          else
                              TRUE
                          end

                      and case
                          when
                              :flight_rules is not null
                              then
                                  charge_view.flight_rules = any (string_to_array(:flight_rules, ','))
                          else
                              TRUE
                          end

                      and case
                          when
                              :mtow_categories is not null
                              then
                                   cast (charge_view.mtow_category as float) = any( cast (string_to_array(:mtow_categories,',') as float[]))
                          else
                              TRUE
                          end

                      and case
                          when
                              :flight_types is not null
                              then
                                  charge_view.flight_type = any (string_to_array(:flight_types, ','))
                          else
                              TRUE
                          end

                      and case
                          when
                              :flight_scopes is not null
                              then
                                  charge_view.flight_scope = any (string_to_array(:flight_scopes, ','))
                          else
                              TRUE
                          end

                      and case
                          when
                              :routes is not null
                              then
                                  charge_view.route = any (string_to_array(:routes, ','))
                          else
                              TRUE
                          end
                      and case
                          when
                              :flight_levels is not null
                              then
                                  charge_view.flight_level = any (string_to_array(:flight_levels, ','))
                          else
                              TRUE
                          end

                      and case
                          when
                              :billing_centres is not null
                              then
                                  charge_view.name = any (string_to_array(:billing_centres, ','))
                          else
                              TRUE
                          end

                  group by
                      date,

                      case when
                      'account' = any (string_to_array(:group_by, ','))
                                  then
                                      charge_view.account_name
                              end,

                      case when
                      'bill_aerodrome' = any (string_to_array(:group_by, ','))
                                  then
                                      charge_view.bill_aerodrome
                              end,

                      case when
                      'aircraft_type' = any (string_to_array(:group_by, ','))
                                  then
                                      charge_view.aircraft_type
                              end,

                      case when
                      'billing_centre' = any (string_to_array(:group_by, ','))
                                  then
                                      charge_view.name
                              end,

                      case when
                      'flight_level' = any (string_to_array(:group_by, ','))
                                  then
                                      charge_view.flight_level
                              end,

                      case when
                      'flight_rules' = any (string_to_array(:group_by, ','))
                                  then
                                      charge_view.flight_rules
                              end,

                      case when
                      'flight_category' = any (string_to_array(:group_by, ','))
                                  then
                                      charge_view.movement_type
                              end,

                      case when
                      'flight_type' = any (string_to_array(:group_by, ','))
                                  then
                                      charge_view.flight_type
                              end,

                      case when
                      'flight_scope' = any (string_to_array(:group_by, ','))
                                  then
                                      charge_view.flight_scope
                              end,

                      case when
                      'mtow_category' = any (string_to_array(:group_by, ','))
                                  then
                                      charge_view.mtow_category
                              end,

                      case when
                      'route' = any (string_to_array(:group_by, ','))
                                  then
                                      charge_view.route
                              end
              ) sub
order by
    date,
    case when
        'Account' = any (string_to_array(:sort, ','))
        then
            account
    when
        'Temporal Group' = any (string_to_array(:sort, ','))
        then
            date
    when
        'Aerodromes' = any (string_to_array(:sort, ','))
        then
            bill_aerodrome
    when
        'Aircraft Type' = any (string_to_array(:sort, ','))
        then
            aircraft_type
    when
        'Billing Centre' = any (string_to_array(:sort, ','))
        then
            billing_centre
    when
        'Route' = any (string_to_array(:sort, ','))
        then
            route
    when
        'Flight Schedule Type' = any (string_to_array(:sort, ','))
        then
            movement_type
    when
        'Flight Type' = any (string_to_array(:sort, ','))
        then
            flight_type
    when
        'Flight Scope' = any (string_to_array(:sort, ','))
        then
            flight_scope
    when
        'Flight Level' = any (string_to_array(:sort, ','))
        then
            flight_level
    when
        'Flight Rules' = any (string_to_array(:sort, ','))
        then
            flight_rules
    when
        'MTOW Category' = any (string_to_array(:sort, ','))
        then
            cast (mtow_category as text)
    end
