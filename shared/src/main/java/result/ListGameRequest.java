package result;

import java.util.Collection;

public record ListGameRequest(Collection<GameDataResult> games) {
}
