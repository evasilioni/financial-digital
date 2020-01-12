CREATE TABLE TRANSFERS
(
    id IDENTITY PRIMARY KEY NOT NULL,
    source bigint NOT NULL,
    destination bigint NOT NULL,
    amount number(30,2) NOT NULL default 0,
    description varchar(500) NOT NULL DEFAULT '',
    transfer_date datetime NOT NULL DEFAULT now()
);

CREATE INDEX source_idx ON TRANSFERS (source);
CREATE INDEX destination_idx ON TRANSFERS (destination);
