package io.github.agileek.cfp.api;

import java.util.Objects;
import java.util.UUID;

public final class Proposal {
    public final UUID id;
    public final String subject;
    public final String content;

    public Proposal(UUID id, String subject, String content) {
        this.id = id;
        this.subject = subject;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposal proposal = (Proposal) o;
        return Objects.equals(id, proposal.id) &&
                Objects.equals(subject, proposal.subject) &&
                Objects.equals(content, proposal.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subject, content);
    }
}
