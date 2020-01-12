CREATE TABLE ACCOUNTS
(
    account_id IDENTITY  PRIMARY KEY NOT NULL,
    iban varchar(250) NOT NULL,
    balance number(30,2) NOT NULL default 0,
    currency varchar(64) NOT NULL default 'EUR',
    open_date datetime NOT NULL DEFAULT now(),
    active smallint DEFAULT 1
);

CREATE UNIQUE INDEX account_iban_idx ON ACCOUNTS (iban);

