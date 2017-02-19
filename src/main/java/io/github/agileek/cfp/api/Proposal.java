package io.github.agileek.cfp.api;

import java.util.Objects;

public final class Proposal {
    public final String content;

    public Proposal(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposal proposal = (Proposal) o;
        return Objects.equals(content, proposal.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
