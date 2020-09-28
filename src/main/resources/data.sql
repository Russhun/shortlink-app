DROP TABLE IF EXISTS link;

CREATE TABLE link
(
    short_path VARChAR(32) NOT NULL
        PRIMARY KEY,
    url        TEXT        NOT NULL,
    expire     DATETIME    NOT NULL
);