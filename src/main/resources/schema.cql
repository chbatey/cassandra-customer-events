CREATE KEYSPACE events WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };

CREATE TABLE  IF NOT EXISTS events.customer_events (customer_id text, staff_id text, store_type text, group text static, content text, time timeuuid, event_type timeuuid, PRIMARY KEY ((customer_id), time) )

CREATE TYPE store (name text, type text, postcode text) ;
CREATE TABLE customer_events_type( customer_id text, staff_id text, time timeuuid, store frozen<store>, event_type text, tags map<text, text>,  PRIMARY KEY ((customer_id), time));
