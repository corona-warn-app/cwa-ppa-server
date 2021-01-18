-- see metrics.proto

-- for recreating from scratch
/* 
DROP TABLE IF EXISTS int_data;
DROP TABLE IF EXISTS text_data;
DROP TABLE IF EXISTS float_data;
DROP TABLE IF EXISTS meta_data;
*/

CREATE TABLE meta_data (
    mid BIGSERIAL PRIMARY KEY, -- unique ID per analytic data submission
    os VARCHAR(10) NOT NULL, -- from which device version did we receive this?
    storeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- required for data rentention
);

-- main table for the different integer metric values 
CREATE TABLE int_data (
    iid BIGINT NOT NULL, -- references mid
    metric INTEGER NOT NULL, -- enum coming from mobile-dev-team
    m_val BIGINT NOT NULL, -- the value
    CONSTRAINT fk_meta_int FOREIGN KEY(iid) REFERENCES meta_data(mid) ON DELETE CASCADE
);

-- optional table in case we need to store string/text values 
CREATE TABLE text_data (
    tid BIGINT NOT NULL, -- references mid
    metric INTEGER NOT NULL, -- enum coming from mobile-dev-team
    m_val TEXT NOT NULL, -- the value
    CONSTRAINT fk_meta_int FOREIGN KEY(tid) REFERENCES meta_data(mid) ON DELETE CASCADE
);

-- optional table in case we need to store float/double values 
CREATE TABLE float_data (
    fid BIGINT NOT NULL, -- references mid
    metric INTEGER NOT NULL, -- enum coming from mobile-dev-team
    m_val DOUBLE PRECISION NOT NULL, -- the value
    CONSTRAINT fk_meta_int FOREIGN KEY(fid) REFERENCES meta_data(mid) ON DELETE CASCADE
);

-- some dummy tests for the relations
/*
insert into meta_data(os) values ('iOS'), ('Android');
select * from meta_data;

insert into int_data(iid, metric, m_val) values (1,1,1), (1,2,2), (2,1,3), (2,2,4);
select * from int_data;

delete from meta_data where mid = 1;
select * from int_data;
*/
