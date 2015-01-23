CREATE TABLE customer_events (
    customer_id text,
    staff_id text,
    store_type text,
    group text static,
    content text,
    time timeuuid,
    event_type text,
    PRIMARY KEY ((customer_id), time) );

insert into customer_events(customer_id, time , staff_id, store_type, group, content, event_type)
    VALUES ( 'chbatey', c8236b90-a316-11e4-871a-bb9f5ed27228, 'trevor', 'online', 'NEW_CUSTOMER', 'awesome content', 'BASKET_ADD');

