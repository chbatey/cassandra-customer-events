CREATE KEYSPACE customers WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };

CREATE TABLE customer_events( customer_id text, staff_id text, time timeuuid, store_type text, event_type text, tags map<text, text>,  PRIMARY KEY ((customer_id), time));

insert into customer_events (customer_id, time , event_type , staff_id , store_type , tags ) VALUES ( 'chbatey', now(), 'basket_add', 'trevor', 'online', { 'item': 'cake'}) ;

CREATE TYPE store (name text, type text, postcode text) ;

CREATE TABLE customer_events_type( customer_id text, staff_id text, time timeuuid, store frozen<store>, event_type text, tags map<text, text>,  PRIMARY KEY ((customer_id), time));

insert into customer_events_type (customer_id, time ,  staff_id , store , event_type , tags ) VALUES ( 'chbatey', now(), 'trevor', { name: 'Ealing', type: 'retail', postcode: 'W3 298' }, 'buy', {});
