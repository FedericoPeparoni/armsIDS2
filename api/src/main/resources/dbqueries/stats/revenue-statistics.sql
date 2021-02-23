/**
    Data Analysis and Statistics - Revenue

    Note: this queries the view `revenue_view`

    Required parameters
    ===================
    @param temporal_group   week | month | quarter | year
    @param start_date       ex. 2013-01-01
    @param end_date         ex. 2018-01-1

    Optional parameters
    ===================
    Where In (comma separated)
    Note: there are all pluralized to differentiate from the `group_by`
    -----------
    @param accounts
    @param aerodromes
    @param analysis_type
    @param billing_centres
    @param charge_category
    @param charge_class
    @param charge_type
    @param fiscal_year
    @param payment_mode

    Group by
    Note: these are all singular to differentiate from the `where in`
    -----------
    @param account
    @param aerodrome
    @param analysis_type
    @param billing_centre
    @param class/category/type
    @param payment_mode

    Order by
    @param sort column to sort by `&sort=aircraft_type`

 */

SELECT *
FROM (
         SELECT
             DISTINCT
             CASE WHEN :temporal_group = 'week'
                 THEN
                     extract(YEAR FROM invoice_period_or_date) || '-' || extract(WEEK FROM invoice_period_or_date)
             WHEN :temporal_group = 'month'
                 THEN
                     extract(YEAR FROM invoice_period_or_date) || '-' || extract(MONTH FROM invoice_period_or_date)
             WHEN :temporal_group = 'quarter'
                 THEN
                     extract(YEAR FROM invoice_period_or_date) || '-' || extract(QUARTER FROM invoice_period_or_date)
             WHEN :temporal_group = 'year'
                 THEN
                     extract(YEAR FROM invoice_period_or_date) || '-' || extract(YEAR FROM invoice_period_or_date)
             END
                                 AS date,
             max(name)           AS billing_centre,
             max(payment_mode)   AS payment_mode,
             max(account_name)   AS account_name,
             max(aerodrome_name) AS aerodrome_name,
             max(charge_class)   AS charge_class,
             max(category)       AS category,
             max(type)           AS type,
             max(is_paid)        AS is_paid,
             count(*),
             round(cast (sum(usd) as numeric),2)    AS usd_sum,
             round(cast (sum(ansp) as numeric),2)   AS ansp_sum

         FROM revenue_view_aviation
         WHERE invoice_period_or_date BETWEEN cast(:start_date AS DATE) AND cast(:end_date AS DATE)
               AND
                   CASE
                       WHEN :analysis_type = 'payment' THEN
                           is_paid = 'T'
                   ELSE
                         TRUE
                   END
               AND
                   CASE
                       WHEN :accounts IS NOT NULL THEN
                           account_name = ANY (string_to_array(:accounts, ','))
                   ELSE
                       TRUE
                   END
               AND
                   CASE
                        WHEN :billing_centres IS NOT NULL THEN
                            name = ANY (string_to_array(:billing_centres, ','))
                   ELSE
                        TRUE
                   END
               AND
                   CASE
                        WHEN :aerodromes IS NOT NULL THEN
                             aerodrome_name = ANY (string_to_array(:aerodromes, ','))
                   ELSE
                        TRUE
                   END
               AND
                   (:charge_class IS NULL OR charge_class = :charge_class)
               AND
                   (:charge_category IS NULL OR category = :charge_category)
               AND
                   (:charge_type IS NULL OR type = :charge_type)
               AND
                    CASE
                          WHEN :payment_mode = 'cash' THEN
                               payment_mode = 'cash'
                          WHEN :payment_mode = 'credit' THEN
                               payment_mode = 'credit'
                          ELSE
                               TRUE
                    END
         GROUP BY
                 date,
                 CASE
                      WHEN 'account_name' = any (string_to_array(:group_by, ',')) THEN
                            revenue_view_aviation.account_name
                 END,
                 CASE
                      WHEN 'aerodrome_name' = any (string_to_array(:group_by, ',')) THEN
                            revenue_view_aviation.aerodrome_name
                 END,
                 CASE
                      WHEN 'analysis_type' = any (string_to_array(:group_by, ',')) THEN
                            revenue_view_aviation.is_paid
                 END,
                 CASE
                      WHEN 'billing_centre' = any (string_to_array(:group_by, ',')) THEN
                            revenue_view_aviation.name
                 END,
                 CASE
                      WHEN 'charge_class' = any (string_to_array(:group_by, ',')) THEN
                            revenue_view_aviation.charge_class
                 END,
                 CASE
                      WHEN 'category' = any (string_to_array(:group_by, ',')) THEN
                            revenue_view_aviation.category
                 END,
                 CASE
                      WHEN 'type' = any (string_to_array(:group_by, ',')) THEN
                            revenue_view_aviation.type
                 END,
                 CASE
                      WHEN 'payment_mode' = any (string_to_array(:group_by, ',')) THEN
                            revenue_view_aviation.payment_mode
                 END
     ) sub
ORDER BY
    date,
    CASE
    WHEN 'Account Name' = any (string_to_array(:sort, ','))
        THEN
            account_name
    WHEN 'Aerodrome Name' = any (string_to_array(:sort, ','))
        THEN
            aerodrome_name
    WHEN 'Analysis Type' = any (string_to_array(:sort, ','))
        THEN
            is_paid
    WHEN 'Billing Centre' = any (string_to_array(:sort, ','))
        THEN
            billing_centre
    WHEN 'Charge Class' = any (string_to_array(:sort, ','))
        THEN
            charge_class
    WHEN 'Category' = any (string_to_array(:sort, ','))
        THEN
            category
    WHEN 'Payment Mode' = any (string_to_array(:sort, ','))
        THEN
            payment_mode
    WHEN 'Type' = any (string_to_array(:sort, ','))
        THEN
            type
    END
