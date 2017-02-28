CREATE TABLE votes (
  proposal_id VARCHAR(36),
  voter       VARCHAR(36),
  vote        INTEGER,
  UNIQUE KEY (proposal_id, voter)
);

CREATE TABLE proposal (
  id      VARCHAR(36) PRIMARY KEY,
  subject TEXT,
  content TEXT
);
